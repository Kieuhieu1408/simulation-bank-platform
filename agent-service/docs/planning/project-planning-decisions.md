# Project Planning Decisions

**Dự án:** CMS Knowledge and Proposal Assistant  
**Phiên bản:** 0.1 — Đề xuất để thống nhất kế hoạch  
**Ngày:** 22/09/2026  
**Liên quan:** [SRD](../srd/cms-ai-assistant-srd.md) · [Feature roadmap](feature-roadmap.md) · [BRD](../brd/cms-knowledge-and-proposal-assistant-brd-v1.0.md)

## 1. Kết luận để chuyển sang lập kế hoạch

Đủ cơ sở để lập kế hoạch và xây prototype bằng dữ liệu mô phỏng. Chưa có bằng chứng để cam kết hiệu quả vận hành, nghiệp vụ ngân hàng thật hoặc đưa vào production.

Mục tiêu đầu tiên: GDV hỏi một tình huống thay đổi thông tin khách hàng, nhận hướng dẫn có căn cứ và phần cần làm rõ, rồi chuẩn bị yêu cầu để kiểm tra trên form CMS. Các quan hệ doanh nghiệp/Paygate phải được rà soát có điều kiện. Chatbot không tự sửa hồ sơ hoặc phê duyệt.

Lộ trình đề xuất: chốt phạm vi → chuẩn hóa nguồn và eval → thử nghiệm giải pháp → xây tính năng và tích hợp CMS → nghiệm thu → vận hành và cải tiến. Mỗi phase kết thúc bằng bằng chứng và quyết định tiếp tục/điều chỉnh/dừng.

Các ký hiệu trong tài liệu:

- **Đã xác nhận:** chủ dự án đã nêu rõ trong trao đổi.
- **Đã kiểm tra:** quan sát từ repository ở thời điểm lập tài liệu.
- **Đề xuất:** lựa chọn dùng để lập bản kế hoạch này, chưa phải quyết định được phê duyệt.
- **Chưa biết:** cần dữ liệu, thử nghiệm hoặc quyết định của chủ dự án; có mốc phải đóng.

SRD này là bản thiết kế đề xuất. Các sai khác với BRD và problem notes được ghi tại mục 5, không được hiểu là BRD đã được sửa hoặc các quy định mô phỏng đã được phê duyệt.

## 2. Những điều đã xác định

| Nội dung | Trạng thái và tác động |
| --- | --- |
| Người dùng | Đã xác nhận: nhân viên CMS; prototype tập trung GDV, người duyệt và admin/chủ tài liệu. |
| Yêu cầu đầu tiên | Đã xác nhận: GDV tạo yêu cầu thay đổi thông tin cá nhân; một số trường hợp cần đính kèm hồ sơ. |
| Người phê duyệt | Đã xác nhận: trưởng đơn vị và cấp cao hơn. Chưa xác nhận một cấp phù hợp hay nhiều cấp duyệt nối tiếp; không suy ra từ chức danh. |
| Quan hệ doanh nghiệp | Đã xác nhận: có thể là người đại diện theo pháp luật hoặc người được ủy quyền; cần xem xét tác động tới hồ sơ doanh nghiệp/Paygate. |
| Nguồn vòng đầu | Đã xác nhận: tài liệu mô phỏng tự xây. Tài liệu ngân hàng bên ngoài chỉ là nguồn tham khảo nếu chưa được phân loại và chấp nhận riêng. |
| Phạm vi nghiệp vụ | Bốn nhóm đang được chọn trong đề bài: thông tin khách hàng; thẻ/tài khoản; phí; yêu cầu/phê duyệt. Phạm vi triển khai từng nhóm được thu hẹp ở roadmap. |
| Nền CMS, Paygate, Profile | Đã kiểm tra: các thư mục hiện chỉ có .gitkeep; chưa có API/form/quy trình thực thi để tích hợp. |
| Corebank | Đã kiểm tra: Customer hiện có CIF và thời điểm tạo; chưa có mô hình CCCD, vai trò doanh nghiệp hoặc thay đổi hồ sơ phục vụ case này. |
| Agent-service | Có tài liệu và raw_data; chưa xác minh được runtime chatbot đã triển khai. Các API trong SRD là hợp đồng đề xuất. |
| Hiệu quả | Chưa có baseline, người dùng thử hoặc kết quả eval được nghiệm thu. Không gán số tiết kiệm thời gian/ROI như kết quả thực tế. |

## 3. Trả lời 4 câu hỏi trọng tâm của Day02

| Câu hỏi | Trả lời cho dự án |
| --- | --- |
| Có cần AI? | Có lý do để thử ở hiểu câu hỏi tiếng Việt, tổng hợp đa nguồn, nhận diện thông tin thiếu và soạn nội dung. Phải so với tìm kiếm/cây tài liệu và form thủ công trên cùng tác vụ. |
| Dùng AI ở mức nào? | Một workflow có trạng thái, cho phép tra cứu động trong giới hạn. Code kiểm soát quyền, hiệu lực đã khai báo, schema, phép tính và trạng thái CMS. Chưa có lý do bắt buộc multi-agent hoặc fine-tuning. |
| Bài toán đủ rõ để triển khai chưa? | Đủ cho prototype mô phỏng và thiết kế hợp đồng. Trước tích hợp phải chốt schema/tuyến duyệt; trước chấm chính thức phải có nguồn và đáp án; trước pilot phải chốt tải, ngân sách, người vận hành. |
| Go / Not Yet / No-Go? | Đề xuất Go cho prototype theo các gate; Not Yet cho vận hành với dữ liệu/nghiệp vụ thật. Thu hẹp hoặc dừng phần AI nếu không đạt chất lượng hoặc không tạo giá trị so với đối chứng. |

## 4. Trả lời 22 câu hỏi khám phá và quyết định

Các mã dưới đây kế thừa cách nhóm câu hỏi Day02 và BRD; câu trả lời được cập nhật theo hiện trạng CMS chưa triển khai.

| Mã | Câu hỏi | Câu trả lời và cách kiểm chứng |
| --- | --- | --- |
| D1 | Giả định nào cần lật lại? | Có citation chưa chắc áp dụng đúng; văn bản mới nhất chưa chắc áp dụng cho mọi hợp đồng; đổi CCCD chưa tự chứng minh phải đổi phí; người dùng chưa chắc phát hiện lỗi AI. Đưa thành cặp case đối chứng. |
| D2 | Cách tiếp cận mới? | Cho hỏi theo tình huống, đọc nguồn liên quan và đưa dữ kiện đã kiểm tra sang form. Đo toàn bộ thời gian gồm đọc nguồn và sửa form. |
| D3 | Nếu thiết kế từ đầu? | Tạo kho nguồn có quyền, phiên bản, phạm vi áp dụng và liên kết; CMS có luồng thủ công độc lập; AI bổ sung vào cùng workflow. |
| D4 | Tại sao cần AI? | Câu hỏi đa dạng và quy định nằm ở nhiều tài liệu. So với checklist/tìm kiếm tốt; chỉ giữ phần AI đem lại lợi ích đo được. |
| D5 | Bước nào chỉ do thói quen? | Nhớ đường dẫn, hỏi người quen, chép lại thông tin là giả thuyết. Cần quan sát tác vụ; chưa được tính các bước này thành thời gian tiết kiệm chắc chắn. |
| D6 | Câu hỏi cốt lõi bị né? | Người dùng không tìm được nguồn hay bản thân quy trình Paygate chưa được định nghĩa? Trường hợp thứ hai cần chủ nghiệp vụ xây quy trình, chatbot chỉ chỉ ra khoảng trống. |
| I1 | Pain point và tần suất? | Tìm/đọc/đối chiếu và nhập lại dữ kiện. Chưa biết tần suất; prototype đo theo nhóm tác vụ, pilot mới đo trên công việc đại diện thực tế. |
| I2 | Workflow và bàn giao? | GDV tra cứu → kiểm chứng → chuẩn bị yêu cầu/đính kèm → chủ động tạo/gửi → người có thẩm quyền xem và duyệt. Thứ tự tạo/gửi và tuyến duyệt là quyết định còn mở. |
| I3 | Hao phí cụ thể? | Phút tìm, đọc, hỏi lại, sửa form; công quản trị nguồn và hỗ trợ. Đo baseline trước khi tính ROI; chưa có con số thực tế. |
| I4 | Sai thì ai kiểm? | Chủ tài liệu xác nhận nguồn/đáp án; GDV kiểm tra hướng dẫn và form; người duyệt kiểm hồ sơ; backend chặn quyền/trạng thái sai. Kiểm thử độc lập từng lớp. |
| I5 | Ai quyết định tiếp tục? | Chủ dự án quyết định prototype; có người đại diện nghiệp vụ xác nhận chính sách mô phỏng và đáp án. Nếu cùng một người kiêm nhiệm, vẫn ghi riêng hành động duyệt nguồn và đánh giá kết quả. |
| P1 | Quy trình hiện tại? | Quy trình mong muốn đã có; chức năng CMS chưa có mã triển khai. Baseline vòng đầu là tìm kiếm và form mô phỏng thủ công, không gọi đây là số liệu của ngân hàng đang vận hành. |
| P2 | Nút thắt ưu tiên? | Tìm đủ căn cứ, áp dụng đúng ngoại lệ và chuẩn bị đúng yêu cầu. Prototype đi theo luồng thay đổi thông tin trước, không xây toàn bộ nghiệp vụ ngân hàng. |
| P3 | Baseline là gì? | Cùng bộ nguồn, quyền và đề bài; người thử dùng tìm kiếm/cây tài liệu và form thủ công. Ghi kết quả đúng, thời gian, lần sửa và công kiểm chứng. Đảo thứ tự/cặp tác vụ tương đương để giảm hiệu ứng học đáp án. |
| P4 | Thế nào là thành công? | Xử lý đúng tình huống và đủ căn cứ; hỏi/dừng đúng khi thiếu; ít hỏi thừa; form đúng; thời gian và tổng công giảm. Định nghĩa và ngưỡng đề xuất nằm trong SRD, chưa phải kết quả đã đạt. |
| P5 | Tự chủ đến đâu? | Tự chọn bước tìm/đọc trong ngân sách, hỏi thêm và soạn phần chuẩn bị. Không tự tạo/gửi/duyệt, sửa hồ sơ, thay biểu phí hoặc khóa thẻ. |
| P6 | Giải pháp đơn giản hơn? | Tìm kiếm từ khóa, cây/liên kết, checklist và form có validation. Giữ làm baseline và fallback. |
| G1 | Cần xử lý ngôn ngữ/suy luận? | Có ở tổng hợp và đối chiếu. Tính phí, phân quyền, điều kiện trạng thái dùng logic xác định, với quy tắc đã được xác nhận. |
| G2 | Đủ nguồn và ngữ cảnh? | Chưa: raw_data có nội dung ban đầu nhưng còn thiếu metadata và có mâu thuẫn. Có thể bắt đầu chuẩn hóa; chưa thể coi mọi file là nguồn trả lời. |
| G3 | Đã đo được chưa? | Có định nghĩa chỉ số và phương án đo; chưa có baseline, holdout đã duyệt hoặc báo cáo chạy. Gate dữ liệu/eval phải hoàn tất trước so sánh chính thức. |
| G4 | Hậu quả sai kiểm soát được? | Prototype dùng dữ liệu giả, công cụ giới hạn và người xác nhận. Phải kiểm tra rò rỉ quyền, kết luận vượt nguồn, sai form và trạng thái giả; không mặc định có HITL là an toàn tuyệt đối. |
| G5 | Có cách rẻ hơn? | Có; chọn giải pháp đơn giản nhất vượt gate. Chỉ thêm hybrid search, orchestration framework hoặc mô hình khác khi trace chỉ ra lỗi cụ thể và thử nghiệm chứng minh cải thiện. |

## 5. Mâu thuẫn và điều chỉnh cần đưa vào kế hoạch

| Mã | Hiện trạng | Quyết định đề xuất |
| --- | --- | --- |
| GAP-01 | BRD nói giữ form/luồng duyệt hiện có; CMS chưa triển khai. | Tách epic nền CMS và epic AI. Dùng adapter mô phỏng trước; không đánh dấu tích hợp thật đã xong. |
| GAP-02 | problem.md nói hợp đồng cũ luôn theo quy định lúc ký. | Xét thời điểm nghiệp vụ, điều khoản hợp đồng, sửa đổi và chuyển tiếp. Metadata ngày chỉ là một phần điều kiện áp dụng. |
| GAP-03 | problem.md nói chưa có feedback/audit. | BRD FR-15/16 đã có. Cần đặc tả vận hành; ticket hỗ trợ là phần mở rộng riêng. |
| GAP-04 | 10 case tự nhận bao phủ cả quản trị tri thức. | Bổ sung case nhập/công bố/thay thế một phần/nguồn tương lai; không tính prompt injection là toàn bộ luồng quản trị. |
| GAP-05 | [Quy trình CMS mô phỏng](../../raw_data/internal_rules/cms_approval_guidelines.md) ghi Maker→Checker/KSV và gọi là hai cấp. | Không đồng nhất hai vai trò với hai cấp duyệt. Chủ dự án đã nêu TĐV/cấp cao hơn; cần chốt ma trận thẩm quyền trước tích hợp. |
| GAP-06 | [Quy tắc phí mô phỏng](../../raw_data/internal_rules/nv3_fee_schedules_conflicts.md) ghi hợp đồng luôn cao nhất nhưng khuyến mãi lại đè mọi thứ; bảng bậc khác case 8. | Xác định một bộ chính sách mô phỏng có phiên bản, phạm vi và thứ tự áp dụng rõ. Không tự chọn bên thắng hoặc đổi đáp án cho khớp đầu ra model. |
| GAP-07 | [Quy tắc CCCD mô phỏng](../../raw_data/internal_rules/nv1_cross_system_update.md) yêu cầu ba cập nhật cho NĐDPL. | Có thể trở thành luật của case mô phỏng sau khi chủ dự án chấp nhận; cần bổ sung điều kiện, trường thay đổi, vai trò ủy quyền, nguồn và ngoại lệ. Không suy rộng thành quy định ngân hàng thật. |
| GAP-08 | [Quy tắc thẻ mô phỏng](../../raw_data/internal_rules/nv2_card_operations.md) có phí 2–4% nhưng chưa đủ điều kiện chọn mức. | Chốt sản phẩm, căn cứ tính và mức áp dụng trước khi chấm số tiền. Không mặc định lấy một giá trị trong khoảng. |
| GAP-09 | Raw_data có tài liệu nhiều ngân hàng/nhà cung cấp. | Gắn tổ chức/phạm vi nguồn; nguồn tham khảo không tự trở thành chính sách của ngân hàng mô phỏng; không trộn chính sách giữa các tổ chức. |
| GAP-10 | Một số mã U trong 10 case không khớp BRD. | Dùng bảng traceability trong SRD để rà soát; việc sửa tài liệu gốc được ghi thành công việc chuẩn hóa, không giả định mapping hiện tại đã đúng. |

## 6. Những lựa chọn đề xuất cho prototype

| Mã | Đề xuất | Khi nào xem lại? |
| --- | --- | --- |
| PD-01 | Nguồn trả lời ban đầu là bộ chính sách mô phỏng đã được chủ dự án xác nhận. | Khi mở thử nghiệm tài liệu thật; cần provenance, quyền và phạm vi áp dụng mới. |
| PD-02 | Loại yêu cầu đầu tiên: thay đổi thông tin cá nhân; doanh nghiệp/Paygate ở mức hướng dẫn tác động và việc tiếp theo. | Khi có quy trình và API thay đổi doanh nghiệp/merchant riêng. |
| PD-03 | Prototype xử lý thẻ và phí ở mức tư vấn có căn cứ/tính minh họa; không thực thi nghiệp vụ. | Chỉ mở hành động sau một quyết định phạm vi riêng. |
| PD-04 | Giai đoạn đầu dùng một workflow điều phối; công cụ đọc nguồn, tra danh bạ, tính toán và chuẩn bị form có schema. | Sau phép thử so sánh; không mặc định multi-agent. |
| PD-05 | Làm tìm kiếm từ khóa/cây nguồn và đọc mục/phụ lục làm đối chứng; thử thêm semantic retrieval khi cần. | Trace cho thấy bỏ sót do cách diễn đạt, cùng eval chứng minh lợi ích. |
| PD-06 | Handoff đầu tiên: đầu mối được cấu hình và bản tóm tắt để người dùng kiểm tra. | Ticket chỉ xây khi có người nhận, schema, trạng thái, quyền chia sẻ và thao tác gửi rõ. |
| PD-07 | Mốc tích hợp đầu tiên kết thúc ở proposal được tạo/duyệt trong CMS mô phỏng; trạng thái phê duyệt không báo hồ sơ đã cập nhật. | Muốn cập nhật thực sự cần epic thực thi phía Profile/Paygate và kiểm thử đối soát riêng. |
| PD-08 | Bắt đầu với Markdown chuẩn hóa; PDF có lớp text sau khi có kiểm tra chất lượng; OCR không nằm trên đường găng đầu tiên. | Nguồn cần thiết chỉ có bản scan hoặc baseline chứng minh giá trị OCR. |

## 7. Các quyết định còn mở và người chịu trách nhiệm

Không cần chờ tất cả các mục này để bắt đầu chuẩn hóa nguồn và thử nghiệm offline. Mục nào đến gate chưa đóng thì chỉ dừng công việc phụ thuộc mục đó.

| Mã | Cần chốt | Đề xuất/đầu ra cụ thể | Chủ trì | Hạn đóng |
| --- | --- | --- | --- | --- |
| O-01 | Chính sách mô phỏng và người xác nhận đáp án | Rà soát GAP-05–09, duyệt từng phiên bản nguồn và case; chủ dự án tạm kiêm chủ nghiệp vụ. | Chủ dự án | Trước đóng Phase 1 |
| O-02 | Schema và tuyến duyệt | Đề xuất một người có thẩm quyền phù hợp duyệt trong prototype; tạo/gửi là thao tác có tên rõ. Không coi đề xuất này là đã thống nhất. Chốt cấp duyệt, phạm vi đơn vị, tự duyệt, trả lại/từ chối và đính kèm. | Chủ dự án/CMS | Trước tích hợp Phase 3 |
| O-03 | Ngưỡng chất lượng, quy mô thử và ngân sách | Dùng bảng metric SRD làm điểm bắt đầu, duyệt ngưỡng trước chạy holdout; cấu hình trần chi phí/thời gian trước gọi model hàng loạt. | Chủ dự án + kỹ thuật | Trước chạy Phase 2 có tính phí |
| O-04 | Runtime/model/provider | Chọn bằng spike trên cùng bộ ca, quyền và ngân sách; ghi model/config/version, lý do chọn và phương án thay thế. | Kỹ thuật | Cuối Phase 2 |
| O-05 | Lưu trữ hội thoại, audit, đính kèm | Chốt thời hạn, quyền xem/xóa, nơi xử lý; chỉ dùng dữ liệu mô phỏng trước khi có chính sách dữ liệu thật. | Chủ dự án/CMS | Trước deploy chia sẻ và trước dữ liệu thật |
| O-06 | Tải, latency, availability, khôi phục | Chốt corpus/tải đo, p95, giới hạn nhiệm vụ và quy trình rollback; không suy ra SLA từ ví dụ khóa học. | Kỹ thuật | Trước Phase 4 |
| O-07 | Đầu mối hỗ trợ và xử lý feedback | Danh bạ, chủ từng nhóm lỗi, cách theo dõi trạng thái; không bịa người nhận hoặc cam kết thời gian chưa thống nhất. | Chủ dự án/chủ nghiệp vụ | Trước bật handoff |
| O-08 | Năng lực nhóm và thời gian | Số người, giờ/tuần, ưu tiên học tập so với deadline; sau đó mới gán tuần/sprint và ước lượng. | Chủ dự án | Trước cam kết lịch |

## 8. Căn cứ từ bộ AI Thực Chiến

Đây là vận dụng phương pháp của khóa học vào dự án, không phải sao chép ngưỡng mẫu, cấu hình lab hay coi slide là quy định ngân hàng.

| Nguồn | Phần áp dụng |
| --- | --- |
| [Day02 — Xác định bài toán](</Users/hieuquockieu/Documents/AI Docs/AI-Thuc-Chien-.md-file/00_Phase1_Nen-tang-AI-LLM/Day02_xac-dinh-bai-toan-cho-ai.md>) | Problem Statement, 22 câu hỏi, baseline, giới hạn và Go/Not Yet/No-Go. |
| [Day06 — AI Product & Project Management](</Users/hieuquockieu/Documents/AI Docs/AI-Thuc-Chien-.md-file/00_Phase1_Nen-tang-AI-LLM/Day06_ai-product-project-management.md>) | Hypothesis→Build→Eval→Iterate; phân biệt MVE/PoC/MVP; Definition of Done và giới hạn thử nghiệm. |
| [Track1 Day20–21 — AI Evaluation](</Users/hieuquockieu/Documents/AI Docs/AI-Thuc-Chien-.md-file/01_Track1_AI-Product-Management/Day20-21_ai-evaluation.md>) | Quan sát prototype trước chấm chính thức, coverage theo hành vi, code-based checks, review con người và phân tích lỗi. |
| [Track3 Day18 — Production RAG](</Users/hieuquockieu/Documents/AI Docs/AI-Thuc-Chien-.md-file/03_Track3_AI-Applications/Day18_production-rag.md>) | Phân biệt lỗi dữ liệu, truy xuất, ghép ngữ cảnh và sinh câu trả lời; sửa đúng công đoạn. |
| [Track3 Day27 — HITL UX](</Users/hieuquockieu/Documents/AI Docs/AI-Thuc-Chien-.md-file/03_Track3_AI-Applications/Day27_human-in-the-loop-ux.md>) | Bản chuẩn bị, kiểm tra của con người, trạng thái chờ, dữ liệu lỗi thời và phản hồi. |
| [Track2 Day24 — Data Governance & Security](</Users/hieuquockieu/Documents/AI Docs/AI-Thuc-Chien-.md-file/02_Track2_AI-Data-Infrastructure/Day24_data-governance-security.md>) | Phạm vi nguồn, quyền theo dữ liệu và từng công cụ, phân loại và truy vết. |
| [Track2 Day21 — CI/CD for AI Systems](</Users/hieuquockieu/Documents/AI Docs/AI-Thuc-Chien-.md-file/02_Track2_AI-Data-Infrastructure/Day21_cicd-for-ai-systems.md>) | Gắn phiên bản dữ liệu/cấu hình với run, kiểm thử trước phát hành và rollback. Không bắt buộc cài toàn bộ công cụ trong lab. |

Ghi chú chất lượng nguồn học: tại lần đọc phục vụ bản 0.1, bản Markdown Day10 data-pipeline có thông báo lỗi API ở đầu và nội dung không liền mạch. Không dùng file đó làm căn cứ duy nhất cho quyết định; cần kiểm tra lại sau khi chuyển đổi được sửa. Các số liệu thị trường, nhận định pháp lý và hướng dẫn API theo phiên bản trong slide không được đưa vào SRD như sự thật đã kiểm chứng.
