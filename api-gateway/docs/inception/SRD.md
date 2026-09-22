# SRD — API Gateway
**Service:** `api-gateway`  
**Phiên bản:** 1.0.0  
**Ngày:** 2026-09-22  
**Trạng thái:** DRAFT  
**Tham chiếu:** BRD.md (BG-01 → BG-07), UC-GW-01 → UC-GW-03

---

## 1. Tổng quan kỹ thuật

API Gateway được xây dựng trên **Spring Cloud Gateway** (reactive, WebFlux) với **Spring Security OAuth2 Resource Server** để validate JWT do Keycloak ký. Gateway hoạt động hoàn toàn stateless ngoại trừ Redis cache dùng cho rate limiting.

**Stack:**
- Java 21 + Spring Boot 4.1.0
- Spring Cloud Gateway (reactive, WebFlux)
- Spring Security OAuth2 Resource Server (JWT validation via JWKS)
- Redis (rate limiting với RequestRateLimiter GatewayFilter)
- OpenTelemetry (distributed tracing)
- Micrometer + Prometheus (metrics)

---

## 2. Yêu cầu chức năng

### SR-01: JWT Authentication

| ID | Yêu cầu |
|----|---------|
| SR-01-1 | Gateway validate chữ ký JWT bằng public key lấy từ Keycloak JWKS endpoint |
| SR-01-2 | JWKS keys được cache, tự làm mới định kỳ (mặc định 5 phút) hoặc khi nhận `kid` không nhận ra |
| SR-01-3 | Validate `iss` claim phải khớp với `${KEYCLOAK_ISSUER_URI}` (realm `simulation-bank`) |
| SR-01-4 | Validate `exp` claim — JWT hết hạn trả `401` |
| SR-01-5 | Mọi route đều yêu cầu authentication — không có public endpoint ngoài `/actuator/health` |
| SR-01-6 | JWT không được lưu cache tại gateway; mỗi request validate lại |

### SR-02: Header Enrichment

| ID | Yêu cầu |
|----|---------|
| SR-02-1 | Sau khi validate JWT thành công, gateway inject header `X-User-Id` với giá trị claim `sub` |
| SR-02-2 | Gateway inject header `X-User-Roles` với giá trị là danh sách role từ `realm_access.roles`, phân cách bằng dấu phẩy |
| SR-02-3 | Header `Authorization: Bearer <jwt>` được **forward** xuống backend service (service vẫn có thể validate lại nếu cần) |
| SR-02-4 | Client không thể tự inject `X-User-Id` hoặc `X-User-Roles` — gateway phải xóa bất kỳ header này từ client request trước khi đặt lại |

### SR-03: Routing

| ID | Yêu cầu |
|----|---------|
| SR-03-1 | Route `/api/v1/money/**` → `money-bank` (http://money-bank:8181), strip prefix `/api/v1/money` |
| SR-03-2 | Route `/api/v1/cms/**` → `cms-backend` (http://cms-backend:8182), strip prefix `/api/v1/cms` |
| SR-03-3 | Route `/api/v1/profiles/**` → `profile-service` (http://profile-service:8183), strip prefix `/api/v1/profiles` |
| SR-03-4 | Route `/api/v1/notifications/**` → `notification-service` (http://notification-service:8184), strip prefix `/api/v1/notifications` |
| SR-03-5 | `corebank` không có route — không exposed qua gateway |
| SR-03-6 | Route không khớp → `404 Not Found` với error envelope chuẩn |

**Bảng routing chi tiết:**

| Gateway Path | Backend Service | Backend Path | Ghi chú |
|-------------|-----------------|-------------|---------|
| `/api/v1/money/**` | `money-bank:8181` | `/api/v1/**` | Chuyển tiền, tài khoản, thẻ |
| `/api/v1/cms/**` | `cms-backend:8182` | `/api/v1/**` | Quản trị nội bộ |
| `/api/v1/profiles/**` | `profile-service:8183` | `/api/v1/**` | Hồ sơ người dùng |
| `/api/v1/notifications/**` | `notification-service:8184` | `/api/v1/**` | Thông báo |
| `/actuator/health` | (gateway itself) | — | Health check, public |

### SR-04: Rate Limiting (Infrastructure Layer)

| ID | Yêu cầu |
|----|---------|
| SR-04-1 | Rate limit **per IP** (unauthenticated requests hoặc fallback): 60 request/phút |
| SR-04-2 | Rate limit **per User-Id** (authenticated): 300 request/phút |
| SR-04-3 | Rate limit storage: Redis (Lettuce client, cluster-ready) |
| SR-04-4 | Vượt ngưỡng → `429 Too Many Requests` với header `Retry-After` |
| SR-04-5 | Rate limit header trả về: `X-RateLimit-Remaining`, `X-RateLimit-Limit`, `X-RateLimit-Reset` |
| SR-04-6 | Rate limit **không** áp dụng cho `/actuator/health` |

**Lưu ý:** Rate limit này là tầng infrastructure. Tầng business-level (vd: tối đa 5 lần chuyển tiền/ngày) do từng service tự quản lý bằng DB counter.

### SR-05: Correlation Tracking

| ID | Yêu cầu |
|----|---------|
| SR-05-1 | Mỗi request phải có `X-Correlation-Id`. Nếu client gửi kèm → giữ nguyên. Nếu không → gateway sinh UUID v4 |
| SR-05-2 | `X-Correlation-Id` được forward xuống backend service |
| SR-05-3 | `X-Correlation-Id` xuất hiện trong response về client |
| SR-05-4 | `X-Correlation-Id` được log tại gateway ở mỗi request/response |

### SR-06: Error Handling

| ID | Yêu cầu |
|----|---------|
| SR-06-1 | Lỗi authentication (401) trả JSON envelope: `{"code": "AUTH_001", "message": "Token invalid or expired"}` |
| SR-06-2 | Lỗi rate limit (429) trả JSON envelope: `{"code": "GW_429", "message": "Too many requests"}` |
| SR-06-3 | Backend không khả dụng (502/503) trả JSON: `{"code": "GW_502", "message": "Service temporarily unavailable"}` |
| SR-06-4 | Response Content-Type luôn là `application/json` cho error |

### SR-11: CORS (Cross-Origin Resource Sharing)

| ID | Yêu cầu |
|----|---------|
| SR-11-1 | CORS xử lý tập trung tại gateway — backend service **không** cần config CORS riêng |
| SR-11-2 | Allowed origins: `http://localhost:4200` (CMS Angular local), `http://localhost:3000` (Profile ReactJS local), `${CMS_FRONTEND_ORIGIN}` (production), `${PROFILE_FRONTEND_ORIGIN}` (production) |
| SR-11-3 | Mobile App (React Native / Native) không cần whitelist CORS — không chạy trong browser, không có same-origin policy |
| SR-11-4 | `allowCredentials: true` — bắt buộc để browser gửi `Authorization: Bearer` header |
| SR-11-5 | Không dùng wildcard `*` cho `allowedOrigins` khi `allowCredentials=true` — browser block |
| SR-11-6 | Allowed methods: GET, POST, PUT, PATCH, DELETE, OPTIONS |
| SR-11-7 | OPTIONS preflight request phải được permit (không cần JWT) — Spring Security layer phải xử lý trước khi check auth |
| SR-11-8 | Exposed headers về client: `X-Correlation-Id`, `X-RateLimit-Remaining`, `X-RateLimit-Limit`, `X-RateLimit-Reset` |
| SR-11-9 | Preflight cache (`Access-Control-Max-Age`): 3600 giây (1 giờ) — giảm OPTIONS round-trip |
| SR-11-10 | Production origins được inject qua env var `CMS_FRONTEND_ORIGIN` và `PROFILE_FRONTEND_ORIGIN` — không hardcode |

**Nguồn gốc yêu cầu:**
```
Browser Clients:
  ┌─────────────────────────────┐
  │ CMS Frontend (Angular)      │ origin: http://localhost:4200 (dev)
  │                             │         $CMS_FRONTEND_ORIGIN (prod)
  ├─────────────────────────────┤
  │ Profile Frontend (ReactJS)  │ origin: http://localhost:3000 (dev)
  │                             │         $PROFILE_FRONTEND_ORIGIN (prod)
  └─────────────────────────────┘
       ↓ CORS preflight (OPTIONS) + actual request
  API Gateway (xử lý CORS ở đây)
       ↓ Forward (no CORS header needed internally)
  Backend Services

Non-browser:
  Mobile App → gọi trực tiếp, không có same-origin restriction → không cần CORS
```

### SR-07: Performance

| ID | Yêu cầu |
|----|---------|
| SR-07-1 | P99 latency overhead của gateway (không tính backend) ≤ 10ms |
| SR-07-2 | Gateway không block trên JWT validation — dùng non-blocking JWKS fetch |
| SR-07-3 | WebClient/WebFlux reactive — không dùng blocking I/O |

### SR-08: Scalability & Availability

| ID | Yêu cầu |
|----|---------|
| SR-08-1 | Gateway stateless — có thể scale horizontal (nhiều pod/container) |
| SR-08-2 | State duy nhất là Redis (rate limit counter) — shared giữa các instance |
| SR-08-3 | Gateway có liveness probe (`/actuator/health/liveness`) và readiness probe (`/actuator/health/readiness`) |

### SR-09: Security

| ID | Yêu cầu |
|----|---------|
| SR-09-1 | HTTPS bắt buộc ở môi trường production (TLS termination tại load balancer hoặc gateway) |
| SR-09-2 | Gateway xóa header `X-User-Id`, `X-User-Roles` từ incoming request của client trước khi tự inject |
| SR-09-3 | JWKS public key cache không lưu private key |
| SR-09-4 | Log không ghi JWT token value |

### SR-10: Observability

| ID | Yêu cầu |
|----|---------|
| SR-10-1 | Mỗi request log: method, path, status, latency, correlation-id, user-id (masked last 4 chars) |
| SR-10-2 | Distributed tracing với OpenTelemetry, propagate `traceparent` header xuống backend |
| SR-10-3 | Prometheus metrics endpoint tại `/actuator/prometheus` |
| SR-10-4 | Metric quan trọng: `gateway_requests_total`, `gateway_request_duration_seconds`, `gateway_rate_limit_hits_total` |

---

## 4. Cấu hình môi trường

### 4.1. Environment Variables

| Biến | Mô tả | Default (local) |
|------|-------|-----------------|
| `KEYCLOAK_ISSUER_URI` | Keycloak realm issuer URI | `http://localhost:8080/realms/simulation-bank` |
| `REDIS_HOST` | Redis host cho rate limiting | `localhost` |
| `REDIS_PORT` | Redis port | `6379` |
| `MONEY_BANK_URL` | Internal URL của money-bank | `http://localhost:8181` |
| `CMS_BACKEND_URL` | Internal URL của cms-backend | `http://localhost:8182` |
| `PROFILE_SERVICE_URL` | Internal URL của profile-service | `http://localhost:8183` |
| `NOTIFICATION_SERVICE_URL` | Internal URL của notification-service | `http://localhost:8184` |
| `RATE_LIMIT_PER_IP` | Requests/phút per IP | `60` |
| `RATE_LIMIT_PER_USER` | Requests/phút per User | `300` |

### 4.2. Ports

| Service | Port |
|---------|------|
| `api-gateway` | `8080` (public facing) |
| Keycloak | `8090` (internal) |

---

## 5. Giao tiếp với các thành phần khác

### 5.1. Gateway → Keycloak (JWKS fetch)

- **Protocol:** HTTP GET
- **Endpoint:** `${KEYCLOAK_ISSUER_URI}/protocol/openid-connect/certs`
- **Tần suất:** Startup + cache miss + định kỳ refresh
- **Blocking:** Không — Spring Security reactive JWKS resolver

### 5.2. Gateway → Backend Services

- **Protocol:** HTTP (internal network, Docker network)
- **Timeout:** connect 3s, read 30s (configurable per route)
- **Circuit breaker:** Resilience4j (future scope — không bắt buộc MVP)

### 5.3. Gateway → Redis

- **Client:** Lettuce (non-blocking)
- **Dùng cho:** RequestRateLimiter filter
- **Pattern:** Token bucket per key (Spring Cloud Gateway built-in)

---

## 6. Quyết định thiết kế và Phương án thay thế

### 6.1. JWT Validation: Direct JWKS vs Introspection

**Chọn:** Direct JWKS validation  
**Lý do:** Keycloak ký JWT bằng RS256 với key pair riêng. Gateway chỉ cần JWKS public key để verify chữ ký — không cần round-trip đến Keycloak cho mỗi request. Giảm latency và dependency runtime vào Keycloak.

**Khi nào dùng Introspection:** Nếu cần revoke token ngay lập tức (vd: logout) — đây là trade-off đã chấp nhận trong MVP.

### 6.2. Header Enrichment vs JWT Forwarding Only

**Chọn:** Header enrichment (inject `X-User-Id`, `X-User-Roles`)  
**Lý do:** Service nội bộ không cần tự parse JWT, giảm coupling với JWT structure. Service chỉ đọc header — dễ test và dễ thay JWT provider tương lai.

### 6.3. Rate Limiting: Gateway vs Service-level

**Chọn:** Hybrid — Gateway (infra, Redis) + Service (business, DB)  
**Phân chia:**
- Gateway: per-IP flood protection, per-User global throttle
- Service: per-feature business quota (vd: max 5 transfers/day)

---

## 7. Ràng buộc kỹ thuật

- Spring Cloud Gateway yêu cầu reactive stack — không mix với servlet stack.
- `RequestRateLimiter` GatewayFilter tích hợp sẵn với Redis — không cần custom implementation cho MVP.
- Keycloak realm `simulation-bank` phải cấu hình sẵn trước khi gateway start (JWKS fetch lúc startup).
- Gateway **không** tự tạo hoặc sign JWT — chỉ validate.
