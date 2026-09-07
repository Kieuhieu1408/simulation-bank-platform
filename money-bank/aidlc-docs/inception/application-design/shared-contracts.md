# Ranh giới `common-service` và versioning contract — MONEY-BANK

**Workstream:** 7 — Shared contracts
**Trạng thái:** `FOR_REVIEW`
**Cơ sở:** SRD mục 7.1, ADR-14, DV-DEC-11, DV-F-09

## 1. Bản chất

`common-service` là **Java/Maven library có version**, không phải Spring Boot runtime service, không có database, không có endpoint.

| Thuộc tính | Giá trị |
|---|---|
| Loại artifact | `jar` library |
| Runtime riêng | Không |
| Database | Không |
| Nghiệp vụ sở hữu | Không |
| Cách tiêu thụ | Dependency có version rõ ràng, không `SNAPSHOT` ở môi trường cần ổn định |

## 2. Nội dung được phép

| Nhóm | Ví dụ | Điều kiện |
|---|---|---|
| API DTO dùng chung | Request/response của internal API giữa ≥ 2 service | Có version, được cả producer và consumer thống nhất |
| Integration event DTO | `TransferSucceeded`, `ProposalStatusChanged` | Có `schemaVersion`, backward-compatible trong cửa sổ retention |
| Error envelope | `code`, `message`, `correlationId`, `details` | Ổn định, không nhúng logic |
| Pagination contract | Cursor/page request + response wrapper | Không chứa policy filter riêng của service |
| Correlation/trace metadata | Tên header, hằng số MDC key | Không chứa implementation của một stack cụ thể |
| Primitive ổn định | `Money`, `Currency`, `AccountRef`, masking utility | Không chứa fee/limit rule |
| Serialization convention | Cấu hình định dạng số/ngày, validation annotation | Không chứa mapping riêng của một bounded context |

## 3. Nội dung bị cấm

| Bị cấm | Lý do |
|---|---|
| JPA entity, repository, database migration | Ép chung schema, phá vỡ ADR-02 |
| State machine Proposal/Transfer | Domain rule của service cụ thể |
| Authorization policy, fee/limit rule | Quyết định thuộc service sở hữu dữ liệu |
| Domain service, workflow, saga | Tạo distributed monolith |
| Adapter DTO nội bộ không phải integration contract | Rò implementation qua ranh giới |
| Client/secret, cấu hình môi trường | Rủi ro bảo mật |
| Utility "kitchen sink" không có owner | Coupling không kiểm soát được |

## 4. Kiểm tra trước khi đưa một class vào `common-service`

```text
1. Có ≥ 2 service thực sự dùng chung ngay bây giờ?          Không -> giữ trong service
2. Nó là contract giữa các service hay implementation nội bộ? Implementation -> giữ lại
3. Nó có chứa quyết định nghiệp vụ nào không?                Có -> giữ lại
4. Nó có ràng buộc service vào một schema/DB/stack cụ thể?    Có -> giữ lại
5. Có ai chịu trách nhiệm versioning và contract test?        Không -> chưa đưa vào
```

Chỉ khi vượt cả 5 bước mới đưa vào library.

## 5. Versioning

| Loại thay đổi | Bump | Yêu cầu kèm theo |
|---|---|---|
| Thêm field optional, thêm class mới | Minor | Contract test cho consumer hiện có |
| Sửa docs/nội bộ không đổi contract | Patch | Không |
| Xóa/đổi tên field, đổi kiểu, đổi semantics | Major | Kế hoạch rollout tương thích, deprecation window, contract test hai chiều |
| Thay đổi `schemaVersion` của event | Major cho event, minor cho library | Consumer rollout trước producer nếu cần |

Ràng buộc:

| ID | Quy tắc |
|---|---|
| AD-SC-V01 | Breaking change phải tăng major version. |
| AD-SC-V02 | Service không bị buộc nâng `common-service` chỉ vì service khác đổi implementation. |
| AD-SC-V03 | Event schema có `schemaVersion`; migration backward-compatible trong cửa sổ retention và rollout consumer. |
| AD-SC-V04 | Có consumer/provider contract test cho mọi contract dùng chung. |
| AD-SC-V05 | Deprecation phải có annotation, ghi chú và thời hạn; không xóa im lặng. |
| AD-SC-V06 | Không dùng `SNAPSHOT` ở môi trường staging/production. |

## 6. Cấu trúc đề xuất

```text
common-service/
  contract/api/v1/        DTO của internal API dùng chung
  contract/event/v1/      Integration event DTO + schemaVersion
  contract/error/         Error envelope, error category
  contract/paging/        Pagination request/response
  primitive/money/        Money, Currency, scale rule
  primitive/reference/    AccountRef, masking
  observability/          Tên header, MDC key
```

Mỗi package version nằm trong đường dẫn để có thể tồn tại song song v1/v2 trong giai đoạn migration.

## 7. Ma trận tiêu thụ dự kiến

| Contract | Money Bank | Profile Service | CMS Backend | Notification |
|---|:---:|:---:|:---:|:---:|
| Proposal internal API DTO | Consumer | Provider | Consumer | — |
| `TransferSucceeded` / `TransferFailedFinal` | Producer | — | — | Consumer |
| `ProposalStatusChanged` | Consumer | Producer | — | Consumer |
| Error envelope | Có | Có | Có | Có |
| Pagination contract | Có | Có | Có | Có |
| `Money`/`Currency` | Có | Có | Có | — |
| Correlation/trace metadata | Có | Có | Có | Có |

Corebank DTO **không** vào `common-service`; adapter của Money Bank/Profile tự chuẩn hóa (anti-corruption layer).

## 8. Kiểm chứng

| ID | Test |
|---|---|
| AD-SC-T01 | Build fail nếu `common-service` phụ thuộc JPA/Spring Data hoặc chứa entity |
| AD-SC-T02 | Contract test provider/consumer cho Proposal internal API |
| AD-SC-T03 | Consumer version cũ vẫn deserialize được event sau minor change |
| AD-SC-T04 | Major change bị phát hiện bởi contract test, không lọt vào minor |
| AD-SC-T05 | Không có class Corebank DTO trong library |
| AD-SC-T06 | Architecture test kiểm chiều dependency: `domain` không phụ thuộc contract package ngoài primitive |

## 9. Điểm mở

| ID | Nội dung | Phụ thuộc |
|---|---|---|
| AD-SC-OPEN-01 | Cơ chế publish artifact (repository nội bộ, monorepo hay git submodule) | Infrastructure/Build Design |
| AD-SC-OPEN-02 | Công cụ contract test (Pact, Spring Cloud Contract hoặc test tự viết) | Test Design |
| AD-SC-OPEN-03 | Format serialization event (JSON schema, Avro, Protobuf) | `OQ-P1-12` |
| AD-SC-OPEN-04 | Ai là owner review thay đổi contract | Architecture governance |
