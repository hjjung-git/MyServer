# Study Notes — 핵심 개념 정리

이 프로젝트를 진행하며 학습한 핵심 개념만 정리한 문서. 실제 구현(정확한 명령어·설정 파일 등)은 AI 도구의 도움을 받아 진행하므로, 여기서는 판단 기준이 되는 개념 위주로 기록한다.

<br>

---

## 아키텍처

- **계층형 아키텍처 (Layered Architecture)**: Controller–Service–Repository로 관심사 분리(SoC). Service를 인터페이스/구현체로 나눠 테스트 용이성과 확장성을 확보.
- **SSR vs CSR**: Thymeleaf(SSR)로 시작해 Spring MVC 요청 흐름을 먼저 익힘 → 이후 React(CSR/SPA)로 프론트엔드를 분리하는 Headless API 전환 진행 (기존 기능을 한 번에 바꾸지 않고 점진적으로 교체하는 **Strangler Fig 패턴** 적용).
- **환경 분리 (Spring Profile)**: `application-{profile}.properties`로 local/prod 등 환경별 설정을 분리하고, 공통 설정 위에 덮어씌우는 방식으로 동작.

<br>

---

## DB 엔지니어링

- **ORM / JPA / Hibernate**: JPA(표준 인터페이스) — Hibernate(구현체) — Spring Data JPA(더 쉽게 쓰도록 감싼 것)의 관계.
- **`ddl-auto` 옵션**: 개발은 `update`(변경분만 반영), 운영은 `none`(스키마를 코드로 직접 관리 — Flyway 등).
- **H2 vs MySQL**: H2는 설정 없이 바로 쓰는 경량 DB(로컬 개발용), MySQL은 독립 프로세스로 동작하는 실서비스용 RDBMS.
- **관리형 DB(AWS RDS) vs 자체 구축 DB**: 관리형은 백업·패치·복구를 서비스가 대신 처리해주고, 자체 구축은 설치·계정·권한 관리를 직접 겪게 됨 — DBA 관점의 경험 스펙트럼을 넓히기 위해 이번 전환에서는 후자를 선택.
- **`@Transactional`**: DB 작업의 원자성 보장(중간 실패 시 전체 롤백), `readOnly=true`로 조회 전용 트랜잭션 성능 최적화.

<br>

---

## 보안

- **인증/인가**: Spring Security 세션 기반 인증 + BCrypt 비밀번호 해시. Service 레이어에서 작성자 검증(`validateOwner()`)을 별도로 수행해, URL 직접 접근으로 권한 검증을 우회하는 취약점을 방지.
- **CSRF/XSS**: Thymeleaf가 폼에 CSRF 토큰을 자동 삽입하고, `th:text`가 출력값을 자동 이스케이프해 별도 조치 없이 XSS를 방어.
- **Rate Limiting**: IP별 로그인 실패 횟수를 추적해 일정 횟수 이상이면 일정 시간 차단.
- **민감정보 관리**: 비밀번호·키 값을 코드/설정 파일에 직접 쓰지 않고 환경변수 또는 Secrets Manager로 외부화. 로컬 개발 자격 증명 파일은 `.gitignore`로 git 추적에서 제외.

<br>

---

## 배포 개념 (서버 구축~보안 강화 단계, AWS 기준)

- **리버스 프록시(Nginx)**: 외부에서 들어오는 80/443 포트 요청을 내부 애플리케이션 포트(8080)로 전달하는 중개자 역할.
- **HTTPS / Let's Encrypt**: TLS로 데이터를 암호화해 평문 전송 시의 도청·위변조를 방지. 무료 인증서를 자동 발급·갱신.

→ 아키텍처 마이그레이션 단계부터는 이 구조를 자체 호스팅 + Cloudflare Tunnel로 대체할 예정 (포트포워딩·인증서 발급 이슈를 근본적으로 없앰). 자세한 배경은 [architecture-migration-log.md](architecture-migration-log.md) 참고.

<br>

---

## 참고: 매매일지 플랫폼 확장 (보류)

암호화폐 실시간 시세(업비트 API), 뉴스 자동 수집(RSS), AI 한국어 요약(Groq/Llama) 등 도메인 확장 실습을 진행했다. 현재는 보류 상태이며, 추후 온톨로지·생성형 챗봇 프로젝트와 연계되는 시점에 재개할 예정이다.
