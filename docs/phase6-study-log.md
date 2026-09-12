# Phase 6 : 포트폴리오 통합 & API 현대화 — 상세 기록

---

## 이 문서의 목적

`README.md`는 포트폴리오용 요약(무엇을 했는지)만 담고, 이 문서는 **왜 그렇게 결정했는지 · 어떻게 구현했는지**를 상세히 남기는 공부 기록용 문서다. Phase 6이 진행되는 동안 계속 이어서 작성한다.

<br>

---

## 왜 브라운필드(Brownfield)인가

새 프로젝트를 처음부터 만드는 대신, 기존에 3년 가까이 쌓아온 MyServer(Phase 1~5)를 그대로 살려서 고도화하는 방식을 택했다.

| 방식 | 설명 | 선택 이유 |
| :--- | :--- | :--- |
| Greenfield | 완전히 새로운 프로젝트로 시작 | 기존 자산(도메인 모델, 보안 설정, CI/CD)을 버리게 됨 |
| **Brownfield** | 기존 시스템을 유지하며 점진적으로 개선 | 실무에서 더 흔한 상황이며, "레거시를 다루는 능력"을 포트폴리오로 보여줄 수 있음 |

전환 전략은 **Strangler Fig Pattern**(기존 기능을 한 번에 바꾸지 않고, 새 구조로 하나씩 감싸 교체해나가는 방식)을 따른다 — Controller를 REST API로 하나씩 전환하고, 그 뒤에 React 프론트가 붙는 순서로 진행할 예정.

<br>

---

## 아키텍처 전환 상세

### Frontend / Backend 분리 (Headless API 전환)

| 항목 | 기존 | 변경 후 |
| :--- | :--- | :--- |
| 렌더링 | Server-Side Rendering (Thymeleaf) | Client-Side (React SPA) |
| 서버 역할 | View 반환 + 비즈니스 로직 | REST API(JSON)만 제공 (Headless) |
| 인증 | 세션 기반 (Spring Security) | 세션 유지, 추후 API 확장 시 토큰 인증 검토 |

SSR은 초기 학습(Spring MVC 흐름 이해)에는 유리했지만, 포트폴리오 콘텐츠처럼 동적이고 자유로운 UI를 만들기엔 제약이 있어 SPA로 전환한다.

### DB 하이브리드 전략

- 기존: AWS RDS MySQL(관리형 서비스)을 운영 DB로 사용 — 배포/백업/보안 설정 경험은 이미 확보
- 이번 Phase: **자체 설치·운영형 MySQL**로 전환 — 관리형 서비스가 대신 해주던 설치, 계정/권한 관리, 버전 관리를 직접 겪어보기 위함
- 목적: "관리형 DB를 다뤄본 경험"과 "자체 DB 서버를 직접 운영해본 경험"을 모두 갖춰 DBA 관점의 역량 스펙트럼을 넓히는 것

### 인프라 (배포 단계, 개발 이후 진행)

운영 서버는 클라우드 VM 대신 자체 소유 하드웨어에서 직접 운영하는 방식으로 방향을 잡았다 (대기업들이 자체 DB/서버 인프라를 보유하는 방식과 유사한 경험을 위함). 외부 HTTPS 노출은 Cloudflare Tunnel로 처리해 포트포워딩·인증서 발급 문제를 단순화할 예정이다. 이 부분은 개발이 끝난 뒤 마지막 단계에서 진행하며, 지금의 개발 진행을 막지 않는다.

<br>

---

## Step 1. 로컬 개발 DB 전환 (H2 → MySQL)

### 1.1. MySQL 버전 선택

최소 반년 이상 지속 개발할 것을 고려해 **LTS(Long-Term Support)** 라인을 선택했다. MySQL은 최근 캘린더 버전 방식(예: 9.7 = 2025년 7월 릴리스)으로 바뀌었고, Innovation 릴리스는 신기능이 빠르지만 안정성이 떨어져 LTS보다 우선순위가 낮다.

- 선택: **MySQL 9.7.2 (LTS)**
- 설치 위치: MacBook (Apple Silicon → **ARM64** 네이티브 빌드)
- 데스크톱(Windows)에는 추후 별도 설치 예정이며, 두 기기 간 스키마 동기화는 DB 파일을 직접 옮기는 대신 **Flyway**(스키마를 코드로 관리)로 처리할 계획

### 1.2. GUI 클라이언트 — MySQL Workbench

- MySQL Workbench도 동일한 캘린더 버전 체계를 사용하며, 최신 버전(26.7)은 "MySQL 8.4 LTS 이상, 9.7 LTS 포함" 공식 지원 범위에 포함되어 호환성 문제 없음
- Apple Silicon Mac은 반드시 **ARM, 64-bit** 빌드를 선택 (구버전 Workbench는 x86 전용이라 Rosetta로 돌아가는 경우가 있었음)

### 1.3. 전용 DB / 계정 생성

root 계정을 애플리케이션에 직접 쓰지 않고, 최소 권한 원칙에 따라 전용 데이터베이스와 계정을 분리했다.

```sql
CREATE DATABASE myserver_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'myserver'@'localhost' IDENTIFIED BY '****';
GRANT ALL PRIVILEGES ON myserver_dev.* TO 'myserver'@'localhost';
FLUSH PRIVILEGES;
```

### 1.4. `application-local.properties` 전환

```diff
- spring.datasource.url=jdbc:h2:file:./data/testdb
- spring.datasource.driver-class-name=org.h2.Driver
- spring.datasource.username=sa
- spring.datasource.password=

+ spring.datasource.url=jdbc:mysql://localhost:3306/myserver_dev?serverTimezone=Asia/Seoul
+ spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
+ spring.datasource.username=myserver
+ spring.datasource.password=****
```

`mysql-connector-j`는 이미 `pom.xml`에 런타임 의존성으로 포함되어 있어 별도 의존성 추가 없이 바로 동작했다.

### 1.5. 로컬 자격 증명 git 보안 처리

작업 중 `application-local.properties`가 git으로 추적되고 있다는 걸 발견했다. H2는 비밀번호가 없어 지금까지 문제가 없었지만, 그대로 MySQL 비밀번호를 적으면 GitHub에 그대로 올라갈 뻔했다.

- `git rm --cached`로 추적에서 제외
- `.gitignore`에 `application-local.properties` 추가
- 비밀번호 없이 형태만 보여주는 `application-local.properties.example` 템플릿을 대신 커밋 — 다른 기기(데스크톱)에서도 이 템플릿을 복사해 각자 비밀번호만 채우면 됨

<br>

---

## 진행 상황

- [x] MySQL 9.7.2 LTS 설치 (MacBook, ARM64)
- [x] MySQL Workbench 설치 및 연결 확인
- [x] 전용 DB(`myserver_dev`) / 계정(`myserver`) 생성
- [x] `application-local.properties` H2 → MySQL 전환
- [x] 로컬 자격 증명 git 추적 제외 + `.example` 템플릿 작성

## 다음 단계

- [ ] Flyway 도입 — 스키마를 코드로 관리해 MacBook / 데스크톱 간 동일한 DB 구조 유지
- [ ] 기존 Controller(Article · Market · Portfolio · Post · User)를 REST API(`@RestController` + DTO)로 전환
- [ ] React 프론트엔드 신규 구축, 포트폴리오 콘텐츠를 신규 도메인으로 통합
- [ ] (개발 완료 후) 자체 하드웨어 상시 구동 설정 + Cloudflare Tunnel로 외부 HTTPS 노출
