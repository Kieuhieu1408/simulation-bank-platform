# Simulation Bank Platform

Nền tảng mô phỏng hệ sinh thái ngân hàng và thanh toán, được xây dựng theo kiến trúc microservice để phục vụ học tập, thử nghiệm nghiệp vụ và phát triển theo nhóm.

## 1. Tổng quan Dự án & Kiến trúc hệ thống
Chi tiết kiến trúc và thiết kế nghiệp vụ của dự án được quy hoạch tập trung tại thư mục `docs/`. Vui lòng tham khảo:
- **[Documentation Hub](docs/README.md):** Mục lục toàn bộ tài liệu BRD và SRD của từng microservice và thư viện nền tảng.
- **[BRD (Business Requirements Document)](docs/brd.md):** Chi tiết về ý tưởng cốt lõi, yêu cầu nghiệp vụ, FR (yêu cầu chức năng) và NFR (yêu cầu phi chức năng).
- **[HLD (High-Level Design)](docs/hld.md):** Kiến trúc hệ thống tổng thể, mô tả các services, thiết kế CSDL (CQRS/Event Sourcing), quy chuẩn Logging (OpenTelemetry) và kiến thức DevOps/Kubernetes.

## 2. Cấu trúc Thư mục

- `docs/`: Chứa các tài liệu thiết kế hệ thống chuẩn (BRD, HLD) và tài liệu hướng dẫn chuyên sâu của từng service.
- `config/`: Chứa các file cấu hình hạ tầng cho môi trường local (Keycloak, Prometheus, Grafana, Nginx, Vault, Otel Collector). Được kết nối trực tiếp với `compose.yaml`.
- `k8s/`: Chứa các cấu hình phân bổ tài nguyên và triển khai lên cụm Kubernetes (Manifests, GitOps).
- `common-service/`: Thư viện dùng chung (Shared library, DTOs, Envelopes) giữa các service, không có runtime độc lập.
- **Microservices chính:**
  - `corebank/`: Sổ cái trung tâm quản lý số dư và lịch sử giao dịch (Sử dụng Oracle DB, Outbox Pattern). 
  - `money-bank/`: Dịch vụ ngân hàng số mô phỏng, xử lý luồng giao dịch chuyển tiền.
  - `cms/`: Hệ thống quản trị nội bộ Back-office.
  - `paygate/`: Cổng thanh toán nghiệp vụ thu/chi hộ.
  - `profile-service/`: Quản lý hồ sơ định danh, RBAC.
  - `notification-service/`: Consumer lắng nghe sự kiện từ Kafka để gửi thông báo.

## 3. Khởi chạy Môi trường Local (Docker Compose)
Dự án sử dụng `compose.yaml` (nằm ở thư mục gốc) để tự động hóa toàn bộ hạ tầng cục bộ (local dev). Các file cấu hình sẽ được tự động trỏ vào thư mục `config/`.

```bash
# Khởi động toàn bộ hệ thống (Bao gồm Hạ tầng + Các microservices)
docker compose up -d

# Hoặc chỉ khởi động nhóm hạ tầng cơ sở (để run các service trên IDE)
docker compose up -d keycloak redis postgres oracle vault
```

## 4. Ranh giới của `common-service`

**Nên đặt vào:**
- API DTO và event DTO chuẩn giao tiếp giữa các service.
- Kiểu dữ liệu nền tảng ổn định (money/currency, correlation ID, error envelope).
- Tiện ích kỹ thuật không phụ thuộc nghiệp vụ cụ thể.

**Không được đặt vào:**
- JPA entity, repository hoặc database schema của một service bất kỳ.
- Business workflow, state machine (VD: Quy trình chuyển tiền thuộc về Money Bank).
- Mỗi service tự quản lý domain model và dữ liệu riêng. Sự thay đổi có rủi ro "breaking change" trong shared contract phải nâng major version.

> Hệ thống luôn duy trì tư duy "Build for Failure" và "Secure by Design" - Mọi giao dịch liên quan tới tiền tệ được bảo vệ nghiêm ngặt tuyệt đối thông qua Data Integrity.

## 5. Danh sách Ports (Cổng kết nối)

### Các dịch vụ Microservices (chạy trên Kubernetes)
- **API Gateway**: `8080` (Có thể truy cập trực tiếp từ máy qua `http://localhost:8080`)
- **Corebank**: `8180`
- **Money Bank**: `8181`
- **CMS**: `8182`

### Các dịch vụ Hạ tầng (chạy qua Docker Compose / make infra)
- **Keycloak**: `8090`
- **Vault**: `8200`
- **Oracle DB**: `1521`
- **PostgreSQL**: `5432`
- **Redis**: `6379`
- **Kafka**: `9092`
- **Loki**: `3100`
- **Jaeger UI**: `16686`
- **Prometheus UI**: `9090`
- **Grafana UI**: `3000`
- **OTel Collector**: `4317` (gRPC) / `4318` (HTTP)
