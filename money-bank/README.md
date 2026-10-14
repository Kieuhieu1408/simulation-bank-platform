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

Hệ thống `money-bank` đóng vai trò là **Channel / BFF (Backend-For-Frontend) Service**, cung cấp API cho Mobile/Web và điều phối các yêu cầu giao dịch tài chính, hoàn toàn tách biệt trách nhiệm (bounded context) với `corebank`:
- **Corebank là System of Record (SoR)**: Sở hữu độc quyền dữ liệu và nghiệp vụ Tài khoản (`Account`), Khách hàng (`Customer`), Thẻ (`BankCard`), Hạch toán và Sổ cái (`Ledger`) theo kiến trúc Event Sourcing + CQRS.
- **Money Bank là Channel / BFF**: Không lưu trữ các entity nghiệp vụ core trong cơ sở dữ liệu nội bộ. Chỉ sở hữu và quản lý bảng Đề xuất chuyển tiền (`transfer_proposals`) và Transactional Outbox (`outbox_event`).
- **Giao tiếp S2S**: Mọi thao tác truy vấn tài khoản, số dư, sao kê, phát hành thẻ, và thực thi chuyển tiền đều được ủy quyền (delegate) sang `corebank` thông qua `CorebankClient` (sử dụng Spring WebClient với OAuth2 Client Credentials grant, role `ROLE_SERVICE_MONEYBANK`).

### Cơ chế Sinh Idempotency Key Server-side
- **Luồng 2 bước (Transfer Proposal - Khuyến nghị cho Mobile/Web)**:
  1. FE gửi ý định chuyển tiền (`POST /api/v1/transfers/proposals`) không cần sinh key.
  2. BE tự động sinh `proposalId` (UUID v4) và gán `idempotencyKey = proposalId`, lưu vào `transfer_proposals` với trạng thái `PENDING`.
  3. Sau khi xác thực OTP/Sinh trắc học (`POST /api/v1/transfers/proposals/{id}/confirm`), BE sử dụng chính `proposalId` làm idempotency key gửi sang `corebank`.
- **Luồng 1 bước (Direct Transfer - `/api/v1/transfers`)**:
  - `idempotencyKey` là trường tuỳ chọn. FE có thể truyền qua HTTP Header `Idempotency-Key` hoặc request body.
  - Nếu FE không truyền, BE tự động sinh UUID v4 server-side trước khi chuyển tiếp sang `corebank`, bảo vệ giao dịch không bị từ chối và ngăn ngừa duplicate processing.

Cấu trúc package lõi:
- **api/**: Giao tiếp HTTP (Controller Interface & Impl).
- **client/**: `CorebankClient` giao tiếp S2S với `corebank` qua WebClient + OAuth2.
- **config/**: Cấu hình Security, OpenAPI, WebClient, CorebankProperties.
- **constant/**: Enums, hằng số (ProposalStatus...).
- **domain/**: JPA Entities thuộc quyền sở hữu (`TransferProposal`).
- **dto/**: Request / Response Payload (tương thích JSON serialization Corebank).
- **exception/**: Các lớp xử lý ngoại lệ và Error Handler toàn cục.
- **handler/**: CQRS Command / Query Handlers (điều phối nghiệp vụ Channel).
- **repository/**: Spring Data JPA Repository (`TransferProposalRepository`).
- **service/**: Interface Service và Implementation (`TransferProposalCommandService`...).

Hệ thống sử dụng **JPA/Hibernate** với `spring.jpa.hibernate.ddl-auto: validate` kết nối Oracle DB, chỉ validate các bảng thuộc phạm vi sở hữu của Channel Service (`transfer_proposals`, `outbox_event`).
