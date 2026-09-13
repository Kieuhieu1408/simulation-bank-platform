# Corebank

Corebank là service trung tâm của `simulation-bank-platform`, chịu trách nhiệm lưu trữ tài khoản thanh toán, thẻ, số dư và lịch sử giao dịch. Đây là bản mô phỏng phục vụ học tập; không xử lý dữ liệu ngân hàng thật.

## Phạm vi chức năng MVP

- Tạo và tra cứu tài khoản thanh toán.
- Tạo và tra cứu thẻ mô phỏng gắn với tài khoản.
- Tra cứu số dư.
- Chuyển tiền nội bộ giữa hai tài khoản.
- Tra cứu lịch sử giao dịch.
- Kiểm tra trạng thái service.

## API dự kiến

Base URL: `/api/v1`

### Tài khoản

`POST /customers` — tạo CIF, mã định danh duy nhất của khách hàng.

`POST /accounts` — tạo tài khoản thanh toán gắn với một CIF đã có.

Request tối thiểu: `cifNumber`, `currency`.

Response: `accountId`, `accountNumber`, `cifNumber`, `currency`, `balance`, `status`, `createdAt`.

`GET /accounts/{accountId}` — lấy thông tin tài khoản và số dư hiện tại.

`GET /accounts/{accountId}/balance` — lấy số dư hiện tại.

`GET /customers/{cifNumber}/accounts` — lấy mọi tài khoản thanh toán của CIF.

`GET /customers/{cifNumber}/cards` — lấy mọi thẻ của CIF.

### Thẻ

`POST /accounts/{accountId}/cards` — phát hành thẻ mô phỏng cho tài khoản.

Response: `cardId`, `maskedCardNumber`, `accountId`, `status`, `createdAt`. Trả về số CVV thật do đây là demo.

`GET /cards/{cardId}` — tra cứu trạng thái thẻ và tài khoản liên kết.

### Chuyển tiền

`POST /transfers` — chuyển tiền nội bộ.

Request tối thiểu: `sourceAccountId`, `destinationAccountId`, `amount`, `currency`, `idempotencyKey`, `description`.

Response: `transactionId`, `status`, `sourceAccountId`, `destinationAccountId`, `amount`, `currency`, `createdAt`.

Các trạng thái MVP: `SUCCESS`, `FAILED`, `REVERSED`. `idempotencyKey` bắt buộc để một yêu cầu gửi lại không trừ tiền hai lần.

### Lịch sử giao dịch

`GET /accounts/{accountId}/transactions?from=&to=&page=&size=` — trả danh sách giao dịch theo thời gian, có phân trang.

### Vận hành

`GET /actuator/health` — health check cho Docker và các service khác.

## Dữ liệu chính

- `customers`: CIF là mã định danh duy nhất của khách hàng.
- `accounts`: số tài khoản, CIF sở hữu, loại tiền, số dư, trạng thái. Một CIF có thể có nhiều tài khoản.
- `cards`: số thẻ được mã hóa/masked, tài khoản liên kết, trạng thái.
- `transactions`: mã giao dịch, tài khoản nguồn/đích, số tiền, trạng thái, idempotency key, thời gian.

Tiền lưu bằng số nguyên đơn vị nhỏ nhất hoặc `BigDecimal` theo một quy ước thống nhất; không dùng `float/double`.

## Công nghệ và Docker

- Backend: Java Spring Boot 4.
- Database: Oracle Database chạy trong Docker cho bản demo.
- Đóng gói: Dockerfile cho ứng dụng và Docker Compose cho app + database oracle.
- Khi container database khởi động, schema và dữ liệu mẫu được tạo tự động.
- Tài khoản/mật khẩu database hardcode trong cấu hình demo chỉ để chạy local; phải thay bằng Docker secrets hoặc biến môi trường trước khi dùng ngoài môi trường demo.

## Chạy demo một lệnh

Yêu cầu duy nhất là Docker Desktop đang chạy. Tại thư mục `corebank`, chạy:

```bash
docker compose up --build
```

Lần đầu Oracle cần một ít thời gian để khởi tạo. Khi log có dòng Spring Boot đã chạy, API sẵn sàng tại `http://localhost:8180`.

Cổng host mặc định là `8180` để tránh va chạm với các ứng dụng thường dùng `8080`. Nếu cần cổng khác, chạy ví dụ `APP_PORT=8090 docker compose up --build`.

Database test đã cố định thông tin đăng nhập:

| Thành phần | Giá trị |
| --- | --- |
| Host / port | `localhost:1521` |
| Service name | `FREEPDB1` |
| App username | `corebank` |
| App password | `corebank_demo` |
| Oracle SYSTEM password | `oracle_demo` |

Compose giữ dữ liệu trong volume `oracle-data`; các lần khởi động sau chỉ cần `docker compose up`. Dừng container bằng `docker compose down`. Nếu muốn xóa hẳn dữ liệu demo để tạo lại từ đầu, dùng `docker compose down -v`.

Sau thay đổi mô hình từ `ownerId` sang CIF, nếu bạn đã từng chạy bản database cũ thì chạy `docker compose down -v` một lần trước khi khởi động lại.

Hai tài khoản mẫu được tạo tự động khi database trống:

- `CIF00000001`: 2 tài khoản, số dư `10,000,000.00 VND` và `5,000,000.00 VND`
- `CIF00000002`: 1 tài khoản, số dư `5,000,000.00 VND`

Tạo một tài khoản để lấy `accountId` và test các API khác:

```bash
curl -X POST http://localhost:8180/api/v1/customers \
  -H 'Content-Type: application/json' \
  -d '{"cifNumber":"CIFTEST001"}'
curl -X POST http://localhost:8180/api/v1/accounts \
  -H 'Content-Type: application/json' \
  -d '{"cifNumber":"CIFTEST001","currency":"VND","initialBalance":1000000}'
```

Health check: `http://localhost:8180/actuator/health`.

## Nguyên tắc thiết kế (Cloud-Native & Distributed System)

- **Source of Truth & ODS (Read Replica):** Corebank là nơi duy nhất giữ sổ cái kế toán và lịch sử giao dịch. Để chịu tải cho 10 triệu người dùng lướt xem lịch sử mà không sập hệ thống Ledger, Corebank sẽ triển khai mô hình **CQRS nội bộ** thông qua một Cụm Oracle Đọc (Operational Data Store - ODS / Active Data Guard).
- **Query API cho Vệ tinh:** Các service như `money-bank` đóng vai trò là BFF, sẽ gọi trực tiếp vào API Đọc của Corebank (chọc vào ODS) để lấy lịch sử. Lịch sử không bao giờ được phép copy ra ngoài Corebank.
- **Race Condition & Locking:** Tạo chuyển tiền và ghi sổ giao dịch trong cùng một database transaction. Bắt buộc dùng Optimistic Locking (hoặc Pessimistic Locking) để khóa dòng số dư.
- **Idempotency (Luỹ đẳng):** Mọi API thay đổi trạng thái (như `POST /transfers`) phải kiểm tra `idempotencyKey` để chống trừ tiền đúp.
- **Distributed Transaction:** Corebank nhận request từ Cadence Workflow / Kafka của các service vệ tinh. Dùng Outbox Pattern để báo kết quả.
- **Database Bottleneck:** Oracle Active Data Guard sẽ giải quyết nút thắt cổ chai cho các tác vụ Query nặng.

## Hướng dẫn Ghi Log & Observability
> ⚠️ Xem tài liệu đầy đủ tại `../../devOps.md` ở root dự án.

Khi code `corebank`, Dev lưu ý:
1. Luôn sử dụng `@Slf4j` cho việc ghi log, tránh dùng `System.out.println`.
2. Truyền đúng `correlationId`, `traceId` và các Context ID khác để liên kết log với các service gọi tới (như Money Bank).
3. Đảm bảo cấu hình log ra định dạng JSON để agent thu thập về Loki.
