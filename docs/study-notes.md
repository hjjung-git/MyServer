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
