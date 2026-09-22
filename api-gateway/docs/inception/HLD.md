# HLD — API Gateway
**Service:** `api-gateway`  
**Phiên bản:** 1.0.0  
**Ngày:** 2026-09-22  
**Trạng thái:** DRAFT  
**Tham chiếu:** BRD.md, SRD.md

---

## 1. Kiến trúc tổng quan

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          EXTERNAL CLIENTS                               │
│    Mobile App          CMS Frontend           External System           │
│  (PKCE / OAuth2)     (Angular, PKCE)         (Client Credentials)      │
└──────────┬──────────────────┬──────────────────────┬────────────────────┘
           │                  │                       │
           └──────────────────┴───────────────────────┘
                              │  HTTPS (TLS termination)
                              ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         API GATEWAY :8080                               │
│                    (Spring Cloud Gateway + WebFlux)                     │
│                                                                         │
│  ┌─────────────────┐  ┌──────────────────┐  ┌───────────────────────┐  │
│  │  Global Filters  │  │  Route Predicate │  │  Per-Route Filters    │  │
│  │                  │  │                  │  │                       │  │
│  │ 1. CorrelationId │  │ /api/v1/money/** │  │ StripPrefix           │  │
│  │ 2. JWT Auth      │  │ /api/v1/cms/**   │  │ AddRequestHeader      │  │
│  │ 3. HeaderEnrich  │  │ /api/v1/profiles/│  │   (X-User-Id)         │  │
│  │ 4. RateLimiter   │  │ /api/v1/notifs/  │  │   (X-User-Roles)      │  │
│  │ 5. AccessLog     │  │                  │  │                       │  │
│  └─────────────────┘  └──────────────────┘  └───────────────────────┘  │
└──────────────────────────────────────────────────────────────────────────┘
           │                  │                    │                │
           ▼                  ▼                    ▼                ▼
    ┌────────────┐   ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
    │ money-bank │   │ cms-backend  │   │profile-service│  │notification  │
    │  :8181     │   │   :8182      │   │   :8183       │   │  service     │
    │            │   │              │   │               │   │  :8184       │
    └──────┬─────┘   └──────────────┘   └───────┬───────┘   └──────────────┘
           │                                     │
           │ OAuth2 Client Credentials            │ OAuth2 Client Credentials
           ▼                                     ▼
    ┌────────────┐                       ┌────────────┐
    │  corebank  │◄──(internal only)────►│  corebank  │
    │  :8180     │                       │            │
    └────────────┘                       └────────────┘

    ┌────────────┐          ┌────────────┐
    │  Keycloak  │◄─JWKS───│ api-gateway│
    │  :8090     │          │  (startup) │
    └────────────┘          └────────────┘

    ┌────────────┐
    │   Redis    │◄─rate limit counter─── api-gateway
    │  :6379     │
    └────────────┘
```

> **Ghi chú:** `corebank` không có mũi tên từ `api-gateway` — hoàn toàn isolated, chỉ được gọi bởi `money-bank` và `profile-service` qua internal network với OAuth2 Client Credentials.

---

## 2. Request Processing Pipeline

Mỗi request qua gateway được xử lý tuần tự qua filter chain:

```
Incoming Request
      │
      ▼
┌─────────────────────────────────────────────────┐
│ Filter 1: CorrelationIdFilter (Order: -100)      │
│   • Đọc X-Correlation-Id từ request               │
│   • Nếu không có → sinh UUID v4                   │
│   • Set vào request context và response header    │
└──────────────────────┬──────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────┐
│ Filter 2: Security Filter (Spring Security)      │
│   [SR-01] JWT Validation                         │
│   • Extract Bearer token từ Authorization header │
│   • Verify signature qua Keycloak JWKS           │
│   • Verify iss, exp claims                       │
│   • Nếu invalid → 401 Unauthorized (stop here)   │
└──────────────────────┬──────────────────────────┘
                       │ (authenticated)
                       ▼
┌─────────────────────────────────────────────────┐
│ Filter 3: HeaderEnrichmentFilter (Order: -90)    │
│   [SR-02] Header Enrichment                      │
│   • Remove X-User-Id, X-User-Roles từ client     │
│   • Extract sub claim → set X-User-Id            │
│   • Extract realm_access.roles → X-User-Roles    │
└──────────────────────┬──────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────┐
│ Filter 4: RequestRateLimiterFilter (Order: -80)  │
│   [SR-04] Rate Limiting via Redis                │
│   • Key: authenticated → userId, else → IP       │
│   • Token bucket: replenish-rate / burst-capacity│
│   • Vượt ngưỡng → 429 Too Many Requests (stop)  │
└──────────────────────┬──────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────┐
│ Filter 5: Route Predicate Matching               │
│   [SR-03] Path-based Routing                     │
│   • /api/v1/money/** → money-bank                │
│   • /api/v1/cms/** → cms-backend                 │
│   • /api/v1/profiles/** → profile-service        │
│   • /api/v1/notifications/** → notification-svc  │
│   • Không khớp → 404                            │
└──────────────────────┬──────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────┐
│ Filter 6: StripPrefixFilter + ProxyFilter        │
│   • Strip /api/v1/{service} prefix               │
│   • Forward request + headers đến backend        │
│   • Stream response về client                    │
└──────────────────────┬──────────────────────────┘
                       │
                       ▼
              Backend Response
                       │
                       ▼
┌─────────────────────────────────────────────────┐
│ Filter 7: AccessLogFilter (post-filter)          │
│   [SR-10] Access Log                             │
│   • Log: method, path, status, latency, corr-id  │
└─────────────────────────────────────────────────┘
                       │
                       ▼
              Client Response
```

---

## 3. JWT Validation Flow Chi tiết

```
Gateway                           Keycloak
   │                                │
   │ ── startup ──────────────────► │
   │ ◄── JWKS (public keys) ─────── │
   │   (cache 5 minutes)            │
   │                                │
Client Request (Bearer JWT)         │
   │                                │
   │ 1. Extract JWT from header     │
   │ 2. Decode header → get "kid"   │
   │ 3. Look up key in JWKS cache   │
   │    │                           │
   │    ├── cache hit → verify sig  │
   │    └── cache miss ─────────── ►│
   │                    ◄── new JWKS│
   │ 4. Verify RS256 signature      │
   │ 5. Verify "iss" == realm URI   │
   │ 6. Verify "exp" not past       │
   │                                │
   │ → Authentication object        │
   │   (sub, roles, scopes)         │
```

**Claims được sử dụng:**

| Claim JWT | Dùng cho | Header inject |
|-----------|----------|---------------|
| `sub` | User ID | `X-User-Id` |
| `realm_access.roles` | User roles | `X-User-Roles` |
| `iss` | Validate issuer | — |
| `exp` | Validate expiry | — |
| `scope` | (future: fine-grained gateway authz) | — |

---

## 4. Rate Limiting Design

### 4.1. Token Bucket Algorithm (Spring Cloud Gateway built-in)

```
Per-User bucket (authenticated):
  replenish-rate:  5 tokens/second  (= 300/minute)
  burst-capacity: 10 tokens          (short burst allowed)

Per-IP bucket (unauthenticated / fallback):
  replenish-rate:  1 token/second   (= 60/minute)
  burst-capacity:  5 tokens
```

### 4.2. Redis Key Pattern

```
rate_limit:{userId}:{routeId}   → authenticated user bucket
rate_limit:ip:{clientIp}        → IP-based bucket (fallback)
```

### 4.3. Key Resolver Priority

```java
// Ưu tiên: userId (nếu đã authenticate) > IP
KeyResolver keyResolver = exchange -> {
    Authentication auth = exchange.getPrincipal()...;
    if (auth.isAuthenticated()) {
        return Mono.just("user:" + auth.getName());
    }
    return Mono.just("ip:" + exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
};
```

---

## 5. Routing Configuration

### 5.1. Route Definitions (application.yaml)

```yaml
spring:
  cloud:
    gateway:
      routes:
        # Route 1: Money Bank
        - id: money-bank-route
          uri: ${MONEY_BANK_URL:http://localhost:8181}
          predicates:
            - Path=/api/v1/money/**
          filters:
            - StripPrefix=3          # strip /api/v1/money
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 5
                redis-rate-limiter.burstCapacity: 10
                key-resolver: "#{@userOrIpKeyResolver}"

        # Route 2: CMS Backend
        - id: cms-backend-route
          uri: ${CMS_BACKEND_URL:http://localhost:8182}
          predicates:
            - Path=/api/v1/cms/**
          filters:
            - StripPrefix=3          # strip /api/v1/cms
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 5
                redis-rate-limiter.burstCapacity: 10
                key-resolver: "#{@userOrIpKeyResolver}"

        # Route 3: Profile Service
        - id: profile-service-route
          uri: ${PROFILE_SERVICE_URL:http://localhost:8183}
          predicates:
            - Path=/api/v1/profiles/**
          filters:
            - StripPrefix=3          # strip /api/v1/profiles

        # Route 4: Notification Service
        - id: notification-service-route
          uri: ${NOTIFICATION_SERVICE_URL:http://localhost:8184}
          predicates:
            - Path=/api/v1/notifications/**
          filters:
            - StripPrefix=3          # strip /api/v1/notifications
```

**Lưu ý `StripPrefix`:**
- `/api/v1/money/accounts` → strip 3 segments → `/accounts` → money-bank nhận `/accounts`
- money-bank expose API tại `/api/v1/accounts` — cần confirm với money-bank team về prefix convention

> **Quyết định cần chốt:** Money-bank hiện expose `/api/v1/...` — nếu gateway strip `/api/v1/money` thì backend nhận `/...` (không có `/api/v1`). Hai phương án:
> - **Option A (preferred):** Gateway dùng `RewritePath=/api/v1/money/(?<segment>.*), /api/v1/$\{segment}` — giữ nguyên `/api/v1/` prefix
> - **Option B:** Backend không dùng `/api/v1` prefix, gateway strip hoàn toàn

**Tạm thời chọn Option A** để không phải đổi backend:

```yaml
filters:
  - RewritePath=/api/v1/money/(?<segment>.*), /api/v1/$\{segment}
```

---

## 6. Security Configuration

### 6.1. Spring Security OAuth2 Resource Server

```
SecurityWebFilterChain:
  ├── CSRF: disabled (stateless REST API)
  ├── Session: STATELESS
  ├── Permit: /actuator/health
  ├── Require authenticated: /** (all other routes)
  └── oauth2ResourceServer:
        └── jwt:
              └── jwkSetUri: ${KEYCLOAK_ISSUER_URI}/protocol/openid-connect/certs
```

### 6.2. Header Security

Gateway tự động xóa các header nguy hiểm từ client request:
- `X-User-Id` (prevent spoofing)
- `X-User-Roles` (prevent privilege escalation)
- `X-Internal-*` (ngăn client giả mạo internal headers)

Sau đó inject lại từ JWT đã validate.

---

## 7. Observability

### 7.1. Logging Format (JSON structured)

```json
{
  "timestamp": "2026-09-22T10:15:30.123Z",
  "level": "INFO",
  "logger": "GatewayAccessLog",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "****1234",
  "method": "POST",
  "path": "/api/v1/money/transfers",
  "route": "money-bank-route",
  "statusCode": 200,
  "durationMs": 45,
  "userAgent": "SimulationBankApp/1.0"
}
```

### 7.2. Prometheus Metrics

| Metric | Type | Labels |
|--------|------|--------|
| `gateway_requests_total` | Counter | route, method, status |
| `gateway_request_duration_seconds` | Histogram | route, method |
| `gateway_rate_limit_hits_total` | Counter | key_type (user/ip) |
| `gateway_jwt_validation_errors_total` | Counter | error_type |

### 7.3. Distributed Tracing

- **Protocol:** OpenTelemetry (OTLP)
- **Propagation:** W3C TraceContext (`traceparent`, `tracestate` header)
- **Gateway tự động:** inject `traceparent` nếu client chưa có, forward nếu có
- **Backend services:** đọc `traceparent`, tạo child span

---

## 8. Project Structure

```
api-gateway/
├── docs/
│   └── inception/
│       ├── BRD.md
│       ├── SRD.md
│       └── HLD.md  (file này)
├── src/
│   └── main/
│       ├── java/
│       │   └── com/hieu/apigateway/
│       │       ├── ApiGatewayApplication.java
│       │       ├── config/
│       │       │   ├── SecurityConfig.java         # OAuth2 RS, permit actuator
│       │       │   ├── RateLimiterConfig.java       # KeyResolver bean (user/IP)
│       │       │   └── GatewayRoutesConfig.java     # Programmatic routes (alt yaml)
│       │       ├── filter/
│       │       │   ├── CorrelationIdFilter.java     # SR-05: inject/forward X-Correlation-Id
│       │       │   ├── HeaderEnrichmentFilter.java  # SR-02: inject X-User-Id, X-User-Roles
│       │       │   └── AccessLogFilter.java         # SR-10: structured access log
│       │       └── exception/
│       │           └── GatewayErrorHandler.java     # SR-06: JSON error envelope
│       └── resources/
│           ├── application.yaml                     # Routes, rate limit, keycloak config
│           └── application-local.yaml               # Local overrides
├── pom.xml
└── Dockerfile
```

### 8.1. Phân chia trách nhiệm

| Package/File | Trách nhiệm |
|-------------|-------------|
| `SecurityConfig` | JWT validation rules, permit list, deny-by-default |
| `RateLimiterConfig` | KeyResolver: user-based vs IP-based |
| `CorrelationIdFilter` | Đảm bảo mọi request có Correlation ID |
| `HeaderEnrichmentFilter` | Xóa client-injected headers, inject từ JWT |
| `AccessLogFilter` | Structured log per request/response |
| `GatewayErrorHandler` | JSON error response chuẩn cho 401/429/502/404 |
| `application.yaml` | Route definitions, rate limit config, backend URLs |

---

## 9. Deployment View

```
Docker Compose (development):
┌──────────────────────────────────────────────────────┐
│  docker network: simulation-bank-net                 │
│                                                      │
│  ┌─────────────┐   ┌──────────────┐                  │
│  │ api-gateway │   │   keycloak   │                  │
│  │  :8080      │   │   :8090      │                  │
│  └──────┬──────┘   └──────────────┘                  │
│         │                                            │
│    ┌────┴────────┬──────────────┬──────────────┐     │
│    ▼             ▼              ▼               ▼     │
│  money-bank  cms-backend  profile-svc  notification  │
│   :8181        :8182        :8183        :8184        │
│         └──────────────────────┘                     │
│                    │ internal only                    │
│                    ▼                                  │
│              corebank :8180                           │
│                                                      │
│  ┌─────────────┐                                     │
│  │    Redis    │ ← rate limit counters               │
│  │   :6379     │                                     │
│  └─────────────┘                                     │
└──────────────────────────────────────────────────────┘
```

---

## 10. Quyết định mở / Câu hỏi còn chờ

| ID | Câu hỏi | Ảnh hưởng | Ưu tiên |
|----|---------|-----------|---------|
| ~~OQ-GW-01~~ | ~~Path convention: backend có giữ `/api/v1/` prefix không?~~ | ~~Route config~~ | ✅ Chốt: giữ `/api/v1/`, dùng `RewritePath` |
| ~~OQ-GW-05~~ | ~~CORS config: frontend domain whitelist?~~ | ~~SR-11~~ | ✅ Chốt: Angular `:4200`, ReactJS `:3000`, Mobile không cần CORS |
| OQ-GW-02 | Rate limit threshold: 300 req/min per user có đủ chưa? | SR-04 | 🟠 P1 |
| OQ-GW-03 | Token revocation: có cần blacklist JWT tại Redis không? | SR-01 | 🟠 P1 |
| OQ-GW-04 | Circuit breaker tại gateway có cần MVP không? | Resilience | 🟡 P2 |
