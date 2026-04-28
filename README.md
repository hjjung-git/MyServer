
# MyServer Project

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=java&logoColor=white)![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white)![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=flat-square&logo=thymeleaf&logoColor=white)![Bootstrap](https://img.shields.io/badge/Bootstrap-7952B3?style=flat-square&logo=bootstrap&logoColor=white)

![AWS](https://img.shields.io/badge/AWS-232F3E?style=flat-square&logo=amazonaws&logoColor=white)![Nginx](https://img.shields.io/badge/Nginx-009639?style=flat-square&logo=nginx&logoColor=white)![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)![H2 Database](https://img.shields.io/badge/H2-003B57?style=flat-square&logo=h2&logoColor=white)

![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=flat-square&logo=intellijidea&logoColor=white)![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white)

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

| 직무             | 프로젝트 내 역할               | 관련 기술/키워드                      |
| :------------- | :---------------------- | :----------------------------- |
| **응용SW엔지니어링**  | 서비스의 핵심 로직 및 API 개발     | Java, Spring Boot, RESTful API |
| **DB엔지니어링**    | 데이터 모델링 및 영속성 관리        | H2, JPA, CRUD                  |
| **UI/UX엔지니어링** | 사용자 인터페이스 설계 및 구현       | HTML, CSS, Bootstrap, 반응형 웹    |
| **IT시스템관리**    | 서버 구축, 배포 및 운영          | AWS EC2, Docker, Nginx         |
| **보안엔지니어링**    | 인증/인가, 데이터 암호화 및 취약점 방어 | HTTPS, Input Validation        |

<br>

#### Phase 2 : Server Maintenance

| 직무            | 프로젝트 내 역할       | 관련 기술/키워드                                                                |
| :------------ | :-------------- | :----------------------------------------------------------------------- |
| **응용SW엔지니어링** | 기능 개발 및 코드 관리   | Layered Architecture, CRUD, Refactoring, Config Externalization          |
| **DB엔지니어링**   | 데이터베이스 관리 및 구조  | Backup & Restore, DB Migration, AWS RDS (MySQL)                          |
| **보안엔지니어링**   | 사용자 인증 및 접근 제어  | Spring Security, Authentication, Authorization, Password Encryption      |
| **IT시스템관리**   | 인프라 운영 및 배포 자동화 | Log Management, OS Security, Resource Monitoring, CI/CD (GitHub Actions) |

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
	- `AWS EC2 (Amazon Linux 2023)`
	- `JAR (Executable Archive)`
	- `Nginx (Reverse Proxy)`
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

## Step 1. Development Environment Setup

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

### 3. Implement View
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

## Step 1. Feature & Code Management
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

| 구분 | AWS RDS 자동 백업 ✅ | EC2 내부 스크립트 (Cron Job) |
| :--- | :--- | :--- |
| **실행 주체** | AWS (Cloud Managed) | 사용자 (EC2 Instance) |
| **서버 상태 의존성** | <u>EC2 꺼져있어도 수행 가능</u> | EC2 반드시 켜져 있어야 함 |
| **관리 편의성** | 콘솔 클릭 몇 번으로 설정 완료 | 쉘 스크립트 작성 및 Cron 설정 필요 |
| **복구 방식** | 특정 시점 복구 (PITR) 가능 | 저장된 Dump 파일로 수동 복구 |
| **비용** | 프리 티어 범위 내 무료 (스토리지 공유) | EBS 볼륨 추가 비용 발생 가능 |
| **적합성** | **크레딧 이슈/비상시 운영**에 최적 | 24시간 가동 서버에 적합 |

#### 2.1. Automated Backup Strategy (AWS RDS Snapshot)
: RDS 자체 기능을 활용한 데이터 보호

- **Backup Settings** (AWS Console)
    
    > 1. AWS RDS 콘솔 -> DB 인스턴스 선택 -> **[수정]** 클릭
    >     
    > 2. **백업 보존 기간** 설정
    >     
    >     > - 0일 (비활성화) -> **1일 이상**으로 변경
    >     > - (프리 티어 기준 최대 7일 권장)
    >     
    > 3. **백업 기간** 설정
    >     
    >     > - 선호하는 기간 선택 (예: 03:00~04:00, 새벽 시간)
    >     
    > 4. **[즉시 적용]** 후 수정 완료
    >     
    

#### 2.2. Recovery & Restore
: 장애 발생 시 데이터 복구 절차

- **Recovery Process**
    
    > 1. **삭제 방지**: DB 삭제 시 **[최종 스냅샷 생성]** 반드시 체크
    > 2. **복원 방법**
    >     
    >     > - RDS 콘솔 -> **[스냅샷]** 메뉴 이동
    >     > - 원하는 시점의 스냅샷 선택 -> **[복원]**
    >     > - 새로운 DB 인스턴스 생성 (기존 DB는 삭제 후 엔드포인트 변경하여 연결)

<br>


<br>

## Step 3. User Authentication and Access Control

### 1. Spring Security

#### 1.1. Dependency (`pom.xml`)
> `spring-boot-starter-security  
> `thymeleaf-extras-springsecurity6`  

-> 추가 후 서버 실행 시, 모든 페이지가 로그인 화면으로 가로채진다.

#### 1.2. Concept
: Spring Security의 두 핵심 개념
- **Authentication** : 사용자의 신원 확인
- **Authorization** : 인증된 사용자의 권한 확인

<br>

### 2. User Model & Repository

#### 2.1. Create Role Enum
: 권한 분류를 위한 Enum 타입 정의

> Enum 분류 클래스 `domain/Role.java` 생성

#### 2.2. Create User Entity
: 사용자의 정보를 저장할 엔터티 생성

> 유저 엔터티 클래스 `domain/User.java` 생성

#### 2.3. Create UserRepository
: DB에서 사용자 조회를 위한 인터페이스

> `User` 엔터티의 Repository 인터페이스 `repository/UserRepository.java`생성

<br>

### 3. Security Configuration

#### 3.1. Create Security Config
: Spring Security 의 핵심 설정 담당

> Spring Security 설정 클래스 `config/SecurityConfig.java` 생성

#### 3.2. Implement UserDetails
: Spring Security 와 엔터티 `User` 의 연결

> `User.java` 가 `UserDetail` 인터페이스(Spring Security 내부 인터페이스)를 구현하도록 수정  
> 로그인 로직 인터페이스 `UserDetailsService`의 구현체 `service/UserSecurityService.java` 구현

<br>

### 4. Authentication Service

#### 4.1. Implementation

1) **Service**
> 회원가입 로직 클래스 `service/UserService.java` 구현

2) **Controller**
> 로그인 및 회원가입 페이지와 처리를 담당하는 클래스 `controller/UserController.java` 구현

3) **View**
> 로그인 폼 `templates/user/login.html` 구현  
> 회원가입 폼 `templates/user/join.html` 구현

<br>

### 5. UI Integration & Authorization
: 로그인/아웃 버튼, 게시글 작성자에게만 수정/삭제 권한 부여

#### 5.1. Refactoring

1) **Domain**
> `Post`엔터티에 `User` 정보를 반영할 수 있도록 `domain/Post.java`수정

2) **Service**
> 작성자 인스턴스를 저장할 수 있도록 구현체 `service/PostServiceImpl.java` 수정

3) **View**
> 로그인 상태에 따라 버튼이 바뀌도록 `list.html` 수정  
> 작성자와 로그인 사용자가 같을 때만 버튼이 보이도록 `detail.html`수정


<br>


<br>

## Step 4. Infrastructure Maintenance

### 

---

# Technical Design Document

### 0. Document Info
- **Project** : my-server
- **Version** : 1.0.0-SNAPSHOT
- **Status** : Phase 2 - Step 3 Completed (User Authentication & Access Control Done)

<br>

## 1. Design Rationale & Trade-offs
: 프로젝트의 주요 설계 결정과 그에 따른 장단점
### 1.1. Full-Stack Learning Architecture

**Rationale**
- NCS ICT 직무(응용SW, DB, 보안, UI/UX, IT시스템관리)를 하나의 서비스 안에서 통합적으로 경험하기 위한 구조
- 단순 기능 개발이 아니라 **개발 → 배포 → 운영 → 유지보수** 전 과정을 직접 구축하는 것을 목표로 설계
    
**Trade-offs**

|Pros|Cons|
|---|---|
|백엔드, DB, 인프라, 보안을 하나의 프로젝트에서 통합적으로 경험 가능|단일 프로젝트 안에서 다뤄야 할 범위가 넓어 설계 복잡도 증가|

<br>

### 1.2. Layered Architecture

**Rationale**
- Controller / Service / Repository 계층 분리를 통해 **관심사 분리(SoC)** 적용
- Service 인터페이스와 구현체 분리를 통해 테스트 및 확장 용이성 확보

**Trade-offs**

| Pros            | Cons                 |
| --------------- | -------------------- |
| 유지보수성과 모듈성이 높아짐 | 초기 프로젝트 규모 대비 코드량 증가 |

<br>

### 1.3. Server-Side Rendering (Thymeleaf)

**Rationale**
- Frontend/Backend 분리보다 **Spring MVC 흐름 이해**를 우선
- 게시판 기반 서비스에서 빠른 구현과 안정적인 서버 렌더링 구조 확보

**Trade-offs**

|Pros|Cons|
|---|---|
|구현 속도가 빠르고 서버 중심 아키텍처 이해에 유리|SPA 수준의 동적 UX 구현에는 제약|

<br>

### 1.4. Hybrid Database Strategy (H2 → MySQL)

**Rationale**
- **Development** : H2 DB를 사용하여 빠른 개발 및 테스트 환경 구성
- **Production** : AWS RDS MySQL을 사용하여 데이터 안정성과 확장성 확보

**Trade-offs**

|Pros|Cons|
|---|---|
|개발 편의성과 운영 안정성을 동시에 확보|DB 환경 차이로 인한 설정 관리 필요|

<br>

### 1.5. Session-Based Authentication

**Rationale**
- Spring Security 기반 **세션 인증 방식** 적용
- 사용자 인증 및 게시글 권한 제어를 서버 중심으로 관리

**Trade-offs**

| Pros                       | Cons                              |
| -------------------------- | --------------------------------- |
| 전통적인 웹 애플리케이션 구조와 자연스럽게 통합 | API 기반 서비스 확장 시 토큰 인증 방식 추가 설계 필요 |

<br>

---

# 2. Architectural Strategy & Characteristics
: 프로젝트의 구조적 전략 및 특성

### 2.1. Evolutionary Architecture

- Phase 1 : 빠른 구현을 통한 기본 서버 구축
- Phase 2 : 구조 개선 및 운영 안정성 확보

프로젝트는 **점진적 설계(Evolutionary Architecture)** 전략을 따른다.

초기에는 기능 구현에 집중하고, 이후 단계에서

- Layered Architecture 적용
- 예외 처리 구조 정비
- 테스트 코드 도입
- 보안 및 인증 시스템 적용

을 통해 구조를 개선하였다.

<br>

### 2.2. Environment Isolation

개발 환경과 운영 환경을 명확히 분리하였다.

|Environment|Database|Profile|
|---|---|---|
|Local|H2|`local`|
|Production|AWS RDS MySQL|`prod`|

- `application-local.properties`
- `application-prod.properties`

를 통해 **환경별 설정을 분리**하였다.

또한 DB 계정 정보와 같은 민감 정보는 **외부 설정으로 관리**하도록 설계하였다.

<br>

### 2.3. Deployment Readiness

프로젝트는 **클라우드 환경에서 직접 운영 가능한 구조**를 목표로 한다.

Deployment Architecture
```
User  
  ↓  
Domain (HTTPS)  
  ↓  
Nginx (Reverse Proxy)  
  ↓  
Spring Boot Application (JAR)  
  ↓  
AWS RDS (MySQL)
```

주요 특징
- EC2 기반 서버 운영
- Nginx Reverse Proxy 적용
- HTTPS 인증서 적용 (Certbot)
- Executable JAR 배포 구조

Docker 없이도 **배포 흐름을 이해할 수 있도록 단순한 구조를 유지**하였다.

<br>

### 2.4. Maintainability First

프로젝트 설계의 핵심 기준은 **유지보수성**이다.

이를 위해 다음 구조를 적용하였다.

- Layered Architecture
- Global Exception Handler
- Validation 기반 입력 검증
- Service 계층 중심 비즈니스 로직 관리
- 테스트 코드 기반 품질 보장

새로운 기능이 추가되어도 **기존 구조를 크게 변경하지 않도록 설계**하였다.

<br>

### 2.5. Security by Design

초기 배포 단계부터 보안을 고려한 설계를 적용하였다.

주요 보안 요소
- Spring Security 기반 인증 시스템
- BCrypt 기반 비밀번호 암호화
- 작성자 기반 게시글 권한 제어
- AWS Security Group 기반 네트워크 제한
- HTTPS 통신 적용

이를 통해 **기본적인 웹 서비스 보안 구조를 확보하였다.**

<br>

---

# 3. Current Design Decisions
: 현재 프로젝트에서 확정된 핵심 설계 사항
### 3.1. User Identity Modeling

사용자 식별을 위해 두 가지 필드를 분리하였다.

|Field|Purpose|
|---|---|
|`loginId`|로그인 인증용 ID|
|`username`|화면 표시용 사용자 이름|

이를 통해 **인증 식별자와 UI 표시 데이터를 분리**하였다.

<br>

### 3.2. Post Ownership

게시글과 사용자 관계를 다음과 같이 설계하였다.

```
User 1 ---- N Post
```

권한 제어 기준
- 게시글 작성자만 수정 가능
- 게시글 작성자만 삭제 가능

권한 검증은
- View 조건
- Service 로직

두 계층에서 모두 수행한다.

<br>

### 3.3. File Upload Strategy

파일 업로드는 **서버 로컬 스토리지 기반**으로 구현하였다.

주요 특징
- UUID 기반 파일명 생성
- OS 독립 경로 처리
- Resource Handler를 통한 웹 접근 매핑

향후 확장 시
- AWS S3 기반 저장소로 이전 가능하도록 설계하였다.

<br>

### 3.4. Local H2 Strategy

로컬 개발 환경에서는 H2 Database를 사용한다.

현재 설정
```
jdbc:h2:mem:testdb
```

인메모리 모드를 사용하여
- 개발 안정성 확보
- 스키마 변경 테스트 용이성 확보

향후 필요 시 **파일 기반 DB로 재구성 가능**하도록 고려하였다.

<br>

---

# 4. Next Architectural Focus
: 다음 단계에서 강화할 운영 설계 영역
### 4.1. Log Management

현재 애플리케이션 로그는 `nohup.out` 중심으로 관리되고 있다.

다음 단계에서는
- Logback 기반 로그 관리
- 로그 레벨 분리 (INFO / WARN / ERROR)
- 로그 롤링 정책 적용

을 통해 **운영 장애 분석 능력을 강화할 예정이다.**

<br>

### 4.2. CI/CD Automation

현재 배포 방식
```
Build → SCP → SSH → Run
```

다음 단계 목표
- GitHub Actions 기반 자동 빌드
- 자동 배포 파이프라인 구축
- 배포 반복성 및 안정성 확보

<br>

### 4.3. Infrastructure Hygiene

운영 서버 유지보수를 위해 다음 항목을 관리할 예정이다.
- OS 패키지 업데이트
- Nginx / Java Runtime 점검
- 접근 권한 및 Security Group 점검
- 서버 리소스 모니터링

이를 통해 **운영 환경의 안정성과 보안을 지속적으로 유지한다.**