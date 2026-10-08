# Simulation Bank Platform — Documentation Hub

Chào mừng bạn đến với trung tâm tài liệu kiến trúc và nghiệp vụ của **Simulation Bank Platform**. Thư mục `docs/` chứa toàn bộ tài liệu đặc tả nghiệp vụ (BRD - Business Requirements Document) và đặc tả kỹ thuật hệ thống (SRD - System Requirements Document) của tất cả các microservices và thư viện nền tảng trong hệ sinh thái.

---

## 1. Tài liệu Kiến trúc Cấp Hệ thống (System-Wide)

- **[Business Requirements Document (BRD) Tổng quan](brd.md):** Tầm nhìn kinh doanh, triết lý thiết kế (Zero Data Loss, Concurrency Control) và yêu cầu chức năng/phi chức năng chung.
- **[High-Level Design (HLD) Tổng thể](hld.md):** Kiến trúc tổng thể hệ thống, mô hình mạng (Edge/Gateway/Internal), Observability (OpenTelemetry, Prometheus, Loki) và triển khai Kubernetes.

---

## 2. Ma trận Tài liệu BRD & SRD theo từng Service

Mỗi service đều được quy hoạch thành một thư mục riêng trong `docs/`, bao gồm tài liệu **BRD (Yêu cầu Nghiệp vụ)** và **SRD (Thiết kế Kỹ thuật Chi tiết)**:

| Phân hệ / Microservice | Cổng kết nối | Vai trò & Trách nhiệm chính | BRD (Nghiệp vụ) | HLD (Kiến trúc Tổng thể) | SRD (Kỹ thuật Chi tiết) |
|---|---|---|---|---|---|
| **Corebank** | `8180` (Internal) | Sổ cái trung tâm (General Ledger), kế toán kép, bảo vệ số dư bằng Optimistic/Pessimistic Locking, CQRS & Event Sourcing. | [corebank/brd.md](corebank/brd.md) | [corebank/hld.md](corebank/hld.md) | [corebank/srd.md](corebank/srd.md) |
| **Money Bank** | `8181` | BFF & Điều phối chuyển tiền bán lẻ, quản lý luồng 2 bước (Transfer Proposal), kiểm soát trùng lệnh Idempotency, tích hợp Corebank. | [money-bank/brd.md](money-bank/brd.md) | [HLD Hệ thống](hld.md) | [money-bank/srd.md](money-bank/srd.md) |
| **API Gateway** | `8080` (Public) | Cổng Ingress duy nhất, xác thực JWT qua Keycloak JWKS, Rate Limiting Redis, CORS toàn cục, Header Enrichment, ngăn lộ Corebank. | [api-gateway/brd.md](api-gateway/brd.md) | [HLD Hệ thống](hld.md) | [api-gateway/srd.md](api-gateway/srd.md) |
| **CMS Portal** | `8182` (Backend)<br>`4200` (Angular) | Cổng quản trị vận hành Back-Office, quy trình 4 mắt Maker-Checker, che giấu dữ liệu PII Masking, upload hồ sơ, phân quyền động. | [cms/brd.md](cms/brd.md) | [HLD Hệ thống](hld.md) | [cms/srd.md](cms/srd.md) |
| **Profile Service** | `8183` | Quản lý hồ sơ định danh khách hàng tập trung (Single Customer View), cấp phát mã CIF, quy trình eKYC, phân tầng hạn mức (KYC Tiering). | [profile-service/brd.md](profile-service/brd.md) | [HLD Hệ thống](hld.md) | [profile-service/srd.md](profile-service/srd.md) |
| **Notification Service** | `8184` | Xử lý thông báo đa kênh bất đồng bộ (In-App Inbox, Push FCM, SMS, Email) qua sự kiện Kafka, Template Engine, Dead-Letter Queue. | [notification-service/brd.md](notification-service/brd.md) | [HLD Hệ thống](hld.md) | [notification-service/srd.md](notification-service/srd.md) |
| **Paygate** | `8185` | Cổng thanh toán B2B cho đối tác/Merchant, thu hộ qua Dynamic VietQR (EMVCo), chi hộ hàng loạt theo lô (Batch Payout), Webhook HMAC. | [paygate/brd.md](paygate/brd.md) | [HLD Hệ thống](hld.md) | [paygate/srd.md](paygate/srd.md) |
| **Common Service** | *(Shared JAR)* | Thư viện dùng chung nền tảng: Chuẩn hóa ApiErrorResponse RFC-7807, CQRS Dispatcher & Pipeline, Idempotency engine, Outbox, Masking. | [common-service/brd.md](common-service/brd.md) | [common-service/hld.md](common-service/hld.md) | [common-service/srd.md](common-service/srd.md) |

---

## 3. Chuyên đề Kỹ thuật Sổ cái Ngân hàng (Corebank Engineering Series)

Riêng phân hệ `corebank` cung cấp thêm bộ tài liệu chuyên sâu dành cho kỹ sư phát triển hệ thống lõi:
- [01. Kiến thức nền tảng để xây dựng Core Banking](corebank/01-kien-thuc-nen-tang.md) (Nguyên tắc ACID, Kế toán kép Double-entry).
- [02. Các Design Pattern được sử dụng trong Core Banking](corebank/02-design-patterns.md) (CQRS, AOP, Event Sourcing).
- [03. Thuật toán & Kỹ thuật xử lý hóc búa](corebank/03-thuat-toan-va-ky-thuat.md) (Race Condition, Optimistic Locking, Distributed Saga, Idempotency).
