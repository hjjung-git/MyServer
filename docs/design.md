# Technical Design Document

| 항목 | 내용 |
| :--- | :--- |
| Project | my-server |
| Version | 1.0.0-SNAPSHOT |
| Status | Phase 2 Completed |

<br>

---

## 1. Design Rationale & Trade-offs

### 1.1. Full-Stack Learning Architecture

**Rationale**

- NCS ICT 직무(응용SW, DB, 보안, UI/UX, IT시스템관리)를 하나의 서비스 안에서 통합적으로 경험
- 개발 → 배포 → 운영 → 유지보수 전 과정을 직접 구축

**Trade-offs**

| Pros | Cons |
| :--- | :--- |
| 백엔드, DB, 인프라, 보안을 하나의 프로젝트에서 통합적으로 경험 | 범위가 넓어 설계 복잡도 증가 |

<br>

### 1.2. Layered Architecture

**Rationale**

- Controller / Service / Repository 계층 분리로 관심사 분리(SoC) 적용
- Service 인터페이스와 구현체 분리로 테스트 및 확장 용이성 확보

**Trade-offs**

| Pros | Cons |
| :--- | :--- |
| 유지보수성과 모듈성이 높아짐 | 초기 프로젝트 규모 대비 코드량 증가 |

<br>

### 1.3. Server-Side Rendering (Thymeleaf)

**Rationale**

- Spring MVC 흐름 이해를 우선
- 게시판 기반 서비스에서 빠른 구현과 안정적인 서버 렌더링 구조 확보

**Trade-offs**

| Pros | Cons |
| :--- | :--- |
| 구현 속도가 빠르고 서버 중심 아키텍처 이해에 유리 | SPA 수준의 동적 UX 구현에는 제약 |

<br>

### 1.4. Hybrid Database Strategy (H2 → MySQL)

**Rationale**

- **Development** : H2 DB로 빠른 개발 및 테스트 환경 구성
- **Production** : AWS RDS MySQL로 데이터 안정성과 확장성 확보

**Trade-offs**

| Pros | Cons |
| :--- | :--- |
| 개발 편의성과 운영 안정성을 동시에 확보 | DB 환경 차이로 인한 설정 관리 필요 |

<br>

### 1.5. Session-Based Authentication

**Rationale**

- Spring Security 기반 세션 인증 방식 적용
- 서버 중심으로 인증 및 게시글 권한 제어

**Trade-offs**

| Pros | Cons |
| :--- | :--- |
| 전통적인 웹 애플리케이션 구조와 자연스럽게 통합 | API 확장 시 토큰 인증 방식 추가 설계 필요 |

<br>

---

## 2. Architectural Strategy

### 2.1. Evolutionary Architecture

프로젝트는 점진적 설계 전략을 따른다.

- **Phase 1** : 빠른 구현을 통한 기본 서버 구축
- **Phase 2** : Layered Architecture, 예외 처리, 테스트, 보안/인증 적용

<br>

### 2.2. Environment Isolation

| Environment | Database | Profile |
| :--- | :--- | :---: |
| Local | H2 | `local` |
| Production | AWS RDS MySQL | `prod` |

<br>

### 2.3. Deployment Architecture

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

<br>

### 2.4. Security by Design

- Spring Security 기반 인증 시스템
- BCrypt 비밀번호 암호화
- 작성자 기반 게시글 권한 제어
- AWS Security Group 기반 네트워크 제한
- HTTPS 통신 적용

<br>

---

## 3. Current Design Decisions

### 3.1. User Identity Modeling

| Field | Purpose |
| :--- | :--- |
| `loginId` | 로그인 인증용 ID |
| `username` | 화면 표시용 닉네임 |

<br>

### 3.2. Post Ownership

```
User 1 ---- N Post
```

- 게시글 작성자만 수정 / 삭제 가능
- 권한 검증 : View 조건 + Service 로직 양쪽에서 수행

<br>

### 3.3. File Upload Strategy

- UUID 기반 파일명 생성 (중복 방지)
- 서버 로컬 스토리지 (`./uploads`) 저장
- Resource Handler를 통한 웹 접근 경로 매핑
- 향후 AWS S3로 이전 가능하도록 Service 레이어에 로직 집중

<br>

### 3.4. Local H2 Strategy

- 로컬 개발 환경에서는 H2 파일 기반 DB 사용
- 스키마 변경 테스트 용이성 확보
