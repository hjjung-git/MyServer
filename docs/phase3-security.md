# Phase 3 : Security Hardening

---

## Step 1. Service 레이어 권한 검증

### 문제점 (수정 전)

수정 / 삭제 버튼은 작성자에게만 보이지만, URL을 직접 입력하면 누구든 실행 가능했다.

```
/post/edit/1    → 누구나 접근 가능
/post/delete/1  → 누구나 실행 가능
```

### 변경 사항

#### 1. `UnauthorizedException` 추가

`exception/UnauthorizedException.java`

권한 없는 요청에 대한 전용 예외 클래스 생성

#### 2. `GlobalExceptionHandler` 업데이트

`UnauthorizedException` 발생 시 `error/403.html` 반환

#### 3. `403.html` 추가

`templates/error/403.html`

#### 4. `PostServiceImpl` 권한 검증 추가

`validateOwner()` 메서드 — 현재 로그인 사용자와 게시글 작성자 비교

| 역할 | 수정/삭제 가능 여부 |
| :--- | :---: |
| ADMIN | ✅ 모든 글 |
| USER (작성자) | ✅ 본인 글만 |
| USER (타인) | ❌ 403 반환 |

`updatePost()`, `delete()` 진입 시 `validateOwner()` 호출

#### 5. `updatePost()` 작성자 변경 버그 수정

기존 코드가 수정 시 `username` 필드까지 덮어쓰는 문제 제거

#### 6. `PostController` 인터페이스 주입으로 변경

`PostServiceImpl` 직접 주입 → `PostService` 인터페이스 주입

<br>

---

## Step 2. Rate Limiting · XSS / CSRF 방어

> 진행 예정

---

## Step 3. 민감정보 외부화

> 진행 예정

---

## Step 4. 컨트롤러 보안 테스트

> 진행 예정
