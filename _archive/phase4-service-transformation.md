# Phase 4 : Service Transformation

---

## Step 1 — 다크 테마 · 대시보드 레이아웃 전환

### Thymeleaf Fragment로 레이아웃 재사용

```html
<!-- fragments/sidebar.html -->
<div th:fragment="sidebar(activeMenu)" class="app-sidebar"> ... </div>

<!-- list.html -->
<div th:replace="~{fragments/sidebar :: sidebar('dashboard')}"></div>
```

`th:fragment`로 정의한 조각을 `th:replace`로 다른 템플릿에 삽입한다. 파라미터(`activeMenu`)를 전달할 수 있어 페이지마다 어떤 메뉴를 활성 상태로 표시할지 제어 가능하다. 사이드바를 페이지마다 복사하지 않고 한 곳에서 관리한다.

### 정적 리소스도 Spring Security 인가 대상이다

다크 테마 CSS를 추가했는데 화면이 흰 배경으로 그대로 보이는 문제가 발생했다.

**원인** : `SecurityConfig`의 `permitAll()` 목록에 `/css/**`가 빠져 있었음.

```java
.requestMatchers("/", "/main/list/**", "/user/**", "/uploads/**", "/h2-console/**")
        .permitAll()
.anyRequest().authenticated()   // /css/** 도 여기 걸림
```

비로그인 상태로 `/css/dark-theme.css`를 요청하면 Spring Security가 이를 인증이 필요한 리소스로 판단해 `/user/login`으로 302 리다이렉트한다. 브라우저는 CSS 대신 로그인 페이지 HTML을 받게 되어 스타일이 전혀 적용되지 않는다.

**해결** : CSS/JS 등 정적 리소스 경로를 `permitAll()`에 명시적으로 추가.

```java
.requestMatchers("/", "/main/list/**", "/user/**", "/uploads/**",
        "/h2-console/**", "/css/**", "/js/**").permitAll()
```

HTML 페이지가 정상 로드되어도 그 안에서 참조하는 CSS/JS/이미지 등 모든 리소스는 별도의 HTTP 요청이며, 각각 Security 인가 규칙을 통과해야 한다는 점을 기억해야 한다.

### CSS Grid `auto-fit`으로 반응형 카드 레이아웃

```css
.kpi-grid {
    grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
}
```

고정 컬럼 수(`repeat(4, ...)`) 대신 `auto-fit`을 쓰면 컨테이너 폭에 따라 컬럼 수가 자동으로 줄어든다. 향후 우측에 분할 패널이 열려 메인 영역이 좁아져도 별도의 JS 없이 카드가 자연스럽게 줄바꿈된다.

<br>

---

## Step 2 — 콘텐츠 타입 분리 (TRADE_LOG / INSIGHT)

### 단일 테이블 + Enum 컬럼 전략

매매일지와 인사이트를 별도 테이블로 나누지 않고 `Post` 테이블 하나에 `type` 컬럼(Enum)으로 구분했다.

```java
@Enumerated(EnumType.STRING)
private PostType type = PostType.INSIGHT;

private String ticker;          // TRADE_LOG일 때만 값이 채워짐
private TradePosition position;
private BigDecimal entryPrice;
```

테이블을 분리했다면 목록 조회 시 두 테이블을 UNION 해야 하고, 댓글·권한·공개설정 같은 공통 기능을 양쪽에 중복 구현해야 한다. 단일 테이블 + 타입 컬럼은 공통 로직을 그대로 재사용하면서 타입별 전용 필드만 nullable로 추가하면 된다. 데이터가 매우 커지거나 필드 구조가 완전히 달라지는 시점이 오면 분리를 재검토한다.

### Spring Data JPA의 And/Or 우선순위

```java
Page<Post> findByTypeAndTitleContainingOrTypeAndContentContaining(
        PostType type1, String title, PostType type2, String content, Pageable pageable);
```

메서드 이름만 보면 `Type AND Title OR Type AND Content`인데, Spring Data JPA는 `And`가 `Or`보다 결합력이 강하다고 해석한다. 즉 `(Type AND Title) OR (Type AND Content)`로 파싱되어, "해당 타입이면서 제목 또는 내용에 키워드가 포함된 글"이라는 의도한 쿼리가 정확히 만들어진다. 파라미터로 `type`을 두 번 전달해야 하는 이유도 이 구조 때문이다.

### `@RequestParam`의 Enum 자동 변환

```java
@RequestParam(value = "type", required = false) PostType type
```

Spring MVC는 쿼리 파라미터 문자열(`?type=TRADE_LOG`)을 `Enum.valueOf()`로 자동 변환해 컨트롤러 파라미터에 바인딩한다. 별도의 변환 코드 없이 `String` 대신 enum 타입을 그대로 받을 수 있다. 단, 존재하지 않는 값(`?type=BOGUS`)을 넘기면 변환 실패로 예외가 발생한다 — 사용자 입력을 직접 받는 파라미터라면 잘못된 값에 대한 처리(400 응답 등)를 별도로 고려해야 한다.

### Thymeleaf 조건부 클래스 적용

```html
<a th:href="@{/main/list(type='TRADE_LOG')}"
   class="filter-chip"
   th:classappend="${type != null and type.name() == 'TRADE_LOG'} ? ' active' : ''">매매일지</a>
```

`th:classappend`는 기존 `class` 속성 값에 조건부로 문자열을 덧붙인다. 현재 선택된 필터를 시각적으로 강조하는 데 쓰였다.

<br>

---

## Step 2.5 — 전체 화면 다크 테마 통일 + 대시보드 차트 틀

### 화면 간 일관성을 CSS 클래스로 관리

로그인, 회원가입, 글쓰기, 수정, 상세, 에러 페이지(403/404/500)가 모두 제각각 Bootstrap 기본 스타일을 쓰고 있었다. 페이지마다 인라인 스타일을 새로 작성하는 대신 `dark-theme.css`에 의미 단위 클래스를 정의하고 재사용했다.

```css
.auth-card { /* 로그인/회원가입 카드 */ }
.error-shell { /* 403/404/500 공통 레이아웃 */ }
.form-page-card { /* 글쓰기/수정 폼 카드 */ }
.detail-card, .detail-stat { /* 상세 페이지 */ }
```

이렇게 하면 추후 색상 하나를 바꿔도 `:root`의 CSS 변수만 수정하면 전체 화면에 일괄 반영된다.

### 비어 있는 영역도 "틀"부터 만드는 이유

대시보드의 메인 차트·워치리스트·미니 차트 그리드는 아직 실제 시세 데이터가 없다. 그렇다고 해당 영역을 비워두지 않고, 정적 SVG와 더미 텍스트("데이터 연동 예정")로 레이아웃 틀을 먼저 만들었다.

```html
<div th:if="${type == null}" class="chart-panel">
    ...
</div>
```

`th:if="${type == null}"`로 감싸서 대시보드(전체) 화면에서만 보이고, "매매일지"/"인사이트" 필터 화면에서는 숨겨지도록 분리했다. 나중에 실제 통계 데이터를 연동할 때(Phase 4 Step 6) 이 틀 안의 SVG `points`만 실제 값으로 교체하면 되므로, 화면 구조와 데이터 연동을 분리해서 작업할 수 있다.

### Controller에서 activeMenu를 일관되게 계산

```java
private String activeMenuFor(PostType type)
{
    if (type == null) return "dashboard";
    return type == PostType.TRADE_LOG ? "tradelog" : "insight";
}
```

목록/상세/글쓰기/수정 등 여러 메서드에서 사이드바 활성 메뉴를 계산하는 로직이 똑같이 필요했다. 메서드로 추출해서 중복을 제거하고, `Post.type`이 있는 곳이면 어디서든 같은 기준으로 사이드바가 강조된다.

<br>

---

## Step 3 — 매매일지 CRUD

### 사용자가 입력한 값을 그대로 믿지 않는다

처음에는 수익률(%)도 사용자가 직접 입력하는 필드였다. 하지만 진입가·청산가를 입력했는데 수익률을 잘못 계산해서 입력하면 데이터가 깨진다. 그래서 수익률 입력 필드를 폼에서 완전히 제거하고, 서버에서 진입가/청산가/포지션으로부터 자동 계산하도록 바꿨다.

```java
private void calculateProfitRate(Post post)
{
    if (post.getType() != PostType.TRADE_LOG
            || post.getEntryPrice() == null
            || post.getExitPrice() == null
            || post.getEntryPrice().signum() == 0)
    {
        post.setProfitRate(null);
        return;
    }

    BigDecimal rate = post.getExitPrice().subtract(post.getEntryPrice())
            .divide(post.getEntryPrice(), 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .setScale(2, RoundingMode.HALF_UP);

    if (post.getPosition() == TradePosition.SHORT)
    { rate = rate.negate(); }

    post.setProfitRate(rate);
}
```

핵심은 **SHORT 포지션의 방향 반전**이다. SHORT는 가격이 떨어질 때 이익을 보는 포지션이므로, 단순히 `(청산가-진입가)/진입가`만 계산하면 SHORT에서는 부호가 거꾸로 나온다. 포지션에 따라 부호를 반전시켜야 실제 손익 방향과 일치한다.

```
LONG  : 1000 → 1100  =>  +10%  (가격 상승 = 수익)
SHORT : 1000 → 900   =>  +10%  (가격 하락 = 수익, 부호 반전)
```

`BigDecimal.divide()`는 나누어떨어지지 않으면 `ArithmeticException`을 던지므로, 반드시 소수점 자릿수(`scale`)와 반올림 모드(`RoundingMode`)를 함께 지정해야 한다.

### `BindingResult.rejectValue()`로 필드별 검증 에러 추가

`@Valid`는 `Post` 엔티티에 붙은 `@NotBlank` 같은 어노테이션만 검증한다. "매매일지 타입일 때만 종목이 필수"처럼 조건에 따라 달라지는 규칙은 어노테이션으로 표현하기 어려워서, 컨트롤러에서 직접 검증하고 `BindingResult`에 에러를 추가했다.

```java
if (!StringUtils.hasText(post.getTicker()))
    bindingResult.rejectValue("ticker", "required", "종목을 입력하세요.");
```

`rejectValue("필드명", "에러코드", "메시지")`로 추가한 에러는 `th:errors="*{ticker}"`로 폼에서 그대로 출력된다. `@Valid` 검증과 수동 검증을 같은 `BindingResult`에 누적시켜 한 번에 처리할 수 있다.

---

## Step 3.5 — 푸시 패널 분할 레이아웃

### 왜 패널(push-panel) 방식인가

사이드바 메뉴 클릭 시 전체 페이지를 교체(`/main/list?type=TRADE_LOG`)하면 대시보드 컨텍스트(차트, 워치리스트)가 사라진다. 우측에서 패널이 밀려 들어오는 방식은 메인 콘텐츠를 유지하면서 게시판을 오버레이 없이 옆에 붙인다.

### Thymeleaf 프래그먼트 부분 응답 (AJAX)

```
GET /panel/board?type=TRADE_LOG  →  fragments/panel-board :: board  (HTML 조각만 반환)
GET /panel/post/{id}             →  fragments/panel-detail :: detail
```

컨트롤러에서 `return "fragments/panel-board :: board"` 처럼 `파일::프래그먼트명` 형식으로 반환하면 Thymeleaf가 해당 `th:fragment`만 렌더링한다. 전체 페이지가 아닌 HTML 조각이 응답으로 오고, JS `fetch()`가 받아서 `innerHTML`에 주입한다.

### 이벤트 위임(Event Delegation)으로 동적 콘텐츠 클릭 처리

패널 내부는 `fetch()`로 교체되므로 직접 이벤트 리스너를 붙이면 교체 후 사라진다. `document.addEventListener('click', ...)` 로 상위에서 잡고, `e.target.closest('[data-panel-post]')` 처럼 data 속성으로 의도한 클릭만 걸러낸다. 동적 DOM에서 표준 패턴이다.

### CSS flex push 레이아웃

```css
.app-shell  { display: flex; }
.app-main   { flex: 1; min-width: 0; }          /* 남은 공간 모두 차지, 줄어들 수 있음 */
.app-panel  { width: 0; overflow: hidden;
              transition: width 0.25s ease; }    /* 닫힘 상태 */
.app-panel.open { width: 400px; }               /* 열림 상태 */
```

`position: fixed/absolute` 없이 flex 흐름 안에서 너비가 늘어나면 `app-main`이 자동으로 압축된다. `overflow: hidden`으로 `width:0` 상태에서 내부 콘텐츠를 숨기고, `transition`으로 부드러운 슬라이드 효과를 낸다.

---

## Step 4-C — 뉴스 내부 상세보기 + AI 한국어 요약

### 외부 링크 대신 내부 패널 탐색

뉴스 카드를 `<a href="..." target="_blank">` 대신 `data-news-id` 속성을 가진 `<div>`로 변경하고, 이벤트 위임으로 `loadNewsDetail(id)`를 호출한다. 패널 내 페이지 전환이므로 브라우저 탭이 열리지 않고 패널 body만 교체된다.

```html
<!-- 변경 전 -->
<a th:href="${article.url}" target="_blank">...</a>

<!-- 변경 후 -->
<div th:attr="data-news-id=${article.id}" style="cursor:pointer;">...</div>
```

### Anthropic Claude API 연동 (RestClient)

`ClaudeClient` 서비스가 RSS 수집 직후 각 기사의 제목+요약을 Claude Haiku에 보내 한국어 두괄식 요약을 생성한다.

- **Why RestClient?** Spring 6+에서 `RestTemplate`을 대체하는 동기 HTTP 클라이언트. `WebClient`보다 코드가 간결하고 reactive 의존성이 없다.
- **Graceful degradation:** `@Value("${anthropic.api.key:}")` — 환경변수 미설정 시 빈 문자열이 주입되어 API 호출을 건너뛴다. 서버는 정상 동작하고 `koreanSummary`는 null로 저장된다.
- **두괄식:** 가장 중요한 결론을 먼저 서술하는 글쓰기 방식. 프롬프트에 명시하여 AI 출력 형식을 유도한다.

### 뉴스 상세 화면 구성

```
[AI 한국어 요약] ← teal 좌측 보더 카드, 두괄식
[원문 영어 요약]
[원문 보기 버튼] ← 외부 링크 (새 탭)
```

`koreanSummary`가 null이면 AI 요약 카드를 렌더링하지 않아 미번역 기사도 깔끔하게 표시된다.

---

## Step 4-D — 번역 비동기 분리 + H2 파일 DB

### 동기 처리의 문제점

RSS 수집 루프 안에서 번역 API를 직접 호출하면 기사 수 × 딜레이만큼 서버 시작이 지연된다. 특히 in-memory DB는 재시작 시 데이터가 초기화되므로 매번 전체 번역을 반복하게 된다.

### 해결: 스케줄러 분리

```
fetchAllFeeds()       — 5초 후 실행, 10분마다 반복 → 저장만 (빠름)
translatePending()    — 15초 후 실행, 1분마다 반복 → 미번역 5건씩 처리
```

- RSS 수집이 즉시 완료되어 서버 시작 직후 뉴스 목록 사용 가능
- 번역은 백그라운드에서 점진적으로 채워짐
- 번역 실패 시 `koreanSummary = ""`(빈 문자열)로 마킹 → 무한 재시도 방지

### H2 파일 DB

```properties
# in-memory (재시작 시 초기화)
spring.datasource.url=jdbc:h2:mem:testdb

# 파일 (재시작 후에도 데이터 유지)
spring.datasource.url=jdbc:h2:file:./data/testdb
```

파일 DB로 전환하면 이미 번역된 기사는 `existsByUrl()` 체크로 건너뛰어 재번역이 발생하지 않는다. 스키마 변경 시 `data/` 디렉토리를 삭제하고 재시작하면 된다.

### Groq API (무료 대체제)

Anthropic API 크레딧 부족, Gemini 무료 할당량 문제 대안으로 채택.
- 완전 무료, 신용카드 불필요
- OpenAI 호환 엔드포인트 (`/openai/v1/chat/completions`)
- 모델: `llama-3.1-8b-instant`, 분당 30회 제한
- API 키: `gsk_...` 형태 (console.groq.com 발급)
