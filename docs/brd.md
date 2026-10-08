# Business Requirements Document (BRD) - Simulation Bank Platform

## 1. Ý tưởng cốt lõi (Initial Idea)
Simulation Bank Platform là một nền tảng giả lập Core Banking, được xây dựng để giải quyết các thách thức khó khăn nhất trong ngành công nghệ tài chính (Fintech): **Tính toàn vẹn dữ liệu (Data Integrity)** và **Xử lý đồng thời (Concurrency)**.

Mục tiêu chính là tạo ra một sổ cái (Ledger) không thể sai sót, không thể mất mát và có hiệu năng cao.

## 2. Yêu cầu Nghiệp vụ (Business Requirements)
- Quản lý Hồ sơ khách hàng (Customer Profile) và Định danh (Identity).
- Quản lý Tài khoản (Account) và Thẻ (Card).
- Thực hiện giao dịch Chuyển tiền nội bộ (Internal Transfer).
- Hỗ trợ tính năng nạp/rút tiền (Deposit / Withdraw).
- Cung cấp tính năng quản trị Back-office (CMS) và tích hợp cổng thanh toán (Paygate).
- Thông báo biến động số dư cho người dùng (Notification).

## 3. Yêu cầu Chức năng (Functional Requirements - FR)
- **Hệ thống người dùng & Phân quyền:** Xác thực người dùng (OAuth2/SSO), cấp token qua Keycloak, quản lý RBAC.
- **Tài khoản:** Khởi tạo tài khoản, khóa/mở khóa tài khoản, truy vấn số dư thực tế (Actual Balance) và số dư khả dụng (Available Balance).
- **Chuyển tiền:** Chuyển tiền nguyên tử (atomic), giữ tiền (Funds Reserved) khi bắt đầu giao dịch, trừ tiền thật (Transfer Completed) khi giao dịch thành công, hoặc hoàn tiền (Transfer Failed) khi có lỗi.
- **Sao kê lịch sử:** Xem lịch sử giao dịch (Transaction History) chi tiết.

## 4. Yêu cầu Phi chức năng (Non-Functional Requirements - NFR)
- **Zero Data Loss:** Tiền của khách hàng là dữ liệu nhạy cảm nhất. Hệ thống không cho phép sai sót trong tính toán hoặc mất giao dịch.
- **Ngăn chặn Double-Spending:** Hệ thống tuyệt đối ngăn chặn việc một người dùng gửi nhiều giao dịch đồng thời để trục lợi bằng cơ chế Optimistic Locking ở cấp độ CSDL.
- **Khả năng kiểm toán & Truy vết (Auditability & Traceability):** Mọi sự thay đổi trên dòng tiền phải được ghi nhận lịch sử dưới dạng sự kiện append-only (Event Sourcing) để phục vụ đối soát (Reconciliation).
- **High Throughput (TPS cao):** Hệ thống phải có khả năng chịu tải hàng ngàn giao dịch mỗi giây. Sử dụng kiến trúc Event-Driven, tách biệt đọc ghi (CQRS).
- **Tính khả dụng cao (Resilience):** Đảm bảo sự cố của một microservice không làm tê liệt toàn bộ hệ thống. Các thay đổi trạng thái đều thông qua Outbox pattern và Kafka để đảm bảo at-least-once delivery và nhất quán cuối cùng (Eventual Consistency).
