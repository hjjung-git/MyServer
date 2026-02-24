
# MyServer Project

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=flat-square&logo=amazonaws&logoColor=white)
![H2 Database](https://img.shields.io/badge/H2-blue)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=MySQL&logoColor=white)

> NCS ICT 직무(응용SW, DB, 보안, UI/UX, IT시스템관리) 를 연계하여  
> 처음부터 끝까지 직접 구축해보는 나만의 웹 서버 프로젝트

<br>

---

# Project Overview

이 프로젝트는 단순한 웹 사이트 개발을 넘어, 하나의 완성된 IT 서비스가 탄생하는 과정 전체를 경험하는 것을 목표로 한다.
컴퓨터공학과 전공자로서 배운 이론을 실제 서비스에 적용하고, NCS에서 제시하는 5가지 핵심 직무의 역할을 이해하며 통합적인 시각을 기르는 데 중점을 두었다.

### Main Features
- 웹서버 구축 및 관리
- 웹서버 유지 보수
- 다양한 포트폴리오 기록

### Contents
1) **Tech Stack**
2) **Development Process**
3) **Technical Design Document**

<br>

## Project Milestone

#### Phase 1 : Server Setting

| 직무             | 프로젝트 내 역할               | 관련 기술/키워드                                 |
| :------------- | :---------------------- | :---------------------------------------- |
| **응용SW엔지니어링**  | 서비스의 핵심 로직 및 API 개발     | Java, Spring Boot, RESTful API            |
| **DB엔지니어링**    | 데이터 모델링 및 영속성 관리        | H2, JPA, CRUD                             |
| **UI/UX엔지니어링** | 사용자 인터페이스 설계 및 구현       | HTML, CSS, Bootstrap, 반응형 웹               |
| **IT시스템관리**    | 서버 구축, 배포 및 운영          | AWS EC2, Docker, Nginx                    |
| **보안엔지니어링**    | 인증/인가, 데이터 암호화 및 취약점 방어 | HTTPS, Password Hashing, Input Validation |

<br>

#### Phase 2 : Server Maintenance

| 직무            | 프로젝트 내 역할          | 관련 기술/키워드                                                      |
| :------------ | :----------------- | :------------------------------------------------------------- |
| **응용SW엔지니어링** | 기능 개발 및 코드 관리      | CRUD, Refactoring                                              |
| **DB엔지니어링**   | 데이터베이스 관리 및 구조     | Backup & Restore, DB Migration, MySQL                          |
| **IT시스템관리**   | 인프라 엔지니어링 및 보안     | Security Patch, Log Management, Resource Monitoring            |
| **IT시스템관리**   | DevOps 엔지니어링 및 자동화 | CI/CD, GitHub Actions, Automated Testing, Automated Deployment |

<br>

---

## Tech Stack

- **OS**
	- `Windows 11 Home x64 (24H2)`
	- `macOS 26 Tahoe`
- **Backend**
	- Runtime / Language
		- `Java 21`
	- Framework
		- `Spring Boot 4.0.0`
		- `Spring Data JPA`
- **Frontend**
	- `HTML5`, `CSS3`
	- `Bootstrap 5`
- **Database**
	- `H2`
	- `MySQL`
- **Deployment**
	- `AWS EC2 Linux / ami-2023`
	- `Docker`
	- `Nginx (Web Server)`
- **Tools**
	- IDE
		- `IntelliJ IDEA (Community Edition)`
	- Build Tool
		- `Maven`
	- Version Control
		- `GitHub`

<br>

---

# Phase 1 : How to set up my WebServer

## Step 1. Development Environment Setup & Testing

### 1. Arranging Development Environment

#### 1.1. JDK Installation  
> Java OpenJDK 21.0.2 (LTS version)

#### 1.2. IDE Installation
> IntelliJ IDEA (Community Edition)

#### 1.3. Build Tools Explained
 : Maven / Gradle 은 프로젝트에 필요한 라이브러리를 자동으로 관리해주는 도구  
  Spring Boot 프로젝트를 생성 시 자동으로 포함

<br>

### 2. First Initializing Spring Boot

#### 2.1. Generate Spring Boot project to Spring Initializer
~ https://start.spring.io/  
> **Project** : Maven Project  
> **Language** : Java  
> **Spring Boot version** : 4.0.0  
> **Packaging** : Jar  
> **Configuration** : Properties  
> **Dependencies** : Spring Web  
#### 2.2. Server run & test
~ http://localhost:8080/hello

<br>



<br>

## Step 2. DB Engineering (H2)
~ https://www.h2database.com/html/main.html

### 1. Configuration

#### 1.1. Dependencies (`pom.xml`)

> `spring-boot-starter-data-jpa` (JPA)  
> `h2`(In-memory DB)
- H2 DB 를 사용하는 이유 : 설정이 간단하여 런타임 DB 개념을 익히기 좋다.

#### 1.2. Settings (application.properties)
```properties
# File-based DB for data persistence
spring.datasource.url=jdbc:h2:file:./data/testdb
# Auto DDL (Create/Update tables)
spring.jpa.hibernate.ddl-auto=update
```

<br>

### 2. Implementation

#### 2.1. Domain (Entity)
: 새로운 domain 클래스 생성 및 구성  
-> Guestbook 클래스는 데이터 모델(엔티티)를 구성하는 클래스가 된다.

>Create New Java Class (`Guestbook.java`)

#### 2.2. Repository
: `JpaRepository`상속을 통한 CRUD 메소드 자동 생성

> Create New Java Class (`GuestbookRepository.java`)

#### 2.3. Controller
: HTTP 요청 처리 및 비즈니스 로직 구현

> Create New Java Class (`GuestbookController.java`)

<br>

### 3. Verification
- 서버 실행 후 데이터 생성 (`/guestbook/write`)
- 서버 재시작 후 <u>데이터 유지(Persistence)</u> 확인

<br>



<br>

## Step 3. UI/UX Engineering

### 1. Configuration

#### 1.1. Dependencies (`pom.xml`)

> `spring-boot-starter-thymeleaf`(Template Engine)

<br>

### 2. Controller Logic
- **Annotation** : `@RestConroller` -> `@Controller` 변경
- **Return Type** : JSON 객체 -> HTML 파일 이름 (`String`) 반환
- **Data Delivery** : `Model`객체를 사용하여 View로 데이터 전달

<br>

### 3. View Implementation
- **Templates** : `src/main/resources/templates/`경로에 HTML 파일 생성
- **Styling** : Bootstrap(CDN) 을 적용하여 UI 디자인 구성

<br>


<br>

## Step 4. System Management
~ https://aws.amazon.com

### 1. Cloud Infrastructure (AWS EC2)

#### 1.1. Instance

> **AMI** : Amazon Linux 2023 AMI  
> **Type** : t2 / t3.micro

#### 1.2. Security
: Key Pair (`.pem`) 생성 및 보안 그룹 설정

> **규칙 1** : 유형 `SSH` , 소스 `내 IP` (나의 컴퓨터에서만 SSH 접속)  
> **규칙 2** : 유형 `HTTP`, 소스 `위치 무관` (아무나 HTTP로 접속 가능)  
> **규칙 3** : 유형 `사용자정의 TCP`, 포트 범위 `8080`, 소스 `위치 무관`

<br>

### 2. Server Environment Setup

#### 2.1. Access
: 만든 클라우드 서버에 원격 접속

```bash
ssh -i [your-key-name.pem] ec2-user@[your.ec2.public.IPv4]
```

#### 2.2. Installation
: 서버에 프로젝트 실행 도구 설치

```bash
# 설치 도구 업데이트
sudo yum update -y

# 자바21 설치
sudo yum install java-21-amazon-corretto -y

# Maven 설치
sudo yum install maven -y
```

<br>

### 3. Deployment
: 프로젝트를 서버로 옮겨서 실행하기

#### 3.1. Build on Local
```bash
# IntelliJ 내 로컬 터미널에서 실행
# 프로젝트를 실행 가능한 .jar 파일로 만들기
mvn clean package
```
-> 프로젝트 폴더 하위에 `target/my-server ... SNAPSHOT.jar`  생성 확인

#### 3.2. Transfer
```bash
# 새로운 로컬 터미널 실행
# jar 파일을 서버의 홈 디렉토리로 복사
scp -i [your-key.pem] /path/to/your/project/target/my-...-SNAPSHOT.jar ec2-user@[your.ec2.public.IPv4]:~/
```

#### 3.3. Run
```bash
# .jar 파일이 잘 복사되었는지 확인
ls

# 서버 실행
java -jar my-server-0.0.1-SNAPSHOT.jar

# 백그라운드에서 실행
nohup java -jar my-server-0.0.1-SNAPSHOT.jar &
```

<br>

### 4. Stability & Networking

#### 4.1. Assign Elastic IP
- **Elastic IP** : 재시작 시 IP 변경 방지를 위해 고정 IP 할당

#### 4.2. Set Domain
: 순수 IP 주소가 아닌 도메인을 구하여 **DNS 설정**을 통해 도메인을 서버 퍼블릭 IPv4로 연결

<br>


<br>

## Step 5. Security Engineering

### 1. Apply HTTPS

#### 1.1. Install Nginx & Setting
```bash
# AWS 서버에 SSH로 접속하여 설치
sudo dnf install nginx -y

# Nginx 시작 및 자동 실행 설정
sudo systemctl start nginx
sudo systemctl enable nginx

# 리버스 프록시 설정
sudo vi /etc/nginx/conf.d/my-server.conf
```
```nginx
# vi 편집기를 통해 프록시 설정 코드
server {
	listen 80;
	server_name [MY_DOMAIN];
	
	location / {
		proxy_pass http://localhost:8080;
		proxy_set_header Host $host;
		proxy_set_header X-Real_IP $remote_addr;
		proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
		proxy_set_header X-Forwarded-Proto $scheme;
	}
}
```
```bash
# Nginx 재시작 (설정 적용)
sudo systemctl restart nginx
```

#### 1.2. Get Free SSL Certification
```bash
# Certbot 설치
sudo dnf install -y certbot python3-certbot-nginx

# 인증서 발급 및 설치
sudo certbot --nginx -d [MY_DOMAIN]
```

#### 1.3. Modify Security Group
: HTTPS 인증서 연결에 따라 HTTPS 포트 (443) 개방  
-> AWS 보안그룹 새로운 인바운드 규칙 추가

> **유형** : HTTPS  
> **포트 범위** : 443  
> **소스** : IPv4 Anywhere (0.0.0.0)

<br>

---


# Phase 2 : Maintaining & Operation

## Step 1. Application Maintenance
: 사용자 친화적인 완성도 있는 서비스 제공

### 1. Architecture Refactoring

#### 1.1. CRUD
: 전체적인 CRUD 구현 및 시간 포맷 / 정렬 처리

- 수정 / 삭제의 버튼 및 폼(`View`), 로직(`Controller`) 구성
- **반복 상태 변수** : 게시글의 고유 ID로 Repository 정렬 처리
- 로컬 / 서버 **시간대** 적용 (Local -> Zoned)

#### 1.2. Layered Architecture (SoC)
- 기존 : Controller - Repository 직접 호출 구조

> Service 계층 추가 구현 (`GuestbookService.java`)

- 이후 : Controller - Service - Repository 계층형 구조

#### 1.3. Entity Remodeling
- 기존 : `Guestbook` (방명록 형식)

> `Guestbook` -> `Post`

- 이후 : `Post` (제목, 작성자, 내용, 파일이 존재하는 게시글 형식)

#### 1.4. Package Decoupling
: 역할별 패키지 분리

`./my-server/`
- `controller/PostController.java`
- `service/`
	- `PostService.java`
	- `PostServiceImpl.java`
- `repository/PostRepository.java`
- `domain/Post.java`

#### 1.5. Business Logic

> `Service.java / ServiceImpl.java` 인터페이스 및 구현체 분리
> `@Transactional` 적용 (원자성 확보)

<br>

### 2. Robustness & Validation

#### 2.1. Validation

- **Dependency** (`pom.xml`)
> `spring-boot-starter-validation`

: `@Valid`, `@NotBlank` 를 사용한 도메인 검증

#### 2.2. Exception Handling

> 패키지 `exception` / 클래스 `GlobalExceptionHandler.java` 

: 전역 예외 처리로 유지 보수성 및 견고함 향상

#### 2.3. Custom Error

> `exception` / 클래스 `PostNotFoundException.java`  
> HTML 폼 : `404.html`, `500.html`

: 에러 페이지를 커스텀화

<br>

### 3. Quality Assurance

#### 3.1. Unit Testing
: 코드 품질을 보증하기 위한 코드 테스트 자동화

- **Dependency** (`pom.xml`)

> `spring-boot-starter-test` (JUnit5, Mockito 등 포함)

#### 3.2. Mocking
: **Mockito** 를 사용하여 `Repository` 를 Mock 으로 만들기  
-> 실제로 DB에 접근하지 않고 로직을 빠르게 검증 가능

>- 작성 순서
>1) 테스트 클래스 생성
>2) `@Mock` 으로 `PostRepository` 모킹
>3) `@InjectMocks` 로 `PostServiceImpl` 주입
>4) 테스트 케이스 작성 (성공/실패)

<br>

### 4. Funtional Enhancement

#### 4.1. Pagination
: 게시글 목록을 페이지 단위로 나누어 한 번에 로드되는 데이터 양을 조절

- **Implementation**:
    - **Spring Data JPA**: `Pageable` 인터페이스와 `Page<T>` 객체 활용
    - **Controller**: `@PageableDefault` 어노테이션으로 기본 페이지 크기 및 정렬 조건 설정
    - **Service**: `Repository`로부터 반환된 `Page` 객체를 그대로 View로 전달
- **View (Thymeleaf)**:
	   - `posts.content`를 통해 실제 게시글 리스트 출력
    - `posts.totalPages`, `posts.number` 등의 메타 데이터로 페이지 네비게이션 UI 구현
	- 페이지 이동 링크에 `page` 파라미터 전달

#### 4.2. Search Funcionality
: 게시글의 제목이나 내용을 기준으로 데이터 필터링

- **Implementation**:
    - **Repository**: Spring Data JPA의 Query Methods 기능 사용
        - `findByTitleContainingOrContentContaining()`
        - 메소드 명명 규칙을 통해 `LIKE` 쿼리 자동 생성 (`Containing`)
    - **Service**: 검색어(`keyword`) 존재 여부에 따른 분기 처리.
        - 검색어 없음: `findAll(pageable)` 호출.
        - 검색어 있음: 검색용 쿼리 메소드 호출.
    - **Controller**: `@RequestParam()`를 통해 검색어 수신 및 `Model`에 다시 담아 View로 전달.
- **State Persistence (상태 유지)**
    : 검색 상태를 유지한 채로 페이지 이동 가능

<br>

### 5. File Upload
: 게시글에 이미지나 문서를 첨부하는 기능 구현

#### 5.1. Configuration
```properties
# File Upload Settings
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Path Settings
file.upload-dir=./uploads
```

#### 5.2. Implementation

 1) **Domain : Entity Field Extention**
 >  : `Post` 엔터티에 파일 경로 컬럼 추가

 2) **Service : File Storage Logic**
 >   -> Service 인터페이스 수정 / 구현체 로직 구현
    -> `UUID`활용

3) **Controller : Request Handling**
>    -> Controller 에서 `MultipartFile` 을 받아 Service 로 전달
    - `@RequestParam` 으로 경로 받기
    - `IOException` 예외처리

4) **View : Form Configuration**
>   : `write-form.html`/ `detail.html` / `edit.html` 이미지 및 파일 다운로드 뷰 추가

#### 5.3. Resource Mapping
: 웹 브라우저가 서버의 로컬 디렉터리 `./uploads` 에 접근할 수 있도록 매핑

> 새로운 패키지 `config` / `WebConfig.java`

- `@Configuration`어노테이션 활용 및 `WebMvcConfigurer`인터페이스 구현체로 작성
- **Key Point** : <u>OS에 독립적인 경로 처리 (`toURI()`)</u>

#### 5.4. Deployment
: AWS(EC2) 배포 시, Nginx 가 대용량 파일 전송을 차단하지 않도록 설정 변경
- File : `/etc/nginx/conf.d/my-server.conf`
- Settings:
	```nginx
	server
	{
		# ... 기존 설정 ...
		
		client_max_body_size 10M;
		
		location / {
			# ...
		}
	}
	```
- Apply : `sudo systemctl restart nginx`

<br>

### 6. Configuration Externalizaiton
: `application.properties`의 설정을 개발 / 운영 환경으로 분리

#### 6.1. Profile Separation
`application.properties` 를 기준으로 파일을 분리 생성

> 1) `application.properties` (공통)
>   : 환경 무관 항상 사용하는 설정
> 2) application-local.properties (개발용)
>   : 로컬 PC 에서 개발할 때 필요한 설정
> 3) application-prod.properties (운영용)
>   : AWS EC2 서버에서 실행할 때 필요한 설정

#### 6.2. Deployment

**A. Local (IntelliJ)**
: 실행 시 `application.properties`에 명시된 대로 `spring.profiles.active=local`이 적용

**B. AWS (배포)**
: 실행 시 <u>프로필을 명시</u>
```bash
java -jar my-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

<br>


<br>

## Step 2. Data Maintenance

### 1. Database Migration
: 데이터베이스 이식 (H2 DB -> <u>MySQL</u>)

| 구분  | H2 (개발/학습용)           | MySQL (실무/운영용) ✅   |
| --- | --------------------- | ------------------ |
| 성격  | 가벼운 내장형 (Embedded) DB | 독립적 프로세스 기반 RDBMS  |
| 강점  | 설정 제로, 빠른 프로토타이핑      | 높은 안정성, 대규모 트래픽 처리 |
| 약점  | 데이터 손실 위험, 동시 접속 취약   | 별도 설치 및 운영 설정 필요   |
| 용도  | 로컬 개발, 테스트, 입문용       | 실제 서비스 운영, 협업 프로젝트 |

| 구분     | AWS RDS ✅                   | EC2 내부 직접 설치                  |
| ------ | --------------------------- | ----------------------------- |
| 핵심 성격  | **완전 관리형** 서비스 (DBaaS)      | **셀프 관리형** 가상 서버              |
| 관리 편의성 | <u>AWS가 백업, 패치, 복구 자동화</u>  | OS부터 DB 설치, 설정, 운영 전담         |
| 자유도    | 설정 제한적 (특정 버전/옵션만)          | **최대 자유도** (모든 설정 커스텀 가능)     |
| 고가용성   | <u>클릭 몇 번으로 다중 AZ 구성</u> 가능 | 직접 복제 및 클러스터 구축               |
| 비용     | 관리 비용 포함으로 다소 높음            | <u>인스턴스 비용</u>만 발생하여 상대적으로 저렴 |
| 권장 대상  | **개발 효율과 안정성** 중시           | **세밀한 튜닝**이나 **비용 절감**이 우선    |

#### 1.1. Infrastructure Setup (AWS RDS)
: AWS RDS 를 사용하여 MySQL DB 인스턴스 생성

- 데이터베이스 생성하기
> - 생성 방식 : 전체 구성
> - 구성 : MySQL
> - 템플릿 : Free Tier
> - DB 인스턴스 유형 : `db.t3.micro`
> - 스토리지 유형 : `범용 SSD(gp3)`
> - 퍼블릭 엑세스 : 예(Yes)
> - VPC 보안 그룹 새로 생성 (가용 영역 : 기본)
> - 추가 구성 - DB 이름

#### 1.2. Security Configuration (Network)
: DB 접속 보안 설정  
-> **로컬 PC(개발용) / EC2 서버(운영용)** 에서만 접속 가능하도록

> 1. DB의 보안 그룹으로 이동  
> 2. 인바운드 규칙 추가  
>> 규칙 1. 로컬 PC 접속 허용  
>>> - 유형 : MYSQL/Aurora  
>>> - 포트 : `3306`(자동 입력)  
>>> - 소스 : **내 IP**  
>>
>> 규칙 2. EC2 서버 접속 허용  
>>> - 유형 : MYSQL/Aurora  
>>> - 포트 : `3306`  
>>> - 소스 : **사용자 지정** (기존 EC2의 보안 그룹 선택)  
> 3. 저장

#### 1.3. Project Configuration (Spring Boot)
: 에플리케이션이 MySQL 에 연결되도록 설정

- **Dependency** (`pom.xml`)
> `<groupId>com.mysql</groupId>`    
> `<artifactId>mysql-connector-j</artifactId>`  
> `<scope>runtime</scope>`  

- **Configuration** (`application-prod.properties`)
> `spring.datasource.url=`  
> `spring.datasource.username=`  
> `spring.datasource.password=`  
> `spring.datasource.driver-class-name=`  
> `spring.jpa.database-platform=`  
> `spring.h2.console.enabled=`  

- Test In Local

#### 1.4. Deployment & Verification
: 변경된 애플리케이션을 서버에 배포하고 DB 연동 확인
- **Build** (mvn)
- **Transfer** (scp)
- **Run** (ssh / java)
> **데이터 독립성 확보**  
> **환경 분리 성공**

<br>

### 2. Data Backup & Restore
: 데이터 유실 방지를 위한 자동화된 백업 시스템 구축

---

# Technical Design Document

### 0. Document Info
- **Project** : my-server
- **Version** : 1.0.0-SNAPSHOT
- **Status** : Phase 2 - Step 2.1 Completed (Database Migration Done)

<br>

## 1. Design Rationale & Trade-offs
: 설계 결정 근거 및 상충 관계

#### 1.1. Database Strategy : Hybrid & Migration (H2 -> MySQL)
- **Rationale**
	- **Development (H2)** : 초기 DB 설치/설정 비용을 최소화하고 도메인 모델링에 집중
	- **Production (MySQL)** : 운영 환경 진입 시 AWS RDS로 이관하여 데이터 안정성 및 확장성 확보
- **Trade-offs**
	- **Pros** : 로컬 개발의 편의성(H2)과 운영 환경의 신뢰성(MySQL) 을 동시 확보
	- **Cons** : DB 이관 공수 및 환경별 설정 분리에 따른 관리 포인트 증가

#### 1.2. Server-Side Rendering : Thymeleaf
- **Rationale**
	- Front/Back 분리보다 백엔드 서버의 MVC 흐름 파악을 우선시
	- 서버 사이드 렌더링을 통한 초기 구현 속도 및 SEO 확보
- **Trade-offs**
	- **Pros** : 백엔드 중심의 빠른 개발 가능, 별도의 API 서버 구축 불필요
	- **Cons** : 동적인 UX 구현에 제약, SPA 전환 시 구조 변경 필요

#### 1.3. Layered Architecture
- **Rationale**
	- SRP(단일 책임 원칙)을 적용하여 Controller-Service-Repository 역할 분리
	- 인터페이스 도입을 통한 OCP(개방-폐쇄 원칙) 준수 및 테스트 용이성 확보
- **Trade-offs**
	- **Pros** : 높은 모듈화로 인한 유지보수성 향상, 영향 범위 최소화
	- **Cons** : 소규모 프로젝트 초기에는 코드량(Boilerplate Code) 증가

<br>

## 2. Architectural Strategy & Characteristics
: 아키텍처 전략 및 특성

#### 2.1. Evolutionary Architecture & Debt Management
: 점진적 설계 지향

- **Intentional Debt & Repayment** : MVP(Phase 1) 에서는 구조적 단순함을 선택, 확장 시점 (Phase 2)에 기술 부채를 상환(Refactoring) 하는 전략적 접근
- **Test Safety Net** : 테스트 코드 도입으로 변경에 따른 회귀(Regression)를 방지 및 지속적 개선 기반 마련

#### 2.2. Cross-Platform & Deployment Readiness
: 크로스 플랫폼 및 배포 준비성

- **OS-Independent Resource Handling** : `toURI()` 등을 활용하여 OS 간 경로 표기법 차이로 인한 호환성 문제 원천 해결
- **Shift-Left Security** : 초기 배포 단계부터 HTTPS 및 리버스 프록시를 적용하여 보안 격차 조기 해소

#### 2.3. Environment Isolation & Configuration Management
: 환경 격리 및 설정 관리

- **Profile-Based Configuration** : `local` / `prod` 프로필 설정 분리를 통해 환경별 설정 자동 적용
- **Externalized Secrets** : `.env` 파일을 통한 민감 정보(DB credentials) 관리로 코드 보안성 강화

#### 2.4. Data Infrastructure Scalability
: 데이터 인프라 확장성

- **Managed Database Migration** : Embedded DB(H2) 에서 Cloud Managed DB(AWS RDS/MySQL) 으로 이식하여 데이터 영속성 및 운영 안정성 확보
- **Network Isolation** : Security Group 을 활용해 DB 접근을 신뢰할 수 있는 주체(Local, EC2) 로만 제한하여 보안 강화