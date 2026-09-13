# Feature Implementation Plan (Money Bank)

Tài liệu này đóng vai trò là Checklist tổng thể các tính năng (Features) cần phát triển cho dịch vụ `money-bank`. Danh sách này sẽ liên tục được cập nhật theo tiến trình.

## Phase 1: Nền tảng (Foundation)
- [x] **F-01: Khởi tạo Project (U-01)**
  - Sinh cấu trúc thư mục, `pom.xml`, cấu hình ArchUnit, Logback JSON.
- [ ] **F-02: Security Foundation (U-02)**
  - Cấu hình Spring Security, OAuth2 Resource Server.
  - Tích hợp Keycloak JWT, thiết lập Identity Context, Deny-by-default.
- [ ] **F-03: Database Foundation (U-03)**
  - Cấu hình kết nối **MongoDB**.
  - Thiết lập Spring Data MongoDB, auditing, transaction manager (ACID).

## Phase 2: Core Nghiệp vụ Chuyển tiền (Transfer Core)
- [ ] **F-04: Idempotency Core (U-04)**
  - Lớp 1: Redis Fast-path Lock.
  - Lớp 2: MongoDB Unique Index (Durable Idempotency).
- [ ] **F-05: State Machine & Chuyển tiền (U-05)**
  - Định nghĩa Entity `Proposal` và `Transfer` trên MongoDB.
  - Giao tiếp với Corebank qua API/Kafka.
  - Tích hợp Uber Cadence/Temporal cho luồng Retry.
- [ ] **F-06: Outbox Pattern & Event Sourcing (U-06)**
  - Lưu event thay đổi trạng thái vào collection Outbox trên MongoDB.
  - Publish message lên Kafka (cho Notification/Profile).

## Phase 3: Vận hành & Mở rộng (Ops & Ext)
- [ ] **F-07: API Chức năng nâng cao**
  - Tra cứu lịch sử, Filter, Pagination.
- [ ] **F-08: Portal/CMS Integration**
  - Xây dựng API cho Giao dịch viên (GDV) duyệt các giao dịch `PENDING`/`MANUAL_REVIEW`.
- [ ] **F-09: Tích hợp Metric & Alert (U-13)**
  - Cấu hình Prometheus, Grafana Alert.
