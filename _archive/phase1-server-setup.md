# Phase 1 : How to set up my WebServer

---

## Step 1. Development Environment Setup

### 1. Arranging Development Environment

#### 1.1. JDK Installation

> Java OpenJDK 21.0.2 (LTS version)

#### 1.2. IDE Installation

> IntelliJ IDEA (Community Edition)

#### 1.3. Build Tools Explained

Maven / Gradle 은 프로젝트에 필요한 라이브러리를 자동으로 관리해주는 도구로,
Spring Boot 프로젝트를 생성 시 자동으로 포함된다.

<br>

### 2. First Initializing Spring Boot

#### 2.1. Generate Spring Boot project via Spring Initializer

> https://start.spring.io/

| 항목 | 설정값 |
| :--- | :--- |
| Project | Maven Project |
| Language | Java |
| Spring Boot | 4.0.0 |
| Packaging | Jar |
| Configuration | Properties |
| Dependencies | Spring Web |

#### 2.2. Server run & test

> http://localhost:8080/hello

<br>

---

## Step 2. DB Engineering (H2)

> https://www.h2database.com/html/main.html

### 1. Configuration

#### 1.1. Dependencies (`pom.xml`)

```
spring-boot-starter-data-jpa   (JPA)
h2                             (In-memory DB)
```

H2 DB를 사용하는 이유 : 설정이 간단하여 런타임 DB 개념을 익히기 좋다.

#### 1.2. Settings (`application.properties`)

```properties
# File-based DB for data persistence
spring.datasource.url=jdbc:h2:file:./data/testdb

# Auto DDL (Create/Update tables)
spring.jpa.hibernate.ddl-auto=update
```

<br>

### 2. Implementation

#### 2.1. Domain (Entity)

새로운 domain 클래스 생성 및 구성  
→ Guestbook 클래스는 데이터 모델(엔티티)를 구성하는 클래스가 된다.

> Create New Java Class → `Guestbook.java`

#### 2.2. Repository

`JpaRepository` 상속을 통한 CRUD 메소드 자동 생성

> Create New Java Class → `GuestbookRepository.java`

#### 2.3. Controller

HTTP 요청 처리 및 비즈니스 로직 구현

> Create New Java Class → `GuestbookController.java`

<br>

### 3. Verification

- 서버 실행 후 데이터 생성 (`/guestbook/write`)
- 서버 재시작 후 데이터 유지(Persistence) 확인

<br>

---

## Step 3. UI/UX Engineering

### 1. Configuration

#### 1.1. Dependencies (`pom.xml`)

```
spring-boot-starter-thymeleaf   (Template Engine)
```

<br>

### 2. Controller Logic

| 항목 | 변경 내용 |
| :--- | :--- |
| Annotation | `@RestController` → `@Controller` |
| Return Type | JSON 객체 → HTML 파일 이름 (`String`) |
| Data Delivery | `Model` 객체를 사용하여 View로 데이터 전달 |

<br>

### 3. Implement View

- **Templates** : `src/main/resources/templates/` 경로에 HTML 파일 생성
- **Styling** : Bootstrap (CDN) 을 적용하여 UI 디자인 구성

<br>

---

## Step 4. System Management

> https://aws.amazon.com

### 1. Cloud Infrastructure (AWS EC2)

#### 1.1. Instance

| 항목 | 설정값 |
| :--- | :--- |
| AMI | Amazon Linux 2023 |
| Type | t2.micro / t3.micro |

#### 1.2. Security Group (Inbound Rules)

| 규칙 | 유형 | 소스 |
| :---: | :--- | :--- |
| 1 | SSH | 내 IP |
| 2 | HTTP | 위치 무관 |
| 3 | 사용자 정의 TCP (8080) | 위치 무관 |

Key Pair (`.pem`) 생성 및 적용

<br>

### 2. Server Environment Setup

#### 2.1. Access

```bash
ssh -i [your-key-name.pem] ec2-user@[your.ec2.public.IPv4]
```

#### 2.2. Installation

```bash
sudo yum update -y
sudo yum install java-21-amazon-corretto -y
sudo yum install maven -y
```

<br>

### 3. Deployment

#### 3.1. Build on Local

```bash
mvn clean package
```

#### 3.2. Transfer

```bash
scp -i [your-key.pem] /path/to/target/my-...-SNAPSHOT.jar ec2-user@[your.ec2.public.IPv4]:~/
```

#### 3.3. Run

```bash
# 포그라운드
java -jar my-server-0.0.1-SNAPSHOT.jar

# 백그라운드
nohup java -jar my-server-0.0.1-SNAPSHOT.jar &
```

<br>

### 4. Stability & Networking

#### 4.1. Assign Elastic IP

- 재시작 시 IP 변경 방지를 위해 고정 IP 할당

#### 4.2. Set Domain

- DNS 설정을 통해 도메인을 서버 퍼블릭 IPv4로 연결

<br>

---

## Step 5. Security Engineering

### 1. Apply HTTPS

#### 1.1. Install Nginx & Setting

```bash
sudo dnf install nginx -y
sudo systemctl start nginx
sudo systemctl enable nginx
sudo vi /etc/nginx/conf.d/my-server.conf
```

```nginx
server {
    listen 80;
    server_name [MY_DOMAIN];

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

#### 1.2. Get Free SSL Certification

```bash
sudo dnf install -y certbot python3-certbot-nginx
sudo certbot --nginx -d [MY_DOMAIN]
```

#### 1.3. Modify Security Group

| 유형 | 포트 | 소스 |
| :--- | :---: | :--- |
| HTTPS | 443 | 0.0.0.0/0 |

<br>

---

## 배경 개념 (Study Notes)

> 이 Phase를 진행하며 정리한 핵심 개념(Why/How).

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
