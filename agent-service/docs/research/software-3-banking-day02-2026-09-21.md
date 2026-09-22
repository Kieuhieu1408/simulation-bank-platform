**Deep research: lựa chọn nghiệp vụ ngân hàng để thử nghiệm Software 3.0**

Ngày nghiên cứu: 21/09/2026. Đối tượng: dự án cá nhân của bạn, có tài liệu và một phần mã nguồn trong thư mục `klb`.

Cập nhật lựa chọn sau phản biện của người dùng: A và B không còn là hướng ưu tiên; tập trung trợ lý tra cứu và đọc hiểu tài liệu cho toàn bộ nhân viên có tài khoản CMS, trong đó GDV là một nhóm sử dụng. Xem [định nghĩa bài toán hiện hành v0.2](./cms-internal-knowledge-assistant.md). Các đánh giá bên dưới giữ lại như hồ sơ nghiên cứu ban đầu, không thay cho lựa chọn mới này.

**Kết luận để ra quyết định**

Có cơ sở để nghiên cứu Software 3.0 trong ngân hàng, đặc biệt ở các công việc đọc, tìm, tổng hợp và chuẩn bị thông tin. Bằng chứng hiện có không đủ để suy ra rằng một agent nên tự quyết định nghiệp vụ hoặc rằng dự án cá nhân sẽ đạt hiệu quả giống ngân hàng lớn.

Năm bài toán được phân tích đầy đủ trong báo cáo này:

| Mã | Công việc cụ thể | Ưu tiên trong dự án cá nhân | Mức kỹ thuật khởi đầu | Quyết định hiện tại |
|---|---|---|---|---|
| A | Chuẩn bị và phân loại yêu cầu hỗ trợ nội bộ SLA | Cao nhất để bắt đầu học và kiểm chứng | Biểu mẫu/rule đối chứng với LLM workflow | Go nghiên cứu; Not Yet pilot nghiệp vụ |
| B | Chuẩn bị phần bổ sung cho hồ sơ PRM bị trả lại | Cao nếu muốn tận dụng mã nguồn đã có | Workflow đọc hồ sơ; agent chỉ khi đường tra cứu thực sự thay đổi | Go nghiên cứu; Not Yet pilot nghiệp vụ |
| C | Tra cứu quy trình và chuẩn bị hướng xử lý cho nhân viên tuyến đầu | Gần nhất với trải nghiệm đóng thẻ của bạn | Tra cứu kèm dẫn chứng, hỏi làm rõ, chuyển chuyên gia | Go nghiên cứu phạm vi hẹp; Not Yet dùng để tư vấn thật |
| D | Tóm tắt tương tác và soạn bản bàn giao vụ việc | Dễ tạo bản thử; cần kiểm tra lỗi bỏ sót | LLM workflow | Go nghiên cứu bằng dữ liệu giả lập; Not Yet pilot nghiệp vụ |
| E | Luyện tập tình huống cho nhân viên mới | Phù hợp nếu mục tiêu chính là học nghiệp vụ | Mô phỏng hội thoại + tiêu chí phản hồi đã duyệt | Go nghiên cứu; Not Yet chấm năng lực thực tế |

“Go nghiên cứu” nghĩa là đủ lý do để đầu tư một thử nghiệm nhỏ, không có nghĩa đã chứng minh giá trị hay đủ điều kiện triển khai. Theo gate Day02, cả năm hiện vẫn thiếu baseline hoặc dữ liệu xác thực để quyết định đầu tư triển khai nghiệp vụ. Nếu mục tiêu là agent, B có đường mở rộng hợp lý nhất; nếu mục tiêu là tìm đúng sản phẩm đầu tiên, A có phạm vi kiểm chứng thuận lợi hơn.

**Phạm vi và cách đọc bằng chứng**

Báo cáo tập trung ngân hàng và vận hành nội bộ ngân hàng theo cuộc trao đổi trước. Đây là nghiên cứu tài liệu và nguồn công khai, chưa có phỏng vấn nhân viên, dữ liệu vận hành thật hay thử nghiệm mô hình. Các ví dụ đầu vào, ngưỡng nghiệm thu và thiết kế sản phẩm bên dưới là đề xuất, không phải kết quả đã đo.

Ba loại phát biểu được phân biệt:

- **Bằng chứng:** nội dung được tài liệu hoặc nguồn sơ cấp xác nhận. Đặc tả cho biết quy trình được thiết kế thế nào, không bảo đảm đang vận hành đúng như vậy.
- **Suy luận/đề xuất:** cách áp dụng vào dự án của bạn, cần kiểm chứng.
- **Chưa biết:** tần suất, tổn thất, chất lượng dữ liệu hoặc hiệu quả chưa có căn cứ; không điền số giả để làm Problem Statement trông hoàn chỉnh.

Nguồn ưu tiên gồm công bố trực tiếp của ngân hàng, nghiên cứu gốc, hướng dẫn thiết kế và tài liệu bạn cung cấp. Công bố của ngân hàng hữu ích để xác nhận use case; các con số hiệu quả tự báo cáo chưa phải đánh giá độc lập. Đây là tập nguồn có chọn lọc, không phải khảo sát toàn bộ thị trường hay một systematic review.

**Software 3.0 và agent là hai câu hỏi khác nhau**

Trong cách diễn giải của Karpathy, Software 1.0 là logic được lập trình tường minh; Software 2.0 là hành vi học qua trọng số từ dữ liệu; Software 3.0 nhấn mạnh lập trình hành vi LLM bằng ngôn ngữ tự nhiên. Một ứng dụng có thể sử dụng cả ba. Không cần tự train LLM để có ứng dụng Software 3.0. Tham chiếu: [Andrej Karpathy, Software Is Changing (Again), 2025](https://www.youtube.com/watch?v=LCEmiRjPEtQ).

Định nghĩa này cũng xuất hiện trong [bản tóm tắt Sequoia Ascent do Karpathy đăng ngày 30/04/2026](https://karpathy.bearblog.dev/sequoia-ascent-2026/), nơi ông mở rộng cách lập trình bằng prompt sang ngữ cảnh, công cụ, ví dụ và chỉ dẫn. Trang đó công khai rằng phần tóm tắt/transcript được AI hỗ trợ biên tập và ông đã đọc lại; báo cáo dùng nó để đối chiếu thuật ngữ, không coi là nghiên cứu thực nghiệm.

Workflow có đường xử lý định trước; agent cho mô hình quyết định động cách đi tiếp và sử dụng công cụ. Nhiều bước hoặc nhiều API chưa tự động biến hệ thống thành agent. Anthropic khuyến nghị tăng độ phức tạp khi giá trị thu được bù chi phí và độ trễ. [Building effective agents, 19/12/2024](https://www.anthropic.com/engineering/building-effective-agents).

Trong báo cáo, “augment” là hỗ trợ người làm nghiệp vụ. Phần tính tiền, kiểm quyền, kiểm trường bắt buộc, lịch SLA và chuyển trạng thái được định nghĩa rõ vẫn là ứng viên cho code/rule. Code cũng có thể có lỗi; tính xác định chỉ làm kết quả dễ tái hiện và kiểm thử hơn, không bảo đảm đúng tuyệt đối.

**Các tổ chức đang tin và thử điều gì?**

| Nguồn, thời điểm | Điều nguồn thực sự xác nhận | Giới hạn khi suy ra cho dự án của bạn |
|---|---|---|
| [DBS CSO Assistant, 18/07/2024](https://www.dbs.com/newsroom/DBS_empowers_its_Customer_Service_Officers_with_Gen_AI_powered_virtual_assistant_to_reduce_toil_and_enhance_customer_experience) | Công bố trợ lý cho nhân viên CSKH: tìm kiến thức liên quan, tóm tắt cuộc gọi, điền trước trường yêu cầu. Con số giảm thời gian xử lý tới 20% trong bài là kỳ vọng khi triển khai đầy đủ. | Không sử dụng 20% như kết quả thực nghiệm cho KLB; bài công bố không cung cấp bộ kiểm thử đủ để tái lập các tuyên bố về độ chính xác. Liên quan C, D. |
| [Morgan Stanley Debrief, 26/06/2024](https://www.morganstanley.com/press-releases/ai-at-morgan-stanley-debrief-launch) | Công cụ tạo ghi chú cuộc họp khi khách hàng đồng ý, rút ra việc cần làm và soạn email để cố vấn chỉnh sửa, quyết định gửi. | Chứng minh một cách đặt AI vào khâu chuẩn bị tài liệu; không chứng minh mọi bản tóm tắt đúng. Liên quan D. |
| [OCBC, Annual Report 2024](https://www.ocbc.com/group/investors/annual-reports/2024-annual-report/creating-value-through-ai.page) | OCBC Buddy được mô tả rõ là trợ lý GenAI cho câu hỏi chính sách nội bộ, gồm nghỉ phép và đề nghị chi phí. | Gần với tra cứu nội bộ; không phải bằng chứng trực tiếp cho việc agent kiểm tra hồ sơ PRM hay phân loại SLA. Liên quan A–C qua các tác vụ tương đồng. |
| [NatWest, triển khai công cụ nội bộ, 04/2025](https://www.natwestgroup.com/news-and-insights/latest-stories/ai-and-data/2025/apr/deploying-new-generative-ai-technology-to-support-our-colleagues.html) | Công bố mở rộng công cụ GenAI nội bộ và đề cập Ask Archie dùng GenAI để hỗ trợ câu hỏi nhân sự. | Là bằng chứng cho nhu cầu tìm hiểu chính sách và tổng hợp thông tin; chưa phải bằng chứng về lợi ích của sản phẩm cụ thể bạn sẽ xây. |
| [HSBC, Transforming HSBC with AI, truy cập 21/09/2026](https://www.hsbc.com/who-we-are/hsbc-and-digital/hsbc-and-ai/transforming-hsbc-with-ai) | Ngân hàng mô tả GenAI hỗ trợ bản viết phân tích tín dụng, tóm tắt chat CSKH và phân tích tài liệu. | Hỗ trợ bản viết tín dụng không đồng nghĩa tự phê duyệt khoản vay. Việc áp dụng cho PRM là suy luận tương tự, chưa có bằng chứng triển khai PRM trong nguồn. |
| [Bank of America, 04/2025](https://newsroom.bankofamerica.com/content/newsroom/press-releases/2025/04/ai-adoption-by-bofa-s-global-workforce-improves-productivity--cl.html) | The Academy dùng AI mô phỏng hội thoại để nhân viên luyện tương tác và nhận phản hồi. | Nguồn xác nhận AI training, nhưng không mô tả đủ để kết luận hệ đó là LLM agent theo định nghĩa kỹ thuật. Phiên bản LLM trong E là đề xuất cần kiểm chứng. |
| [Bank of America EricaAssist, 21/07/2026](https://newsroom.bankofamerica.com/content/newsroom/press-releases/2026/07/bank-of-america-enhances-ericaassist-with-generative-ai-to-help-.html) | Công bố bổ sung GenAI cho công cụ hỗ trợ hơn 18.000 nhân viên: tổng hợp lý do khách gọi, thông tin liên quan và bước tiếp theo theo ngữ cảnh. | Là bằng chứng cập nhật về trợ lý trong quy trình phục vụ. Từ “agent” trong công bố không đủ để xác định mô hình tự điều phối công cụ ra sao; không dùng số liệu tự báo cáo làm dự báo hiệu quả của dự án. |
| [HSBC – HKMA GenAI Sandbox, tài liệu 10 trang, 2025](https://www.about.hsbc.com.hk/-/media/hong-kong/en/news-and-media/251024-hsbc-hkma-genai-sandbox-use-cases-summary.pdf?sc_lang=en-GB) | PoC chatbot website thử trên hơn 800 tình huống với hơn 400 nhân viên. Phạm vi gồm tìm và so sánh thông tin thực tế; câu hỏi dẫn tới khuyến nghị sản phẩm được chuyển người. | Đây là PoC với nhân viên, không phải kiểm chứng production trên khách hàng. Điểm đáng học là ranh giới, không phải suy diễn tỷ lệ hài lòng thành tỷ lệ đúng nghiệp vụ. |
| [Brynjolfsson, Li, Raymond, NBER WP 31161, bản 2023](https://www.nber.org/papers/w31161) | Nghiên cứu việc đưa trợ lý GenAI vào hỗ trợ khách hàng trên 5.179 nhân viên ghi nhận năng suất trung bình tăng 14%; hiệu ứng khác nhau theo kinh nghiệm. | Đây là nghiên cứu ngoài ngân hàng và về triển khai theo giai đoạn, không phải bằng chứng rằng mọi nhóm ngân hàng sẽ tăng 14%. Không trộn số liệu bản working paper này với bản xuất bản cập nhật. |

Suy luận từ các nguồn: nhóm việc có tiền lệ rõ là tra cứu tri thức và chuẩn bị thông tin cho con người. Chưa có căn cứ từ tập nguồn này để khẳng định agent tự trị tốt hơn workflow cho năm bài toán được chọn. Số lượng ngân hàng công bố một ý tưởng cũng không thay cho kiểm chứng nhu cầu tại nơi áp dụng.

**Khung Day02 được dùng đầy đủ ra sao?**

Nguồn gốc: [Day02 – Xác định bài toán cho AI](</Users/hieuquockieu/Documents/AI Docs/AI-Thuc-Chien/00_Phase1_Nen-tang-AI-LLM/Day02_xac-dinh-bai-toan-cho-ai.pdf>). Số trang dưới đây theo thứ tự trang PDF.

| Nhóm | Câu hỏi cần trả lời | Vị trí |
|---|---|---|
| Trọng tâm | Có thật sự cần AI? Rule/Workflow/Agent? PS đủ rõ chưa? Go/Not Yet/No-Go? | Trang 3 |
| D1–D6: Phân kỳ | Giả định cần lật lại; cách tiếp cận mới; thiết kế lại từ đầu; vì sao cần AI; thói quen đang duy trì quy trình; câu hỏi bị né tránh | Trang 21 |
| I1–I5: Stakeholder | Pain point/tần suất; workflow/công cụ/bàn giao; thiệt hại; hậu quả AI sai/HITL; ai nói YES và metric/risk quyết định | Trang 25 |
| P1–P6: Cấu trúc PS | Workflow; nút thắt; hao phí hiện tại; tiêu chí thành công; hậu quả sai/boundary; phương án phi AI | Trang 30 |
| G1–G5: Readiness | Cần ngôn ngữ/tri thức/suy luận? Đủ ngữ cảnh? Có metric? Kiểm soát hậu quả? Có cách đơn giản hơn? | Trang 69 |
| PS9 | Actor, Workflow, Bottleneck, Impact, Success Metric, Boundary, AI entry, Level, Risk/HITL | Trang 67 |
| Định lượng và vận hành | Baseline–target–measurement; automate/augment; FP/FN; metric–ngưỡng–hành động; lỗi/UX; eval và vận hành | Trang 31–32, 43–45, 57–65 |

Từng hồ sơ A–E bên dưới có đủ D1–D6, I1–I5, P1–P6, G1–G5 và PS9. Các câu hỏi trùng ý được trả lời bằng tham chiếu cụ thể trong cùng hồ sơ, không coi việc thiếu số liệu là câu trả lời “đã có”.

Để chọn giữa các hướng, báo cáo dùng thêm sáu tiêu chí vận dụng từ Day02: mức thiệt hại nếu sai, khả năng phát hiện/sửa sai, nguồn xác định đúng sai, lợi thế so với phương án đơn giản, dữ liệu bạn tiếp cận được, và công sức duy trì. Không cộng chúng thành điểm 87/100 vì hiện chưa có dữ liệu để biện minh trọng số.

**A — Chuẩn bị yêu cầu hỗ trợ nội bộ SLA**

**Bài toán một câu:** Nhân viên cần gửi yêu cầu hỗ trợ nhưng khó chọn đúng dịch vụ và cung cấp đủ thông tin, khiến người phân bổ phải đọc và sửa lại.

**Bằng chứng tại chỗ:** Đặc tả SLA mô tả email có subject không đúng cấu trúc bị phân loại “Khác”, sau đó người có quyền phân bổ có thể sửa kiểu yêu cầu, loại dịch vụ và phân loại chi tiết. Tài liệu cho biết cơ chế; chưa có log chứng minh tỷ lệ lỗi hay thời gian mất. Nguồn: [SLA v2.0, quy trình tạo yêu cầu, trang 16–17](</Users/hieuquockieu/Documents/Dự án cũ/klb/SLA/QTPTPM_BM02_DacTaYeuCauPhanMem_SLA_v2.0_11022025.docx>).

**PS9**

| Trường | Định nghĩa cho thử nghiệm |
|---|---|
| Actor | Nhân viên gửi yêu cầu; người phân bổ của một bộ phận hỗ trợ nội bộ |
| Workflow | Viết email/điền form → hệ thống tạo yêu cầu → phân loại → phân bổ → người xử lý đọc hoặc hỏi lại |
| Bottleneck | Đọc nội dung tự do để chọn dịch vụ; phát hiện thông tin cần hỏi thêm |
| Impact | Công phân bổ lại và thời gian chờ; quy mô chưa được đo |
| Success Metric | Thời gian từ bắt đầu soạn đến yêu cầu đủ thông tin, tỷ lệ sửa nhãn/hỏi lại, chất lượng sau người duyệt |
| Boundary | Chỉ đề xuất trên bản nháp; không tự gửi, đổi đơn vị nhận, cấp quyền hoặc điều chỉnh đồng hồ SLA |
| AI entry | Sau khi người gửi mô tả nhu cầu; trước khi xác nhận tạo yêu cầu |
| Level | Biểu mẫu/rule làm đối chứng; LLM workflow cho nội dung tự do |
| Risk/HITL | Người gửi xác nhận nhu cầu; người phân bổ kiểm tra nhãn bằng mô tả danh mục, có lựa chọn “chưa xác định” |

**22 câu hỏi Day02**

- **D1 — Giả định cần lật lại:** Nhân viên phải hiểu cây danh mục nội bộ mới có thể yêu cầu hỗ trợ. Có thể trách nhiệm ánh xạ thuộc hệ thống/bộ phận tiếp nhận.
- **D2 — Cách tiếp cận mới:** Cho mô tả vấn đề trước, hiện lựa chọn phù hợp và thông tin cần bổ sung sau.
- **D3 — Thiết kế lại từ đầu:** Một điểm tiếp nhận có danh mục dễ tìm, dữ liệu có cấu trúc và quyền truy cập rõ; chưa cần hội thoại tự do ở mọi bước.
- **D4 — Vì sao AI:** Có thể hữu ích khi nhiều cách diễn đạt cùng một nhu cầu. Nếu người dùng chọn dịch vụ nhanh bằng tìm kiếm, chưa có lý do dùng AI.
- **D5 — Thói quen:** Ép người gửi mã hóa danh mục vào subject có thể là di sản của email; cần hỏi chủ quy trình trước khi coi nó là nguyên nhân chính.
- **D6 — Câu hỏi bị né tránh:** Nhầm nhãn có thật sự phổ biến, hay vấn đề chính là danh mục trùng nghĩa và trách nhiệm tiếp nhận chưa rõ?
- **I1 — Pain/tần suất:** Bước phân loại lại có trong đặc tả. Chưa biết số yêu cầu/ngày hoặc tỷ lệ “Khác”; cần đo theo loại và đơn vị.
- **I2 — Workflow/công cụ/bàn giao:** Email Outlook hoặc màn hình SLA → hệ thống tiếp nhận → người phân bổ → người xử lý; cần quan sát một ca thực tế.
- **I3 — Thiệt hại:** Phút đọc/sửa/hỏi lại và thời gian chờ thêm. Chưa có số; thu thời điểm và thời gian thao tác, tránh coi toàn bộ thời gian chờ là công lao động.
- **I4 — AI sai:** Nhãn sai, câu hỏi thừa hoặc bỏ sót thông tin. Nếu tự chuyển yêu cầu có thể lộ thông tin hoặc trễ xử lý; giới hạn bản nháp giữ hậu quả ở công sửa.
- **I5 — Ai nói YES:** Dự án cá nhân: bạn quyết định thời gian/ngân sách thử. Pilot thật: chủ dịch vụ/chủ quy trình cùng bên quản lý dữ liệu và hệ thống; vai trò cụ thể chưa xác nhận. Tiêu chí là giảm công thực tế mà không tăng sai luồng/lộ dữ liệu.
- **P1 — Quy trình:** PS9 và I2; gồm cả vòng hỏi lại, không chỉ lần tạo ticket đầu.
- **P2 — Nút thắt:** Phân loại nội dung và hỏi thông tin còn thiếu, không phải tính giờ SLA.
- **P3 — Hao phí hiện tại:** Chưa biết. Đo thời gian xử lý ít nhất một tập ca thuộc phạm vi, tách dễ/khó/người mới; không dùng con số DBS làm baseline.
- **P4 — Thành công:** Đề xuất thử ngưỡng giảm ít nhất 20% trung vị tổng thời gian thao tác so với form tốt; tỷ lệ phân loại sai sau duyệt không cao hơn đối chứng. Đây là giả thuyết nghiệm thu, cần chốt lại sau baseline.
- **P5 — Sai/boundary:** Như I4; lưu nhãn đề xuất và nhãn cuối, hiển thị mô tả dịch vụ cạnh gợi ý, không dùng câu “AI chắc chắn đúng”.
- **P6 — Phi AI:** Form tìm kiếm danh mục, ví dụ cho từng dịch vụ, trường bắt buộc theo nhánh, sửa taxonomy. Đây là đối thủ bắt buộc trong thử nghiệm.
- **G1 — Cần ngôn ngữ/tri thức:** Có ở mô tả tự do; không ở các trường bắt buộc đã xác định.
- **G2 — Đủ ngữ cảnh:** Chưa. Đặc tả tham chiếu catalogue; cần danh mục đầy đủ, trường cần có, ngoại lệ và nhãn chuẩn.
- **G3 — Có metric:** Đã đề xuất; chưa có baseline và kết quả đo.
- **G4 — Kiểm soát sai:** Khả thi trong bản nháp nếu người phân bổ kiểm được; chưa chứng minh bằng thử nghiệm người dùng.
- **G5 — Đơn giản hơn:** Có, form/rule. Chỉ chọn LLM nếu thắng đối chứng trên ca thực tế.

**FP/FN, UX và điều kiện dừng:** Với quyết định “cần hỏi bổ sung”, FP là hỏi thừa, FN là bỏ sót thông tin cần thiết; phân loại nhãn đo riêng bằng ma trận nhầm lẫn. Test yêu cầu nhiều ý, ngoài danh mục, sai chính tả, trùng tên dịch vụ và nội dung cố chỉ đạo AI. Nếu bất kỳ ca nào làm lộ dữ liệu chéo quyền, dừng phiên bản. Nếu kiểm tra AI mất công hơn form hoặc không đạt chất lượng đối chứng, bỏ phần sinh gợi ý và giữ tìm kiếm/form.

**Bốn kết luận trọng tâm:** AI có khả năng thêm giá trị nhưng chưa được chứng minh; chọn workflow để thử; PS đủ hẹp để lập eval nhưng impact chưa đủ rõ để đầu tư production; Go discovery, Not Yet pilot. Agent chỉ đáng xét khi phải tra nhiều nguồn và chọn bước hỏi khác nhau mà workflow cố định không đạt yêu cầu.

**B — Chuẩn bị bổ sung hồ sơ thanh toán nội bộ PRM**

**Bài toán một câu:** Người lập hồ sơ bị trả về để bổ sung phải diễn giải lý do, tìm chứng từ liên quan và xác định việc cần sửa trước khi gửi lại.

**Bằng chứng tại chỗ:** Tài liệu có chứng từ đính kèm và luồng trả bổ sung; mã nguồn có trạng thái `waiting_for_sup`, lý do và lịch sử trả. Nguồn: [đặc tả quản lý yêu cầu thanh toán](</Users/hieuquockieu/Documents/Dự án cũ/klb/Espo-learning/QTPTPM_BM02_QL_yeu_cau_TT.docx>), [nâng cấp PRM](</Users/hieuquockieu/Documents/Dự án cũ/klb/prm/QTPTPM_BM02_NangcapPRM.docx>), [hành động trả bổ sung](</Users/hieuquockieu/Documents/Dự án cũ/klb/prm/code/src/files/custom/Espo/Modules/Prm/Services/CPaymentApplication/Actions/RejectForSup.php:61>). Sự tồn tại của luồng chưa chứng minh hồ sơ bị trả thường xuyên.

**PS9**

| Trường | Định nghĩa cho thử nghiệm |
|---|---|
| Actor | Người lập hồ sơ chi phí nội bộ và nhân sự kế toán/kiểm tra hồ sơ |
| Workflow | Tạo hồ sơ, đính kèm → người xử lý kiểm tra → trả bổ sung với lý do → người lập đọc/tìm/sửa → gửi lại |
| Bottleneck | Liên kết ghi chú yêu cầu bổ sung với đúng tệp, khoản mục và phiên bản |
| Impact | Công đọc lại, số vòng bổ sung và thời gian hoàn thiện; chưa có baseline |
| Success Metric | Tỷ lệ yêu cầu bổ sung được xử lý đầy đủ, công chuẩn bị/kiểm tra, số lỗi gợi ý |
| Boundary | Không xác nhận hợp lệ kế toán, tự duyệt/chi tiền, sửa chứng từ hay cập nhật trạng thái hồ sơ |
| AI entry | Khi người lập mở một yêu cầu bổ sung đã có lý do |
| Level | Workflow đọc ghi chú và tài liệu; có thể thử agent tra cứu chỉ đọc ở giai đoạn sau |
| Risk/HITL | Mỗi việc gợi ý nối về ghi chú và chứng từ; người lập xác nhận, kế toán vẫn kiểm tra theo quy trình |

**22 câu hỏi Day02**

- **D1:** Hồ sơ bị trả không nhất thiết do người lập thiếu hiểu biết; lý do trả có thể mơ hồ hoặc checklist không rõ.
- **D2:** Biến lý do trả thành danh sách việc cần làm có bằng chứng và câu hỏi cần làm rõ, thay vì chỉ hiển thị đoạn ghi chú.
- **D3:** Mỗi yêu cầu bổ sung có loại vấn đề, tài liệu liên quan, kỳ thanh toán và người giải đáp; nhiều lỗi có thể giảm từ thiết kế form.
- **D4:** AI có ích ở diễn giải ghi chú tự do và liên kết nội dung nhiều tệp. Tồn tại tệp, định dạng, tổng số tiền có thể kiểm bằng code.
- **D5:** Trao đổi bổ sung ngoài hệ thống hoặc ghi chú không cấu trúc có thể là thói quen; cần xác minh, không mặc định đang xảy ra.
- **D6:** Hồ sơ trả vì thiếu tài liệu thật, thông tin không nhất quán, hay do chưa thống nhất tiêu chuẩn giữa người kiểm tra?
- **I1:** Luồng trả bổ sung có căn cứ. Tần suất, số vòng và loại lỗi phổ biến chưa biết; cần lịch sử ca được phép sử dụng.
- **I2:** Người lập dùng PRM/tệp → người kiểm tra trả yêu cầu → người lập bổ sung → người kiểm tra nhận lại. Phải giữ phiên bản từng vòng để đối chiếu.
- **I3:** Đo tổng phút đọc/tìm/chỉnh sửa ở cả hai phía và số vòng trả lại. Chưa thể tính giá trị tiền khi chưa có khối lượng và đơn giá công.
- **I4:** AI có thể đòi giấy tờ không cần hoặc bỏ sót vấn đề. Gợi ý sai làm chậm hồ sơ; nếu coi đầu ra là phê duyệt có thể phát sinh thanh toán sai. Vì vậy giới hạn ở chuẩn bị bổ sung.
- **I5:** Bạn quyết định sandbox; chủ quy trình PRM và bộ phận kiểm tra quyết định pilot. Chỉ số quyết định: giảm vòng sửa/công xử lý, không làm tăng bỏ sót chứng từ quan trọng.
- **P1:** PS9 và I2, bắt đầu sau khi đã có ghi chú trả hồ sơ, không bao trùm cả vòng thanh toán.
- **P2:** Đọc hiểu và ánh xạ yêu cầu bổ sung; chưa chứng minh OCR hay kế toán là nút thắt chính.
- **P3:** Chưa biết; đo theo loại chi phí, độ dài hồ sơ, số vòng bổ sung. Tách hồ sơ scan kém khỏi tệp đọc được.
- **P4:** Ngưỡng đề xuất: giảm 20% trung vị công hoàn thiện một vòng; phát hiện ít nhất 95% yêu cầu bổ sung ghi rõ trong tập thử; không bỏ sót mục được chủ nghiệp vụ gắn nhãn nghiêm trọng. Không dùng tỷ lệ này để tuyên bố an toàn production.
- **P5:** Không kết luận “không có chứng từ” khi chỉ không tìm được. Hiển thị “chưa tìm thấy trong các tệp đã đọc”, tên tệp/trang, lý do và phần chưa đọc được.
- **P6:** Lý do trả có cấu trúc, checklist theo loại chi phí, gắn lỗi trực tiếp vào chứng từ, quy tắc bắt trường bắt buộc.
- **G1:** Có tác vụ đọc hiểu và liên kết bằng chứng; không cần LLM cho mọi phép kiểm.
- **G2:** Chưa đủ. Danh mục chứng từ và loại chi phí không thay thế quy định “loại chi phí nào cần chứng từ nào”. Cần hồ sơ mẫu, lý do trả và đáp án chuyên gia.
- **G3:** Có kế hoạch đo, chưa có baseline và gold set.
- **G4:** Có thể giới hạn ở bản gợi ý chỉ đọc; phải thử xem người lập có phát hiện gợi ý thiếu/sai không.
- **G5:** Có phương án phi AI khá mạnh. Ưu tiên sửa lý do trả/checklist nếu nguyên nhân gốc là thiết kế quy trình.

**FP/FN, UX và điều kiện dừng:** Với phát hiện thiếu chứng từ, FP là báo thiếu khi đã đủ; FN là bỏ qua thiếu thật. Chọn đánh đổi theo loại chứng từ, không dùng một accuracy chung. Test tệp giống tên, nhiều phiên bản, kỳ thanh toán khác, OCR lỗi, ghi chú mâu thuẫn và tệp có nội dung cố điều khiển agent. Dừng gợi ý ở hồ sơ không đọc được hoặc quy định mâu thuẫn; chuyển người kiểm tra với danh sách điểm chưa rõ.

**Bốn kết luận trọng tâm:** Có lý do thử AI ở đọc hiểu; bắt đầu workflow; PS đủ cho thử nghiệm khâu bổ sung nhưng chưa đủ cho tự kiểm tra tính hợp lệ; Go nghiên cứu, Not Yet pilot. Agent chỉ đọc được cân nhắc khi phải chọn động tệp/lịch sử cần xem; đặt giới hạn số lượt đọc, ngân sách và điều kiện dừng. Phải chứng minh nó tốt hơn workflow trên cùng bộ ca.

**C — Tra cứu quy trình cho nhân viên tuyến đầu**

**Bài toán một câu:** Nhân viên nhận một câu hỏi nhiều điều kiện nhưng khó tìm đúng quy trình và ngoại lệ để biết cần hỏi thêm gì, xử lý ở đâu hoặc chuyển ai.

**Căn cứ:** DBS/OCBC xác nhận tiền lệ trợ lý tri thức. Trong thư mục KLB, quy trình Pay/PayGate có nhiều nhánh theo loại khách hàng và giai đoạn đăng ký, thích hợp để dựng một phạm vi học ban đầu. [Quy trình KLB Pay/PayGate](</Users/hieuquockieu/Documents/Dự án cũ/klb/PL01 - QT dang ky dich vu KLB Pay va KLB PayGate (1) (1).docx>). Trải nghiệm đóng thẻ của bạn là một tín hiệu discovery; chưa có hồ sơ và quy trình thẻ để kết luận chính xác trách nhiệm hoặc điều kiện đóng thẻ.

**PS9**

| Trường | Định nghĩa cho thử nghiệm |
|---|---|
| Actor | Nhân viên mới, người hỗ trợ tuyến hai, chủ quy trình |
| Workflow | Nhận câu hỏi → thu ngữ cảnh → tìm tài liệu → đối chiếu điều kiện → hỏi chuyên gia nếu cần → trả lời |
| Bottleneck | Chọn đúng quy trình/phiên bản và nhận biết điều kiện còn thiếu |
| Impact | Công tra cứu, chuyển tiếp sai, khách phải hỏi lại; mức tổn thất chưa đo |
| Success Metric | Đúng quy trình/ngoại lệ, chất lượng chuyển tiếp, thời gian có phương án đã kiểm chứng |
| Boundary | Không cam kết quyền lợi, phí, khả năng đóng thẻ hay điều kiện sử dụng khi thiếu căn cứ; không thao tác tài khoản |
| AI entry | Sau khi có mô tả tình huống, trước khi nhân viên quyết định hướng dẫn |
| Level | Workflow tra cứu có nguồn; agent chỉ nếu cần chọn động nguồn/tra trạng thái được cấp quyền |
| Risk/HITL | Chủ nghiệp vụ duyệt tình huống mẫu; nội dung ảnh hưởng tiền/quyền khách hàng cần người có năng lực kiểm chứng |

**22 câu hỏi Day02**

- **D1:** Nhân viên mới không nhất thiết cần nhớ tất cả; họ cần nhận diện điều kiện quyết định và biết khi nào không đủ căn cứ.
- **D2:** Trả một phiếu gồm dữ kiện đã biết, câu hỏi cần bổ sung, quy trình có thể áp dụng và người nhận chuyển tiếp; không chỉ một câu trả lời tự tin.
- **D3:** Quy trình có chủ sở hữu, ngày hiệu lực, các nhánh điều kiện và đường chuyển tiếp rõ; chức năng tìm kiếm tốt là nền tảng.
- **D4:** AI có lợi thế ở ánh xạ lời khách sang thuật ngữ nghiệp vụ và tổng hợp đoạn liên quan. Không thể thay thế quy trình chưa được xác định.
- **D5:** Phụ thuộc “người quen ở core” là một tín hiệu từ trải nghiệm của bạn; cần kiểm tra tổ chức có đầu mối hỗ trợ chính thức và vì sao nhân viên không dùng.
- **D6:** Sai do không biết, tài liệu khó tìm, thông tin khách chưa đủ, hay thiếu quyền xử lý? Bốn nguyên nhân dẫn tới bốn giải pháp khác nhau.
- **I1:** Có trải nghiệm cá nhân và tiền lệ công khai; chưa biết tần suất tại nơi áp dụng. Thu một nhóm câu hỏi khó và các lần chuyển tiếp đã xử lý đúng.
- **I2:** Quầy/call center → kho tài liệu hoặc chuyên gia tuyến hai → nhân viên → khách. Luồng này là mô hình cần xác nhận, không khẳng định mọi ngân hàng giống nhau.
- **I3:** Đo thời gian tra cứu và số lần khách phải liên hệ lại; không quy tiền mọi khiếu nại khi chưa có dữ liệu.
- **I4:** Trả lời sai có thể khiến khách mất thời gian, chi phí hoặc cơ hội. Dẫn chứng sai nhưng thuyết phục khó bị nhân viên mới phát hiện; HITL bởi người mới đơn độc chưa đủ.
- **I5:** Bạn duyệt nghiên cứu; chủ sản phẩm/quy trình, vận hành và bên quản lý rủi ro liên quan cần tham gia nếu pilot. Chất lượng và lỗi nghiêm trọng có quyền phủ quyết KPI tốc độ.
- **P1:** PS9; phải bao gồm bước làm rõ và chuyển người, không giả định mọi câu đều trả lời được.
- **P2:** Tìm nguồn đúng ngữ cảnh, không phải sinh câu văn trôi chảy.
- **P3:** Chưa có baseline. Đo bằng nhân viên/chuyên gia thực hiện ca cùng loại trên kho tài liệu hiện có.
- **P4:** Ngưỡng thử đề xuất: giảm 20% thời gian có phương án đã kiểm tra; ít nhất 95% nhận định kiểm chứng được có dẫn chứng thực sự hỗ trợ; không có cam kết nghiêm trọng vô căn cứ trong tập thử. Báo riêng mức độ bao phủ, không để hệ thống từ chối mọi câu để đạt “an toàn”.
- **P5:** Mọi kết luận gắn đúng phiên bản và điều kiện; thông tin thiếu phải hiện rõ. Không giải thích một suy luận như trích quy định. Ngoại lệ chưa xác định chuyển chủ nghiệp vụ.
- **P6:** Cây quyết định, FAQ được biên tập, tìm kiếm theo từ đồng nghĩa, đầu mối hỗ trợ tuyến hai.
- **G1:** Có ngôn ngữ và tri thức theo bối cảnh.
- **G2:** Chưa đủ cho case đóng thẻ. Pay/PayGate có tài liệu để mô phỏng nhưng cần xác nhận hiệu lực và phụ lục được dẫn chiếu.
- **G3:** Có thiết kế metric; chưa có bộ câu hỏi/đáp án được chủ nghiệp vụ xác nhận.
- **G4:** Kiểm soát tốt hơn trong sandbox; tư vấn thật vẫn có rủi ro đáng kể dù AI không chạm tiền.
- **G5:** Cây quyết định có thể đủ cho một quy trình nhỏ; chỉ thử LLM khi biến thể ngôn ngữ/tra cứu làm cách này kém hiệu quả.

**FP/FN, UX và điều kiện dừng:** Với quyết định chuyển chuyên gia, FP là chuyển ca nhân viên có thể xử lý; FN là không chuyển ca cần chuyên gia. FN có thể nghiêm trọng hơn. Test quy định hết hiệu lực, sản phẩm khác nhau nhưng cùng tên gọi, thiếu dữ kiện, tài liệu mâu thuẫn và câu hỏi gài yêu cầu AI khẳng định. Nếu có câu trả lời sai về quyền/chi phí mà người duyệt bỏ qua, tạm dừng sinh kết luận ở nhóm đó, chỉ hiển thị nguồn và đầu mối.

**Bốn kết luận trọng tâm:** Có căn cứ về nhu cầu trợ lý tri thức; workflow là điểm bắt đầu; PS đủ cho một quy trình mô phỏng nhưng chưa đủ cho case thẻ thật; Go nghiên cứu, Not Yet tư vấn vận hành. Ưu tiên Pay/PayGate do đã có tài liệu, hoặc chờ đủ hồ sơ thẻ trước khi dùng ví dụ đóng thẻ làm bài kiểm tra đúng/sai.

**D — Tóm tắt tương tác và bàn giao vụ việc**

**Bài toán một câu:** Nhân viên phải đọc lại hội thoại và ghi chú để chuẩn bị bản bàn giao đủ dữ kiện, việc đã làm và việc còn chờ.

**Căn cứ:** Morgan Stanley Debrief và DBS CSO Assistant xác nhận ứng dụng chuẩn bị tóm tắt/ghi chú. Chưa tìm thấy tập transcript được gán nhãn trong tài liệu KLB đã đọc. Phiên bản cá nhân nên bắt đầu từ hội thoại text giả lập, chưa cần ghi âm hay nhận dạng giọng nói.

**PS9**

| Trường | Định nghĩa cho thử nghiệm |
|---|---|
| Actor | Nhân viên CSKH và nhân viên nhận bàn giao |
| Workflow | Kết thúc trao đổi → đọc lại → ghi vấn đề/dữ kiện/việc đã làm → xác định việc chờ → kiểm tra → bàn giao |
| Bottleneck | Tổng hợp đúng các mốc và giữ được phủ định, thay đổi ý định, trách nhiệm |
| Impact | Công ghi chú, hỏi lại và xử lý theo thông tin sai; chưa đo |
| Success Metric | Thời gian hoàn thành bản bàn giao đã kiểm tra; độ đầy đủ dữ kiện quan trọng; lỗi bịa hoặc đổi nghĩa |
| Boundary | Bản nháp, chưa ghi thành hồ sơ chính thức hoặc gửi khách; không tự tạo cam kết hoàn tiền/hạn xử lý |
| AI entry | Khi chọn cuộc trao đổi cần bàn giao |
| Level | Workflow với cấu trúc đầu ra cố định |
| Risk/HITL | Người phụ trách đối chiếu các dữ kiện quan trọng với đoạn hội thoại gốc trước khi xác nhận |

**22 câu hỏi Day02**

- **D1:** Không phải mọi bản tóm tắt cần văn xuôi; người nhận có thể cần năm trường thông tin hơn một đoạn đẹp.
- **D2:** Tạo bản nháp theo trường, mỗi dữ kiện mở được đoạn gốc; tách lời khách nói, việc đã xác minh và hành động dự kiến.
- **D3:** Ghi nhận sự kiện có cấu trúc trong quá trình làm việc để giảm nhu cầu tái dựng sau cuộc gọi.
- **D4:** LLM phù hợp đọc hội thoại biến thiên và rút thông tin. Nếu sự kiện đã có cấu trúc, template có thể đủ.
- **D5:** Sao chép hội thoại dài vào CRM hoặc viết lại điều đã có có thể là công việc do công cụ rời rạc; cần xác minh.
- **D6:** Cần tóm tắt hơn hay cần thống nhất chuẩn bàn giao và đồng bộ dữ liệu giữa hệ thống?
- **I1:** Tiền lệ thị trường rõ; pain và số lần bàn giao tại nơi áp dụng chưa được đo.
- **I2:** Kênh trao đổi → ghi chú/case → người nhận bàn giao. Trong sandbox dùng dữ liệu text và phiếu bàn giao, không giả định kết nối CRM thật.
- **I3:** Đo thời gian soạn, kiểm tra, sửa và đọc lại do thiếu thông tin. Không chỉ đo tốc độ model sinh chữ.
- **I4:** Bỏ từ “chưa”, nhầm số tiền/ngày/người, biến ý định thành cam kết. Những lỗi này có thể tác động nghiệp vụ dù sản phẩm chỉ tạo văn bản.
- **I5:** Bạn duyệt thử nghiệm; trưởng nhóm dịch vụ và chủ quy trình ghi nhận hồ sơ duyệt pilot. Chất lượng bàn giao và thời gian tổng là metric chính.
- **P1:** PS9; tính cả việc người nhận phải quay lại nguồn, không kết thúc đo ở thời điểm nhấn tạo tóm tắt.
- **P2:** Trích đúng dữ kiện và phân biệt trạng thái đã làm/chưa làm.
- **P3:** Chưa có số; baseline là điền cùng một mẫu bằng tay với quyền xem cùng nguồn.
- **P4:** Ngưỡng thử đề xuất: giảm ít nhất 25% trung vị thời gian tạo và kiểm tra; giữ 100% trường nghiêm trọng đã được gán nhãn trong tập thử; không có cam kết bịa. Mẫu nhỏ không đủ suy ra tỷ lệ lỗi thật bằng 0.
- **P5:** Nối số tiền, ngày, người chịu trách nhiệm và cam kết về nguồn; trường không có thông tin phải để “chưa xác định”, không điền cho đủ.
- **P6:** Template ghi chú, trường sự kiện có cấu trúc, checklist bàn giao và đồng bộ dữ liệu sẵn có.
- **G1:** Có, khi nguồn là hội thoại tự do.
- **G2:** Có thể tạo đủ ngữ cảnh cho ca giả lập; chưa có transcript thực tế và yêu cầu lưu hồ sơ được xác nhận.
- **G3:** Metric rõ; gold set và baseline chưa có.
- **G4:** Hậu quả được giới hạn hơn ở bản nháp, nhưng phụ thuộc khả năng người duyệt phát hiện bỏ sót.
- **G5:** Template là đối chứng bắt buộc; không cần agent để tóm tắt một nguồn đã chọn.

**FP/FN, UX và điều kiện dừng:** Ở cấp dữ kiện, FP là thêm nhận định không được nguồn hỗ trợ; FN là bỏ dữ kiện quan trọng. Đánh giá theo trọng số nghiệp vụ, không chỉ mức tương đồng văn bản. Test phủ định, đổi lịch, sửa số tiền, hai người trùng tên, hội thoại dài và đoạn bị thiếu. Có sai nghiêm trọng sau người duyệt thì dừng phiên bản, sửa thiết kế đối chiếu và kiểm thử lại.

**Bốn kết luận trọng tâm:** Bằng chứng về ứng dụng khá rõ; chọn workflow; đủ định nghĩa để làm eval mô phỏng nhưng chưa đủ chứng minh nhu cầu nội bộ; Go nghiên cứu, Not Yet pilot. Agent không có lý do ở phạm vi này; chỉ xem lại khi thật sự phải tìm và hợp nhất nhiều hồ sơ được cấp quyền.

**E — Luyện tập xử lý tình huống cho nhân viên mới**

**Bài toán một câu:** Nhân viên mới ít cơ hội thực hành tình huống khó trước khi phải trả lời khách thật, trong khi người hướng dẫn có thời gian hữu hạn.

**Căn cứ:** Bank of America công bố sử dụng mô phỏng hội thoại bằng AI. Đây là tiền lệ cho hình thức đào tạo; hiệu quả của một phiên bản LLM/agent do bạn xây vẫn là giả thuyết. Tài liệu Pay/PayGate hoặc SLA có thể làm nền cho bài luyện, sau khi một người hiểu quy trình duyệt đáp án.

**PS9**

| Trường | Định nghĩa cho thử nghiệm |
|---|---|
| Actor | Nhân viên mới và người hướng dẫn nghiệp vụ |
| Workflow | Học quy trình → đọc tình huống → hỏi làm rõ → đề xuất xử lý/chuyển tiếp → nhận phản hồi → làm tình huống mới |
| Bottleneck | Thiếu tình huống đa dạng và phản hồi cụ thể theo câu trả lời |
| Impact | Công hướng dẫn, thời gian đạt năng lực, lỗi khi áp dụng; chưa đo |
| Success Metric | Kết quả trên tình huống mới do người đánh giá độc lập chấm; khả năng nhận biết khi chưa đủ thông tin |
| Boundary | Không chấm KPI nhân sự, xếp hạng hay kết luận kỷ luật; không dùng đầu ra chưa duyệt làm quy trình |
| AI entry | Đóng vai khách/người yêu cầu, sau đó đề xuất phản hồi theo rubric |
| Level | Hội thoại có trạng thái và giới hạn; chưa cần agent đa công cụ |
| Risk/HITL | Dữ kiện kịch bản và đáp án chuẩn được khóa; chuyên gia kiểm tra phản hồi, người học mở được căn cứ |

**22 câu hỏi Day02**

- **D1:** Trả lời lưu loát hoặc nhớ đúng từ khóa chưa chứng minh nhân viên biết hỏi đúng câu và chuyển tuyến đúng lúc.
- **D2:** Luyện xác định dữ kiện còn thiếu và giới hạn thẩm quyền, rồi mới luyện trả lời cuối cùng.
- **D3:** Bộ tình huống có mục tiêu học, điều kiện ẩn, tiêu chí và phản hồi chuẩn; cho phép làm lại bằng tình huống tương đương.
- **D4:** LLM có thể tạo hội thoại thích ứng với câu hỏi của người học. Nếu mục tiêu chỉ nhớ quy định thì flashcard/quiz đủ.
- **D5:** Đào tạo chỉ đọc tài liệu rồi làm trắc nghiệm có thể bỏ qua kỹ năng xử lý; cần quan sát đào tạo thật trước khi kết luận.
- **D6:** Tổ chức có thống nhất đáp án và tiêu chí đánh giá không? AI không giải quyết được bất đồng nghiệp vụ bằng cách tự chọn một đáp án.
- **I1:** Trải nghiệm của bạn gợi ý khoảng trống năng lực, chưa chứng minh đào tạo là nguyên nhân. Cần biết loại lỗi và tần suất nhân viên mới gặp.
- **I2:** Người hướng dẫn cấp tài liệu/kịch bản → nhân viên thực hành → nhận phản hồi → đánh giá trên ca mới. Đây là quy trình thử nghiệm đề xuất.
- **I3:** Đo thời gian hướng dẫn và kết quả học; số lượt chat không chứng minh tiết kiệm hay cải thiện năng lực.
- **I4:** AI có thể dạy sai hoặc đánh giá bất công, khiến người học tự tin sai. Hậu quả không mất tiền ngay nhưng có thể tích lũy và xuất hiện khi làm việc thật.
- **I5:** Bạn duyệt nghiên cứu; chủ đào tạo và chủ nghiệp vụ duyệt nội dung/pilot. Metric quyết định là năng lực chuyển sang ca mới và độ đúng của phản hồi, không chỉ mức hài lòng.
- **P1:** PS9; giữ bước kiểm tra độc lập sau học, không dùng chính câu đã luyện làm chứng minh tiến bộ.
- **P2:** Thiếu thực hành và phản hồi; cần xác thực thay vì mặc định thiếu chatbot.
- **P3:** Chưa có baseline. So sánh với học tài liệu + role-play/quiz truyền thống trong cùng thời lượng.
- **P4:** Ngưỡng thử đề xuất: tăng ít nhất 10 điểm phần trăm trên rubric ca mới, không tăng tự tin vào câu trả lời sai; kiểm tra duy trì kiến thức sau một khoảng thời gian thống nhất. Đây chưa phải hiệu quả đã chứng minh.
- **P5:** Tách AI đóng vai khách khỏi căn cứ chấm. Không để AI tự sửa đáp án để khớp câu trả lời của người học. Phản hồi sai nghiêm trọng phải rút bài và thông báo nội dung sửa cho người đã học.
- **P6:** Role-play với người hướng dẫn, bộ case có đáp án, cây quyết định, quiz theo lỗi thường gặp.
- **G1:** Có ngôn ngữ và tương tác động, phù hợp để thử mô phỏng.
- **G2:** Chưa có bộ kịch bản/rubric chuẩn; tài liệu thô không đủ để chấm đúng sai đáng tin.
- **G3:** Có metric đề xuất; chưa có nhóm người học và phép đo đối chứng.
- **G4:** Sandbox giảm tác động trực tiếp nhưng không loại bỏ rủi ro học sai. Cần người có chuyên môn kiểm tra nội dung.
- **G5:** Quiz và role-play là đối chứng; chỉ dùng LLM nếu tạo thêm giá trị ở tương tác hoặc giảm công hướng dẫn có đo lường.

**FP/FN, UX và điều kiện dừng:** Với phát hiện lỗi thao tác, FP là phê bình một hành vi đúng; FN là bỏ qua lỗi thật. Chấm nhiều cách diễn đạt, cho phép nhiều phương án hợp lệ, có lựa chọn khiếu nại phản hồi. LLM tự chấm có thể hỗ trợ sàng lọc nhưng không là trọng tài duy nhất. Dừng bài có phản hồi sai về điều kiện nghiệp vụ; không gộp điểm luyện tập thành điểm đánh giá nhân sự.

**Bốn kết luận trọng tâm:** Có lý do thử mô phỏng ngôn ngữ; workflow hội thoại đủ cho phiên bản đầu; PS đủ để thiết kế thử nghiệm học tập nhưng chưa có rubric/baseline; Go nghiên cứu, Not Yet dùng chấm năng lực thật. Agent chỉ đáng xét nếu phải tự chọn bài tiếp theo theo lịch sử học và chứng minh lợi ích so với bộ quy tắc chọn bài.

**Các hướng đã cân nhắc nhưng chưa chọn làm dự án đầu tiên**

| Hướng | Vì sao có vẻ hấp dẫn | Quyết định cho phạm vi hiện tại |
|---|---|---|
| Tự quyết định đóng thẻ, xử lý trả góp, hoàn tiền | Gần trải nghiệm của bạn, tác động rõ | No-Go cho agent tự quyết ở dự án cá nhân hiện tại: thiếu quy trình/sự thật giao dịch và hậu quả khó đảo ngược. Nghiên cứu trợ lý chuẩn bị phương án vẫn có thể nằm trong C. |
| Tự mở khóa ngân hàng điện tử, thay đổi hạn chế giao dịch | Có tài liệu tại KLB, nhiều nhánh xử lý | No-Go cho quyền hành động của LLM trong phạm vi này. Quyền truy cập và điều kiện trạng thái cần logic được kiểm thử; trợ lý chỉ giải thích/tìm nguồn là bài toán khác. |
| Tự chấm điểm nhân viên từ hội thoại | Gần ý tưởng phát hiện tư vấn sai | Not Yet cho gợi ý QA; No-Go cho dùng điểm AI đơn độc quyết định nhân sự. Thiếu rubric, nhãn đáng tin và cơ chế phản biện; chuyển trọng tâm sang coaching E. |
| Agent đọc thay đổi quy trình và đề xuất cập nhật tài liệu | Có đường tra cứu nhiều bước thực sự phù hợp agent | Not Yet: chưa có chuỗi phiên bản, chủ tài liệu và bản đồ phụ thuộc đủ rõ; có thể nghiên cứu sau A/C. |
| Viết nháp phân tích tín dụng/KYC | Có tiền lệ ngân hàng lớn dùng GenAI trong một số khâu | Not Yet cho dự án đầu tiên: chi phí kiểm chứng và dữ liệu chuyên môn cao hơn. Không suy từ “hỗ trợ viết” thành “tự phê duyệt”. |

Đây là quyết định theo dữ liệu/quyền hạn hiện có của bạn, không phải kết luận rằng ngành ngân hàng không bao giờ có thể áp dụng AI ở các lĩnh vực đó. Các hướng bị loại sớm chưa được coi là Problem Statement hoàn chỉnh để triển khai.

**Nếu agent sai, ai và cơ chế nào thực sự chặn được?**

“Có người duyệt” là một giả thuyết cần thử, không phải một thuộc tính bảo đảm an toàn. NIST liệt kê rủi ro nội dung sai nhưng được trình bày tự tin, cùng các vấn đề tương tác người–AI như quá tin vào hệ thống. [NIST AI 600-1, 2024](https://nvlpubs.nist.gov/nistpubs/ai/NIST.AI.600-1.pdf).

Thiết kế đề xuất cho năm bài toán:

| Loại lỗi theo Day02 | Ví dụ | Cách phát hiện và phục hồi |
|---|---|---|
| Sai bối cảnh | Dùng quy trình tổ chức cho cá nhân, dùng phiên bản hết hiệu lực | Bắt buộc hiện loại đối tượng, phiên bản và dữ kiện còn thiếu; kiểm nhánh trước khi sinh kết luận |
| Không thực hiện được | Tệp hỏng, không có nguồn, dịch vụ model lỗi | Thông báo phần đã làm/chưa làm; trả form, nguồn hoặc checklist cho người tiếp tục |
| Lỗi ngầm | Nguồn có thật nhưng không hỗ trợ kết luận; tóm tắt bỏ mất phủ định | Kiểm tra chủ động bằng ca lỗi cài sẵn, đối chiếu chuyên gia, lấy mẫu cả output được chấp nhận |
| Lỗi dữ liệu/đầu vào | Nhãn sai, scan mờ, lời nói mơ hồ | Gắn trạng thái chất lượng; không biến OCR lỗi thành kết luận nghiệp vụ |
| Lỗi giữa các thành phần | Bộ phân loại chọn nhánh A nhưng bộ sinh dùng quy trình B | Truyền mã nguồn/phiên bản có cấu trúc, kiểm nhất quán và dừng khi mâu thuẫn |

Với một agent thử nghiệm, phân quyền và quyền ghi do ứng dụng kiểm soát; nội dung trong tài liệu là dữ liệu, không có quyền thay đổi chỉ dẫn hay mở rộng công cụ. Chỉ cấp công cụ đọc trong phạm vi case; mọi đầu ra là bản nháp. Giới hạn vòng lặp, thời gian và chi phí; khi hết ngân sách trả phần việc đã xác minh và phần còn thiếu. Log cần đủ để dựng lại nguồn, phiên bản, lời gọi công cụ và chỉnh sửa của người dùng, đồng thời tránh sao chép dữ liệu nhạy cảm không cần thiết.

Không dùng mức “confidence 95%” do LLM tự nói như ngưỡng an toàn. Cần đo hiệu năng trên tập ca có đáp án, chia theo nhóm và kiểm tra sự phù hợp của việc từ chối. Hệ thống đạt tỷ lệ đúng cao nhờ chỉ trả lời câu dễ vẫn có thể không giúp người dùng hoàn thành công việc.

**Kế hoạch đo lường trước khi viết sản phẩm đầy đủ**

1. **Khóa một phạm vi:** một bộ phận SLA, một nhóm chi phí PRM, hoặc một quy trình Pay/PayGate. Chỉ chọn một trong năm hướng cho vòng đầu.
2. **Viết nguồn chuẩn:** chủ sở hữu, phiên bản, điều kiện áp dụng, ví dụ đúng/sai và trường hợp phải chuyển người. Nếu chưa có chuyên gia, ghi rõ bộ quy tắc là mô phỏng; không coi kết quả là xác nhận nghiệp vụ thật.
3. **Đo baseline:** quan sát khoảng 20–30 ca để khám phá biến thể và ước lượng công; số này là gợi ý ban đầu, không bảo đảm đủ độ tin cậy thống kê. Ghi cả công soạn, tra cứu, kiểm tra, sửa và bàn giao.
4. **Làm đối chứng tốt:** form/checklist/tìm kiếm đã cải thiện, thay vì cố tình so AI với quy trình thủ công yếu. Nếu ngân sách cho phép, thêm mô hình phân loại nhỏ cho A.
5. **Tạo tập thử tách biệt:** khởi đầu đề xuất 100 ca cho mỗi phạm vi được chọn, ví dụ 50 thông thường, 20 mơ hồ/thiếu thông tin, 15 ngoài phạm vi hoặc mâu thuẫn, 15 lỗi nguồn/quyền/prompt injection. Đây là thiết kế, chưa phải dữ liệu đã có. Số cuối cùng tùy độ đa dạng/rủi ro.
6. **Gán đáp án đáng tin:** chủ nghiệp vụ xác nhận; ca bất đồng cần phân xử hoặc đánh dấu không đủ căn cứ. Giữ các bản diễn đạt lại của cùng tình huống trong cùng một tập để tránh rò rỉ giữa phát triển và kiểm thử.
7. **So sánh trên người dùng:** phân nhóm hoặc đổi thứ tự tác vụ giữa các người, dùng ca tương đương để giảm hiệu ứng học. Đo người mới và người có kinh nghiệm riêng; không để trung bình che nhóm bị ảnh hưởng xấu.
8. **Thử cả người duyệt:** cài những lỗi có kiểm soát trong sandbox để đo tỷ lệ phát hiện và thời gian sửa. Nếu người mới thường bỏ qua lỗi, phải đổi thiết kế hoặc chuyển người duyệt; không chỉ tăng một thông báo cảnh báo.
9. **Chốt quyết định:** tăng phạm vi chỉ khi đạt chất lượng, hiệu quả và ranh giới. Chưa đủ bằng chứng là Not Yet; đối chứng đơn giản thắng thì chọn nó và No-Go cho phần AI tương ứng.

**Output metric** là thời gian hoàn thành công việc có chất lượng, số vòng hỏi/sửa hoặc năng lực trên ca mới. **Input metrics** là tỷ lệ nhãn đúng, dẫn chứng đúng, dữ kiện thiếu, thời gian sửa bản nháp. Cần cả hai: cải thiện chỉ số model nhưng không cải thiện công việc thì chưa chứng minh giá trị sản phẩm.

Với “reward function” của Day02, đề xuất dùng thứ tự ưu tiên: vượt ranh giới hoặc lỗi nghiêm trọng thì fail; nếu qua điều kiện chất lượng mới so hiệu quả thời gian/chi phí. Không cho điểm văn phong bù cho một kết luận nghiệp vụ sai. Đây là tiêu chí chấm sản phẩm/eval, không có nghĩa phải huấn luyện reinforcement learning. Đúng/sai do nguồn và đáp án nghiệp vụ quyết định; ý kiến LLM thứ hai chỉ là một tín hiệu hỗ trợ.

Đo thêm p50/p95 độ trễ từ thao tác đến kết quả dùng được, chi phí mỗi ca hoàn thành, tỷ lệ bỏ ngang và mức người dùng cảm thấy kiểm soát được công việc. Ngưỡng độ trễ/ngân sách phải chốt với bạn sau phép đo đầu, vì bản nháp hồ sơ và hỗ trợ trong cuộc gọi có yêu cầu rất khác nhau. Chưa có căn cứ để dùng một giới hạn chung cho cả năm bài toán.

Chi phí đầy đủ mỗi ca gồm chi phí mô hình/công cụ, thời gian người kiểm tra, sửa lỗi, duy trì tài liệu và hỗ trợ vận hành. Không chỉ so giá token. Có thể tính:

`Giá trị thời gian/ca = công lao động ở đối chứng − công lao động với AI, bao gồm kiểm tra và làm lại`.

Sau đó mới quy đổi bằng đơn giá công thực tế và trừ chi phí hệ thống. Không tự gán xác suất hoặc giá tiền cho lỗi nghiêm trọng khi chưa có căn cứ; các lỗi vượt ranh giới là điều kiện loại, không được bù bằng tốc độ trung bình.

**Ngưỡng và hành động đề xuất**

Các ngưỡng trong A–E là điểm xuất phát để thương lượng và thử, không phải chuẩn ngành. Phải khóa ngưỡng trước khi xem kết quả tập kiểm thử cuối.

| Tín hiệu | Hành động |
|---|---|
| Bất kỳ truy cập/ghi dữ liệu vượt quyền hoặc rò rỉ chéo hồ sơ | Dừng phiên bản, lưu bằng chứng phù hợp, sửa lớp quyền và kiểm thử lại trước khi tiếp tục |
| Có lỗi nghiệp vụ nghiêm trọng vượt qua người duyệt trong sandbox | Hạ về tra cứu/hiện nguồn, xem lại điều kiện chuyển chuyên gia và cách kiểm tra |
| Không đạt lợi ích thời gian/chất lượng so với đối chứng | Not Yet hoặc No-Go phần AI; giữ cải tiến form/checklist có hiệu quả |
| Hệ thống từ chối quá nhiều khiến công việc không hoàn thành | Xem lại dữ liệu/phạm vi; không tuyên bố thành công chỉ vì tỷ lệ sai thấp |
| Quy trình hoặc danh mục đổi phiên bản | Chạy lại nhóm ca bị ảnh hưởng, kiểm tra nguồn và cập nhật đáp án trước khi mở dùng |
| Chất lượng giữa nhóm nhân viên/ngôn ngữ khác nhau chênh đáng kể | Phân tích riêng, thu thêm ca và giới hạn phạm vi phù hợp; không chỉ công bố số trung bình |

Các mẫu ít ca không chứng minh mức lỗi hiếm đủ thấp. Ví dụ không thấy lỗi nghiêm trọng trong 100 ca chỉ nói rằng bộ thử đó chưa phát hiện lỗi; không tương đương hệ thống không bao giờ sai. Trước pilot cần kế hoạch lấy mẫu và độ tin cậy phù hợp rủi ro, chưa có trong dữ liệu hiện tại.

**Ai duy trì vào ngày 1000?**

Trong sandbox, bạn phụ trách dữ liệu, đo chi phí/lỗi và phiên bản cấu hình. Nếu tiến tới pilot, phải xác định người sở hữu danh mục SLA, checklist PRM, quy trình phục vụ hoặc rubric đào tạo; người theo dõi lỗi; và người có quyền tắt tính năng. Đề xuất review mỗi tuần khi thử, sau mỗi thay đổi tài liệu/model/prompt, rồi điều chỉnh nhịp theo dữ liệu vận hành. Giữ phiên bản cũ để so sánh/khôi phục và một đường làm việc thủ công khi AI không dùng được.

Theo hướng buy/boost/build trong Day02, dự án cá nhân phù hợp bắt đầu bằng mô hình có sẵn và nguồn được tuyển chọn, tự xây phần workflow/eval nhỏ. Đây là lựa chọn thực dụng của báo cáo, không phải yêu cầu phải fine-tune hoặc mua một nền tảng agent. Quyền dùng tài liệu và nơi xử lý dữ liệu cần được xác định trước thử nghiệm với dữ liệu thật; nghiên cứu này không gửi tài liệu KLB lên dịch vụ mô hình bên ngoài.

**Đặt Software 1.0, 2.0 và 3.0 cạnh nhau mà không ép đủ bộ**

| Công việc | 1.0 | 2.0, nếu có dữ liệu phù hợp | 3.0 |
|---|---|---|---|
| SLA | Danh mục, trường bắt buộc, quyền, lịch và trạng thái | Phân loại mô tả → nhãn từ các ví dụ được gán nhãn | Hiểu mô tả, đề xuất nhãn, hỏi làm rõ |
| PRM | Kiểm tồn tại tệp, định dạng, tổng số, quy tắc trùng | Phân loại chứng từ/loại chi phí | Đọc ghi chú, liên kết bằng chứng, soạn checklist bổ sung |
| Quy trình tuyến đầu | Điều kiện, phiên bản, quyền tra cứu | Phân loại ý định nếu dữ liệu chứng minh cần | Tìm và diễn đạt đoạn liên quan, nhận biết thiếu ngữ cảnh |
| Bàn giao | Mẫu và kiểm tra trường | Thành phần trích xuất/phân loại được huấn luyện riêng nếu cần | Tóm tắt, giữ sự khác biệt giữa đã làm/chưa làm |
| Đào tạo | Kịch bản, trạng thái, tiêu chí và đáp án | Không cần đưa vào chỉ để đủ ba loại | Đối thoại biến thiên và phản hồi có căn cứ |

Danh mục 33 loại chi phí trong mã PRM là nhãn tham khảo, không phải 33 ví dụ huấn luyện. Dữ liệu synthetic có thể giúp phát triển ban đầu nhưng không thay thế đánh giá trên cách diễn đạt và lỗi thực tế. Không bắt buộc xây cả ba loại trong một sản phẩm.

**Đề xuất ưu tiên cuối cùng và điều gì có thể làm thay đổi lựa chọn**

Chọn **A – SLA** nếu bạn muốn một bài toán dễ hiểu từ đầu tới cuối, có đầu ra tương đối dễ kiểm và ít phụ thuộc dữ liệu tiền thật. Điều kiện trước tiên là có danh mục nhỏ đủ rõ. Nếu form tìm kiếm giải quyết tốt hoặc người dùng ít gặp vấn đề phân loại, bỏ AI ở hướng này.

Chọn **B – PRM** nếu bạn muốn tận dụng code và nghiên cứu agent một cách có lý do. Bắt đầu từ một yêu cầu bổ sung đã có, với một nhóm chi phí và tệp đọc được. Nếu không có quy tắc chứng từ/đáp án hoặc lý do trả quá mơ hồ, chuẩn hóa chúng trước.

Chọn **E – đào tạo** nếu động lực lớn nhất là học nghiệp vụ và giúp người mới, gần trải nghiệm cá nhân của bạn. Đây có thể là sandbox tốt nhưng không tự nhiên “an toàn”: cần kiểm chứng tránh dạy sai. Khi chưa có người duyệt đáp án, chỉ dùng để khám phá câu hỏi và đọc nguồn, chưa dùng để kết luận năng lực.

**Điểm cần làm rõ trước khi viết code:** một người cụ thể đang mắc ở bước nào, bao nhiêu lần, nguồn nào phân xử đúng/sai, và nếu AI bỏ sót thì ai có đủ năng lực phát hiện. Đó là thông tin quyết định lựa chọn theo Day02. Việc một ngân hàng khác đã dùng GenAI giúp xác nhận hướng nghiên cứu có cơ sở; nó không trả lời thay bốn câu hỏi này cho dự án của bạn.

**Dấu vết nghiên cứu và giới hạn**

- Đã đọc lại 76 trang Day02, đối chiếu các nhóm câu hỏi và PS9; tham khảo đặc tả SLA, PRM, Pay/PayGate cùng một phần mã PRM đã đọc trong lượt trước.
- Đã tìm nguồn sơ cấp về trợ lý nhân viên, tóm tắt, đào tạo, tài liệu nghiệp vụ, kiến trúc workflow/agent và rủi ro tương tác người–AI. Nguồn được dẫn trực tiếp cạnh nhận định.
- Chưa phỏng vấn, chưa xác minh hiệu lực vận hành của tài liệu KLB, chưa có dataset nghiệp vụ được duyệt, chưa chạy benchmark hoặc huấn luyện mô hình.
- Bằng chứng quốc tế không xác nhận hiệu quả với tiếng Việt, cách viết nội bộ, quyền truy cập hay quy trình tại ngân hàng Việt Nam; đó là những phần phải kiểm chứng tại chỗ.
- Không khẳng định đã tìm hết các use case hoặc rằng ngân hàng luôn ưu tiên con người hơn máy trong mọi tác vụ. Đơn vị phân tích ở đây là từng bước công việc và quyền hành động.
- Không dùng các số tăng năng suất trong công bố thị trường làm dự báo ROI, không dùng tên “agent” trong marketing để suy ra kiến trúc, không dùng câu chuyện cá nhân làm quy định ngân hàng.
