# MyServer Project

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=flat-square&logo=thymeleaf&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-7952B3?style=flat-square&logo=bootstrap&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=flat-square&logo=amazonaws&logoColor=white)
![Nginx](https://img.shields.io/badge/Nginx-009639?style=flat-square&logo=nginx&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)
![H2 Database](https://img.shields.io/badge/H2-003B57?style=flat-square&logo=h2&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=flat-square&logo=intellijidea&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white)

> NCS ICT 직무(응용SW, DB, 보안, UI/UX, IT시스템관리)를 연계하여  
> 처음부터 끝까지 직접 구축해보는 나만의 웹 서버 프로젝트

<br>

---

## Tech Stack

| 분류 | 기술 |
| :--- | :--- |
| Language | Java 21 |
| Framework | Spring Boot 4.0.0 · Spring Data JPA · Spring Security |
| View | Thymeleaf · Bootstrap 5 |
| Database | H2 (local) · MySQL / AWS RDS (production) |
| Deployment | AWS EC2 · Nginx · JAR |
| Build | Maven |

<br>

---

## Project Milestone

### Phase 1 — Server Setup

| 단계 | 내용 | 상태 |
| :--- | :--- | :---: |
| Step 1 | 개발 환경 구성 (JDK, IDE, Spring Boot 초기화) | ✅ |
| Step 2 | DB 엔지니어링 (H2, JPA, Entity/Repository 구성) | ✅ |
| Step 3 | UI/UX 엔지니어링 (Thymeleaf, Bootstrap) | ✅ |
| Step 4 | 시스템 관리 (AWS EC2, Elastic IP, 도메인) | ✅ |
| Step 5 | 보안 엔지니어링 (Nginx, HTTPS, Let's Encrypt) | ✅ |

### Phase 2 — Maintenance & Operation

| 단계 | 내용 | 상태 |
| :--- | :--- | :---: |
| Step 1 | 기능 및 코드 관리 (계층형 아키텍처, 검색, 파일 업로드) | ✅ |
| Step 2 | 데이터 관리 (MySQL 이관, RDS 백업) | ✅ |
| Step 3 | 사용자 인증 및 접근 제어 (Spring Security) | ✅ |
| Step 4 | 인프라 유지보수 (CI/CD, 로그 관리) | ✅ |

### Phase 3 — Security Hardening

| 단계 | 내용 | 상태 |
| :--- | :--- | :---: |
| Step 1 | Service 레이어 권한 검증 | ⬜ |
| Step 2 | Rate Limiting · XSS / CSRF 방어 | ⬜ |
| Step 3 | 민감정보 외부화 (환경변수 / Secrets Manager) | ⬜ |
| Step 4 | 컨트롤러 보안 테스트 | ⬜ |

### Phase 4 — Service Transformation

| 단계 | 내용 | 상태 |
| :--- | :--- | :---: |
| Step 1 | 카테고리 / 태그 시스템 | ⬜ |
| Step 2 | 매매일지 특화 필드 (종목, 가격, 전략) | ⬜ |
| Step 3 | 마크다운 에디터 | ⬜ |
| Step 4 | 파일 스토리지 AWS S3 이전 | ⬜ |
| Step 5 | 공개 / 비공개 설정 · 통계 뷰 | ⬜ |

### Phase 5 — Community Forum

| 단계 | 내용 | 상태 |
| :--- | :--- | :---: |
| Step 1 | 댓글 시스템 | ⬜ |
| Step 2 | 좋아요 / 북마크 | ⬜ |
| Step 3 | 회원 등급 | ⬜ |
| Step 4 | 알림 | ⬜ |

<br>

---

## Quick Start

```bash
cd my-server
mvn spring-boot:run
```

접속 → http://localhost:8080

운영 서버 배포 → [docs/deploy.md](docs/deploy.md)

<br>

---

## Documentation

| 문서 | 내용 |
| :--- | :--- |
| [docs/study-notes.md](docs/study-notes.md) | 핵심 개념 학습 정리 |
| [docs/design.md](docs/design.md) | 기술 설계 문서 (아키텍처, 설계 결정) |
| [docs/deploy.md](docs/deploy.md) | 배포 및 운영 절차 |
| [docs/phase1-server-setup.md](docs/phase1-server-setup.md) | Phase 1 상세 기록 |
| [docs/phase2-maintenance.md](docs/phase2-maintenance.md) | Phase 2 상세 기록 |
