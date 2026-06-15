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
