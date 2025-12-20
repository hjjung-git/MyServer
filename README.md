
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

## Step 3. 



---

# Getting Started

이 프로젝트를 로컬 환경에서 실행하는 방법을 안내한다.

### Requirements
- JDK 21
- Maven 3.9.11
- Git

### Install & Run
1. 해당 저장소를 클론
```bash
git clone https://github.com/~
```
