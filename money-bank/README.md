# Money Bank Service

Dịch vụ lõi xử lý yêu cầu tài khoản thanh toán và điều phối chuyển tiền.

## Hướng dẫn Ghi Log & Truy vết (Observability) cho Developer

> ⚠️ **Quy tắc bắt buộc:** Để đảm bảo việc tìm lỗi trên Grafana (Loki/Tempo) nhanh chóng, mọi Developer khi code phải tuân thủ các quy tắc sau (Xem chi tiết tại file `../../devOps.md` ở root dự án).

1. **KHÔNG in log ra file/console bằng `System.out.println`.** Phải dùng `@Slf4j` (ví dụ: `log.info(...)`).
2. **Luôn chứa ID ngữ cảnh (MDC Context):** Các ID như `traceId`, `transactionId`, `correlationId`, `errorCode` sẽ tự động được log vào JSON. Đảm bảo bạn đã truyền / sử dụng đúng các biến này trong request/response.
3. **KHÔNG log thông tin nhạy cảm (PII/PCI):** Xóa hoặc mask (che) số tài khoản đầy đủ, số thẻ, mật khẩu, OTP trước khi gọi `log.info()`.
4. **Log lỗi kèm StackTrace:** Khi bắt Exception, hãy log toàn bộ lỗi để dễ debug: `log.error("Failed to process transfer", ex);`
5. **Trace xuyên Service:** Truyền header `traceId` / `correlationId` mỗi khi gọi sang service khác (Profile, Corebank).

**Troubleshooting nhanh:** Lên Grafana -> Mở Loki -> Query `{app="money-bank", level="ERROR"} |= "Mã lỗi hoặc ID giao dịch"` -> Lấy `traceId` -> Tìm trên Tempo.

## Project Structure (Kiến trúc)

Hệ thống `money-bank` hiện đã được chuyển đổi sang kiến trúc Layered + CQRS (tương tự như `corebank`) để tận dụng thư viện `common-service`.

Cấu trúc package lõi:
- **api/**: Giao tiếp HTTP (Controller Interface & Impl).
- **config/**: Cấu hình Security, OpenAPI, Database.
- **constant/**: Enums, hằng số.
- **domain/**: JPA Entities (Account, Transaction...).
- **dto/**: Request / Response Payload.
- **exception/**: Các lớp xử lý ngoại lệ và Error Handler toàn cục.
- **handler/**: CQRS Command / Query Handlers (Nơi chứa Business Logic chính).
- **pipeline/**: Interceptors của Mediator (Validation, Logging, Idempotency).
- **repository/**: Spring Data JPA Repository (Oracle/H2).
- **service/**: Các interface Service và Implementation.

Hệ thống sử dụng **JPA/Hibernate** làm ORM thay thế cho MongoDB, quản lý version schema bằng **Flyway**.
