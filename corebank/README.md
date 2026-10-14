# Corebank Service

Corebank là phân hệ sổ cái ngân hàng trung tâm (Core Ledger Service) của `simulation-bank-platform`, chịu trách nhiệm lưu trữ tài khoản thanh toán, thẻ, số dư và lịch sử giao dịch. Đây là nguồn chân lý duy nhất (Single Source of Truth) trong hệ thống mô phỏng phục vụ nghiên cứu và phát triển kiến trúc tài chính ngân hàng hiện đại (Domain-Driven Design, Event Sourcing, Saga Orchestration, CQRS).

## Tài liệu Dự án
- **[BRD (Business Requirements Document)](docs/brd.md):** Yêu cầu nghiệp vụ, tính toàn vẹn sổ cái, nguyên tắc kế toán kép và tiêu chí nghiệm thu.
- **[HLD (High-Level Design)](docs/hld.md):** Thiết kế kiến trúc tổng thể, mô hình CQRS vật lý trên K8s, Event Sourcing và điều phối Saga.
- **[SRD (System Requirements Document)](docs/srd.md):** Đặc tả kỹ thuật chi tiết, schema CSDL Oracle, API endpoints và phân tầng thẩm định (4-Level Validation).
- **[Kế hoạch Chuyển đổi (Plan)](docs/plan.md):** Lộ trình 7 giai đoạn chuyển đổi sang kiến trúc DDD & Saga đã nghiệm thu.

## Phạm vi Chức năng
- **Quản lý Hồ sơ Khách hàng (Customer CIF):** Tạo CIF 8 số tự động hoặc chỉ định, lưu trữ thông tin CCCD, Họ tên, SĐT, Email với ràng buộc duy nhất chống trùng lặp.
- **Quản lý Tài khoản Thanh toán (Account):** Mở tài khoản đa tiền tệ, theo dõi Số dư sổ cái (`ledgerBalance`), Số dư khả dụng (`availableBalance`) và các khoản giữ tiền (`holds`).
- **Quản lý Thẻ (Card):** Phát hành thẻ thanh toán liên kết tài khoản, che mờ số thẻ (Masking) chuẩn PCI/Nghị định 13/2023/NĐ-CP, tuyệt đối **không lưu trữ và không trả về mã CVV**.
- **Chuyển tiền Nội bộ (Transfer Saga):** Điều phối chuyển tiền qua Saga Orchestration 2 pha (Reserve → Credit [Pivot] → Settle), tự động bù trừ nếu lỗi trước pivot, tự phục hồi sau sự cố qua `SagaRecoveryJob`.
- **Tự bảo vệ Sổ cái (Zero-Trust Idempotency):** Sinh `transferId = UUID v3(sourceAccountId + ":" + idempotencyKey)` tất định; phát hiện gửi lại trùng hash thì replay kết quả trong $\le 50\text{ms}$; nếu đổi nội dung thì phản hồi lỗi HTTP `409 Conflict`.
- **Tra cứu Số dư & Lịch sử Giao dịch:** Tra cứu số dư tức thời và danh sách giao dịch qua Read Projections (`account_view`, `transfer_view`, `transaction_history_view`).

## Đặc tả API

Base URL: `/api/v1`

### 1. Khách hàng (Customer)

#### `POST /api/v1/customers`
Tạo hồ sơ khách hàng mới và sinh CIF.
- **Request Body mẫu:**
  ```json
  {
    "fullName": "NGUYEN VAN A",
    "nationalId": "012345678901",
    "phoneNumber": "0987654321",
    "email": "nguyenvana@example.com",
    "cifNumber": "CIF00000003"
  }
  ```
  *(Ghi chú: `cifNumber` là tuỳ chọn; nếu không truyền hệ thống sẽ tự động cấp mã CIF 8 số tự tăng).*
- **Response 201 Created:**
  ```json
  {
    "cifNumber": "00000003",
    "fullName": "NGUYEN VAN A",
    "nationalId": "012345****01",
    "phoneNumber": "0987654321",
    "email": "nguyenvana@example.com",
    "createdAt": "2026-10-14T10:00:00Z"
  }
  ```
  *(CCCD tự động được che mờ dạng `012345****01` tuân thủ Nghị định 13/2023/NĐ-CP).*

#### `GET /api/v1/customers/{cifNumber}`
Tra cứu thông tin khách hàng qua CIF.

#### `GET /api/v1/customers/{cifNumber}/accounts`
Lấy danh sách mọi tài khoản thanh toán thuộc sở hữu của CIF.

#### `GET /api/v1/customers/{cifNumber}/cards`
Lấy danh sách mọi thẻ ngân hàng thuộc CIF.

---

### 2. Tài khoản (Account)

#### `POST /api/v1/accounts`
Mở tài khoản thanh toán mới gắn với CIF.
- **Request Body mẫu:**
  ```json
  {
    "cifNumber": "00000001",
    "currency": "VND",
    "initialBalance": 1000000
  }
  ```
- **Response 200 OK:**
  ```json
  {
    "accountId": "acc-7f8e-4a12",
    "accountNumber": "100200300401",
    "cifNumber": "00000001",
    "currency": "VND",
    "balance": 1000000,
    "status": "ACTIVE",
    "createdAt": "2026-10-14T10:01:00Z"
  }
  ```

#### `GET /api/v1/accounts/{id}`
Lấy thông tin tài khoản và số dư hiện tại.

#### `GET /api/v1/accounts/{id}/balance`
Truy vấn số dư tức thời.
- **Response 200 OK:**
  ```json
  {
    "accountId": "acc-7f8e-4a12",
    "accountNumber": "100200300401",
    "currency": "VND",
    "actualBalance": 1000000,
    "availableBalance": 800000,
    "holdBalance": 200000,
    "status": "ACTIVE"
  }
  ```

#### `GET /api/v1/accounts/{id}/transactions?from=&to=&page=0&size=20`
Truy vấn lịch sử biến động số dư có phân trang.

---

### 3. Thẻ Ngân hàng (Card)

#### `POST /api/v1/cards` hoặc `POST /api/v1/accounts/{id}/cards`
Phát hành thẻ mô phỏng gắn với tài khoản thanh toán đang hoạt động.
- **Request Body (`POST /api/v1/cards`):**
  ```json
  {
    "accountId": "acc-7f8e-4a12"
  }
  ```
- **Response 200 OK:**
  ```json
  {
    "cardId": "crd-9a1b-3c4d",
    "maskedCardNumber": "9704-12XX-XXXX-3456",
    "accountId": "acc-7f8e-4a12",
    "cifNumber": "00000001",
    "status": "ACTIVE",
    "createdAt": "2026-10-14T10:02:00Z"
  }
  ```
  > ⚠️ **Tuân thủ Bảo mật & NĐ 13/2023/NĐ-CP:** Số thẻ luôn được che mờ (Masked PAN). Hệ thống **tuyệt đối KHÔNG lưu trữ và KHÔNG trả về mã bí mật CVV/PIN**.

#### `GET /api/v1/cards/{id}`
Tra cứu trạng thái và thông tin thẻ.

---

### 4. Chuyển tiền (Transfer Saga)

#### `POST /api/v1/transfers`
Tiếp nhận lệnh chuyển tiền nội bộ được điều phối bằng Saga Orchestration.
- **Request Body mẫu:**
  ```json
  {
    "sourceAccountId": "acc-7f8e-4a12",
    "destinationAccountId": "acc-3b2a-9f81",
    "amount": 250000,
    "currency": "VND",
    "idempotencyKey": "trans-client-uuid-001",
    "description": "Chuyen tien an trua"
  }
  ```
- **Response thành công (200 OK):**
  ```json
  {
    "transactionId": "d3b07384-d113-3920-a193-cc449be9d4a4",
    "status": "SUCCESS",
    "sourceAccountId": "acc-7f8e-4a12",
    "destinationAccountId": "acc-3b2a-9f81",
    "amount": 250000,
    "currency": "VND",
    "description": "Chuyen tien an trua",
    "createdAt": "2026-10-14T10:03:00Z"
  }
  ```
- **Trường hợp quá hạn chờ đồng bộ (Async Fallback):**
  Nếu Saga chưa kịp hoàn tất trong giới hạn thời gian chờ đồng bộ (cấu hình `corebank.transfer.sync-timeout-seconds: 3`), API phản hồi kết quả tức thì với `status: "PENDING"`. Giao dịch tiếp tục hoàn tất ngầm trong nền và đồng bộ trạng thái cuối cùng sang Kafka topic `bank.transfers.events`.
- **Trường hợp lỗi nghiệp vụ:**
  - Không đủ số dư: Ném ngoại lệ nghiệp vụ trả về lỗi HTTP `422 Unprocessable Entity` (hoặc `400 Bad Request`).
  - Gửi lại cùng `idempotencyKey` nhưng sai khác số tiền / tài khoản nhận: Trả về lỗi HTTP `409 Conflict`.
  - Gửi lại cùng `idempotencyKey` và khớp payload: Trả ngay kết quả cũ trong $\le 50\text{ms}$ (Idempotent Replay).

#### `GET /api/v1/transfers/{id}`
Tra cứu trạng thái lệnh chuyển tiền theo `transferId`.

#### `GET /api/v1/transfers?accountId={accountId}`
Lấy danh sách các lệnh chuyển tiền liên quan đến tài khoản.

---

### 5. Vận hành & Giám sát (Observability)
- **Health check:** `GET /actuator/health`
- **Prometheus Metrics:** `GET /actuator/prometheus` (bao gồm các metric nghiệp vụ `corebank_saga_step_total`, `corebank_saga_stuck_total`, `corebank_concurrency_retries_total`).

---

## Kiến trúc Kỹ thuật & Hạ tầng

- **Ngôn ngữ & Framework:** Java 21 LTS, Spring Boot 4.1.0 (Spring Framework 7), Jackson 3 (`tools.jackson.*`).
- **Mô hình Kiến trúc:** Modular Monolith chia theo Bounded Contexts:
  - `com.hieu.corebank.account`: Aggregate tài khoản, quản lý Holds và sổ cái, Event Sourcing.
  - `com.hieu.corebank.transfer`: Aggregate chuyển tiền, Saga Orchestrator, Tự phục hồi (`SagaRecoveryJob`).
  - `com.hieu.corebank.card`: Quản lý phát hành thẻ, che mờ thông tin PAN (Zero CVV).
  - `com.hieu.corebank.customer`: Quản lý hồ sơ CIF, đối soát tính duy nhất của CCCD.
  - `com.hieu.corebank.access`: Phân quyền nội bộ qua `@CoreBankAuthorization` Aspect và vai trò dịch vụ (`ROLE_SERVICE_*`).
  - `com.hieu.corebank.shared`: Shared Kernel (Value Objects `Money`, `Currency`, `NationalId`, `CardNumber`, Event Store, Outbox).
- **Phân tách Vật lý CQRS trên K8s:**
  Hệ thống hỗ trợ Spring Profiles tập trung qua `ProfileConstants`:
  - `command`: Pod ghi, kết nối Primary Write Master DB, thực thi nghiệp vụ và Saga.
  - `query`: Pod đọc, kết nối Read Replica DB, phục vụ truy vấn sao kê/số dư, co giãn tự động theo HPA (3-20 Pods).
  - `all`: Mặc định cho môi trường local/docker-compose, kích hoạt đầy đủ cả Command và Query.
- **Database:** Oracle Database 23c Free. Quản lý schema bằng Flyway (`V1__init_schema.sql`).
- **Event-Driven & Outbox:** Transactional Outbox Pattern đẩy integration events (`TransferCompletedEventDTO`, `TransferFailedEventDTO`) sang Apache Kafka topic `bank.transfers.events`.

---

## Chạy Demo Local

### Yêu cầu Tiên quyết
Docker Desktop đang chạy trên máy.

### Khởi động nhanh
Tại thư mục `corebank`:
```bash
docker compose up --build
```

API sẵn sàng tại `http://localhost:8180` khi log xuất hiện thông báo Spring Boot đã khởi động thành công.

Thông số kết nối Database mặc định trong demo:
| Thành phần | Giá trị |
| --- | --- |
| Host / Port | `localhost:1521` |
| Service name | `FREEPDB1` |
| App username | `corebank` |
| App password | `corebank_demo` |
| Oracle SYSTEM password | `oracle_demo` |

### Dữ liệu Mẫu Khởi tạo
Hệ thống tự động seed 2 khách hàng mẫu khi database trống:
- `CIF00000001`: 2 tài khoản, số dư `10,000,000 VND` và `5,000,000 VND`
- `CIF00000002`: 1 tài khoản, số dư `5,000,000 VND`

### Kịch bản Thử nghiệm Nhanh với cURL

1. Tạo khách hàng mới:
```bash
curl -X POST http://localhost:8180/api/v1/customers \
  -H 'Content-Type: application/json' \
  -d '{
    "fullName": "TRAN VAN TEST",
    "nationalId": "079090123456",
    "phoneNumber": "0901234567",
    "email": "test@simulation.bank"
  }'
```

2. Mở tài khoản thanh toán:
```bash
curl -X POST http://localhost:8180/api/v1/accounts \
  -H 'Content-Type: application/json' \
  -d '{
    "cifNumber": "00000003",
    "currency": "VND",
    "initialBalance": 2000000
  }'
```

3. Phát hành thẻ:
```bash
curl -X POST http://localhost:8180/api/v1/cards \
  -H 'Content-Type: application/json' \
  -d '{
    "accountId": "<ACCOUNT_ID_FROM_STEP_2>"
  }'
```

4. Thực hiện chuyển tiền có Idempotency:
```bash
curl -X POST http://localhost:8180/api/v1/transfers \
  -H 'Content-Type: application/json' \
  -d '{
    "sourceAccountId": "<ACCOUNT_ID_FROM_STEP_2>",
    "destinationAccountId": "<DESTINATION_ACCOUNT_ID>",
    "amount": 500000,
    "currency": "VND",
    "idempotencyKey": "demo-transfer-req-001",
    "description": "Test transfer"
  }'
```

---

## Ghi Log & Khả năng Quan sát (Observability)
Chi tiết hướng dẫn hạ tầng Kubernetes và Logging xem tại [`docs/K8S_LOCAL_GUIDE.md`](../docs/K8S_LOCAL_GUIDE.md).

Quy tắc bắt buộc đối với Developer khi phát triển Corebank:
1. Luôn sử dụng `@Slf4j`, tuyệt đối không dùng `System.out.println`.
2. Tuân thủ `setIncludePayload(false)` trong [`RequestLoggingConfig`](src/main/java/com/hieu/corebank/shared/infrastructure/web/RequestLoggingConfig.java) để không in plain text nội dung payload nhạy cảm ra log.
3. Không ghi log chứa thông tin PII không che mờ (CCCD, số thẻ đầy đủ, số dư chi tiết).
4. Truyền đúng `traceId` và `correlationId` để phân tích truy vết tập trung trên Grafana Loki và Tempo.
