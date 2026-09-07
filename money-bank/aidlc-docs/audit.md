# Nhật ký kiểm toán AI-DLC — MONEY-BANK

## 2026-07-12T23:01:48+07:00 — Task xác minh thiết kế mới

### Nội dung nguyên văn của người dùng

> Chúng ta chưa có file đó bạn hay đọc brd.md và review srd.md và coi nó như 1 task mới áp dụng aidlc cũng sẽ có câu hỏi vậy các service cần api-gateway không? Nếu có sau khi api-gateway điều hướng đến money-bank mà money-bank cần gọi qua profile service khi này việc auth và authen giữa câc service nên như nào để private giữa các service hay sao

### Diễn giải ban đầu

- Bắt đầu một task xác minh thiết kế AI-DLC mới.
- Xác minh sự thống nhất giữa BRD và SRD.
- Làm rõ mô hình API Gateway và cơ chế xác thực/phân quyền giữa Money Bank → Profile Service.

### Hành động trước khi được điều chỉnh

- Chỉ đọc `docs/brd.md` và các phần liên quan trong `docs/srd.md`.
- Không chỉnh sửa SRD/BRD trong task xác minh này.

## 2026-07-12T23:01:48+07:00 — Điều chỉnh workflow

### Nội dung nguyên văn của người dùng

> Bạn đang đi sai kiến trúc aildlc chúng ta sẽ tạo các file và câu hỏi trước khi chỉnh sửa hay làm điều gì đó chúng ta lên plan và làm rõ các rủi ro trước

### Quyết định

- Dừng hoạt động chỉnh sửa thiết kế và triển khai.
- Lập kế hoạch, xác định rủi ro và tạo câu hỏi trước.
- Chờ câu trả lời/phê duyệt rõ ràng trước khi lập findings xác minh hoặc thay đổi tài liệu.

### Artifact đã tạo

- `aidlc-state.md`
- `inception/plans/design-validation-plan.md`
- `inception/requirements/design-risk-register.md`
- `inception/requirements/requirement-verification-questions.md`

## 2026-07-12 — Làm rõ tên service và module dùng chung

### Nội dung nguyên văn của người dùng

> money-bank là tên mới tôi chưa kịp cập nhập lại readme.md bạn hãy cập nhập tên mới và có thêm module common-service lưu các DTO hay các logic dùng chung giữa các service nhé. Và tiếp tục

### Quyết định được xác nhận

- `money-bank` là tên chính thức thay cho tên cũ `smartbank` trong tài liệu nền tảng.
- Bổ sung `common-service` làm module thư viện dùng chung, không phải runtime service độc lập và không sở hữu database.

### Hành động

- Cập nhật README gốc để thay `smartbank` bằng `money-bank`.
- Bổ sung trách nhiệm, công nghệ và ranh giới dependency cho `common-service`.
- Đồng bộ mô tả database Corebank với trạng thái demo Oracle đã ghi trong README và implementation hiện tại; lựa chọn production vẫn chưa được chốt.
- Không chỉnh sửa BRD/SRD.

## 2026-07-12 — Tiếp nhận câu trả lời và yêu cầu tư vấn

### Nội dung nguyên văn của người dùng

> Tôi đã trả lời các câu hỏi có những phàn thiết kế cần bạn tư vấn trong chat bạn hãy đọc readme.md để hiểu hơn nhé

### Câu trả lời đã nhận trong question file

- `VAL-001`: `A a` — được hiểu tạm thời là phê duyệt phương án A; cần chuẩn hóa câu trả lời.
- `ARC-001`: yêu cầu giải thích rõ API Gateway có phải là đường đi bắt buộc của toàn bộ service hay không.
- `ARC-002`: `A`.
- `ARC-003`: CMS phê duyệt; Profile lưu lịch sử và thông tin — đã xác nhận trách nhiệm nhưng chưa chọn topology A/B/C/D.
- `SEC-001`: yêu cầu tư vấn các phương án.
- `SEC-002`: phụ thuộc kết luận của `SEC-001`.
- `SEC-003`: `A`.
- `SEC-004`: yêu cầu tư vấn cách tốt nhất trước khi tạo câu hỏi tiếp theo.
- `SEC-005`: `D` — chưa quyết định nền tảng.
- `OPS-001`: `A`.
- `AUD-001`: `A`.
- `RES-001`: `A`.
- Ràng buộc bổ sung: chưa có câu trả lời.

### Ngữ cảnh thu được từ README gốc

- Đây là nền tảng mô phỏng phục vụ học tập, thử nghiệm nghiệp vụ và phát triển nhóm.
- Corebank là nguồn sự thật duy nhất cho số dư và lịch sử giao dịch; REST là giao tiếp MVP hiện tại.
- Các lựa chọn hạ tầng, API contract và kiến trúc chi tiết còn có thể thay đổi.
- Tên `smartbank` trong README là tên cũ; người dùng đã xác nhận tên mới là `money-bank`.

### Trạng thái xử lý

- Cổng kế hoạch đã được phê duyệt về ý định nhưng câu trả lời kiến trúc chưa hoàn tất.
- Chuyển sang tư vấn kiến trúc trong chat; chưa lập findings cuối và chưa chỉnh sửa BRD/SRD.

## 2026-07-12 — Đầu vào tham khảo về idempotency

### Nội dung người dùng cung cấp

- Kafka producer idempotence không bảo vệ consumer/database khỏi xử lý sự kiện lặp.
- Cảnh báo anti-pattern check-then-act/TOCTOU.
- Không dùng distributed lock/Redis làm lớp an toàn cuối cùng.
- Dùng database unique constraint làm bảo đảm bền vững.
- Đề xuất MySQL `INSERT IGNORE` và giới hạn concurrency/connection pool.

### Phạm vi validation

- Đối chiếu với SRD Money Bank và implementation Corebank hiện tại.
- Không thay đổi code, BRD hoặc SRD.
- Ghi chú công nghệ: dự án hiện dùng Oracle cho Corebank demo; `INSERT IGNORE` là giải pháp riêng của MySQL và không thể áp dụng trực tiếp.

## 2026-07-12 — Phê duyệt cập nhật tài liệu và chuyển bước

### Nội dung nguyên văn của người dùng

> Nếu không còn câu hỏi nào nữa bạn hãy cập nhập tài liệu chúng ta qua bước sau

### Diễn giải và quyết định

- Không còn câu hỏi blocking trong phạm vi validation hiện tại.
- Chuẩn hóa các câu trả lời còn diễn giải theo khuyến nghị đã trao đổi trong chat.
- Cho phép cập nhật question file, risk register, validation report và SRD.
- Chuyển workflow sang Application Design sau khi validation tài liệu hoàn tất.

### Giới hạn

- Chỉ cập nhật tài liệu; không sửa implementation.
- Các action IAM PoC, infrastructure selection và Corebank idempotency implementation vẫn là công việc của giai đoạn sau.

## 2026-07-31 — Tiếp tục workflow: hoàn thành artifact Application Design

### Nội dung nguyên văn của người dùng

> Bạn hãy tiếp tục dự án này

### Diễn giải

- Yêu cầu tiếp tục workflow AI-DLC tại điểm dừng gần nhất.
- Điểm dừng thực tế: Design Validation đã `COMPLETED_WITH_OPEN_ACTIONS`; `application-design-plan.md` ở `IN_PROGRESS` nhưng chưa có artifact workstream nào.
- Cổng kiểm soát hiện hành cấm viết code trước khi Application Design được review, nên tiếp tục bằng việc tạo artifact thiết kế.

### Artifact đã tạo

- `inception/application-design/components.md` — container/component, chiều dependency, mapping component ↔ yêu cầu.
- `inception/application-design/api-boundaries.md` — phân lớp API, contract `POST /transfers`, ranh giới facade Proposal, event phát ra.
- `inception/application-design/proposal-sequences.md` — sequence create/approve/execute/reconcile, bảng transition, ma trận lỗi.
- `inception/application-design/transfer-sequences.md` — ba giai đoạn claim/execute/settle, luồng ambiguous, phân loại lỗi Corebank.
- `inception/application-design/security-design.md` — client/audience/scope, token exchange, tiêu chí PoC, audit actor.
- `inception/application-design/data-and-consistency-design.md` — canonical hash, thuật toán claim-first, transaction boundary, lược đồ logic.
- `inception/application-design/shared-contracts.md` — ranh giới `common-service`, quy tắc versioning, ma trận tiêu thụ.
- `inception/application-design/unit-candidates.md` — 15 unit candidate, thứ tự phụ thuộc, unit bị chặn, cổng trước Units Generation.

### Artifact đã cập nhật

- `inception/application-design/application-design-plan.md` — trạng thái workstream, trạng thái cổng hoàn thành, ràng buộc còn hiệu lực.
- `aidlc-state.md` — đồng bộ giai đoạn và cổng kiểm soát tiếp theo.

### Giới hạn đã tuân thủ

- Không chỉnh sửa `docs/brd.md` và `docs/srd.md`.
- Không viết code implementation, không thêm dependency, không sửa `pom.xml`.
- Không biến quyết định tạm thời hoặc điểm mở thành business rule đã chốt; mọi giả định được ghi thành `AD-*-OPEN-*` có phụ thuộc rõ ràng.

### Cổng kiểm soát tiếp theo

- Người dùng review Application Design.
- Sau phê duyệt mới chuyển sang Units Generation hoặc PoC token exchange.
