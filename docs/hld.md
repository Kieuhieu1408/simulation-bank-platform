# High-Level Design (HLD) - Simulation Bank Platform

## 1. Kiến trúc hệ thống tổng quan (System Architecture)
Hệ thống tuân theo các triết lý thiết kế hiện đại như Domain-Driven Design (DDD), Clean Architecture, CQRS, và Event Sourcing, được chia thành các lớp chính:
- **Edge & Ingress Layer:** Cloudflare Tunnel ➔ Nginx Edge LB ➔ Spring Cloud API Gateway.
- **Security & Identity Layer:** Keycloak (IAM cho OIDC/OAuth2) và HashiCorp Vault (Quản lý secrets).
- **Core Microservices Layer:** Corebank (sổ cái chính, Oracle DB), Money-Bank (luồng giao dịch tiền, MongoDB), Profile Service (thông tin user), CMS (hệ thống admin).
- **Event-Driven & Async Layer:** Apache Kafka kết hợp với Outbox Pattern.
- **Observability Layer:** OpenTelemetry Collector, Prometheus (Metrics), Loki (Logs), Jaeger (Traces), Grafana.

## 2. Mô tả chức năng các Services
- **api-gateway:** Cổng giao tiếp duy nhất ra bên ngoài. Chịu trách nhiệm kiểm tra hợp lệ JWT Token, xử lý CORS, Rate Limiting, Header Enrichment (chuyển đổi thông tin token thành header nội bộ truyền xuống backend).
- **corebank:** Service lõi hoạt động như sổ cái ngân hàng (Double-entry accounting). Quản lý dữ liệu gốc thông qua CQRS và Event Sourcing. Sử dụng Optimistic Locking (thay vì Pessimistic Locking) để tránh deadlocks và tăng hiệu năng.
- **money-bank:** Dịch vụ quản lý các nghiệp vụ hướng khách hàng như Proposal Chuyển tiền, phối hợp giao dịch qua State Machine trước khi tương tác với corebank.
- **profile-service:** Quản lý thông tin CIF, định danh, phân quyền RBAC và quản lý thông tin merchant/user.
- **notification-service:** Lắng nghe event từ Kafka để gửi SMS/Email báo biến động số dư mà không làm chậm quy trình chính.
- **cms:** Hệ thống back-office quản trị hệ thống (sử dụng PostgreSQL).

## 3. Quy tắc Logging và Observability
### 3.1. Structured Logging
Thay vì log dưới dạng văn bản tự do, mọi log trong hệ thống phải tuân thủ chuẩn Structured Logging qua SLF4J/MDC.
- Quy tắc:
  - Mọi log quan trọng phải có định danh sự kiện: `eventName=...`
  - Cần đính kèm `traceId` và `spanId` từ OpenTelemetry để phục vụ Distributed Tracing xuyên suốt qua các service.
- Ví dụ định dạng log chuẩn:
  ```java
  log.error("eventName=OUTBOX_SERIALIZE_FAILED proposalId={} eventType={} traceId={} spanId={}", proposal.getId(), eventType, traceId, spanId, exception);
  ```

### 3.2. OpenTelemetry (OTel) Collector
- OTel Collector nhận dữ liệu OTLP từ các service, thực hiện Batching và Data Masking (xóa/ẩn thông tin PII/PCI như số thẻ, số CMND) trước khi đẩy vào Loki.
- Hỗ trợ truy vết chéo (Distributed Tracing) hoàn chỉnh: Lấy `traceId` gặp lỗi để xem điểm nghẽn tại bất kỳ khâu nào trên Grafana.

## 4. Thiết kế Triển khai và Kiến thức Kubernetes (K8s)
- **Externalized Configuration:** Mã nguồn ứng dụng (App Code) hoàn toàn độc lập với môi trường. Cấu hình (Timeout, Database URI) được truyền qua biến môi trường để đảm bảo "Build once, deploy anywhere".
- **Network Resiliency:** Luôn thiết lập các Timeout (Connect Timeout, Read Timeout, Kafka delivery timeout) để tránh ứng dụng bị kẹt dẫn đến Cascading Failure.
- **Kubernetes (Self-healing & Auto-scaling):** Sử dụng `Liveness/Readiness Probes` tự động phát hiện và khởi động lại Pod lỗi. Hỗ trợ `HPA (Horizontal Pod Autoscaler)` mở rộng tài nguyên dựa trên metrics thực tế.
- **Resource Limits:** Giới hạn tài nguyên (CPU/RAM requests, limits) để đảm bảo không một service nào bị memory leak ảnh hưởng đến cả cụm.
- **CI/CD & GitOps:** Quy trình chuẩn: Code ➔ Test ➔ Build Image bằng Jenkins ➔ Scan bảo mật (Trivy) ➔ Harbor ➔ Thay đổi phiên bản trên K8s Manifests (GitOps) để Kubernetes tự động cập nhật (Rolling Update/Zero Downtime).
- **DevSecOps (Hardening):** Chạy ứng dụng dưới quyền user phi hệ thống (`USER spring` - Non-root Container), sử dụng Read-only Filesystem để chống tấn công Container Breakout, kéo mật khẩu qua Vault lúc runtime thay vì lưu trong file yaml.
