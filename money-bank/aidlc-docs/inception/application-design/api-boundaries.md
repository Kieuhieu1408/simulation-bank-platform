# Ranh giới API và quyền sở hữu — MONEY-BANK

**Workstream:** 2 — Danh mục public/internal API và quyền sở hữu
**Trạng thái:** `FOR_REVIEW`
**Cơ sở:** SRD mục 13, ADR-11, ADR-12, ADR-13, DV-DEC-02..05

## 1. Phân lớp API

| Lớp | Định nghĩa | Ai được gọi | Đường đi |
|---|---|---|---|
| `PUBLIC_CHANNEL` | API kênh khách hàng | Mobile/Web đã đăng nhập | Client → Gateway → Money Bank |
| `PUBLIC_CMS` | API kênh vận hành | CMS Angular của GDV | Browser → Gateway → CMS Backend |
| `INTERNAL_EAST_WEST` | API giữa service | Service identity đúng audience | Private DNS, không qua Gateway |
| `MANAGEMENT` | Health/metric/actuator | Management network | Không public |
| `EVENT` | Kafka topic | Producer/consumer nội bộ có ACL | Broker |

Gateway không route `/internal/**` và không route `/actuator/**`.

## 2. Danh mục API do Money Bank sở hữu

### 2.1. Command

| API | Lớp | Scope yêu cầu | Idempotency | Mã HTTP thành công |
|---|---|---|---|---|
| `POST /api/v1/transfers` | `PUBLIC_CHANNEL` | `transfer:create` | `Idempotency-Key` bắt buộc | `200` kết quả cuối, `202` chưa cuối |
| `POST /api/v1/account-proposals` | `PUBLIC_CHANNEL` | `account:proposal:create` | `Idempotency-Key` bắt buộc, truyền nguyên vẹn sang Profile | `201`/`202` |
| `POST /internal/v1/transfers/{transactionId}/reconcile` | `INTERNAL_EAST_WEST` | `transfer:reconcile` | Theo `transactionId` | `200`/`202` |

### 2.2. Query

| API | Lớp | Scope yêu cầu | Nguồn dữ liệu |
|---|---|---|---|
| `GET /api/v1/transfers/{transactionId}` | `PUBLIC_CHANNEL` | `transfer:read` | Money Bank DB (workflow state) |
| `GET /api/v1/accounts` | `PUBLIC_CHANNEL` | `account:read` | Corebank |
| `GET /api/v1/accounts/{accountId}/balance` | `PUBLIC_CHANNEL` | `account:read` | Corebank, không dùng cache cũ để quyết định |
| `GET /api/v1/beneficiaries/verify` | `PUBLIC_CHANNEL` | `account:read` | Corebank, rate limit + chống enumeration |
| `GET /api/v1/account-proposals/{proposalId}` | `PUBLIC_CHANNEL` | `account:proposal:read` | Profile Service |
| `GET /api/v1/transactions` | `PUBLIC_CHANNEL` | `transaction:read` | Corebank (ADR-10) |

Lưu ý quyền sở hữu: `GET /api/v1/transfers/{id}` trả trạng thái workflow của Money Bank, không phải bút toán chính thức. Lịch sử tài chính luôn lấy từ Corebank.

## 3. API Money Bank tiêu thụ

| API đích | Chủ sở hữu | Loại token | Audience | Ghi chú |
|---|---|---|---|---|
| `POST /internal/v1/account-proposals` | Profile Service | Exchanged token (V2) | `profile-service` | Giữ user `sub`, ràng buộc calling client |
| `GET /internal/v1/account-proposals/{id}` | Profile Service | Exchanged token | `profile-service` | Ownership check cuối tại Profile |
| Corebank account/balance/beneficiary | Corebank | Theo contract Corebank | `corebank` | Read-only |
| Corebank transfer | Corebank | Theo contract Corebank | `corebank` | Bắt buộc `externalTransactionId` |
| Corebank query-by-reference | Corebank | Theo contract Corebank | `corebank` | Điều kiện cho auto recovery (`OQ-P0-02`) |
| Keycloak token exchange | Keycloak | Client credentials của Money Bank | Keycloak | Fail closed khi IAM lỗi |

## 4. API Money Bank không được sở hữu và không được gọi

| Ranh giới | Quy tắc |
|---|---|
| `POST /internal/v1/proposals/{id}/decisions` | Chỉ CMS Backend gọi; Money Bank không có quyền approve/reject |
| Profile DB | Không truy cập trực tiếp |
| Corebank cho nghiệp vụ TKTT sau phê duyệt | Profile Service gọi, không phải Money Bank |
| Notification/inbox/promotion | Notification Service sở hữu; Money Bank chỉ phát event |

## 5. Contract của `POST /api/v1/transfers`

### 5.1. Request

| Header | Bắt buộc | Quy tắc |
|---|:---:|---|
| `Authorization` | Y | Bearer token `aud=money-bank`, kiểm tra issuer/expiry/scope |
| `Idempotency-Key` | Y | UUID/ULID, unique theo `customerId + operation` |
| `X-Correlation-Id` | N | Nhận từ trusted edge, thiếu thì sinh mới |
| `traceparent` | N | W3C Trace Context |

| Field | Bắt buộc | Ghi chú |
|---|:---:|---|
| `sourceAccount` | Y | Ownership xác định từ identity context, không nhận `customerId` từ client |
| `destinationAccount` | Y | Đã qua verify beneficiary |
| `amount` | Y | Decimal có scale xác định, `> 0`, không floating point |
| `currency` | Y | ISO 4217, thuộc allow-list (`OQ-P1-09`) |
| `description` | TBD | Thuộc canonical hash nếu là phần của contract |
| `authenticationEvidence` | Y | Bind source/destination/amount/currency/nonce/expiry (`OQ-P0-06`) |

### 5.2. Response theo trạng thái claim

| Tình huống | HTTP | Body chính | Ghi chú |
|---|---:|---|---|
| Key mới, Corebank SUCCESS | `200` | `status=SUCCESS`, `transactionId`, `corebankReference` | Chỉ khi Corebank xác nhận |
| Key mới, Corebank từ chối nghiệp vụ | `422` | `status=FAILED_FINAL`, `code` đã ánh xạ | Không lộ raw error |
| Key mới, timeout/ambiguous | `202` | `status=UNKNOWN`, `transactionId`, `Location` | Không kết luận FAILED |
| Cùng key, cùng hash, đã xong | `200` | Response snapshot đã sanitize | Không tạo giao dịch mới |
| Cùng key, cùng hash, đang chạy | `202` | `status=PROCESSING` | Client polling |
| Cùng key, khác hash | `409` | `MB-TRF-409-001` | Từ chối tuyệt đối |
| Thiếu/sai token | `401` | `MB-AUT-401-*` | Fail closed |
| Không sở hữu source account | `403` hoặc `404` | `MB-AUT-403-*` | Không để lộ dữ liệu tài khoản |
| Vượt rate limit | `429` | `MB-CMN-429-*` | Có `Retry-After` |

Ràng buộc: không dùng `5xx` khi kết quả Corebank còn ambiguous. Duplicate race không được rò lỗi database (ví dụ `ORA-00001`) ra client.

## 6. Contract của `POST /api/v1/account-proposals`

Money Bank là facade, không phải nơi quyết định.

| Trách nhiệm | Money Bank | Profile Service |
|---|---|---|
| Validate schema/transport | Có | Có (lần cuối) |
| Kiểm tra token kênh | Có | Không áp dụng |
| Token exchange sang `aud=profile-service` | Có | Xác minh |
| Ownership/role/state/version của Proposal | Không | Có, quyết định cuối |
| Durable idempotency của Proposal | Không sở hữu | Có (DV-DEC-09) |
| Ghi `proposal_log` | Không | Có |

Money Bank truyền `Idempotency-Key` end-to-end không biến đổi và ánh xạ lỗi Profile theo error catalog trước khi trả client.

## 7. Contract chung

| Quy ước | Nội dung |
|---|---|
| Versioning | Version trong path; breaking change tạo version mới |
| Phân trang | Cursor hoặc page có sort ổn định, giới hạn page size, filter/sort theo allow-list |
| Timestamp | ISO 8601 UTC |
| Amount | Decimal có scale chốt hoặc string decimal; không binary floating point |
| Error envelope | `code`, `message`, `correlationId`, `details` |
| `202` | Bắt buộc kèm `transactionId`, trạng thái non-terminal và đường dẫn polling |
| Deny-by-default | Thiếu policy/principal thì từ chối |

## 8. Event do Money Bank phát

| Event | Trigger | Consumer dự kiến | Ghi chú |
|---|---|---|---|
| `TransferSucceeded` | Transaction sang `SUCCESS` | Notification, audit projector | Qua Outbox cùng transaction |
| `TransferFailedFinal` | Transaction sang `FAILED_FINAL` | Notification, audit projector | Không rollback nghiệp vụ khi consumer lỗi |
| `TransferAmbiguousDetected` | Transaction sang `UNKNOWN` | Observability/ops | Phục vụ alert `OPS-002` |
| `TransferManualReviewRequired` | Vượt ngưỡng reconciliation | Ops queue | Có RBAC và audit |

Mọi event có `eventId`, `schemaVersion` và payload tối thiểu; không chứa full account number/PII không cần thiết.

## 9. Điểm mở

| ID | Nội dung | Phụ thuộc |
|---|---|---|
| AD-API-OPEN-01 | `description` có thuộc canonical request hash hay không | Business + Corebank contract |
| AD-API-OPEN-02 | Contract `authenticationEvidence` cụ thể | `OQ-P0-06` |
| AD-API-OPEN-03 | `GET /api/v1/transactions` có cần projection để phân trang/tìm kiếm hay gọi trực tiếp Corebank | `OQ-P1-10` |
| AD-API-OPEN-04 | Tên/format chính xác của Corebank API | Tài liệu Corebank (TBD trong BRD 1.4) |
