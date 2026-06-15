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
