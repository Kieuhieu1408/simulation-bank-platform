# Feature Roadmap — Simulation Bank Platform

> **Phiên bản:** 1.0.0
> **Cập nhật lần cuối:** 2026-09-09
> **Mục đích:** Quản lý tiến độ theo **tính năng nghiệp vụ xuyên service** — đọc file này để biết đang làm tính năng gì, service nào đang active, unblock tiếp theo là gì.
>
> 📌 **Quy ước trạng thái:**
> - `🔲 BACKLOG` — Chưa bắt đầu
> - `🔵 INCEPTION` — Đang phân tích / thiết kế
> - `🟡 IN PROGRESS` — Đang triển khai code
> - `✅ DONE` — Hoàn thành, đã test
> - `⏸️ BLOCKED` — Bị chặn, cần unblock trước

---

## 🗺️ Bản đồ phụ thuộc tổng quan

```
[BASE-01: Service Foundation]
         |
         ├──> [BASE-02: Auth & Identity (Profile + Token Exchange)]
         |
         ├──> [FEAT-01: Chuyển tiền nội bộ]
         |         |
         |         ├── corebank (ledger core) ✅ MVP done
         |         ├── money-bank (Transfer flow) 🟡
         |         ├── profile-service (CIF lookup) 🔵
         |         └── notification-service (event) 🔲
         |
         └──> [FEAT-02: Quản lý thông tin người dùng]
                   └── profile-service (CRUD user/merchant) 🔲
```

---

## PHASE 0 — Cơ sở hạ tầng chung (Infrastructure)

> Làm một lần, dùng cho tất cả tính năng. Không có tính năng nào chạy được nếu thiếu phase này.

### BASE-00: common-service (Shared Library)
**Trạng thái:** ✅ DONE  
**Service liên quan:** Tất cả (Maven dependency)

| Task | Trạng thái | Ghi chú |
|---|---|---|
| DTO, Envelope, Error codes, Currency, CorrelationId | ✅ | Đã build thành `common-service-1.0.0.jar` |

---

### BASE-01: Project Skeleton & Observability

> Mỗi service cần có khung project chuẩn trước khi làm bất kỳ tính năng nào.

| Service | Task | Trạng thái | Ghi chú |
|---|---|---|---|
| `money-bank` | Khung project (hexagonal), logging JSON, ArchUnit, error envelope | ✅ DONE | U-01 hoàn thành |
| `money-bank` | Observability: Prometheus metrics, health liveness/readiness | 🔲 BACKLOG | U-13 |
| `corebank` | Khung project (Layered), logging, error handler | ✅ DONE | MVP đã có |
| `corebank` | Flyway schema migration (thay `ddl-auto: update`) | 🔲 BACKLOG | Technical debt |
| `profile-service` | Khởi tạo Spring Boot project | 🔲 BACKLOG | Chưa có code |
| `api-gateway` | Khởi tạo Spring Cloud Gateway | 🔲 BACKLOG | Sau khi service đầu tiên sẵn sàng |
| `notification-service` | Khởi tạo Spring Boot + Kafka consumer | 🔲 BACKLOG | Sau khi Kafka contract chốt |

---

### BASE-02: Auth & Identity Foundation

> **Prerequisite của mọi tính năng có user context.** Phải xong trước FEAT-01.

| Service | Task | Trạng thái | Ghi chú |
|---|---|---|---|
| `money-bank` | OAuth2 Resource Server, SecurityConfig, IdentityContext, deny-by-default | ✅ DONE | U-02 hoàn thành (2026-09-09) |
| `money-bank` | PoC Token Exchange V2 (Keycloak → profile-service) | 🔲 BACKLOG | U-03 — cần Keycloak test env |
| `profile-service` | IAM: JWT issuer, RBAC, CIF lookup API | 🔵 INCEPTION | Requirements Analysis chưa hoàn thành |
| `corebank` | Security layer (network policy / service-to-service auth) | 🔲 BACKLOG | Hiện tại open internal only |
| `api-gateway` | JWT forward, rate limiting, WAF | 🔲 BACKLOG | Phụ thuộc Keycloak realm config |

---

## PHASE 1 — Tính năng: Chuyển tiền nội bộ

> **Mô tả:** Người dùng đăng nhập qua mobile/web → tạo Proposal → xác nhận chuyển tiền → Corebank ghi sổ cái → Notification gửi biến động số dư.
>
> **Prerequisite:** BASE-01 (skeleton) + BASE-02 (auth) của tất cả service liên quan phải xong.

### Luồng end-to-end

```
Mobile/Web
   |
   v
api-gateway (JWT forward)
   |
   v
money-bank
   ├── Proposal API (tạo, duyệt)
   ├── Transfer API (execute, idempotency, state machine)
   └── Corebank Adapter (gọi corebank để debit/credit)
          |
          v
       corebank
          ├── Transfer API (atomic debit/credit, idempotency)
          └── Event → Outbox
                    |
                    v
             notification-service
                    └── Gửi SMS/Email biến động số dư
```

### FEAT-01A: Corebank — Ledger Core (đã có MVP)

**Trạng thái tổng:** 🟡 IN PROGRESS (cần cải thiện)  
**Service:** `corebank`

| Task | Trạng thái | Unit/Ref |
|---|---|---|
| API: Customer (CIF), Account, Card, Transfer, History | ✅ DONE | MVP |
| Idempotency via Unique Constraint (Transfer) | ✅ DONE | MVP |
| Pessimistic Lock + deadlock prevention | ✅ DONE | MVP |
| Đồng bộ kiến trúc CQRS Pipeline Behaviors | ✅ DONE | 2026-09-15 |
| Reverse Engineering & aidlc-docs | ✅ DONE | 2026-09-09 |
| Flyway schema migration | 🔲 BACKLOG | Cần làm trước production |
| Test coverage: TransferService unit + IT | 🔲 BACKLOG | Hiện tại 0% |
| Outbox/Kafka: publish event khi transfer thành công | 🔲 BACKLOG | Cần sau khi Kafka chốt |
| Security layer (service-to-service) | 🔲 BACKLOG | — |

### FEAT-01B: Money-Bank — Transfer Flow

**Trạng thái tổng:** 🟡 IN PROGRESS  
**Service:** `money-bank`

| Task | Trạng thái | Unit/Ref |
|---|---|---|
| Project skeleton, logging, ArchUnit | ✅ DONE | U-01 |
| Security Foundation (OAuth2 Resource Server) | ✅ DONE | U-02 (2026-09-09) |
| MongoDB Foundation (domain data layer) | ✅ DONE | U-03 |
| CQRS Pipeline & Idempotency Refactoring | ✅ DONE | U-04 |
| Corebank Adapter + Resilience4j (Circuit Breaker) | ✅ DONE | U-05 |
| Transfer Command (Proposal entity, state machine, domain invariant) | 🔲 BACKLOG | U-06 |
| Outbox + Kafka publisher | 🔲 BACKLOG | U-07 |
| Kafka Inbox consumer | 🔲 BACKLOG | U-08 |
| Reconciliation Job (retry, MANUAL_REVIEW) | 🔲 BACKLOG | U-09 |
| Proposal Facade (token exchange + profile lookup) | ⏸️ BLOCKED | U-10 — chờ Profile API contract |
| Query side (GET transfers, pagination, masking) | 🔲 BACKLOG | U-11 |

### FEAT-01C: Profile-Service — CIF & Identity Lookup

**Trạng thái tổng:** 🔵 INCEPTION  
**Service:** `profile-service`

> ⚠️ **Người dùng hiện tại import thủ công** — FEAT-01C chỉ cần làm API lookup để money-bank gọi được, không cần full CRUD user management ngay.

| Task | Trạng thái | Ghi chú |
|---|---|---|
| Workspace Detection | ✅ DONE | |
| Requirements Analysis | 🔵 IN PROGRESS | Chưa hoàn thành |
| API: Lookup CIF/customer info (internal, service-to-service) | 🔲 BACKLOG | Prerequisite của U-10 money-bank |
| IAM / Token Exchange endpoint | 🔲 BACKLOG | Prerequisite của U-03 money-bank |
| RBAC, phân quyền | 🔲 BACKLOG | Sau khi lookup API xong |

### FEAT-01D: Notification-Service — Biến động số dư

**Trạng thái tổng:** 🔲 BACKLOG  
**Service:** `notification-service`

| Task | Trạng thái | Ghi chú |
|---|---|---|
| Khởi tạo project | 🔲 BACKLOG | |
| Kafka consumer: lắng nghe event transfer từ corebank/money-bank | 🔲 BACKLOG | Chờ Kafka event contract chốt |
| Strategy: SMS / Email / In-app push | 🔲 BACKLOG | |

---

## PHASE 2 — Tính năng: Quản lý thông tin người dùng

> **Mô tả:** CRUD profile khách hàng cá nhân và merchant. Phase này làm **sau** khi FEAT-01 hoạt động ổn định.

**Trạng thái tổng:** 🔲 BACKLOG  
**Service:** `profile-service`

| Task | Trạng thái | Ghi chú |
|---|---|---|
| API: CRUD user profile (name, phone, email, KYC status) | 🔲 BACKLOG | |
| API: CRUD merchant profile | 🔲 BACKLOG | |
| Upload KYC document (future: media-service) | 🔲 BACKLOG | |

---

## PHASE 3 — Tính năng: Cổng thanh toán (Paygate)

**Trạng thái tổng:** 🔲 BACKLOG  
**Service:** `paygate`, `corebank`

> Bắt đầu sau khi FEAT-01 hoàn thành và ổn định.

---

## PHASE 4 — Tính năng: Back-office CMS

**Trạng thái tổng:** 🔲 BACKLOG  
**Service:** `cms`, tất cả service

---

## 🔑 Unblock Tracker — Những gì cần quyết định ngay

| ID | Vấn đề cần chốt | Ảnh hưởng | Ưu tiên |
|---|---|---|---|
| OQ-P0-01 | Cơ chế transfer nguyên tử: có cần state `RESERVED/COMMITTING` không? | U-06 money-bank | 🔴 P0 |
| OQ-P0-02 | Query Corebank theo reference ID (để reconciliation) | U-09 money-bank | 🔴 P0 |
| OQ-P0-03 | Phân loại lỗi Corebank: final / retryable / ambiguous | U-05, U-06 | 🔴 P0 |
| OQ-P1-01 | Kafka event contract (topic, schema, partition key) | U-07 money-bank, Corebank Outbox, Notification | 🟠 P1 |
| OQ-P1-02 | Profile Service API contract (internal endpoint) | U-10 money-bank | 🟠 P1 |
| OQ-P1-03 | Keycloak realm config cho test environment | U-03 money-bank | 🟠 P1 |

---

## 📍 Focus hiện tại (2026-09-15)

### 🟡 Đang làm
| Service | Task | Người phụ trách |
|---|---|---|
| `money-bank` | **U-06: Transfer Command & State Machine** — Functional Design | @dlc |
| `profile-service` | **Requirements Analysis** — đang dang dở | @dlc |

### ⏸️ Chờ quyết định
| Quyết định | Tác động |
|---|---|
| Chốt Data Model cho Proposal & Transfer State Machine | Unblock Code Gen U-06 |
