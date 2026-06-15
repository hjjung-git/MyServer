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
