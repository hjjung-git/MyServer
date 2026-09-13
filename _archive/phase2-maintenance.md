# Phase 2 : Maintaining & Operation

---

## Step 1. Feature & Code Management

### 1. Architecture Refactoring

#### 1.1. CRUD

전체적인 CRUD 구현 및 시간 포맷 / 정렬 처리

- 수정 / 삭제의 버튼 및 폼(`View`), 로직(`Controller`) 구성
- **반복 상태 변수** : 게시글의 고유 ID로 Repository 정렬 처리
- 로컬 / 서버 **시간대** 적용 (Local → Zoned)

#### 1.2. Layered Architecture (SoC)

| 구분 | 구조 |
| :--- | :--- |
| 기존 | Controller → Repository 직접 호출 |
| 이후 | Controller → Service → Repository 계층형 구조 |

> Service 계층 추가 구현 → `GuestbookService.java`

#### 1.3. Entity Remodeling

| 구분 | 엔터티 |
| :--- | :--- |
| 기존 | `Guestbook` (방명록 형식) |
| 이후 | `Post` (제목, 작성자, 내용, 파일이 존재하는 게시글 형식) |

#### 1.4. Package Decoupling

역할별 패키지 분리

```
./my-server/
├── controller/PostController.java
├── service/
│   ├── PostService.java
│   └── PostServiceImpl.java
├── repository/PostRepository.java
└── domain/Post.java
```

#### 1.5. Business Logic

- `Service.java / ServiceImpl.java` 인터페이스 및 구현체 분리
- `@Transactional` 적용 (원자성 확보)

<br>

### 2. Robustness & Validation

#### 2.1. Validation

- **Dependency** : `spring-boot-starter-validation`
- `@Valid`, `@NotBlank` 를 사용한 도메인 검증

#### 2.2. Exception Handling

`exception/GlobalExceptionHandler.java` — 전역 예외 처리로 유지보수성 및 견고함 향상

#### 2.3. Custom Error

- `exception/PostNotFoundException.java`
- 커스텀 에러 페이지 : `404.html`, `500.html`

<br>

### 3. Quality Assurance

#### 3.1. Unit Testing

- **Dependency** : `spring-boot-starter-test` (JUnit5, Mockito 포함)

#### 3.2. Mocking

**Mockito** 를 사용하여 `Repository` 를 Mock 으로 만들기  
→ 실제 DB에 접근하지 않고 로직을 빠르게 검증

작성 순서

1. 테스트 클래스 생성
2. `@Mock` 으로 `PostRepository` 모킹
3. `@InjectMocks` 로 `PostServiceImpl` 주입
4. 테스트 케이스 작성 (성공 / 실패)

<br>

### 4. Functional Enhancement

#### 4.1. Pagination

| 레이어 | 구현 내용 |
| :--- | :--- |
| Spring Data JPA | `Pageable` 인터페이스와 `Page<T>` 객체 활용 |
| Controller | `@PageableDefault` 으로 기본 페이지 크기 및 정렬 조건 설정 |
| View | `posts.totalPages`, `posts.number` 등의 메타 데이터로 네비게이션 UI 구현 |

#### 4.2. Search Functionality

| 레이어 | 구현 내용 |
| :--- | :--- |
| Repository | `findByTitleContainingOrContentContaining()` — `LIKE` 쿼리 자동 생성 |
| Service | 검색어 존재 여부에 따른 분기 처리 |
| Controller | `@RequestParam()` 으로 검색어 수신 |
| 상태 유지 | 검색 상태를 유지한 채로 페이지 이동 가능 |

<br>

### 5. File Upload

#### 5.1. Configuration

```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
file.upload-dir=./uploads
```

#### 5.2. Implementation

| 단계 | 내용 |
| :---: | :--- |
| 1 | **Domain** : `Post` 엔터티에 `filePath` 컬럼 추가 |
| 2 | **Service** : UUID 기반 파일명 생성 및 저장 로직 구현 |
| 3 | **Controller** : `MultipartFile` 수신 및 Service 전달 |
| 4 | **View** : `write-form.html` / `detail.html` / `edit.html` 파일 업로드 · 다운로드 UI 추가 |

#### 5.3. Resource Mapping

`config/WebConfig.java` — `WebMvcConfigurer` 구현체  
OS에 독립적인 경로 처리 (`toURI()`)

#### 5.4. Nginx 설정 (대용량 파일 허용)

```nginx
server {
    client_max_body_size 10M;
}
```

<br>

### 6. Configuration Externalization

#### 6.1. Profile Separation

| 파일 | 용도 |
| :--- | :--- |
| `application.properties` | 공통 설정 |
| `application-local.properties` | 로컬 개발 환경 (H2) |
| `application-prod.properties` | 운영 환경 (MySQL) |

#### 6.2. Deployment

```bash
# 운영 서버 실행 시 프로필 명시
java -jar my-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

<br>

---

## Step 2. Data Maintenance

### 1. Database Migration (H2 → MySQL)

#### 1.1. Infrastructure Setup (AWS RDS)

| 항목 | 설정값 |
| :--- | :--- |
| 구성 | MySQL |
| 템플릿 | Free Tier |
| DB 인스턴스 유형 | `db.t3.micro` |
| 스토리지 유형 | 범용 SSD (gp3) |
| 퍼블릭 엑세스 | Yes |

#### 1.2. Security Configuration

인바운드 규칙 — 로컬 PC (내 IP) + EC2 보안 그룹만 3306 포트 허용

#### 1.3. Project Configuration

- **Dependency** : `mysql-connector-j`
- `application-prod.properties` 에 datasource URL / username / password / driver 설정

<br>

### 2. Data Backup & Restore

#### 2.1. Automated Backup (AWS RDS Snapshot)

RDS 콘솔 → DB 수정 → 백업 보존 기간 1일 이상 설정 → 즉시 적용

#### 2.2. Recovery Process

RDS 콘솔 → 스냅샷 → 원하는 시점 선택 → 복원 → 새 인스턴스 생성 후 엔드포인트 교체

<br>

---

## Step 3. User Authentication and Access Control

### 1. Spring Security

#### 1.1. Dependencies

```
spring-boot-starter-security
thymeleaf-extras-springsecurity6
```

#### 1.2. Concepts

| 개념 | 설명 |
| :--- | :--- |
| Authentication | 사용자의 신원 확인 |
| Authorization | 인증된 사용자의 권한 확인 |

<br>

### 2. User Model & Repository

| 클래스 | 역할 |
| :--- | :--- |
| `domain/Role.java` | ADMIN / USER 권한 Enum |
| `domain/User.java` | UserDetails 구현 엔터티 |
| `repository/UserRepository.java` | 사용자 DB 조회 인터페이스 |

<br>

### 3. Security Configuration

`config/SecurityConfig.java`

- BCrypt 비밀번호 암호화
- URL별 접근 권한 설정
- 로그인 / 로그아웃 처리

<br>

### 4. Authentication Service

| 클래스 | 역할 |
| :--- | :--- |
| `service/UserService.java` | 회원가입, 중복 검사 |
| `service/UserSecurityService.java` | UserDetailsService 구현 |
| `controller/UserController.java` | 로그인 / 회원가입 요청 처리 |
| `templates/user/login.html` | 로그인 폼 |
| `templates/user/join.html` | 회원가입 폼 |

<br>

### 5. UI Integration & Authorization

- `Post` 엔터티에 `User` 연관관계 (`@ManyToOne`) 추가
- `PostServiceImpl` — 게시글 저장 시 로그인 사용자 정보 반영
- `list.html` — 로그인 상태에 따라 글쓰기 버튼 표시
- `detail.html` — 작성자와 로그인 사용자가 같을 때만 수정 / 삭제 버튼 표시

<br>

---

## Step 4. Infrastructure Maintenance

### 1. Log Management (Logback)

#### 1.1. Configuration

`src/main/resources/logback-spring.xml` 생성

- **local** 프로필 : 콘솔 출력, 애플리케이션 로그 DEBUG 레벨
- **prod** 프로필 : 파일 분리 저장 (콘솔 출력 없음)

| 파일 | 보관 기간 |
| :--- | :--- |
| `logs/info.log` | 30일 |
| `logs/warn.log` | 30일 |
| `logs/error.log` | 90일 |

매일 자정 롤링 — `logs/info.2025-06-14.log` 형식으로 날짜별 분리

<br>

### 2. CI/CD Automation (GitHub Actions)

#### 2.1. Workflow

`.github/workflows/deploy.yml` 생성

`main` 브랜치 push 시 자동 실행

```
Checkout → JDK 21 설치 → Maven 빌드 → SCP 전송 → systemctl restart
```

#### 2.2. GitHub Secrets 등록

| Secret | 값 |
| :--- | :--- |
| `EC2_HOST` | EC2 퍼블릭 IP 또는 도메인 |
| `EC2_USER` | `ec2-user` |
| `EC2_SSH_KEY` | `.pem` 파일 전체 내용 |

<br>

### 3. systemd 서비스 등록

EC2 재부팅 시 자동 실행 + 비정상 종료 시 자동 재시작

`/etc/systemd/system/my-server.service`

```ini
[Unit]
Description=My Server Spring Boot Application
After=network.target

[Service]
User=ec2-user
WorkingDirectory=/home/ec2-user
ExecStart=/usr/bin/java -jar /home/ec2-user/my-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
```

```bash
sudo systemctl daemon-reload
sudo systemctl enable my-server
sudo systemctl start my-server
```

> **WorkingDirectory 설정이 필요한 이유**  
> systemd는 기본 작업 디렉토리가 `/` (루트)이므로, `./logs` 같은 상대경로가 `/logs`로 해석된다.  
> `WorkingDirectory=/home/ec2-user` 로 고정해야 상대경로가 올바르게 동작한다.

<br>

---

## 배경 개념 (Study Notes)

> 이 Phase를 진행하며 정리한 핵심 개념(Why/How).

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
