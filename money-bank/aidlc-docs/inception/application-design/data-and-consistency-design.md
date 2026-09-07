# Thiết kế dữ liệu và tính nhất quán — MONEY-BANK

**Workstream:** 6 — Idempotency, state machine, Outbox/Inbox và transaction boundary
**Trạng thái:** `FOR_REVIEW`
**Cơ sở:** SRD mục 6.2, 10; ADR-02, ADR-03, ADR-06, ADR-08, ADR-10; DV-DEC-09, DV-DEC-10; DV-F-04, DV-F-05

## 1. Phạm vi dữ liệu Money Bank

Money Bank **không** lưu bản sao lịch sử tài chính. Relational DB chỉ giữ trạng thái kỹ thuật cần cho idempotency, workflow, reconciliation và Outbox/Inbox.

| Bảng logic | Mục đích | Không được dùng để |
|---|---|---|
| `idempotency_record` | Claim duy nhất một ý định client | Lưu payload nhạy cảm chưa sanitize |
| `transfer_transaction` | Workflow/reference tối thiểu của một giao dịch | Làm ledger hoặc lịch sử chính thức |
| `workflow_execution` | Bước, attempt, lease, backoff | Lưu business rule |
| `outbox_event` | Phát event nhất quán với state change | Làm event store dài hạn |
| `inbox_event` | Chặn event lặp | Thay thế business unique constraint |
| `audit_event` (projection) | Truy vết append-only | Thay Corebank làm nguồn hạch toán |

## 2. Idempotency: claim-first

### 2.1. Canonical request hash

Khắc phục DV-F-05. Quy tắc bắt buộc:

| ID | Quy tắc |
|---|---|
| AD-DC-H01 | Hash phải phủ toàn bộ field nghiệp vụ có ý nghĩa: `sourceAccount`, `destinationAccount`, `amount`, `currency` và `description` nếu thuộc contract. |
| AD-DC-H02 | Canonical hóa trước khi hash: sắp xếp key xác định, chuẩn hóa Unicode, trim theo quy tắc đã chốt, chuẩn hóa số tiền theo scale của currency. |
| AD-DC-H03 | Số tiền chuẩn hóa dạng decimal string, không dùng biểu diễn floating point. |
| AD-DC-H04 | Thuật toán SHA-256; lưu hash hex/base64, không lưu payload gốc. |
| AD-DC-H05 | Field không thuộc ý nghĩa nghiệp vụ (correlation, trace, user agent) không được vào hash. |
| AD-DC-H06 | Thay đổi định nghĩa canonical là breaking change: cần version hash và kế hoạch rollout. |

### 2.2. Thuật toán claim

```text
1. (Tùy chọn) Redis SET idem:{customerId}:{operation}:{key} = requestHash NX EX ttl
   - Nếu tồn tại và hash khác  -> trả 409 ngay (fast-path), vẫn phải xác nhận lại ở DB
   - Nếu Redis lỗi            -> bỏ qua fast-path, tiếp tục bước 2
2. TX-A (transaction ngắn):
   INSERT INTO idempotency_record(customer_id, operation, idempotency_key, request_hash,
                                  status='IN_PROGRESS', transaction_id, workflow_id, created_at)
   - saveAndFlush để constraint được kiểm tra ngay trong TX-A
   - INSERT transfer_transaction + workflow_execution trong cùng TX-A
   - Commit
3. Nếu TX-A ném unique violation:
   - TX-A đã rollback; KHÔNG tái sử dụng EntityManager của TX-A
   - TX-B (transaction mới, readOnly): SELECT theo (customer_id, operation, idempotency_key)
     - hash khác     -> 409 IDEMPOTENCY_CONFLICT
     - IN_PROGRESS   -> 202 PROCESSING + transactionId
     - kết quả cuối  -> trả response snapshot đã sanitize
     - không tìm thấy (race hiếm) -> retry bước 3 có giới hạn, sau đó 503
4. Claim thắng -> tiếp tục domain validate và gọi Corebank
```

Ràng buộc:

| ID | Quy tắc |
|---|---|
| AD-DC-C01 | Không `SELECT`-rồi-`INSERT` làm thao tác quyết định (khắc phục DV-F-04, TOCTOU). |
| AD-DC-C02 | Unique constraint `(customer_id, operation, idempotency_key)` là lớp an toàn cuối (DV-DEC-10). |
| AD-DC-C03 | Redis chỉ là fast-path; Redis down không làm mất tính đúng đắn. |
| AD-DC-C04 | Không sao chép `INSERT IGNORE` của MySQL sang Oracle (DV-F-07). |
| AD-DC-C05 | Duplicate không bao giờ được trả `5xx` hay lộ mã lỗi database ra client. |
| AD-DC-C06 | `request_hash` và key là immutable sau claim. |
| AD-DC-C07 | Response snapshot lưu ở dạng đã sanitize, không chứa dữ liệu nhạy cảm. |

### 2.3. Ba lớp và phạm vi

| Lớp | Cơ chế | Bảo vệ | Bắt buộc |
|---|---|---|:---:|
| 1 | Redis `SET NX EX` | Double click, burst retry | Không (MVP có thể bỏ) |
| 2 | Unique constraint Money Bank DB | Trùng giao dịch bền vững | Có |
| 3 | `externalTransactionId` tại Corebank | Trùng hạch toán ở lõi | Có, phụ thuộc `OQ-P0-02` |
| Inbox | Unique `(consumer, event_id)` | Event lặp | Có khi có consumer |

## 3. Transaction boundary

| Use case | Trong cùng một local transaction | Ngoài transaction |
|---|---|---|
| Claim | `idempotency_record` + `transfer_transaction` + `workflow_execution` | Gọi Corebank |
| Chốt SUCCESS | Cập nhật transaction + workflow + `outbox_event` | Publish Kafka, ghi audit store |
| Chốt FAILED_FINAL | Cập nhật transaction + workflow + `outbox_event` | Publish Kafka |
| Chuyển UNKNOWN | Cập nhật transaction + workflow (`next_retry_at`) | Query Corebank |
| Consume event | `inbox_event` + business effect | Side effect ngoài DB (push/SMS) |
| Audit projection | Đọc outbox → ghi audit store idempotent | Không nằm trong transaction nghiệp vụ |

Ràng buộc:

| ID | Quy tắc |
|---|---|
| AD-DC-TX01 | Không gọi Corebank/Kafka/Redis bên trong một database transaction đang mở. |
| AD-DC-TX02 | Không dual-write relational DB + audit store trong cùng use case (ADR-10). |
| AD-DC-TX03 | Transaction ngắn; không giữ connection qua network call. |
| AD-DC-TX04 | Mọi transition state kiểm tra `currentStatus + version` (optimistic locking). |
| AD-DC-TX05 | Audit projector lỗi không rollback giao dịch Corebank đã xác nhận; Outbox giữ event để retry. |

## 4. State machine

### 4.1. Transaction

```text
RECEIVED -> VALIDATING -> PROCESSING
PROCESSING -> SUCCESS | FAILED_FINAL | UNKNOWN
UNKNOWN -> RECONCILING
RECONCILING -> SUCCESS | FAILED_FINAL | MANUAL_REVIEW
```

Bảng transition được phép (mọi cặp không có trong bảng là bất hợp lệ):

| Từ | Tới | Guard |
|---|---|---|
| `RECEIVED` | `VALIDATING` | Claim thành công |
| `VALIDATING` | `PROCESSING` | Invariant `TRF-INV-001..005` đạt |
| `VALIDATING` | `FAILED_FINAL` | Invariant vi phạm xác định |
| `PROCESSING` | `SUCCESS` | Corebank xác nhận trạng thái cuối |
| `PROCESSING` | `FAILED_FINAL` | Lỗi nghiệp vụ final |
| `PROCESSING` | `UNKNOWN` | Timeout/ambiguous sau khi gửi |
| `UNKNOWN` | `RECONCILING` | Job giành được lease |
| `RECONCILING` | `SUCCESS`/`FAILED_FINAL` | Kết quả query Corebank xác định |
| `RECONCILING` | `MANUAL_REVIEW` | Vượt ngưỡng attempt/SLA |
| `RECONCILING` | `UNKNOWN` | Chưa xác định, đặt lại `next_retry_at` |

Cấm: `SUCCESS → *`. Reversal/correction là giao dịch mới liên kết giao dịch gốc.

Nếu Corebank dùng `reserve/commit/release`, bổ sung `RESERVED`, `COMMITTING`, `RELEASING` và các state này chỉ phản ánh trạng thái Corebank.

### 4.2. Thực thi transition

- Một `TransactionStateMachine` tập trung, không rải `setStatus` khắp code.
- Transition trả về danh sách event cần ghi Outbox, để state change và event luôn đi cùng nhau.
- Update dạng `UPDATE ... WHERE transaction_id=? AND status=? AND version=?`; 0 dòng nghĩa là conflict, không phải thành công.

## 5. Lược đồ logic

### 5.1. `idempotency_record`

| Cột | Kiểu logic | Ràng buộc |
|---|---|---|
| `id` | PK kỹ thuật | Sequence/identity |
| `customer_id` | string | Not null |
| `operation` | string | Not null, ví dụ `TRANSFER_CREATE` |
| `idempotency_key` | string | Not null |
| `request_hash` | char(64) | Not null, immutable |
| `transaction_id` | string | Unique, nullable trước khi liên kết |
| `workflow_id` | string | Index |
| `status` | enum | `IN_PROGRESS`, `COMPLETED`, `FAILED` |
| `response_snapshot` | clob/json | Đã sanitize |
| `response_code` | string | Mã trả về đã ánh xạ |
| `created_at`/`updated_at` | timestamp UTC | Not null |
| `expires_at` | timestamp UTC | Theo retention tra soát, không theo Redis TTL |

Unique: `(customer_id, operation, idempotency_key)`.

### 5.2. `transfer_transaction`

| Cột | Ghi chú |
|---|---|
| `transaction_id` | PK, unique |
| `customer_id` | Index |
| `source_account_ref` | Masked/token; full number chỉ khi bắt buộc và phải mã hóa |
| `destination_account_ref` | Như trên |
| `amount` | Decimal có scale xác định |
| `currency` | ISO 4217 |
| `description_sanitized` | Đã lọc |
| `status` | Theo state machine |
| `corebank_reference` | Nullable, index cho reconciliation |
| `version` | Optimistic locking |
| `created_at`/`updated_at` | UTC |

Không lưu `authenticationEvidence` dạng rõ.

### 5.3. `workflow_execution`

| Cột | Ghi chú |
|---|---|
| `workflow_id` | PK |
| `transaction_id` | FK, index |
| `current_step`/`status` | Bước hiện tại |
| `attempt_count` | Giới hạn theo policy |
| `next_retry_at` | Index cùng `status` |
| `lease_owner`/`lease_expires_at` | Chống hai worker cùng xử lý |
| `last_error_code` | Mã an toàn, không stack trace |
| `version` | Optimistic locking |

Index vận hành: `(status, next_retry_at)`, `(lease_expires_at)`.

### 5.4. `outbox_event` và `inbox_event`

| Bảng | Khóa | Ghi chú |
|---|---|---|
| `outbox_event` | PK `event_id`; index `(published_at IS NULL, occurred_at)` | `type`, `schema_version`, payload tối thiểu, `attempt`, `occurred_at`, `published_at` |
| `inbox_event` | Unique `(consumer, event_id)` | `processed_at`, `result` |

Ràng buộc:

| ID | Quy tắc |
|---|---|
| AD-DC-O01 | Outbox ghi cùng transaction nghiệp vụ; publisher là tiến trình riêng có retry. |
| AD-DC-O02 | Publisher at-least-once; consumer phải idempotent. Kafka `enable.idempotence` không thay Inbox. |
| AD-DC-O03 | Inbox insert atomic trong cùng local transaction với business effect. |
| AD-DC-O04 | Duplicate event đã xử lý phải được acknowledge mà không lặp hiệu ứng. |
| AD-DC-O05 | Side effect ngoài DB (push/SMS) cần provider idempotency key riêng; Inbox không tạo exactly-once cho external call. |
| AD-DC-O06 | Payload event không chứa full account number/PII không cần thiết; có `schema_version`. |

### 5.5. `audit_event` (projection)

- Unique `event_id`, append-only, tạo từ Outbox.
- Field theo `security-design.md` mục 8.
- Application role không có quyền update/delete.
- Nếu không có nền tảng MongoDB, dùng append-only store khác nhưng giữ nguyên contract audit (AD-C-OPEN-02).

## 6. Quy tắc dữ liệu chung

| ID | Quy tắc |
|---|---|
| AD-DC-D01 | Amount dùng decimal/number có scale xác định; cấm binary floating point trong domain. |
| AD-DC-D02 | Timestamp lưu UTC ISO 8601; UI chuyển múi giờ. |
| AD-DC-D03 | Full account number chỉ lưu khi thật cần cho integration/reconciliation, phải mã hóa ở cấp ứng dụng/cột. |
| AD-DC-D04 | Read model và log chỉ dùng masked value hoặc token/reference. |
| AD-DC-D05 | Không cascade delete cho transaction, idempotency, outbox, audit. |
| AD-DC-D06 | Purge/archive theo retention policy, có phê duyệt và audit. |
| AD-DC-D07 | Migration backward-compatible theo expand-migrate-contract. |
| AD-DC-D08 | Index theo use case đã duyệt; không index payload JSON tùy tiện. |

## 7. Kiểm chứng bắt buộc

| ID | Test |
|---|---|
| AD-DC-T01 | 100 request đồng thời cùng key/cùng hash: một transaction, mọi request nhận cùng `transactionId` hoặc `PROCESSING` |
| AD-DC-T02 | Duplicate race không rò `ORA-00001`/`500` |
| AD-DC-T03 | Cùng key khác hash luôn `409` |
| AD-DC-T04 | Canonical hash đổi khi bất kỳ field nghiệp vụ có ý nghĩa đổi, kể cả `description` nếu thuộc contract |
| AD-DC-T05 | Crash trước/sau commit ở từng điểm: outbox vẫn phát đúng một hiệu ứng nghiệp vụ |
| AD-DC-T06 | Redis down/restart không tạo giao dịch trùng |
| AD-DC-T07 | Hai worker reconcile cùng transaction: chỉ một xử lý nhờ lease/`SKIP LOCKED` |
| AD-DC-T08 | Cùng `eventId` redelivery đồng thời: một business effect nhờ Inbox unique |
| AD-DC-T09 | Transition bất hợp lệ bị từ chối, gồm `SUCCESS → FAILED` |
| AD-DC-T10 | Audit record không thể update/delete bằng application role |

## 8. Điểm mở

| ID | Nội dung | Phụ thuộc |
|---|---|---|
| AD-DC-OPEN-01 | `description` có nằm trong canonical hash | AD-API-OPEN-01 |
| AD-DC-OPEN-02 | Retention cho transaction/idempotency/audit | `OQ-P1-06` |
| AD-DC-OPEN-03 | Oracle là chuẩn tổ chức hay chỉ dependency demo; ảnh hưởng DDL/partition | `OQ-P1-12` |
| AD-DC-OPEN-04 | Redis fast-path có thuộc MVP | Load test |
| AD-DC-OPEN-05 | Audit store cụ thể | AD-C-OPEN-02 |
| AD-DC-OPEN-06 | Physical DDL, partition, tablespace | Data/Infrastructure Design |
