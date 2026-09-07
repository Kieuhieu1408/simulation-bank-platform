# Sequence luồng Proposal — MONEY-BANK

**Workstream:** 3 — Proposal create/approve/reconcile
**Trạng thái:** `FOR_REVIEW`
**Cơ sở:** BRD 3.1.1–3.1.6, SRD mục 5, DV-DEC-04, DV-DEC-08, DV-DEC-09

## 1. Phân định quyết định

| Quyết định | Chủ sở hữu |
|---|---|
| Nhận request kênh khách hàng, token exchange | Money Bank |
| Ownership, role, state/version, maker-checker | Profile Service |
| Durable idempotency của Proposal | Profile Service |
| Ghi `proposal_log` | Profile Service |
| Thực thi Corebank sau phê duyệt | Profile Service |
| Màn hình và thu nhận quyết định GDV | CMS Angular + CMS Backend |

## 2. Tạo Proposal

```mermaid
sequenceDiagram
  actor U as Mobile/Web
  participant G as Gateway
  participant M as Money Bank
  participant KC as Keycloak
  participant P as Profile Service
  participant PD as Profile DB

  U->>G: POST /api/v1/account-proposals + Idempotency-Key
  G->>M: Forward (đã strip header định danh không tin cậy)
  M->>M: Xác minh token aud=money-bank, validate schema
  M->>KC: Token Exchange V2 (aud=profile-service)
  KC-->>M: Exchanged token (user sub + actor claim)
  M->>P: POST /internal/v1/account-proposals + cùng Idempotency-Key
  P->>P: Xác minh issuer/aud/expiry/scope, ownership customer
  P->>PD: Atomic claim idempotency + insert Proposal PENDING_APPROVAL
  alt Claim mới
    PD-->>P: proposalId
    P->>PD: Ghi proposal_log + outbox trong cùng transaction
    P-->>M: 201 proposalId, PENDING_APPROVAL
  else Cùng key, cùng hash
    PD-->>P: Duplicate constraint
    P->>PD: Đọc Proposal hiện có (transaction mới)
    P-->>M: 200 kết quả cũ
  else Cùng key, khác hash
    P-->>M: 409 IDEMPOTENCY_CONFLICT
  end
  M-->>U: Kết quả đã ánh xạ error catalog
```

Ràng buộc:

- `AD-PR-R01`: Money Bank không sinh `Idempotency-Key` thay client và không biến đổi key khi forward.
- `AD-PR-R02`: Nếu Keycloak token exchange lỗi, Money Bank trả `503`/`401` theo phân loại và không fallback header user (SRD 9.2).
- `AD-PR-R03`: Profile claim bằng atomic insert, không `SELECT` rồi insert.
- `AD-PR-R04`: `proposal_log` và `outbox_event` ghi cùng local transaction với state change (`ACC-CMD-008`).

## 3. Phê duyệt/từ chối trên CMS

```mermaid
sequenceDiagram
  actor GDV as GDV
  participant A as CMS Angular
  participant G as Gateway
  participant CB as CMS Backend
  participant KC as Keycloak
  participant P as Profile Service
  participant PD as Profile DB

  GDV->>A: Chọn Proposal và ra quyết định
  A->>G: POST /cms/api/v1/proposals/{id}/decisions
  G->>CB: Forward
  CB->>KC: Token Exchange V2 (aud=profile-service)
  CB->>P: POST /internal/v1/proposals/{id}/decisions (expectedVersion)
  P->>P: RBAC + ABAC: role GDV, branch/amount, maker != checker
  P->>PD: UPDATE ... WHERE status=PENDING_APPROVAL AND version=expectedVersion
  alt Cập nhật 1 dòng
    P->>PD: proposal_log + outbox (cùng transaction)
    P-->>CB: 200 APPROVED hoặc REJECTED
  else Cập nhật 0 dòng
    P-->>CB: 409 STATE_OR_VERSION_CONFLICT
  end
  CB-->>A: Kết quả
```

Ràng buộc:

- `AD-PR-R05`: Chỉ quyết định hợp lệ đầu tiên được ghi nhận (BRD 3.1.5); hai GDV đồng thời thì một bên nhận `409`.
- `AD-PR-R06`: Lý do là bắt buộc khi từ chối.
- `AD-PR-R07`: Maker không tự approve nếu chính sách maker-checker áp dụng (`ACC-CMD-005`); phạm vi chính sách chờ `OQ-P1-07`.
- `AD-PR-R08`: CMS Backend không gọi Corebank và không ghi Profile DB.

## 4. Thực thi Corebank sau phê duyệt

```mermaid
sequenceDiagram
  participant P as Profile Service
  participant PD as Profile DB
  participant C as Corebank

  P->>PD: APPROVED -> PROCESSING (guard status+version)
  P->>C: Operation TKTT với externalOperationId = proposalId
  alt Thành công
    C-->>P: OK + corebankReference
    P->>PD: COMPLETED + log + outbox
  else Lỗi nghiệp vụ xác định
    C-->>P: Business error final
    P->>PD: FAILED_FINAL + reason
  else Lỗi kỹ thuật retryable
    C-->>P: Retryable error
    P->>PD: FAILED_RETRYABLE + next_retry_at
  else Timeout / mất response
    P->>PD: UNKNOWN
    P->>C: Query theo externalOperationId
  end
```

Ràng buộc:

- `AD-PR-R09`: `externalOperationId = proposalId`, không tạo operation mới khi retry (`ACC-CMD-006`, `ACC-CMD-007`).
- `AD-PR-R10`: Timeout chuyển `UNKNOWN`, không gửi lệnh create mới (SRD 5.4).
- `AD-PR-R11`: Chỉ lỗi được phân loại retryable mới được retry.

## 5. Reconcile Proposal `UNKNOWN`

```mermaid
sequenceDiagram
  participant J as Reconcile Job (Profile)
  participant PD as Profile DB
  participant C as Corebank
  participant O as Ops queue

  J->>PD: Lấy Proposal UNKNOWN/RECONCILING theo next_retry_at (lease/SKIP LOCKED)
  J->>C: Query theo externalOperationId
  alt Corebank đã thực hiện
    C-->>J: Kết quả + reference
    J->>PD: COMPLETED, liên kết tài khoản đã tạo với Proposal cũ
  else Corebank chưa thực hiện
    C-->>J: Not found
    J->>PD: FAILED_RETRYABLE hoặc FAILED_FINAL theo phân loại
  else Vượt ngưỡng attempt/thời gian
    J->>PD: MANUAL_REVIEW
    J->>O: Alert có RBAC + audit
  end
```

## 6. Trạng thái và transition được phép

```text
PENDING_APPROVAL -> REJECTED | CANCELLED | APPROVED
APPROVED         -> PROCESSING
PROCESSING       -> COMPLETED | FAILED_RETRYABLE | FAILED_FINAL | UNKNOWN
FAILED_RETRYABLE -> PROCESSING | FAILED_FINAL
UNKNOWN          -> RECONCILING
RECONCILING      -> COMPLETED | FAILED_FINAL | MANUAL_REVIEW
```

- Terminal: `REJECTED`, `CANCELLED`, `COMPLETED`, `FAILED_FINAL`.
- Mọi transition kiểm tra `currentStatus + version`.
- Không xóa vật lý Proposal/log (BRD 3.1.3).
- `CANCELLED` chỉ tồn tại nếu nghiệp vụ cho phép hủy; chờ `OQ-P1-08`.

## 7. Ma trận lỗi phía Money Bank

| Tình huống Profile | Money Bank trả client | Ghi chú |
|---|---|---|
| `409` idempotency conflict | `409 MB-ACC-409-*` | Không tạo lại request |
| `409` state/version conflict | `409 MB-ACC-409-*` | Client refresh trạng thái |
| `403` không sở hữu | `403` hoặc `404` | Không lộ tồn tại dữ liệu người khác |
| `401` token sai audience | `500`/`503` nội bộ + alert | Lỗi cấu hình phía Money Bank, không phải lỗi client |
| Timeout Profile | `202` + hướng dẫn tra cứu `proposalId` nếu đã sinh, ngược lại `503` | Không tự tạo Proposal thứ hai |
| Profile unavailable | `503 MB-CMN-503-*` | Circuit breaker + bulkhead |

## 8. Điểm mở

| ID | Nội dung | Phụ thuộc |
|---|---|---|
| AD-PR-OPEN-01 | Cho phép khách hàng hủy Proposal hay không | `OQ-P1-08` |
| AD-PR-OPEN-02 | Phạm vi maker-checker và thẩm quyền GDV theo branch/amount | `OQ-P1-07` |
| AD-PR-OPEN-03 | Danh sách field create/update và điều kiện close | `OQ-P1-09` |
| AD-PR-OPEN-04 | Corebank có hỗ trợ query theo `externalOperationId` cho nghiệp vụ TKTT | `OQ-P0-02` |
