
# MyServer Project

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=flat-square&logo=amazonaws&logoColor=white)
![H2 Database](https://img.shields.io/badge/H2-blue)

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
| **DB엔지니어링**   | 데이터베이스 관리 및 구조     | Backup & Restore, DB Migration                                 |
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

## Step 2. DB Engineering to H2 DB
~ https://www.h2database.com/html/main.html

### 1. Add DB Dependency
> H2 Database

#### 1.1. Add Following Dependencies in `pom.xml`
```xml
<!-- JPA: Java 객체와 DB 테이블을 자동으로 매핑해주는 도구 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- H2 Database: 개발 및 테스트용으로 매우 가벼운 인메모리 DB -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```
- H2 DB 를 사용하는 이유 : 설정이 간단하여 런타임 DB 개념을 익히기 좋다.

#### 1.2. Refresh `Maven` in IntelliJ to Download Libraries


<br>

### 2. DB Setting in `application.properties`
: DB가 서버 재시작 후에도 데이터를 유지하도록 '파일 기반' 으로 설정

#### 2.1. Create Persistent Storage File
```properties
# application.properties 파일에 다음 줄 추가
spring.datasource.url=jdbc:h2:file:./data/testdb
```

#### 2.2. Add Auto-Create DDL Setting
```properties
# application.properties 파일에 다음 줄 추가
spring.jpa.hibernate.ddl-auto=update
```
- create : 서버 시작마다 새로운 테이블을 생성 (DB의 영속성 위반)
- <u>update</u> : 시작 시 테이블이 없다면 생성 / 있다면 수정하는 방식

<br>

### 3. Structure a Data Model (Entity)

#### 3.1. Create New Java Class (Guestbook)
: 새로운 domain 클래스 생성 및 구성  
-> Guestbook 클래스는 데이터 모델(엔티티)를 구성하는 클래스가 된다.

<br>

### 4. Design Data Access Layer (Repository)

#### 4.1. Create New Java Interface (GuestbookRepository)
```java
package com.example.myfirstserver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestbookRepository extends JpaRepository<Guestbook, Long>
{
	// JpaRepository를 상속받으면, save(), findAll(), findById() 등
	// 기본적인 CRUD 메소드를 자동으로 사용할 수 있게 됨
}
```

<br>

### 5. Connecting the Controller to the Database (Controller)

#### 5.1. Create New Java Class (GuestbookController)
: 새로운 Controller 클래스 생성 및 구성  
-> Controller 클래스는 HTTP 요청 및 처리, 비즈니스 로직이 구현되어 있다.

<br>

### 6. DB Test

#### 6.1. Run Server

#### 6.2. Create Data in DB
~ http://localhost:8080/guestbook/write?author=테스트&content=첫번째글입니다.

#### 6.3. Check the Data Persistence
~ http://localhost:8080/guestbook/list  
서버를 껐다 켠 후에도 데이터가 보존되는지 확인

<br>



<br>

## Step 3. UI/UX Engineering

### 1. Add Thymeleaf Dependency

#### 1.1. Add Following Dependencies in `pom.xml`
```xml
<!-- Thymeleaf: 서버에서 HTML을 동적으로 생성해주는 템플릿 엔진 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

#### 1.2. Refresh `Maven` in IntelliJ to Download Libraries

<br>

### 2. Modify Controller
: Controller 는 더 이상 JSON 타입을 반환하지 않음  
-> HTML 데이터를 담은 객체를 반환하도록 수정

#### 2.1. Modify Controller Class (GuestbookController)
- **핵심 변경점**
	- `@RestController`-> `@Controller`
	- 반환 타입 : `Guestbook` (JSON) -> `String` (HTML 파일 이름)
	- `Model` 객체를 사용하여 데이터를 View로 전달
	- `@PostMapping` 을 사용하여 폼 데이터 처리
	- `return "redirect:/";` : 작업 후 홈페이지로 리디렉션

<br>

### 3. Configure HTML Page (View)
: Controller 가 반환하는 HTML 파일을 만드는 단계

#### 3.1. Configure Thymeleaf Templates Path

`src/main/resources`디렉토리 내 `templates` 폴더 생성  
(Spring Boot 가 여기서 HTML 파일을 찾는다.)

#### 3.2. Configure `list.html`
: 화면에 표시할 메인 UI 를 HTML 파일을 생성해 구현

<br>

### 4. Styling with Bootstrap

#### 4.1. Add Bootstrap CSS for CDN in `<head>` Tag
: Bootstrap CSS 파일을 웹에서 가져다 쓰는 CDN 방식

#### 4.2. Modify `<body>`Tag to use Bootstrap class

<br>


<br>

## Step 4. System Management

### 1. Create Cloud Server (EC2)
: 아마존 AWS 에서 클라우드 서버 사용  
~ https://aws.amazon.com

#### 1.1 AWS Management Console Log-in
: 로그인하여 **EC2** 탐색

#### 1.2. EC2 Instance Start
: EC2 대시보드에서 **인스턴스 시작** 하기

#### 1.3. Set Name (Tag)
: '이름' 필드에 이름 설정

#### 1.4. Select AMI
> **'Amazon Linux 2023 AMI'**

#### 1.5. Select Instance Type
>**t2.micro** / **t3.micro**

#### 1.6. Create Key Pair (Password)
: **새 키 페어 생성** 을 클릭하여, 키 이름을 짓고 생성  
~ `my-server-key.pem` 파일 보관하기 (**보안 철저히**)

#### 1.7. Set Firewall (Secure Group)
: '보안 그룹 규칙' 에서 다음 세 가지의 규칙 추가

> **규칙 1** : 유형 `SSH` , 소스 `내 IP` (나의 컴퓨터에서만 SSH 접속)  
> **규칙 2** : 유형 `HTTP`, 소스 `위치 무관` (아무나 HTTP로 접속 가능)  
> **규칙 3** : 유형 `사용자정의 TCP`, 포트 범위 `8080`, 소스 `위치 무관`

#### 1.8. Start Instance


<br>

### 2. Server Access (SSH)
: 만든 AWS 클라우드 서버에 원격 접속

#### 2.1. Check Public IP
: EC2 인스턴스의 정보 중 **'Public IPv4 주소'** 복사

#### 2.2. Set Authorization of Key File
```bash
# 키 파일 권한 변경 (나만 읽을 수 있도록)
chmod 400 my-server-key.pem
```

#### 2.3. Access SSH
```bash
ssh -i my-server-key.pem ec2-user@[MY_PUBLIC_IP]
```

<br>

### 3. Build Server Environment
: 서버에 프로젝트를 실행할 도구 설치

#### 3.1. Server Update
```bash
sudo yum update -y
```

#### 3.2. Install Java 21
```bash
sudo yum install java-21-amazon-corretto -y
```

#### 3.3. Install Maven
```bash
sudo yum install maven -y
```

#### 3.4. Check Installations
```bash
java -version
mvn -version
```

<br>

### 4. Project Deployment
: 프로젝트를 서버로 옮겨서 실행하기

#### 4.1. Modify Application Properties
: `application.properties` 파일에 추가

```properties
server.address=0.0.0.0
```

#### 4.2. Build Project on Local
```bash
# IntelliJ 내 로컬 터미널에서 실행
# 프로젝트를 실행 가능한 .jar 파일로 만들기
mvn clean package
```
-> 프로젝트 폴더에 `target` 이라는 폴더가 생성  
하위에 `my-server ~ SNAPSHOT.jar` 스냅샷 파일 생성 확인

#### 4.3. Transfer Files to Server
```bash
# 새로운 로컬 터미널 실행
# jar 파일을 서버의 홈 디렉토리로 복사
scp -i my-web-key.pem /path/to/your/project/target/my-...-SNAPSHOT.jar ec2-user@[MY_PUBLIC_IP]:~/
```

#### 4.4. Run Application in Server
```bash
# .jar 파일이 잘 복사되었는지 확인
ls

# 서버 실행
java -jar my-server-0.0.1-SNAPSHOT.jar

# 백그라운드에서 실행
nohup java -jar my-server-0.0.1-SNAPSHOT.jar &
```

<br>

### 5. Maintain Operation Stability

#### 5.1. Assign Elastic IP
: EC2 서버에 연결할 때마다 퍼블릭 IPv4 주소가 변경됨  
-> AWS 에서 **'Elasic IP'** 를 할당받아 인스턴스에 연결

#### 5.2. Set Domain
: 순수 IP 주소가 아닌 도메인 이름을 구하여 연결  
-> 무료 도메인을 구하여 **DNS 설정**을 통해 도메인을 서버 퍼블릭 IPv4로 연결

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

### 1. Implement Full CRUD
: 현재 서비스는 Create 와 Read만 가능  
-> **Update** 과 **Delete** 기능 추가

#### 1.1. Add Update/Delete Button on `list.html`
: 화면 상에 표시될 수정 및 삭제 버튼을 `list.html` 에서 구현

#### 1.2. Add Update/Delete Logic on `GuestbookController.java`
: 컨트롤러 상에서 실제 로직 구현

#### 1.3. Create Update Form HTML file (`edit.html`) 
: 별도의 수정 폼(HTML) 을 생성

#### 1.4. Update Serial Number
: 생성 / 삭제 후에도 순차 번호가 갱신되도록 기능 고도화  
-> 반복 상태 변수 추가

<br>

### 2. Enhancement of Functions

#### 2.1. Recently Updated Time
: 수정 시 작성 시간 대신 최근 수정 시간으로 갱신되도록 변경  
-> `@PreUpdate` 어노테이션

#### 2.2. Time Format & Adjust Table Layout

#### 2.3. Post Sorting
: ID를 기준으로 Repository 에 정렬 기능 추가  

> Spring Data JPA

#### 2.4. Set Time Zone
: AWS EC2 사용 시 기본적으로 UTC 시간대 적용  
-> LocalDateTime > ZonedDateTime 전환

<br>

### 3. Refactoring the Domain Model
: 방명록을 **'포스트(Post)'** 형태의 애플리케이션으로 발전시키기  
(`Guestbook`-> `Post`)

#### 3.1. Separation of Concerns (SoC)
- **주요 변경점**
	- **Before**
		- `Controller` 가 `Repository` 를 직접 호출하여 데이터를 조작
		- `Controller` 가 HTTP 처리와 데이터 로직까지 모두 책임
	- **After**
		- `Service` 계층 도입
		- `Controller` -> `Service` -> `Repository` 순 호출
		- `Controller` : 오직 HTTP 요청을 받고 응답을 보내는 역할에 집중
		- `Service` : 실제 데이터 처리하는 핵심 비즈니스 로직

#### 3.2. Decoupling Packages
- **주요 변경점**
	- **Before**
		- 모든 클래스가 하나의 패키지 내에 존재
	- **After**
		- 역할별 **패키지 분리**
		- `controller` : 웹 요청/응답 담당
		- `service` : 비즈니스 로직 담당
		- `repository` : DB 접근 담당
		- `domain` : 데이터 모델 (엔티티) 담당
		- `exception` : 커스텀 예외 클래스 담당

#### 3.3. Solidifying Business Logic
- **주요 변경점**
	- **Before**
		- CRUD 에 대한 별도의 로직이 없거나 Controller 에 산개
		- 데이터 변경 중 오류 발생 시 일부만 변경될 위험
	- **After**
		- `PostService` 인터페이스와 `PostServiceImpl` 구현체 분리
		- `PostService` 내부에 `save`, `updatePost`, `delete` 와 같은 메소드로 로직 분리
		- `@Transactional` 어노테이션을 도입하여 태스크의 **원자성 확보**


**3.4. Creating Custom Exception Classes**
- 주요 변경점
	- Before
		- 존재하지 않는 데이터 요청 시 Spring이 제공하는 오류가 발생
		- 오류의 원인 찾기 힘듦
	- After
		- `PostNotFoundException` 커스텀 **예외 클래스** 생성
		- `PostService`에서 데이터를 찾지 못하면 이 예외를 명확히 Throw

<br>

### 4. Automated Testing
: 코드 품질을 보증하기 위한 코드 테스트를 자동화

#### 4.1. Test Environment Setup
: `pom.xml` 내 Spring Boot 테스트를 위한 의존성 포함 여부 확인
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```
- JUnit5, Mockito, AssertJ 등이 포함

#### 4.2. Service Layer Unit Testing
: **Mockito** 를 사용하여 `Repository` 를 Mock 으로 만들기  
-> 실제로 DB에 접근하지 않고 로직을 빠르게 검증 가능

>- 작성 순서
>1) 테스트 클래스 생성
>2) `@Mock` 으로 `PostRepository` 모킹
>3) `@InjectMocks` 로 `PostServiceImpl` 주입
>4) 테스트 케이스 작성 (성공/실패)

<br>

### 5. Input Validation / Exception Handling
: 입력값 검증 / 예외 처리 및 에러 페이지 표시

#### 5.1. Add Dependencies
: 입력값 검증을 위한 라이브러리를 추가

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

#### 5.2. Apply Validation Rules in Domain
: 현재 프로젝트는 Entity를 그대로 사용 중  
-> `Post` Entity에 검증 어노테이션 추가

```java
// 기존 @Column 속성에 validation 어노테이션 추가
    @NotBlank(message = "제목은 비워둘 수 없습니다.")
    @Size(max = 100, message = "제목은 100자를 넘을 수 없습니다.")
    @Column(nullable = false, length = 100)
    private String title;
```

#### 5.3. Apply Validation Logic in Controller

> 1) 데이터를 주고받기 전에 자동으로 검증 수행
> - 글 작성 처리  
> `@Valid Post post`: Post 객체를 검증하겠다는 의미  
> `BindingResult bindingResult`: 검증 결과(에러 정보)를 담는 객체

> 2) HTML View 에서 에러 메세지 출력  
> `PostController.java`   
> 글쓰기 폼(`write-form`)에 처음 진입할 때 `th:object`를 사용하기 위해    
> 비어있는 `Post` 객체를 Model에 담아줘야 한다.

> 3) HTML View 수정  
> `write-form.html` 의 `<form>`태그 구조를 Thymeleaf 에 맞게 변경   
> `edit.html`또한 동일

#### 5.4. Create Global Exception Handler
: 전역 예외 처리기 생성  
 - `my_server/exception/` 패키지에 `GlobalExceptionHandler.java` 구성
 
```java
@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. PostNotFoundException (게시글을 찾을 수 없음) 처리
    @ExceptionHandler(PostNotFoundException.class)
    public String handlePostNotFoundException(PostNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404"; // templates/error/404.html 로 이동
    }

    // 2. 그 외 모든 Exception (서버 에러 등) 처리
    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        model.addAttribute("errorMessage", "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.");
        return "error/500"; // templates/error/500.html 로 이동
    }
}
```

#### 5.5. Create Custom Error Page

> 1) 디렉토리 생성 : `src/main/resources/templates/error/404.html
> 2) `404.html` 파일 생성
> 3) `500.html` 파일 생성

<br>

### 6. Post Search Functionality
: 특정 포스팅을 제목이나 내용을 기준으로 검색하는 기능 추가

#### 6.1. Repository Layer : Define Searching Query Method
: Spring Data JPA 는 메소드명 만으로 자동으로 쿼리를 만들어준다.  
`PostRepository.java` 에 검색용 메소드 추가

```java
public interface PostRepository extends JpaRepository<Post, Long> {
    // 검색 기능: 제목(title) 또는 내용(content)에 키워드가 포함된 게시글 조회
    // Containing: LIKE '%keyword%' 와 같은 역할
    // Or: 두 조건 중 하나라도 만족하면 조회
    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
}
```

#### 6.2. Service Layer : Process Searching Logic
: 검색어(Keyword) 의 유무에 따라 다른 로직 수행

```java
public interface PostService // 기존 list 메소드 수정 ... 나머지 메소드 동일
{ Page<Post> list(String keyword, Pageable pageable); }

@Override
    public Page<Post> list(String keyword, Pageable pageable)
    {
        // 1. 검색어가 없거나 공백일 경우 -> 전체 조회
        if (keyword == null || keyword.trim().isEmpty())
        { return postRepository.findAll(pageable); }
        
        // 2. 검색어가 있을 경우 -> 제목 또는 내용에서 검색
        // (파라미터로 keyword를 두 번 넘겨서 제목에서도 찾고 내용에서도 찾도록 함)
        return postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
    }
```

#### 6.3. Controller Layer : Receive `keyword` Parameter
: 클라이언트에서 보낸 검색어(`keyword`) 를 받아서 Service 로 넘겨줘야 함.

```java
@GetMapping("/main/list")
    public String list(Model model,
                       @RequestParam(value = "keyword", required = false) String keyword, // 검색어 파라미터 추가
                       @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        // 검색어를 포함하여 Service 호출
        Page<Post> postsPage = postServiceImpl.list(keyword, pageable);

        model.addAttribute("posts", postsPage);
        model.addAttribute("keyword", keyword); // 뷰에서 검색어 유지를 위해 전달

        return "list";
    }
```

#### 6.4. View Layer : Paging & Linking
: 사용자가 사용할 수 있는 검색어 입력창 만들고, 페이지를 넘겨도 검색어가 유지되도록 처리  
`list.html` 수정

---

# Technical Design Document

### 0. Document Info
-  **Project** : my-server
- **Version** : 1.0.0-SNAPSHOT
- **Status** : Phase 2 - Step 1.6 Completed (Search Functionality Done)

<br>

## 1. Design Rationale & Trade-offs
: 설계 결정 근거 및 상충 관계

#### 1.1. Database Strategy : H2 File-based DB
- **Rationale**
	- 학습 목적에 맞춰, 별도의 DB 서버 설치 및 설정에 드는 비용 최소화
	- <u>JPA 및 도메인 모델링 학습에 집중</u>
	- 파일 기반 (`jdbc:h2:file`) 으로 설정하여 서버 재시동 간 데이터 유지 확보
- **Trade-offs**
	- **장점**
		- Zero-configuration
		- 빠른 개발 속도
		- 가벼운 리소스
	- **단점**
		- 대용량 트래픽 처리 및 동시성 제어에 취약
		- 운영 환경(Production) 으로 확장 시 MySQL/PostgreSQL 등으로 이식 비용 발생

#### 1.2. Server-Side Rendering : Thymeleaf
- **Rationale**
	- Front/Back 역할 분리 보다, <u>Back-end 서버의 요청-응답 흐름 (MVC) 의 명확한 파악</u>을 우선시
	- 서버 사이드 렌더링을 통해 SEO(검색 엔진 최적화) 및 초기 로딩 속도 확보 용이
- **Trade-offs**
	- **장점**
		- 백엔드 중심의 개발 용이성
		- 별도의 API 서버/클라이언트 이원화 불필요
	- **단점**
		- UX 측면에서 페이지 전체가 새로고침되어 동적인 UI 구현에 제약
		- 향후 SPA(React/Vue) 도입 시 구조 변경 필요

#### 1.3. Layered Architecture
- **Rationale**
	- 단일 책임 원칙(SRP) 적용하여 Controller-Service-Repository 분리
	- Service Layer 의 인터페이스 구현을 통해 개방-폐쇠 원칙(OCP) 준수 및 테스트 용이성 확보
- **Trade-offs**
	- **장점**
		- 코드의 가독성, 유지보수성 증가
		- 향후 기능 변경 시 영향 범위 최소화 (모듈화)
	- 단점
		- 적은 규모 대비 초기 작성 코드량(Boilerplate Code) 증가
<br>

## 2. Architectural Strategy & Characteristics
: 아키텍처 전략 및 특성

#### 2.1. Evolutionary Design
: 점진적 설계 지향

- **MVP First** : Phase 1 에서는 구조적 단순함을 유지하여 핵심 기능 작동에 집중하여 개발 초기 복잡도를 낮추고 빠른 배포 달성
- **Just-in-Time Refactoring** : Phase 2 로 진입하며 계층 분리 등 필요한 시점에 리팩토링 수행하여 오버엔지니어링 방지 및 확장성 확보

#### 2.2. Technical Debt Management
: 기술 부채 관리

- **Intentional Debt & Repayment** : Phase 1 에서의 빠른 구현을 위해 감수한 계층 구조의 단순화(부채)를 Phase 2 에서 구조적 개선(상환)으로 해소하여 유지보수 비용을 선제적으로 절감
- **Separation of Concerns** : 객체지향 설계 원칙을 적용하여 관심사를 명확한 분리함. 향후 기술 부채가 누적되는 것을 방지하는 방어적 설계

#### 2.3. Shift-Left Security
: 보안의 조기 적용

- **Proactive Hardening** : 초기 배포 이전에 HTTPS(SSL) 적용 및 Nginx 리버스 프록시 구성을 완료하여 보안 격차 및 이슈를 사전에 차단

#### 2.4. Test-Driven Quality Assurance
: 테스트 주도 품질 보증

- **Automated Unit Testing** : JUnit 5와 Mockito 를 도입하여 Service 계층 단위 테스트 구현
- **Living Documentation** : 테스트 코드를 단순한 검증 도구가 아닌, 요구사항의 명세서로 관리

#### 2.5. Robustness & Usability
: 견고성 및 사용성 확보

- **Centralized Exception Handling**: `@ControllerAdvice`를 도입하여 흩어져 있던 예외 처리 로직을 전역(Global)으로 통합 관리.
- **Declarative Validation**: Bean Validation(`@Valid`)을 통해 검증 로직을 비즈니스 로직에서 분리하여 선언적으로 처리.

#### 2.6. Searchability & State Management
: 검색 기능 및 상태 관리

- **Dynamic Querying**: Spring Data JPA의 Method Naming Convention을 활용하여 별도의 쿼리 작성 없이 동적 검색 기능 구현.
- **State Persistence**: 페이징 처리 시 검색 키워드(`keyword`)를 URL 파라미터로 전달하여, 페이지 이동 간에도 검색 컨텍스트가 끊기지 않도록 설계.