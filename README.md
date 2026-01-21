
# MyServer Project

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=flat-square&logo=amazonaws&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-07405E?style=flat-square&logo=sqlite&logoColor=white)

> NCS ICT 직무(응용SW, DB, 보안, UI/UX, IT시스템관리) 를 연계하여,
> 처음부터 끝까지 직접 구축해보는 나만의 웹 서버 프로젝트

---

## Project Overview

이 프로젝트는 단순한 웹 사이트 개발을 넘어, 하나의 완성된 IT 서비스가 탄생하는 과정 전체를 경험하는 것을 목표로 한다.
컴퓨터공학과 전공자로서 배운 이론을 실제 서비스에 적용하고, NCS에서 제시하는 5가지 핵심 직무의 역할을 이해하며 통합적인 시각을 기르는 데 중점을 두었다.

### Main Features
- 서버 구축 및 관리
- 포트폴리오 저장

---

## Project Architecture

각 개발 단계를 NCS ICT 직무와 명확하게 연계하였다.

| NCS 직무         | 프로젝트 내 역할               | 관련 기술/키워드                                 |
| :------------- | :---------------------- | :---------------------------------------- |
| **응용SW엔지니어링**  | 서비스의 핵심 로직 및 API 개발     | Java, Spring Boot, RESTful API            |
| **DB엔지니어링**    | 데이터 모델링 및 영속성 관리        | SQLite, JPA, CRUD                         |
| **UI/UX엔지니어링** | 사용자 인터페이스 설계 및 구현       | HTML, CSS, Bootstrap, 반응형 웹               |
| **IT시스템관리**    | 서버 구축, 배포 및 운영          | AWS EC2, Docker, Nginx                    |
| **보안엔지니어링**    | 인증/인가, 데이터 암호화 및 취약점 방어 | HTTPS, Password Hashing, Input Validation |

---

## Tech Stack

- **Backend**
	- `Java 21`
	- `Spring Boot 4.0.0`
	- `Spring Data JPA`
- **Frontend**
	- `HTML5`, `CSS3`
	- `Bootstrap 5`
- **Database**
	- `SQLite`
- **Deployment**
	- `AWS EC2`
	- `Docker`
	- `Nginx (Web Server)`
- **Tools**
	- `IntelliJ IDEA (Community Edition)`
	- `GitHub`

---

# Current Progress

## Step 1. Server Testing to Java

### 1. Arranging Development Environment

**1.1. JDK Installation**
: Java OpenJDK 21.0.2 (LTS version)

**1.2. IDE Installation**
: IntelliJ IDEA (Community Edition)

**1.3. Build Tools Explained**
: Maven 이나 Gradle 은 프로젝트에 필요한 라이브버리를 자동으로 다운로드하고 관리해주는 도구이다.
Spring Boot 프로젝트를 생성하면 자동으로 포함된다.

<br>

### 2. First Initializing Spring Boot

**2.1. Generate Spring Boot project to Spring Initializer**
~ https://start.spring.io/
> Project : Maven Project  
> Language : Java  
> Spring Boot version : 4.0.0  
> Packaging : Jar  
> Configuration : Properties  
> Dependencies : Spring Web  

**2.2. Server run & test**
~ http://localhost:8080/hello

<br>



<br>

## Step 2. DB Engineering to H2 DB

### 1. Add DB Dependency

**1.1. Add Following Dependencies in** `pom.xml`
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

**1.2. IntelliJ 의** `Maven` **새로고침하여 라이브러리 다운로드**

<br>

### 2. DB Setting in `application.properties`
: DB가 서버 재시작 후에도 데이터를 유지하도록 '파일 기반' 으로 설정

**2.1. Create Temporary File**
```properties
spring.datasource.url=jdbc:h2:file:./data/testdb
```

**2.2. Add Auto-Create DDL Setting**
```properties
spring.jpa.hibernate.ddl-auto=update
```
- create : 서버 시작마다 새로운 테이블을 생성 (DB의 영속성 위반)
- <u>update</u> : 시작 시 테이블이 없다면 생성 / 있다면 수정하는 방식

<br>

### 3. Structure a Data Model (Entity)

**3.1. Create New Java Class (Guestbook)**
```java
package com.example.myfirstserver;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// 이 클래스가 DB의 'guestbook' 테이블과 매핑된다고 알려줌
@Entity
public class Guestbook {

    // 테이블의 기본키(Primary Key) 알림
    @Id
    // 값이 자동으로 1씩 증가
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 글 번호

    private String content; // 글 내용
    private String author; // 작성자

    // DB에 저장할 때 현재 시간을 자동으로 넣어줌
    @PrePersist
    private void createdAt() {
        this.createdAt = LocalDateTime.now();
    }
    private LocalDateTime createdAt; // 작성 시간

    // 생성자, Getter, Setter (우측 클릭 -> Generate -> Getter and Setter)
    public Guestbook() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
```

<br>

### 4. Design Data Access Layer (Repository)

**4.1. Create New Java Interface (GuestbookRepository)**
```java
package com.example.myfirstserver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// 이 인터페이스가 DB에 접근하는 Repository 역할을 한다고 알려줌
@Repository
// JpaRepository를 상속받으면, save(), findAll(), findById() 등
// 기본적인 CRUD 메소드를 자동으로 사용할 수 있게 됨
public interface GuestbookRepository extends JpaRepository<Guestbook, Long> {
}
```

<br>

### 5. Connecting the Controller to the Database (Controller)

**5.1. Create New Java Class (GuestbookController)**
```java
package com.example.myfirstserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GuestbookController {

    // 1. 필드를 final로 만들어 한번 설정되면 변경되지 않음을 보장
    private final GuestbookRepository guestbookRepository;

    // 2. 생성자를 만들고, @Autowired를 여기로 옮김
    // Spring이 이 클래스를 만들 때, 생성자를 통해 GuestbookRepository 객체를 주입해줌
    @Autowired
    public GuestbookController(GuestbookRepository guestbookRepository) {
        this.guestbookRepository = guestbookRepository;
    }

    // 이 아래 메소드들은 그대로 둡니다.
    @GetMapping("/guestbook/write")
    public Guestbook write(@RequestParam String author, @RequestParam String content) {
        Guestbook guestbook = new Guestbook();
        guestbook.setAuthor(author);
        guestbook.setContent(content);
        return guestbookRepository.save(guestbook);
    }

    @GetMapping("/guestbook/list")
    public List<Guestbook> list() {
        return guestbookRepository.findAll();
    }
}
```

<br>

### 6. DB Test

**6.1. Run Server**

**6.2. Create Data in DB**
: `http://localhost:8080/guestbook/write?author=테스트&content=첫번째 글입니다.`

**6.3. Check the Data Persistence**
: `http://localhost:8080/guestbook/list`
서버를 껐다 켠 후에도 데이터가 보존되는지 확인!

<br>



<br>

## Step 3. UI/UX Engineering

### 1. Add Thymeleaf Dependency

**1.1. Add Following Dependencies in** `pom.xml`
```xml
<!-- Thymeleaf: 서버에서 HTML을 동적으로 생성해주는 템플릿 엔진 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

**1.2. IntelliJ 의** `Maven` **새로고침하여 라이브러리 다운로드**

<br>

### 2. Modify Controller
: Controller 는 더 이상 JSON 타입을 반환하지 않음
-> HTML 데이터를 담은 객체를 반환하도록 수정하기

**2.1. Modify Controller Class (GuestbookController)**
```java
package com.example.my_server;  
  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.web.bind.annotation.GetMapping;  
import org.springframework.web.bind.annotation.RequestParam;  
import org.springframework.stereotype.Controller;  
import org.springframework.ui.Model;  
import org.springframework.web.bind.annotation.PostMapping;  
  
import java.util.List;  
  
// @RestController -> @Controller  
// 이제 JSON 데이터가 아닌 HTML 파일을 반환  
@Controller  
public class GuestbookController  
{  
    private final GuestbookRepository guestbookRepository;  
  
    // 생성자를 통한 객체 주입은 유지  
    @Autowired  
    public GuestbookController(GuestbookRepository guestbookRepository)  
    {  
        this.guestbookRepository = guestbookRepository;  
    }  
  
    // 홈페이지(/)로 GET 요청이 오면 실행될 메소드  
    @GetMapping("/")  
    public String index(Model model)  
    {  
        // 1. DB에서 모든 방명록 데이터를 가져온다.  
        List<Guestbook> guestbookList = guestbookRepository.findAll();  
  
        // 2. Model에 "guestbooks"라는 이름으로 데이터를 담아서 HTML로 전달한다.  
        model.addAttribute("guestbooks", guestbookList);  
  
        // 3. "list"라는 이름의 HTML 파일을 찾아서 반환하라는 의미.  
        return "list";  
    }  
  
    @PostMapping("/guestbook/write")  
    public String write(@RequestParam String author, @RequestParam String content)  
    {  
        // 1. Guestbook 객체 생성 및 데이터 설정  
        Guestbook guestbook = new Guestbook();  
        guestbook.setAuthor(author);  
        guestbook.setContent(content);  
  
        // 2. Repository를 통해 DB에 저장  
        guestbookRepository.save(guestbook);  
  
        // 3. 글쓰기가 완료되면 홈페이지(/)로 리다이렉트한다.  
        return "redirect:/";  
    }
```

- 핵심 변경점
	- `@RestController`-> `@Controller`
	- 반환 타입 : `Guestbook` (JSON) -> `String` (HTML 파일 이름)
	- `Model` 객체를 사용하여 데이터를 View로 전달
	- `@PostMapping` 을 사용하여 폼 데이터 처리
	- `return "redirect:/";` : 작업 후 홈페이지로 리디렉션

<br>

### 3. Configure HTML Page (View)
: Controller 가 반환하는 HTML 파일을 만드는 단계

**3.1. Configure Thymeleaf Templates Path**

`src/main/resources`디렉토리 내 `templates` 폴더 생성
(Spring Boot 가 여기서 HTML 파일을 찾는다.)

**3.2. Configure `list.html`**
```html
<!DOCTYPE html>
<html lang="ko" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>나만의 방명록</title>
</head>
<body>
    <h1>방명록</h1>

    <!-- 글쓰기 폼 -->
    <form action="/guestbook/write" method="post">
        <input type="text" name="author" placeholder="작성자" required>
        <input type="text" name="content" placeholder="내용" required>
        <button type="submit">글쓰기</button>
    </form>

    <hr>

    <!-- 방명록 목록 -->
    <h2>방명록 목록</h2>
    <table border="1">
        <thead>
            <tr>
                <th>번호</th>
                <th>작성자</th>
                <th>내용</th>
                <th>작성 시간</th>
            </tr>
        </thead>
        <tbody>
            <!-- 반복문: guestbooks 리스트에 있는 각 아이템을 'item'이라는 변수로 꺼내서 반복 -->
            <tr th:each="item : ${guestbooks}">
                <td th:text="${item.id}">1</td>
                <td th:text="${item.author}">작성자</td>
                <td th:text="${item.content}">내용</td>
                <td th:text="${item.createdAt}">2024-01-01</td>
            </tr>
        </tbody>
    </table>
</body>
</html>
```

<br>

### 4. Styling with Bootstrap

**4.1. Add Bootstrap CSS for CDN in `<head>` Tag**
: Bootstrap CSS 파일을 웹에서 가져다 쓰는 CDN 방식
```html
<head>
    <meta charset="UTF-8">
    <title>나만의 방명록</title>
    <!-- Bootstrap CSS CDN 추가 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
```

**4.2. Modify `<body>`Tag to use Bootstrap class**
```html
<body>
<div class="container">
    <h1 class="my-4">나만의 방명록</h1>

    <!-- 글쓰기 폼 -->
    <div class="card mb-4">
        <div class="card-body">
            <form action="/guestbook/write" method="post" class="row g-3">
                <div class="col-md-4">
                    <input type="text" name="author" class="form-control" placeholder="작성자" required>
                </div>
                <div class="col-md-6">
                    <input type="text" name="content" class="form-control" placeholder="내용" required>
                </div>
                <div class="col-md-2">
                    <button type="submit" class="btn btn-primary w-100">글쓰기</button>
                </div>
            </form>
        </div>
    </div>

    <!-- 방명록 목록 -->
    <h2>방명록 목록</h2>
    <table class="table table-striped">
        <thead>
            <tr>
                <th>번호</th>
                <th>작성자</th>
                <th>내용</th>
                <th>작성 시간</th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="item : ${guestbooks}">
                <td th:text="${item.id}">1</td>
                <td th:text="${item.author}">작성자</td>
                <td th:text="${item.content}">내용</td>
                <td th:text="${item.createdAt}">2024-01-01</td>
            </tr>
        </tbody>
    </table>
</div>
</body>
```

<br>


<br>

## Step 4. System Management

### 1. Create Cloud Server (EC2)
: 아마존 AWS 에서 클라우드 서버 사용

**1.1 AWS Management Console Log-in**
: aws.amazon.com 에 로그인하여 **EC2** 를 찾는다.

**1.2. EC2 Instance Start**
: EC2 대시보드에서 '인스턴스 시작' 버튼 클릭

**1.3. Set Name (Tag)**
: '이름' 필드에 이름 설정 (`my-web-server`)

**1.4. Select AMI**
: 'Amazon Machine Image (AMI)' 검색창에 `Amazon Linux` 를 검색하고,
**'Amazon Linux 2023 AMI'** 선택

**1.5. Select Instance Type**
: `t2.micro`또는 `t3.micro` 선택

**1.6. Create Key Pair (Password)**
: **새 키 페어 생성** 을 클릭하여, 키 이름을 짓고 생성.
~ `my-server-key.pem` 파일이 자동 다운로드 (**잃어버리면 안된다!**)

**1.7. Set Firewall (Secure Group)**
: '보안 그룹 규칙' 에서 다음 세 가지의 규칙 추가
  - **규칙 1** : 유형 `SSH` , 소스 `내 IP` (나의 컴퓨터에서만 SSH 접속)
  - **규칙 2** : 유형 `HTTP`, 소스 `위치 무관` (아무나 HTTP로 접속 가능)
  - **규칙 3** : 유형 `사용자정의 TCP`, 포트 범위 `8080`, 소스 `위치 무관`

**1.8. Start Instance**

<br>

### 2. Server Access (SSH)
: 만든 클라우드 서버에 원격 접속하기

**2.1. Check Public IP**
: EC2 인스턴스의 정보 중 **'Public IPv4 주소'** 를 복사해둔다.

**2.2. Set Authorization of Key File**
```bash
# 키 파일 권한 변경 (나만 읽을 수 있도록)
chmod 400 my-server-key.pem
```

**2.3. Access SSH**
```bash
ssh -i my-server-key.pem ec2-user@[MY_PUBLIC_IP]
```

<br>

### 3. Build Server Environment
: 서버에 프로젝트를 실행할 도구 설치

**3.1. Server Update**
```bash
sudo yum update -y
```

**3.2. Install Java 21**
```bash
sudo yum install java-21-amazon-corretto -y
```

**3.3. Install Maven**
```bash
sudo yum install maven -y
```

**3.4. Check Installations**
```bash
java -version
mvn -version
```

<br>

### 4. Project Deployment
: 프로젝트를 서버로 옮겨서 실행하기

**4.1. Modify Application Properties**
: application.properties 파일에 추가하기.
```properties
server.address=0.0.0.0
```

**4.2. Build Project on Local**
```bash
# IntelliJ 내 로컬 터미널에서 실행
# 프로젝트를 실행 가능한 .jar 파일로 만들기
mvn clean package
```
-> 프로젝트 폴더에 `target` 이라는 폴더가 생기고, 그 안에 `my-server ~ SNAPSHOT.jar` 파일 생성 확인

**4.3. Duplicate Files to Server**
```bash
# 새로운 로컬 터미널 실행
# jar 파일을 서버의 홈 디렉토리로 복사
scp -i my-web-key.pem /path/to/your/project/target/my-...-SNAPSHOT.jar ec2-user@[MY_PUBLIC_IP]:~/
```

**4.4. Run Application in Server**
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

**5.1. Assign Elastic IP**
: EC2 서버에 연결할 때마다 동적 IP 사용으로 퍼블릭 IPv4 주소가 변경된다.
-> AWS 에서 **'Elasic IP'** 를 할당받아 인스턴스에 연결한다.

**5.2. Set Domain**
: 순수 IP 주소가 아닌 도메인 이름을 구하여 연결한다.
-> 무료 도메인을 구하여 **DNS 설정을 통해 도메인을 서버 퍼블릭 IPv4로 연결**한다.

<br>


<br>

## Step 5. Security Engineering

### 1. Apply HTTPS

**1.1. Install Nginx & Setting**
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
	server_name [MY_PUBLIC_IP];
	
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

**1.2. Set Domain**
: 무료 도메인을 발급받거나 도메인을 구매하여 DNS를 통해 EC2의 퍼블릭 IPv4 주소를 연결시킨다.

**1.3. Get Free SSL Certification**
```bash
# Certbot 설치
sudo dnf install -y certbot python3-certbot-nginx

# 인증서 발급 및 설치
sudo certbot --nginx -d [MY_PUBLIC_IP]
```

<br>

