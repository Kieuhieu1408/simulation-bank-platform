# BRD — API Gateway
**Service:** `api-gateway`  
**Phiên bản:** 1.0.0  
**Ngày:** 2026-09-22  
**Trạng thái:** DRAFT

---

## 1. Bối cảnh và Động lực

### 1.1. Vấn đề hiện tại

Simulation Bank Platform đang phát triển theo kiến trúc microservice với nhiều service độc lập:
`money-bank` (port 8181), `corebank` (port 8180), `cms-backend` (port 8182), `profile-service` (port 8183), `notification-service` (port 8184).

Khi không có API Gateway, client (Mobile App, CMS Frontend, External System) phải:

| Vấn đề | Hệ quả |
|--------|--------|
| Gọi trực tiếp nhiều port khác nhau | Client phải biết toàn bộ topology nội bộ |
| Mỗi service tự xử lý JWT validation | Logic trùng lặp, dễ cấu hình sai, khó audit |
| Không có điểm kiểm soát rate limit thống nhất | Dễ bị abuse, DDoS tầng infra |
| Không có cross-cutting logging/tracing | Khó debug luồng request xuyên service |
| Client bị ảnh hưởng khi service di chuyển port/host | Tight coupling giữa client và infra |

### 1.2. Lý do đặt API Gateway

- **Single Entry Point**: Tất cả traffic từ bên ngoài đi qua một điểm duy nhất.
- **Security Enforcement**: JWT validation, authentication tập trung — service nội bộ không cần tự lo.
- **Operational Control**: Rate limiting, circuit breaking, logging, tracing nhất quán.
- **Backend for Frontend (BFF-like)**: Routing đúng service dựa trên path prefix, không expose internal topology.

> **Lưu ý quan trọng:** `corebank` là internal-only ledger. API Gateway **không** expose `corebank` ra ngoài. Chỉ `money-bank` và `profile-service` được phép gọi `corebank` qua internal network.

---

## 2. Mục tiêu nghiệp vụ

| Mã | Mục tiêu | Mức độ |
|----|----------|--------|
| BG-01 | Cung cấp một URL entry point duy nhất cho tất cả client | MUST |
| BG-02 | Xác thực danh tính người dùng trước khi request đến service | MUST |
| BG-03 | Kiểm soát tốc độ gọi API để bảo vệ hệ thống | MUST |
| BG-04 | Định tuyến request đến đúng backend service | MUST |
| BG-05 | Cung cấp log và trace thống nhất cho toàn bộ request | SHOULD |
| BG-06 | Hỗ trợ mở rộng service mới mà không ảnh hưởng client | SHOULD |
| BG-07 | Không trở thành single point of failure (horizontal scalable) | MUST |

---

## 3. Stakeholder và Người dùng

| Stakeholder | Vai trò | Yêu cầu chính |
|-------------|---------|----------------|
| Mobile App / Web App | Client cuối gọi API Money Bank | Một URL duy nhất, response nhanh |
| CMS Frontend (Angular) | Admin gọi API quản trị | Auth đồng nhất, phân quyền rõ ràng |
| Backend Service Teams | Dev các service nội bộ | Không cần tự handle JWT, nhận sẵn user context qua header |
| DevOps / Platform | Vận hành hệ thống | Rate limit, monitoring, không downtime khi scale |
| External Systems (future) | Third party tích hợp | API key hoặc OAuth2 machine-to-machine (future scope) |

---

## 4. Phạm vi

### 4.1. Trong phạm vi (MVP)

- JWT validation qua Keycloak JWKS public key (direct, không introspect)
- Routing dựa trên path prefix đến các service: `money-bank`, `cms-backend`, `profile-service`, `notification-service`
- Header enrichment: inject `X-User-Id`, `X-User-Roles` sau khi validate JWT
- Rate limiting tầng infrastructure: per-IP và per-User-Id (Redis-backed)
- Request/response logging với correlation ID (`X-Correlation-Id`)
- Distributed tracing (OpenTelemetry)
- Health check endpoint (`/actuator/health`)

### 4.2. Ngoài phạm vi (không làm trong MVP)

- **API Key management** cho external party (future: Phase 3 Paygate)
- **WAF (Web Application Firewall)** — để sau khi có CloudFront/Nginx ở tầng trên
- **Request transformation / aggregation** (BFF pattern nâng cao)
- **GraphQL gateway**
- **Expose `corebank`** qua gateway — tuyệt đối không làm

---

## 5. Use Case Cốt lõi

### UC-GW-01: Người dùng gọi Money Bank API

**Actor:** Mobile App / Web App  
**Tiền điều kiện:** Người dùng đã đăng nhập, có JWT hợp lệ từ Keycloak  
**Luồng chính:**
1. Client gửi `GET /api/v1/money/accounts` với `Authorization: Bearer <jwt>`
2. Gateway validate JWT (chữ ký, issuer, expiry) qua Keycloak JWKS
3. Gateway extract `sub` (userId) và `realm_access.roles` từ JWT
4. Gateway inject header `X-User-Id: <sub>` và `X-User-Roles: <roles>` vào request
5. Gateway forward request đến `money-bank:8181/api/v1/accounts`
6. `money-bank` xử lý, trả response
7. Gateway forward response về client

**Luồng ngoại lệ:**
- JWT hết hạn hoặc chữ ký sai → Gateway trả `401 Unauthorized`, không forward
- User không có role phù hợp → Service tự xử lý `403 Forbidden` (Gateway không làm RBAC tầng nghiệp vụ)
- Rate limit vượt ngưỡng → Gateway trả `429 Too Many Requests`

### UC-GW-02: CMS Admin gọi CMS API

**Actor:** CMS Frontend (Angular, PKCE flow)  
**Tiền điều kiện:** Admin đã đăng nhập, JWT có role `cms-operator` hoặc `cms-admin`  
**Luồng chính:**
1. Frontend gửi request đến `/api/v1/cms/**`
2. Gateway validate JWT → inject header → route đến `cms-backend`
3. `cms-backend` kiểm tra RBAC nghiệp vụ bằng `@CmsAuthorization(menuCode, action)`

### UC-GW-03: Service nội bộ gọi lẫn nhau

**Actor:** `money-bank` gọi `profile-service`  
**Luồng:** Internal network, bypass gateway — không qua gateway  
**Ghi chú:** Service-to-service dùng OAuth2 Client Credentials trực tiếp, không cần gateway

---

## 6. Yêu cầu phi chức năng cấp cao

| Yêu cầu | Mục tiêu |
|---------|----------|
| Latency overhead | < 10ms p99 cho routing + JWT validation |
| Availability | 99.9% (horizontal scale, no SPOF) |
| Rate limit granularity | Per-IP (anonymous) và Per-User-Id (authenticated) |
| Security | Không cache JWT, JWKS refresh định kỳ |
| Observability | Mỗi request có `X-Correlation-Id` được log và trace |

---

## 7. Ràng buộc và Quyết định đã chốt

| Quyết định | Lý do |
|-----------|-------|
| Dùng **Spring Cloud Gateway** | Cùng hệ sinh thái Spring Boot, hỗ trợ tốt WebFlux, reactive, dễ tích hợp Spring Security OAuth2 |
| Validate JWT **trực tiếp** qua JWKS (không introspect) | Keycloak ký JWT bằng public key — gateway chỉ cần JWKS endpoint để verify, giảm latency so với introspection round-trip |
| Keycloak realm: `simulation-bank` | Thống nhất toàn platform, không dùng realm `master` |
| **Không** expose `corebank` qua gateway | Corebank là internal ledger, chỉ `money-bank` và `profile-service` được gọi |
| Rate limiting tại **gateway** + tại **service** (hybrid) | Gateway: infra-level (Redis, per-IP/User) — Service: business-level (DB, per-day quota) |
| Header enrichment tại gateway | Service nội bộ đọc `X-User-Id`/`X-User-Roles` thay vì tự parse JWT |

---

## 8. Tiêu chí thành công

| Tiêu chí | Cách đo |
|---------|---------|
| Tất cả request có JWT hợp lệ đều được route đúng service | Integration test: gửi valid JWT, kiểm tra response từ đúng service |
| Request thiếu/sai JWT trả `401` tại gateway | Test: no token, expired token, wrong issuer → 401 |
| Rate limit hoạt động | Load test: vượt threshold → 429 trong < 1s |
| Latency gateway overhead < 10ms | Benchmark: so sánh direct call vs gateway call |
| Header `X-User-Id` có mặt tại service | Service log kiểm tra header |
