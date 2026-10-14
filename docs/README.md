# Simulation Bank Platform — Documentation Hub

Chào mừng bạn đến với trung tâm tài liệu kiến trúc và nghiệp vụ của **Simulation Bank Platform**. Thư mục `docs/` chứa toàn bộ tài liệu đặc tả nghiệp vụ (BRD - Business Requirements Document) và đặc tả kỹ thuật hệ thống (SRD - System Requirements Document) của các microservices và thư viện nền tảng trong hệ sinh thái.

---

## 1. Tài liệu Kiến trúc Cấp Hệ thống (System-Wide)

- **[Business Requirements Document (BRD) Tổng quan](brd.md):** Tầm nhìn kinh doanh, triết lý thiết kế (Zero Data Loss, Concurrency Control) và yêu cầu chức năng/phi chức năng chung.
- **[High-Level Design (HLD) Tổng thể](hld.md):** Kiến trúc hệ thống tổng thể, mô hình mạng (Edge/Gateway/Internal), Observability (OpenTelemetry, Prometheus, Loki) và triển khai Kubernetes.
- **[Hướng dẫn Vận hành Cụm Kubernetes Local](K8S_LOCAL_GUIDE.md):** Hướng dẫn dựng môi trường Minikube/K8s nội bộ, tách cụm Command/Query và scripts tự động hóa.

---

## 2. Ma trận Tài liệu BRD & SRD theo từng Service

Mỗi service đều có bộ tài liệu kỹ thuật chuẩn mực (BRD, HLD, SRD) lưu tại thư mục `docs/` của service tương ứng:

| Phân hệ / Microservice | Cổng kết nối | Vai trò & Trách nhiệm chính | BRD (Nghiệp vụ) | HLD (Kiến trúc Tổng thể) | SRD (Kỹ thuật Chi tiết) |
|---|---|---|---|---|---|
| **Corebank** | `8180` (Internal) | Sổ cái trung tâm (General Ledger), kế toán kép, bảo vệ số dư bằng Optimistic Locking, Modular Monolith DDD, Event Sourcing & Saga Orchestration. | [corebank/docs/brd.md](../corebank/docs/brd.md) | [corebank/docs/hld.md](../corebank/docs/hld.md) | [corebank/docs/srd.md](../corebank/docs/srd.md) |
| **Money Bank** | `8181` | BFF & Điều phối chuyển tiền bán lẻ, quản lý luồng 2 bước (Transfer Proposal), kiểm soát trùng lệnh Idempotency, tích hợp Corebank. | [money-bank/docs/brd.md](../money-bank/docs/brd.md) | [HLD Hệ thống](hld.md) | [money-bank/docs/srd.md](../money-bank/docs/srd.md) |
| **API Gateway** | `8080` (Public) | Cổng Ingress duy nhất, xác thực JWT qua Keycloak JWKS, Rate Limiting Redis, CORS toàn cục, Header Enrichment, bảo vệ mạng Corebank. | [api-gateway/docs/inception/BRD.md](../api-gateway/docs/inception/BRD.md) | [api-gateway/docs/inception/HLD.md](../api-gateway/docs/inception/HLD.md) | [api-gateway/docs/inception/SRD.md](../api-gateway/docs/inception/SRD.md) |
| **CMS Portal** | `8182` (Backend)<br>`4200` (Angular) | Cổng quản trị vận hành Back-Office, quy trình 4 mắt Maker-Checker, che giấu dữ liệu PII Masking, upload hồ sơ, phân quyền động. | [cms/docs/BRD.md](../cms/docs/BRD.md) | [cms/docs/HLD.md](../cms/docs/HLD.md) | [cms/docs/SRD.md](../cms/docs/SRD.md) |
| **Common Service** | *(Shared JAR)* | Thư viện dùng chung nền tảng: Chuẩn hóa ApiErrorResponse RFC-7807, CQRS Dispatcher & Pipeline, Idempotency engine, Outbox, Masking. | [common-service/docs/brd.md](../common-service/docs/brd.md) | [common-service/docs/hld.md](../common-service/docs/hld.md) | [common-service/docs/srd.md](../common-service/docs/srd.md) |
| **Profile Service** | `8183` | Quản lý hồ sơ định danh khách hàng tập trung (Single Customer View), cấp phát mã CIF, quy trình eKYC, phân tầng hạn mức (KYC Tiering). | *(Đang phát triển)* | [HLD Hệ thống](hld.md) | *(Đang phát triển)* |
| **Notification Service** | `8184` | Xử lý thông báo đa kênh bất đồng bộ (In-App Inbox, Push FCM, SMS, Email) qua sự kiện Kafka, Template Engine, Dead-Letter Queue. | *(Đang phát triển)* | [HLD Hệ thống](hld.md) | *(Đang phát triển)* |
| **Paygate** | `8185` | Cổng thanh toán B2B cho đối tác/Merchant, thu hộ qua Dynamic VietQR (EMVCo), chi hộ hàng loạt theo lô (Batch Payout), Webhook HMAC. | *(Đang phát triển)* | [HLD Hệ thống](hld.md) | *(Đang phát triển)* |

---

## 3. Chuyên đề Kỹ thuật Sổ cái Ngân hàng (Corebank Engineering Series)

Phân hệ `corebank` cung cấp bộ tài liệu kỹ thuật chuyên sâu về sổ cái và điều phối giao dịch phân tán:
- **Nguyên tắc ACID & Kế toán Kép Sổ cái (Double-Entry Bookkeeping):** Xem tại [Corebank BRD](../corebank/docs/brd.md#42-quy-tắc-sổ-cái-kép-double-entry-bookkeeping-rule).
- **Mô hình Modular Monolith DDD & CQRS Phân tách Vật lý trên K8s:** Xem tại [Corebank HLD](../corebank/docs/hld.md#3-các-mẫu-thiết-kế-chủ-đạo-design-patterns-applied).
- **Saga Orchestration, Tự phục hồi sau sự cố (`SagaRecoveryJob`) & Zero-Trust Idempotency:** Xem tại [Corebank SRD](../corebank/docs/srd.md#4-kiểm-soát-xử-lý-đồng-thời-idempotency--tự-phục-hồi-saga).
- **Kế hoạch Chuyển đổi và Nghiệm thu 1-1 với Tiêu chuẩn Ngân hàng:** Xem tại [Corebank Plan](../corebank/docs/plan.md).
