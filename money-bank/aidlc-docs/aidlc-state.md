# Trạng thái AI-DLC — MONEY-BANK

## Dự án

- **Dự án:** MONEY-BANK
- **Loại công việc:** Xác minh thiết kế trên dự án hiện hữu (brownfield)
- **Tài liệu nguồn:** `docs/brd.md`, `docs/srd.md`
- **Thời điểm bắt đầu:** 2026-07-12T23:01:48+07:00

## Trạng thái hiện tại

- **Giai đoạn:** `CONSTRUCTION` — Thiết kế chi tiết & Sinh mã
- **Bước:** Vòng lặp Unit — Vòng lặp Unit — Đang thực hiện U-06 (Transfer Command & Proposal State Machine)
- **Trạng thái:** `CONSTRUCTION_U06_IN_PROGRESS`
- **Công việc hoàn thành gần nhất:** 
  - U-05 Corebank Adapter hoàn tất.
  - Refactor toàn bộ CQRS Pipeline Behavior (tách Idempotency ra khỏi Handler) cho CẢ `money-bank` VÀ `corebank` (Đồng nhất kiến trúc).
  - Tích hợp Resilience4j (Circuit Breaker & Retry) vào `money-bank`.
  - Khởi tạo Grafana Dashboard (Spring Boot Observability).
- **Hoạt động hiện tại:** Thực hiện Functional Design cho U-06 (Transfer Command).
- **Cổng kiểm soát tiếp theo:** Functional Design cho U-06.

## Nguyên tắc kiểm soát

- Không chỉnh sửa `docs/brd.md` hoặc `docs/srd.md` trước khi cổng câu hỏi/câu trả lời được phê duyệt.
- Không coi phương án được khuyến nghị là quyết định kiến trúc đã được duyệt.
- Ghi nguyên văn câu trả lời mới của người dùng vào `audit.md` trước khi phân tích.
- Sau khi có câu trả lời, phải xác minh yêu cầu và thiết kế trước; đề xuất thay đổi trong bước riêng; chỉ chỉnh sửa SRD khi được phê duyệt rõ ràng.

## Cấu hình phần mở rộng

| Phần mở rộng | Trạng thái | Ghi chú |
|---|---|---|
| Baseline bảo mật | `NOT_CONFIGURED` — Chưa cấu hình | Bảo mật có ảnh hưởng trực tiếp tới task; chỉ kích hoạt/áp dụng sau khi xác định đúng nguồn quy tắc |
| Kiểm thử dựa trên thuộc tính | `NOT_CONFIGURED` — Chưa cấu hình | Chưa áp dụng ở bước lập kế hoạch; đánh giá lại khi thiết kế kiểm thử |

## Danh mục artifact

| Artifact | Mục đích | Trạng thái |
|---|---|---|
| `inception/plans/design-validation-plan.md` | Phạm vi, workflow và các cổng phê duyệt | `COMPLETED` — Đã hoàn thành |
| `inception/requirements/design-risk-register.md` | Rủi ro cần xử lý trước khi thay đổi thiết kế | `COMPLETED` — Đã hoàn thành |
| `inception/requirements/requirement-verification-questions.md` | Câu hỏi cần người dùng/các bên liên quan trả lời | `ANSWERED` — Đã trả lời |
| `inception/validation/design-validation-report.md` | Kết quả xác minh sau khi câu trả lời được phê duyệt | `COMPLETED_WITH_OPEN_ACTIONS` — Hoàn thành, còn action mở |
| `inception/application-design/application-design-plan.md` | Kế hoạch và cổng hoàn thành Application Design | `ARTIFACTS_COMPLETED_PENDING_REVIEW` — Chờ review |
| `inception/application-design/components.md` | Ranh giới container/component và chiều dependency | `FOR_REVIEW` — Chờ review |
| `inception/application-design/api-boundaries.md` | Danh mục API public/internal và quyền sở hữu | `FOR_REVIEW` — Chờ review |
| `inception/application-design/proposal-sequences.md` | Sequence create/approve/execute/reconcile Proposal | `FOR_REVIEW` — Chờ review |
| `inception/application-design/transfer-sequences.md` | Sequence chuyển tiền success/failure/unknown | `FOR_REVIEW` — Chờ review |
| `inception/application-design/security-design.md` | IAM, token exchange, PoC criteria và audit actor | `FOR_REVIEW` — Chờ review |
| `inception/application-design/data-and-consistency-design.md` | Idempotency, state machine, Outbox/Inbox, transaction boundary | `FOR_REVIEW` — Chờ review |
| `inception/application-design/shared-contracts.md` | Ranh giới `common-service` và versioning | `FOR_REVIEW` — Chờ review |
| `inception/application-design/unit-candidates.md` | Dependency tổng hợp và unit candidate | `FOR_REVIEW` — Chờ review |
