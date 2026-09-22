# Feature Roadmap and Delivery Plan

**Phiên bản:** 0.1 — Đề xuất  
**Ngày:** 22/09/2026  
**Phạm vi:** prototype dữ liệu mô phỏng → CMS tích hợp → nghiệm thu → vận hành có kiểm soát.  
**Tham chiếu:** [Planning decisions](project-planning-decisions.md) · [SRD](../srd/cms-ai-assistant-srd.md).

Mọi tính năng dưới đây ở trạng thái **chưa xác nhận đã triển khai**. Tài liệu này chia công việc và điều kiện hoàn thành; không phải lịch cam kết. Chưa có số người/giờ làm việc mỗi tuần nên chưa gán tuần hoặc ngày hoàn thành.

## 1. Sáu phase lớn

| Phase | Câu hỏi cần trả lời | Các bước nhỏ | Đầu ra và gate |
| --- | --- | --- | --- |
| **0 — Scope and requirements** | Ta xây gì, cho ai và AI được quyết định gì? | 0.1 Xác nhận problem statement và vai trò. 0.2 Thu hẹp bốn miền theo SRD. 0.3 Tách AI/CMS/domain systems. 0.4 Ghi giả định và quyết định mở. 0.5 Thống nhất SRD v0.1 để thử nghiệm. | Phạm vi, owner, ranh giới và giả thuyết có người xác nhận. **G0:** cho phép chuẩn hóa nguồn và thử nghiệm trong phạm vi mô phỏng. |
| **1 — Knowledge and evaluation foundation** | Có nguồn và cách nhận biết đúng/sai chưa? | 1.1 Kiểm kê raw_data, tổ chức và xuất xứ. 1.2 Sửa mâu thuẫn chính sách mô phỏng. 1.3 Gắn metadata/quyền/hiệu lực/liên kết. 1.4 Chốt dữ kiện/đáp án 10 họ case. 1.5 Bổ sung ca quản trị, quyền, lỗi và phản hồi. 1.6 Tách dev/holdout; dựng đối chứng thủ công. | Corpus v0.1, manifest, fixture/rubric và kịch bản baseline. **G1:** các case dùng chấm có nguồn/đáp án được duyệt; không đưa raw chưa duyệt vào câu trả lời. |
| **2 — Prototype and experiments** | AI có xử lý tốt hơn cách đơn giản với chi phí phù hợp? | 2.1 Dựng workflow tối thiểu bằng adapter mô phỏng. 2.2 Chạy thử quan sát lỗi. 2.3 So baseline với tìm/đọc có LLM. 2.4 Chỉ thử retrieval/model khác theo lỗi quan sát. 2.5 Đo chất lượng, thời gian, công và chi phí. 2.6 Chốt kiến trúc/runtime hoặc thu hẹp. | Báo cáo run, error taxonomy, ADR và ngưỡng đã thống nhất. **G2:** đạt tiêu chí thử nghiệm đã chốt; chọn Go/Iterate/Reduce/Stop bằng bằng chứng. |
| **3 — CMS foundation and integration** | Người dùng hoàn thành được workflow từ chat đến yêu cầu và duyệt? | 3.1 Chốt O-02 và hợp đồng CMS. 3.2 Xây quyền, loại yêu cầu/form/đính kèm. 3.3 Xây tạo/gửi và đối soát chống trùng. 3.4 Xây danh sách và quyết định duyệt. 3.5 Tích hợp preview/prefill/citation. 3.6 Nối feedback, trace và kiểm thử lỗi. | Một luồng hoàn chỉnh trong CMS mô phỏng. **G3:** integration/contract tests đạt; có ID/trạng thái thật, không mất dữ liệu hoặc tạo trùng. |
| **4 — Acceptance and controlled pilot** | Đạt chất lượng và có ích khi người dùng kiểm chứng? | 4.1 Chốt cấu hình và corpus. 4.2 Chạy holdout/regression nhiều lượt. 4.3 Kiểm tra quyền/injection/trạng thái/timeout. 4.4 Đo tải theo profile đã chốt. 4.5 UAT cùng người dùng đại diện và đối chứng. 4.6 Diễn tập fallback/rollback. | Báo cáo nghiệm thu, giới hạn đã biết và quyết định pilot. **G4:** không còn lỗi nghiêm trọng quan sát được; đạt metric, có owner vận hành. Không có người dùng đại diện thì chỉ kết luận nghiệm thu kỹ thuật. |
| **5 — Operate and improve** | Chất lượng còn giữ khi nguồn và hệ thống thay đổi? | 5.1 Theo dõi lỗi/chi phí/latency và nguồn. 5.2 Triage feedback. 5.3 Bổ sung regression từ lỗi thật. 5.4 Sửa nguồn/prompt/tool đúng nguyên nhân. 5.5 Chạy eval trước phát hành. 5.6 Mở rộng nghiệp vụ theo bằng chứng. | Lịch review và các phiên bản có báo cáo so sánh. **G5:** mỗi release giữ gate chất lượng và kế hoạch khôi phục; không tự học từ feedback chưa duyệt. |

Phase 1 và một phần spike kỹ thuật có thể chạy song song, nhưng không chấm chính thức trên nguồn/đáp án chưa xác nhận. Có thể nghiên cứu schema CMS trong Phase 2; tích hợp hành vi tạo/duyệt phải chờ O-02.

## 2. Phân chia tính năng và phụ thuộc

Ưu tiên **P0:** cần cho luồng đầu tiên hoặc kiểm soát bắt buộc. **P1:** cần để hoàn thành phạm vi prototype bốn nhóm/nghiệm thu. **P2:** mở rộng sau bằng chứng; không nằm trên đường găng.

| ID | Nhóm tính năng và các việc nhỏ | Ưu tiên / phase | Phụ thuộc | Bằng chứng hoàn thành |
| --- | --- | --- | --- | --- |
| F01 | **Nguồn mô phỏng:** kiểm kê; tách chính sách và tham khảo; xử lý GAP-05–09; xác nhận điều khoản, tổ chức và owner. | P0 / 1 | O-01 | Nguồn có version/metadata và quyết định duyệt; mọi đáp án có điều khoản hỗ trợ. |
| F02 | **Nhập/rà soát nguồn:** đọc Markdown; báo lỗi chuyển đổi/thiếu phụ lục; AI đề xuất cấu trúc/ngoại lệ; admin xử lý. | P0 / 1–3 | F01 | Fixture thiếu metadata/phụ lục/AI lỗi không được công bố; phát hiện có vị trí nguồn. |
| F03 | **Vòng đời nguồn:** snapshot; hiệu lực/phạm vi; sửa một phần; lịch sử có điều kiện; cache invalidation và quyền. | P0 / 1–3 | F01, quyết định GAP-02 | Kiểm thử U03/04 và giữa hai lượt chat; lỗi cập nhật không tạo trạng thái kho dở dang. |
| F04 | **Eval và baseline:** schema case, rubric, dev/holdout, chấm code + người; manifest/run report; baseline tìm kiếm/form. | P0 / 1, xuyên suốt | F01, O-03 | Chạy lại được với cùng input/config; không lẫn nhãn holdout vào phát triển; báo tử số/mẫu số. |
| F05 | **Hỏi đáp có căn cứ:** intent; tìm nguồn; đọc mục/phụ lục; giới hạn bước; giải thích phần có và thiếu căn cứ. | P0 / 2 | F01, F03, F04 | Đạt case 1/4 và biến thể thiếu nguồn; không bịa điều kiện hoặc quy trình Paygate. |
| F06 | **Hội thoại theo tình huống:** facts/provenance; hỏi phần cần; giữ ngữ cảnh; vô hiệu nhận định khi dữ kiện đổi. | P0 / 2 | F05 | Case 2/3, sửa dữ kiện và ca đủ dữ kiện đều đúng; đo hỏi thừa. |
| F07 | **Chat/citation UI:** câu trả lời và nguồn; popup nguyên văn; trạng thái thiếu/mâu thuẫn; bàn phím và fallback tìm kiếm. | P0 / 2–3 | F05, CMS UI nền | Mở đúng version/anchor trong quyền; nguồn thay đổi có cảnh báo; lỗi AI vẫn đọc/tìm được tài liệu. |
| F08 | **Phí có căn cứ:** chọn policy theo hợp đồng/thời điểm; input typed; calculator xác định; công thức/biên/làm tròn. | P1 / 2–3 | F01, F04, F06, O-01 | Case 7/8 đủ-thiếu dữ kiện, whole-volume/progressive và điểm biên; đúng policy và số tiền. |
| F09 | **Identity và quyền CMS:** phiên; vai trò/đơn vị; scope hồ sơ/nguồn; thu hồi quyền; actor context giữa service. | P0 / 2 mock, 3 tích hợp | O-02 | U01/14 bằng backend; không dùng quyền gửi từ prompt; test trực tiếp endpoint ngoài UI. |
| F10 | **Nền proposal CMS:** danh mục/schema/validation; upload và quyền attachment; tạo/gửi; idempotency; đối soát timeout. | P0 / 3 | F09, O-02, O-05 | Tạo thành công có ID thật; validation giữ dữ liệu; double-click/timeout không tạo trùng; attachment đúng quyền. |
| F11 | **Chuẩn bị và prefill:** tổng hợp dữ kiện; thiếu đánh dấu; schema allowlist; preview; mở form; giữ chỉnh sửa; apply diff. | P0 / 2 mock, 3 tích hợp | F06, F10/schema ổn định | Case 9 và U09–12; mở form không tạo; không bịa định danh hoặc ghi đè sửa đổi. |
| F12 | **Phê duyệt CMS:** tab chờ duyệt; đọc hồ sơ; kiểm thẩm quyền; quyết định/lý do; concurrency/version; audit. | P0 / 3 | F09, F10, O-02 | Người hợp lệ xem/duyệt được; ngoài phạm vi bị chặn; hai quyết định đồng thời không làm sai trạng thái. |
| F13 | **Feedback và đầu mối:** nút phản hồi; lý do; danh bạ chính thức; tóm tắt hỗ trợ; triage owner; liên kết regression. | P1 / 2 tối thiểu, 3–4 hoàn thiện | F05, O-07 | Thiếu danh bạ không bịa; feedback gắn run/version; sửa lỗi không tự xuất bản nguồn hoặc huấn luyện. |
| F14 | **An toàn và observability:** allowlist tool; injection fixtures; trace/version; token/cost/latency; che bí mật; retention. | P0 / từ 2, xuyên suốt | F04, F09/mock scope, O-03/05 | Case 6/10, U13/14; trace giải thích công đoạn lỗi; không lộ credential; hết budget dừng có kiểm soát. |
| F15 | **Release và vận hành:** cấu hình môi trường; kiểm tra CI; package/deploy; health checks; fallback/rollback; runbook. | P1 / 4–5 | Các F trong release, O-05/06 | Regression/holdout report; diễn tập model lỗi và rollback; có owner và giới hạn công bố. |
| F16 | **Mở rộng sau prototype:** ticket hỗ trợ, OCR, proposal doanh nghiệp/Paygate, thực thi cập nhật, thêm sản phẩm. | P2 / sau G4 | Bằng chứng nhu cầu + quyết định phạm vi mới | Mỗi phần có BRD/SRD delta, API/quyền/nguồn/eval riêng; không tự phát sinh từ chat. |

## 3. Phần việc AI và phần nền

| Luồng công việc | Người chịu trách nhiệm theo vai trò | Sản phẩm bàn giao |
| --- | --- | --- |
| Nghiệp vụ và dữ liệu | Chủ dự án/chủ tài liệu | Chính sách mô phỏng nhất quán, quyền nguồn, đáp án được xác nhận, danh bạ. |
| AI/backend | Người phát triển agent-service | Retrieval, workflow, tool contracts, preparation, trace và adapter model. |
| CMS/backend | Người phát triển CMS | Identity, proposal/attachment, validation, trạng thái, phê duyệt và idempotency. |
| Frontend | Người phát triển CMS UI | Chat/citation, preview, form và màn hình phê duyệt; không ẩn kiểm soát chỉ ở UI. |
| QA/evaluation | Người kiểm thử + chủ nghiệp vụ | Fixture, expected results, checks, baseline/UAT và báo cáo release. |
| Vận hành | Chủ dự án/kỹ thuật | Cấu hình, ngân sách, theo dõi, fallback/rollback và xử lý phản hồi. |

Một người có thể làm nhiều phần. Chia vai trò để không quên công việc; chưa suy ra dự án có đủ sáu người. Công việc xây Profile/Paygate để cập nhật thật không nằm trong effort AI.

## 4. Thứ tự triển khai nên bắt đầu

### 4.1. Lát cắt đầu tiên

GDV hỏi đổi CCCD → đọc quy định đã duyệt → hỏi thiếu vai trò/thông tin cần thiết → chỉ ra ảnh hưởng có căn cứ → người dùng yêu cầu chuẩn bị → preview đúng dữ kiện và phần thiếu.

Lát cắt này dùng fixture khách hàng và schema CMS mô phỏng; chưa cần xây toàn bộ Paygate. Kèm các ca đối chứng: không có ảnh hưởng doanh nghiệp, chưa có quy trình Paygate, tài liệu ngoài quyền và dữ kiện được sửa.

### 4.2. Lát cắt tích hợp

Preview → mở form → người dùng sửa/đính kèm → tạo/gửi → có mã thật → TĐV/người đủ thẩm quyền xem và quyết định. Thử validation fail, timeout, bấm lặp, quyền bị thu hồi, sửa form và duyệt đồng thời.

### 4.3. Mở rộng kiểm chứng

Thêm đóng thẻ/trả góp, sửa đổi có phạm vi, phí hợp đồng và tính phí. Sau đó kiểm tra cập nhật nguồn, feedback→sửa→eval và các lỗi vận hành. Không trì hoãn quyền/injection đến cuối; chúng đi cùng từng lát cắt.

Đường găng: F01 → F04/F03 → F05/F06 → G2 → F10/F11/F12 → G3 → UAT/G4. F09, F14 và schema CMS được chuẩn bị song song. Thiếu hợp đồng CMS chỉ chặn tích hợp, không chặn thử nghiệm offline bằng fixture rõ ràng.

## 5. Danh sách công việc ngay sau khi rà soát tài liệu

| Thứ tự | Việc cụ thể | Kết quả cần có |
| --- | --- | --- |
| 1 | Chốt chủ nguồn mô phỏng và sửa mâu thuẫn người duyệt/biểu phí/thứ tự ưu tiên. | O-01 có quyết định, nguồn v0.1 không còn mâu thuẫn chưa giải thích. |
| 2 | Chốt case 1/3/7/8 trước, rồi hoàn thiện các case còn lại và biến thể làm đổi đáp án. | Case fixtures có nguồn/expected behavior và câu trả lời được phép. |
| 3 | Chốt loại yêu cầu cá nhân, fields, attachments, thẩm quyền và tạo/gửi. | O-02 và contract draft; chưa cần xây tất cả nghiệp vụ. |
| 4 | Dựng baseline tìm kiếm/cây nguồn + form mô phỏng; chạy cùng người thử. | Số đo thời gian/chất lượng ban đầu, gồm việc kiểm chứng và sửa. |
| 5 | Chốt metric, budget, giới hạn thử; dựng prototype nhỏ. | O-03, run manifest và báo cáo lỗi đầu tiên. |
| 6 | So sánh thiết kế/model/retrieval rồi chốt runtime/kiến trúc. | ADR cùng bằng chứng; không chọn vì tên công nghệ trong khóa học. |

## 6. Mẫu thử nghiệm trong mỗi vòng

- **Giả thuyết:** thay đổi nào sẽ sửa nhóm lỗi nào hoặc giảm công nào?
- **Đối chứng:** cấu hình hiện tại và tập case cố định.
- **Biến thay đổi:** source, retrieval, prompt, model hoặc UX; ghi rõ các biến khác nếu bắt buộc đổi cùng.
- **Giới hạn:** người chịu trách nhiệm, thời lượng và trần chi phí được chốt trước chạy.
- **Đo:** metric chính, lỗi nghiêm trọng, latency/cost và công kiểm chứng.
- **Quyết định:** giữ, thử lại với lý do cụ thể, thu hẹp hoặc bỏ.

Ví dụ: kiểm tra đọc thêm phụ lục có giảm bỏ sót ngoại lệ ở case 4 so với chỉ đọc đoạn tìm kiếm hay không; đo recall điều kiện, đúng nghiệp vụ và thời gian. Không xem chỉ tăng số đoạn đọc là cải thiện.

## 7. Definition of Done

Một tính năng chỉ được hoàn thành khi:

1. Có requirement và fixture/tiêu chí chấp nhận tương ứng.
2. Đạt kiểm tra chức năng/quyền/lỗi phù hợp; phần AI đạt ngưỡng đã chốt trên tập dùng nghiệm thu.
3. Kết quả có nguồn hoặc trạng thái backend kiểm chứng được, không chỉ lời giải thích của chatbot.
4. Có fallback, trace/version và giới hạn chi phí/thời gian cần thiết.
5. Không còn lỗi nghiêm trọng trong phạm vi kiểm tra; các giới hạn được ghi rõ.
6. Có người xác nhận và bằng chứng chạy gắn với phiên bản.

Với tài liệu yêu cầu, Done là được rà soát và chốt các quyết định áp dụng; không có nghĩa tính năng đã được code.

## 8. Điều kiện dừng hoặc thu hẹp

- Nguồn mô phỏng chưa nhất quán: dừng chấm đáp án phụ thuộc, quay lại F01.
- Thêm agent/retrieval phức tạp không cải thiện metric tương ứng với chi phí: giữ phương án đơn giản hơn.
- Có lỗi quyền, tự tạo/duyệt, tạo trùng hoặc trạng thái giả: chặn release liên quan và xử lý nguyên nhân.
- AI giảm thời gian sinh câu trả lời nhưng tăng tổng thời gian kiểm chứng/sửa: điều chỉnh UX/phạm vi, chưa tuyên bố tăng năng suất.
- Chưa có người duyệt nguồn hoặc người tiếp nhận feedback: chưa chuyển sang dùng chung/pilot.
- Chưa chốt nhân lực và thời gian: giữ kế hoạch theo gate; không đưa lịch tuần giả định thành cam kết.
