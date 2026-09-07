# Kế hoạch Application Design — MONEY-BANK

**Trạng thái:** `ARTIFACTS_COMPLETED_PENDING_REVIEW`  
**Đầu vào:** BRD revision 1, SRD 0.3, báo cáo Design Validation và các quyết định đã duyệt

## 1. Mục tiêu

Chuyển thiết kế mức hệ thống trong SRD thành component, contract, sequence và data responsibility đủ rõ để tách unit of work và lập kế hoạch implementation.

## 2. Workstream

| Thứ tự | Workstream | Artifact | Trạng thái |
|---:|---|---|---|
| 1 | Ranh giới container/component | `components.md` | `FOR_REVIEW` |
| 2 | Danh mục public/internal API và quyền sở hữu | `api-boundaries.md` | `FOR_REVIEW` |
| 3 | Luồng Proposal create/approve/reconcile | `proposal-sequences.md` | `FOR_REVIEW` |
| 4 | Luồng Transfer success/failure/unknown | `transfer-sequences.md` | `FOR_REVIEW` |
| 5 | IAM, token exchange và actor audit | `security-design.md` + PoC criteria mục 4.1 | `FOR_REVIEW` |
| 6 | Idempotency, state machine, Outbox/Inbox và Oracle transaction boundary | `data-and-consistency-design.md` | `FOR_REVIEW` |
| 7 | Ranh giới `common-service` và versioning contract | `shared-contracts.md` | `FOR_REVIEW` |
| 8 | Tổng hợp dependency và unit candidate | `unit-candidates.md` | `FOR_REVIEW` |

## 3. Nguyên tắc

- Thiết kế logic độc lập với Docker/Kubernetes/VM cho tới Infrastructure Design.
- Không public Profile/Corebank API cho Mobile/Web/CMS browser.
- Không dùng private IP, Redis hoặc Gateway trust thay authentication/authorization tại service đích.
- Không đưa entity/repository/domain workflow vào `common-service`.
- Không dùng check-then-act làm cơ chế idempotency quyết định.

## 4. Cổng hoàn thành

- Mọi component có owner, input/output và dependency direction.
- Mọi command có identity, authorization, idempotency và transaction boundary.
- Mọi timeout sau side effect có state/reconciliation path.
- IAM PoC criteria và open infrastructure assumptions được ghi rõ.
- Application Design được review trước Units Generation hoặc code implementation.

## 5. Trạng thái cổng hoàn thành

| Điều kiện | Trạng thái | Bằng chứng |
|---|---|---|
| Component có owner, input/output, chiều dependency | `MET` | `components.md` mục 4, 6 |
| Command có identity, authorization, idempotency, transaction boundary | `MET` | `api-boundaries.md` mục 5; `security-design.md` mục 3, 6; `data-and-consistency-design.md` mục 2, 3 |
| Timeout sau side effect có state/reconciliation path | `MET` | `transfer-sequences.md` mục 4, 5; `proposal-sequences.md` mục 5 |
| IAM PoC criteria và open assumption | `MET` | `security-design.md` mục 4.1; mục "Điểm mở" từng artifact |
| Review trước Units Generation | `PENDING` | Cần người dùng phê duyệt |

## 6. Ràng buộc còn hiệu lực

- Chưa được viết code implementation cho tới khi cổng review được thông qua.
- Các điểm P0 của Corebank (`OQ-P0-01..07`) vẫn chặn quyết định cuối của luồng chuyển tiền.
- PoC Standard Token Exchange V2 là điều kiện tiên quyết trước implementation IAM.

