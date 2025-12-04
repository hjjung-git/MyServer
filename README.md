
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

## Current Progress

### Step 1. Server Testing to Java

#### 1. Arranging Development Environment

1.1. JDK Installation
: Java OpenJDK 21.0.2 (LTS version)

1.2. IDE Installation
: IntelliJ IDEA (Community Edition)

1.3. Build Tools Explained
: Maven 이나 Gradle 은 프로젝트에 필요한 라이브버리를 자동으로 다운로드하고 관리해주는 도구이다.
Spring Boot 프로젝트를 생성하면 자동으로 포함된다.

#### 2. First Initializing Spring Boot

2.1. Generate Spring Boot project to Spring Initializer
~ https://start.spring.io/
> Project : Maven Project 
> Language : Java
> Spring Boot version : 4.0.0
> Packaging : Jar
> Configuration : Properties
> Dependencies : Spring Web

2.2. Server run & test
~ http://localhost:8080/hello

---

## Getting Started

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
