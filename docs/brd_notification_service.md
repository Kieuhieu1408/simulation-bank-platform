# Business Requirements Document (BRD) - Notification Service

---

## 1. TỔNG QUAN & MỤC TIÊU (OVERVIEW & OBJECTIVES)

### 1.1. Bối cảnh
Trong hệ thống Ngân hàng số (`simulation-bank-platform`), các nghiệp vụ trọng yếu như chuyển tiền, xác thực 2 bước (2FA/OTP), và biến động số dư đòi hỏi kênh thông báo độc lập, bảo mật và gần như thời gian thực (near real-time).
Hiện tại, `money-bank` đang đóng vai trò Channel/BFF Service. Việc xử lý logic gửi tin và trả OTP trực tiếp trong response vi phạm nguyên tắc bảo mật ngân hàng (Out-Of-Band - OOB) và làm gia tăng độ trễ mạng (coupling).

### 1.2. Mục tiêu dự án
Xây dựng một microservice độc lập — **Notification Service** — đóng vai trò trung tâm xử lý, định tuyến và phân phối toàn bộ thông báo của nền tảng qua đa kênh (Multi-channel: SMS, Email, Push Notification), hoạt động theo mô hình hướng sự kiện (Event-Driven Architecture) với Apache Kafka.

---

## 2. PHẠM VI DỰ ÁN (SCOPE OF WORK)

### 2.1. In-Scope (Trong phạm vi)
- **Tiêu thụ sự kiện từ Kafka**:
  - `otp.requested` (Sinh OTP chuyển khoản hoặc đăng nhập).
  - `balance.fluctuated` hoặc `transfer.completed` (Biến động số dư tài khoản).
  - `security.alert` (Cảnh báo đăng nhập mới, đổi mật khẩu).
- **Hỗ trợ đa kênh phân phối**:
  - **SMS**: Gửi mã OTP OOB, biến động số dư khẩn cấp (qua SMS Brandname Gateway: Twilio/VNPT/Viettel/Mock).
  - **Email**: Gửi hoá đơn điện tử, sao kê PDF, biên nhận chuyển khoản (qua SMTP/SendGrid/SES).
  - **Push Notification**: Bắn thông báo về ứng dụng Mobile (FCM / APNs / WebPush).
  - **In-App Notification**: Lưu trữ hòm thư thông báo trong ứng dụng cho khách hàng tra cứu.
- **Quản lý Mẫu tin (Template Engine)**: Định nghĩa mẫu nội dung (Thymeleaf/FreeMarker) với tham số động, hỗ trợ đa ngôn ngữ (vi, en).
- **Quản lý Retry & Dead Letter Queue (DLQ)**: Cơ chế thử lại thông minh khi nhà mạng viễn thông / gateway bên thứ ba gặp sự cố.
- **Idempotency & Deduplication**: Ngăn chặn gửi trùng lặp tin nhắn khi Kafka redeliver message.

### 2.2. Out-of-Scope (Ngoài phạm vi ban đầu)
- Chiến dịch Marketing/Bulk SMS cho hàng triệu khách hàng không liên quan đến giao dịch tài chính (sẽ tách phân hệ riêng sau).

---

## 3. CÁC USE CASE NGHIỆP VỤ CHI TIẾT (BUSINESS USE CASES)

### UC-01: Gửi mã OTP xác thực giao dịch chuyển tiền (OOB Transfer OTP)
* **Tác nhân kích hoạt**: `money-bank` đẩy sự kiện `notification.otp.requested`.
* **Luồng xử lý**:
  1. Notification Service nhận message chứa: `proposalId`, `customerId`, `otpCode` (hoặc encrypted OTP), `expiresInSeconds`, `channel` (SMS/Email/Push).
  2. Tra cứu thông tin liên hệ của khách hàng (SĐT/Email) qua Customer Profile hoặc từ Token context.
  3. Áp dụng template: `"Mã OTP xác thực chuyển tiền cho giao dịch {proposalId} là {otpCode}. Mã có hiệu lực trong {expiresInMinutes} phút. Tuyệt đối KHÔNG chia sẻ mã này cho bất kỳ ai."`
  4. Gửi qua SMS Provider được chỉ định.
  5. Ghi log kiểm toán (Audit Log) trạng thái gửi tin (PENDING $\rightarrow$ SENT $\rightarrow$ DELIVERED / FAILED).

### UC-02: Thông báo biến động số dư (Balance Fluctuation / Transaction Alert)
* **Tác nhân kích hoạt**: `corebank` hoặc Transactional Outbox đẩy sự kiện `notification.balance.fluctuated` khi lệnh ghi sổ cái (Ledger) hoàn tất.
* **Luồng xử lý**:
  1. Notification Service nhận event: `accountId`, `transactionType` (DEBIT / CREDIT), `amount`, `currency`, `balanceAfter`, `occurredAt`, `description`.
  2. Lắp ráp template thông báo biến động:
     - Biến động giảm (Debit): `"Tài khoản {maskedAccount} giảm -{amount} {currency} lúc {time}. Số dư khả dụng: {balance} {currency}. ND: {desc}"`
     - Biến động tăng (Credit): `"Tài khoản {maskedAccount} tăng +{amount} {currency} lúc {time}. Số dư khả dụng: {balance} {currency}. ND: {desc}"`
  3. Bắn Push Notification về thiết bị Mobile của chủ tài khoản; nếu không có kết nối app thì fallback sang SMS (nếu khách hàng đăng ký dịch vụ SMS Banking).
  4. Lưu thông báo vào bảng `user_notifications` để hiển thị trên chuông thông báo trong app.

### UC-03: Cảnh báo bảo mật & Tài khoản (Security Alert)
* **Tác nhân kích hoạt**: `api-gateway` hoặc Keycloak webhook / `common-service`.
* **Nội dung**: Thông báo đăng nhập thiết bị mới, IP lạ, thay đổi mật khẩu hoặc khóa thẻ khẩn cấp.

---

## 4. THIẾT KẾ KIẾN TRÚC KỸ THUẬT (TECHNICAL ARCHITECTURE)

### 4.1. Sơ đồ luồng dữ liệu (Data Flow Diagram)

```
[Producer Services]
  ├── money-bank   (Proposal created -> Push OTP Event)
  └── corebank     (Ledger posted    -> Push Balance Event)
          │
          ▼  (Transactional Outbox + Kafka Producer)
   ┌─────────────────────────────────────────────────────────────┐
   │                        APACHE KAFKA                         │
   │  Topics:                                                    │
   │   - notification.otp.requested                              │
   │   - notification.balance.fluctuated                         │
   │   - notification.security.alert                             │
   │   - notification.dlq (Dead Letter Queue)                    │
   └─────────────────────────────────────────────────────────────┘
          │
          ▼  (Kafka Consumer Group: notification-service-group)
┌─────────────────────────────────────────────────────────────────┐
│                      NOTIFICATION-SERVICE                       │
│                                                                 │
│  1. Idempotency Filter (Redis / DB Unique Event ID)             │
│  2. Customer Contact Resolver (Local Cache / Gateway S2S)       │
│  3. Template Engine (Thymeleaf / Mustache)                      │
│  4. Channel Router & Rate Limiter                               │
│     ├── SMS Adapter       ──► (Twilio / MockProvider)           │
│     ├── Email Adapter     ──► (SMTP / Amazon SES / SendGrid)    │
│     └── Push Adapter      ──► (Firebase Cloud Messaging - FCM)  │
│  5. Audit Log & Delivery Status Tracker                         │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2. Cấu trúc Message Event chuẩn trên Kafka (Contract Specification)

#### Event 1: `notification.otp.requested`
```json
{
  "eventId": "evt-7a8b9c0d-1234",
  "eventType": "OTP_REQUESTED",
  "timestamp": "2026-10-15T11:00:00Z",
  "producer": "money-bank",
  "payload": {
    "proposalId": "prop-bd4e21bc",
    "customerId": "CIF00000001",
    "targetPhone": "+84901234567",
    "targetEmail": "customer@example.com",
    "otpCode": "111111",
    "expiresInSeconds": 120,
    "preferredChannel": "SMS"
  }
}
```

#### Event 2: `notification.balance.fluctuated`
```json
{
  "eventId": "evt-8b9c0d1e-5678",
  "eventType": "BALANCE_FLUCTUATED",
  "timestamp": "2026-10-15T11:05:00Z",
  "producer": "corebank",
  "payload": {
    "transactionId": "tx-core-9999",
    "accountId": "ACC001",
    "customerId": "CIF00000001",
    "direction": "DEBIT",
    "amount": "50000.00",
    "currency": "VND",
    "balanceAfter": "450000.00",
    "occurredAt": "2026-10-15T11:05:00Z",
    "description": "Chuyen tien den ACC002"
  }
}
```

---

## 5. YÊU CẦU PHI CHỨC NĂNG (NON-FUNCTIONAL REQUIREMENTS - NFR)

1. **Hiệu năng & Độ trễ (SLA & Latency)**:
   - Thời gian từ khi Kafka nhận message OTP đến khi gọi nhà mạng viễn thông (Provider Dispatch): $< 500\text{ ms}$.
   - Tỷ lệ xử lý thành công tin nhắn OTP trong 5 giây đầu tiên: $> 99.5\%$.
2. **Khả năng chịu lỗi & Chống lặp (Reliability & Idempotency)**:
   - Dùng `eventId` để làm khoá phân biệt (deduplication key) trong Redis với TTL 24h.
   - Khi gọi Provider bên ngoài thất bại (Network Timeout, 5xx), thực hiện **Exponential Backoff Retry** (tối đa 3 lần: 1s, 2s, 4s). Nếu quá số lần thử lại, đẩy message vào `notification.dlq` để kiểm tra thủ công.
3. **Bảo mật & Quyền riêng tư (Security & PII Compliance)**:
   - **Data Masking**: Khi lưu log hoặc hiển thị lịch sử thông báo, số tài khoản phải được che (`ACC***001`), số điện thoại (`090***567`), mã OTP **tuyệt đối không được in plaintext ra hệ thống logging tập trung (Loki/ELK)**.
   - Quản lý Secret Key của các dịch vụ bên thứ ba (Twilio token, FCM server key) bằng HashiCorp Vault hoặc Kubernetes Secrets / ConfigMap mã hoá.
4. **Môi trường Test / Dev (Mocking Mode)**:
   - Trong môi trường `dev` hoặc `local`: Tự động kích hoạt `MockSmsProvider` và `MockEmailProvider` — chỉ log nội dung tin nhắn ra Console thay vì gửi qua nhà mạng thật, giúp tiết kiệm chi phí và tiện lợi khi debug.

---

## 6. LỘ TRÌNH TRIỂN KHAI ĐỀ XUẤT (IMPLEMENTATION ROADMAP)

| Giai đoạn | Nội dung công việc | Kết quả bàn giao (Deliverable) |
| :--- | :--- | :--- |
| **Pha 1: Setup Service & Kafka Consumer** | Khởi tạo module `notification-service`, cấu hình Spring Boot, Kafka Consumer, và DTO Contracts. | Module chạy độc lập, lắng nghe được topic `notification.otp.requested`. |
| **Pha 2: Mock Provider & OTP Flow** | Xây dựng `MockSmsProvider`, tích hợp với `money-bank` để chuyển đổi hoàn toàn luồng sinh OTP sang bắn Kafka Event. | Khách hàng bấm gửi OTP $\rightarrow$ Event lên Kafka $\rightarrow$ Notification log tin nhắn OTP. HTTP Response không còn chứa OTP. |
| **Pha 3: Template Engine & In-app Inbox** | Tích hợp Thymeleaf/Mustache template, thiết kế bảng CSDL `notifications` để lưu trữ lịch sử tin nhắn trong ứng dụng cho user. | API `GET /api/v1/notifications` để mobile app hiển thị chuông thông báo. |
| **Pha 4: Tích hợp Third-party & Production Ready** | Tích hợp Twilio/FCM thực tế, cấu hình Redis Idempotency, Micrometer Prometheus metrics và Alerting. | Notification Service hoàn chỉnh, sẵn sàng scale multi-pod. |
