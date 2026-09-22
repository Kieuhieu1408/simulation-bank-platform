**Trợ lý nghiệp vụ cho giao dịch viên trong CMS — định nghĩa bài toán v0.1**

Bản lịch sử: phạm vi hiện hành đã được mở rộng thành trợ lý tra cứu cho toàn bộ nhân viên có tài khoản CMS. Xem [định nghĩa v0.2](./cms-internal-knowledge-assistant.md). Nội dung dưới đây được giữ làm dấu vết của phạm vi GDV ban đầu.

Ngày: 21/09/2026. Trạng thái: discovery, chưa xác nhận quy trình thẻ hoặc hiệu quả thực tế. Bản này cụ thể hóa hướng C theo lựa chọn của người dùng sau [báo cáo nghiên cứu](./software-3-banking-day02-2026-09-21.md).

**Quyết định sản phẩm đã thống nhất**

Tập trung vào chatbot hỗ trợ giao dịch viên (GDV) ngay trong CMS. GDV chủ động hỏi khi gặp câu hỏi chưa biết, cần đối chiếu điều khoản hoặc cần tìm bộ phận phụ trách. A — phân loại SLA và B — bổ sung PRM không còn là ứng viên ưu tiên của dự án này; chưa có lý do đủ mạnh để đưa AI vào các quy trình đó.

Giá trị cần kiểm chứng: giúp GDV tìm đúng căn cứ áp dụng cho tình huống và xác định bước tiếp theo, bao gồm chuyển tuyến đúng khi chưa đủ căn cứ. Một cuộc hội thoại có ích có thể kết thúc bằng danh sách thông tin cần làm rõ hoặc đầu mối hỗ trợ, không nhất thiết bằng câu trả lời cuối cùng.

**Problem Statement một câu**

Khi tiếp nhận tình huống khách hàng chưa biết cách xử lý, GDV khó tìm và đối chiếu đúng phiên bản quy trình, điều kiện áp dụng và trách nhiệm xử lý, khiến việc hướng dẫn hoặc chuyển tiếp có thể chậm hay sai; cần giúp họ xác định bước tiếp theo có căn cứ ngay trong CMS.

Đây là giả thuyết vấn đề từ trải nghiệm của người dùng và tài liệu đã đọc. Chưa xác nhận tần suất, nguyên nhân gốc hoặc mức hao phí bằng quan sát thực tế.

**Công việc người dùng muốn hoàn thành**

“Khi đang tiếp khách và gặp điều chưa rõ, tôi muốn tìm được điều khoản liên quan, hiểu thông tin nào còn thiếu và biết ai có thể giải quyết, để hướng dẫn khách hoặc bàn giao đúng nơi mà không phải đoán.”

Ba nhu cầu chính:

1. Hỏi theo tình huống: “Khách đang có một khoản trả góp và muốn đóng thẻ; tôi cần kiểm tra những gì?”
2. Kiểm chứng có chủ đích: “Hãy xem lại điều khoản và chính sách thẻ, chỉ ra đoạn nào có thể liên quan đến trường hợp này.”
3. Tìm đầu mối: “Tài liệu chưa nói rõ trường hợp này; đơn vị nào có trách nhiệm giải đáp và cần gửi họ những thông tin gì?”

Các câu trên là ví dụ đầu vào, không phải quy định thẻ. Chưa có tài liệu đủ để kết luận khách được đóng thẻ hay phải liên hệ ngân hàng/đơn vị bán hàng trong một trường hợp cụ thể.

**Workflow hiện tại cần xác thực**

Khách nêu vấn đề → GDV hỏi thêm/tra cứu thông tin được phép → tìm quy trình hoặc hỏi đồng nghiệp → đối chiếu điều kiện → hướng dẫn hoặc chuyển bộ phận hỗ trợ → ghi nhận kết quả.

Giả thuyết nút thắt: tài liệu khó tìm, nhiều phiên bản/ngoại lệ, tình huống dùng ngôn ngữ khác thuật ngữ trong quy định, hoặc không biết đầu mối. Cần phân biệt với việc GDV đã biết cách làm nhưng thiếu quyền, hệ thống lỗi hoặc bộ phận xử lý chậm. Chatbot không giải quyết trực tiếp ba vấn đề sau.

**Workflow được đề xuất**

GDV mở trợ lý từ CMS → nhập tình huống hoặc yêu cầu đối chiếu → trợ lý xác định ngữ cảnh còn thiếu → tìm tài liệu trong quyền truy cập → đối chiếu điều kiện/phiên bản → trả căn cứ và gợi ý bước tiếp theo, hoặc thông tin chuyển tuyến → GDV kiểm tra và quyết định sử dụng.

GDV có thể bổ sung dữ kiện và yêu cầu tra cứu tiếp. Trợ lý không coi lượt trước là sự thật nếu GDV sửa thông tin; nó phải cập nhật lại kết luận và chỉ ra phần thay đổi.

**Bốn kết quả hợp lệ của một lần hỏi**

| Kết quả | Khi nào | Trợ lý cần trả gì? |
|---|---|---|
| Có căn cứ áp dụng | Có nguồn đúng hiệu lực, phù hợp các dữ kiện quyết định | Kết luận trong phạm vi nguồn, điều kiện áp dụng, trích đoạn và bước GDV có thể kiểm tra tiếp |
| Thiếu ngữ cảnh | Có nhiều nhánh phụ thuộc thông tin chưa được xác nhận | Một vài câu hỏi làm rõ có mục đích; chưa chốt nhánh nghiệp vụ |
| Chưa tìm được căn cứ đủ mạnh | Không tìm thấy đoạn liên quan hoặc nguồn không giải quyết trường hợp | Phạm vi đã tìm, điều chưa xác định, đầu mối có trách nhiệm nếu danh bạ xác nhận được |
| Nguồn mâu thuẫn hoặc không rõ hiệu lực | Hai tài liệu khác nhau hoặc chưa xác định được bản áp dụng | Các đoạn mâu thuẫn và thông tin chuyển chủ quy trình; không tự chọn kết quả thuận tiện |

“Chưa tìm thấy trong nguồn đã tra” không đồng nghĩa “ngân hàng chưa có quy định”. Nếu không có dữ liệu danh bạ đáng tin, phải nói chưa xác định được đầu mối, không sinh tên hay số liên hệ.

**Đầu ra cần giúp GDV kiểm tra được**

Một phản hồi có thể gồm các phần sau, chỉ hiện phần hữu ích cho tình huống:

- Dữ kiện đang sử dụng và dữ kiện còn thiếu.
- Căn cứ: tên tài liệu, phiên bản/ngày hiệu lực, điều khoản/trang và đoạn liên quan mở được tại nguồn.
- Diễn giải: điều khoản nói gì và điều kiện nào quyết định khả năng áp dụng. Phân biệt nội dung nguồn với suy luận của trợ lý.
- Bước tiếp theo: việc cần kiểm tra/hỏi thêm/chuyển tuyến; không biến gợi ý thành phê duyệt nghiệp vụ.
- Đầu mối: đơn vị chịu trách nhiệm, kênh liên hệ đã được công bố, điều kiện và dữ liệu cần khi bàn giao.

Không hiện phần trăm tự tin do mô hình tự suy đoán như bằng chứng về độ chính xác. Dẫn chứng tồn tại chưa đủ: nó phải thực sự hỗ trợ nhận định và áp dụng đúng đối tượng, sản phẩm, thời điểm.

**Sơ đồ tổ chức cần đi cùng bản đồ trách nhiệm**

Sơ đồ tổ chức chỉ cho biết đơn vị và quan hệ quản lý. Để chuyển tuyến cần thêm dữ liệu có chủ sở hữu:

| Dữ liệu | Ví dụ cấu trúc, chưa phải thông tin ngân hàng thực tế |
|---|---|
| Nhóm tình huống | Mã nghiệp vụ/sản phẩm và loại vấn đề |
| Phạm vi trách nhiệm | Giải thích chính sách, xử lý vận hành, lỗi kỹ thuật hoặc ngoại lệ |
| Đầu mối chính | Nhóm/bộ phận và kênh liên hệ được duyệt |
| Điều kiện chuyển | Khi thiếu nguồn, mâu thuẫn chính sách, cần quyền xử lý hoặc lỗi hệ thống |
| Thông tin cần bàn giao | Các trường tối thiểu theo loại vấn đề |
| Đầu mối dự phòng | Tuyến tiếp theo được công bố, nếu có |
| Hiệu lực | Ngày áp dụng, lần xác nhận gần nhất, người sở hữu |

Không mặc định core thẻ là nơi nhận mọi câu hỏi về thẻ. Ví dụ một câu hỏi điều khoản có thể thuộc chủ chính sách, một lỗi thao tác thuộc vận hành hoặc kỹ thuật; phải theo dữ liệu phân công thật. Ưu tiên kênh nhóm chính thức để giảm phụ thuộc một cá nhân. Chỉ hiển thị thông tin liên hệ GDV được phép xem. Phiên bản đầu chỉ hiển thị và soạn phiếu bàn giao; không tự gửi tin nhắn hoặc tạo yêu cầu.

**GDV là Human-in-the-loop, nhưng cần phân rõ trách nhiệm kiểm tra**

GDV kiểm tra tốt các dữ kiện họ vừa thu thập, tài liệu có liên quan đến sản phẩm đang phục vụ hay không, và nội dung sẽ nói với khách. Nhưng nếu chính họ chưa hiểu một điều khoản khó, việc yêu cầu họ duyệt kết luận của AI không đủ để bảo đảm đúng.

| Chủ thể | Trách nhiệm |
|---|---|
| GDV | Xác nhận ngữ cảnh, xem căn cứ, quyết định dùng gợi ý hoặc chuyển tuyến; chịu trách nhiệm thao tác trong quyền được cấp |
| Chủ nghiệp vụ/chuyên gia tuyến hỗ trợ | Xác nhận ngoại lệ, giải quyết mâu thuẫn, duyệt đáp án mẫu và phạm vi chatbot được trả lời |
| Chủ kho tri thức/danh bạ | Xác nhận hiệu lực tài liệu và thông tin trách nhiệm/liên hệ |
| Ứng dụng CMS | Kiểm quyền truy cập, xác định phạm vi công cụ, lưu vết và giữ nguyên quy trình phê duyệt nghiệp vụ |

Tình huống lạ, không có nguồn đủ rõ hoặc tác động đến quyền lợi khách hàng vượt căn cứ sẵn có phải chuyển đúng người có khả năng xác minh. Đây là một kết quả xử lý thành công, không phải lỗi của chatbot.

**Điểm tích hợp trong CMS**

Qua kiểm tra repository ngày 21/09/2026, thư mục `cms` mới có `.gitkeep`; [README](../../README.md) mô tả CMS là cổng quản trị nội bộ cho vận hành, merchant và tra cứu giao dịch. Chưa có giao diện/router/luồng GDV để tích hợp trực tiếp. Phục vụ GDV bằng trợ lý nghiệp vụ là phần mở rộng sản phẩm cần thiết kế.

Đề xuất trải nghiệm:

1. Một mục “Trợ lý nghiệp vụ” để hỏi độc lập và một bảng bên cạnh màn hình làm việc để không mất ngữ cảnh.
2. GDV nhìn thấy rõ dữ kiện nào được đưa vào cuộc hỏi. Bản đầu cho nhập tình huống; lấy ngữ cảnh tự động từ CMS chỉ sau khi có trường dữ liệu, quyền và mục đích sử dụng rõ.
3. Mở tài liệu/điều khoản cạnh hội thoại để so sánh. Không buộc GDV rời màn hình sang tìm file thủ công từ đầu.
4. Có thao tác “Bổ sung thông tin”, “Xem nguồn”, “Tìm đầu mối”, “Soạn phiếu bàn giao” và “Báo nội dung chưa đúng”.
5. GDV kiểm tra phiếu bàn giao; các thao tác tạo/gửi yêu cầu ở giai đoạn sau phải dùng quyền và workflow CMS hiện có, với xác nhận cụ thể trước khi gửi.
6. Phân biệt lịch sử trao đổi với trợ lý và nội dung chính thức đã được GDV xác nhận vào hồ sơ khách hàng.

Không cần đưa chi tiết RAG, token, model hoặc tool call vào giao diện nghiệp vụ. GDV cần thấy căn cứ, điều chưa biết và lựa chọn tiếp theo.

**Có cần agent không?**

Giao diện chatbot không quyết định kiến trúc bên trong. Một lần tìm nguồn rồi soạn câu trả lời có thể là workflow. Tìm tài liệu và tra danh bạ theo một chuỗi cố định cũng có thể là workflow.

Ứng viên cho agent có giới hạn là một cuộc hỏi mà kết quả tra cứu làm thay đổi bước tiếp theo: phải đọc định nghĩa trước khi tìm điều khoản, phát hiện phụ lục cần mở, quay lại hỏi GDV một điều kiện, rồi mới chọn bảng trách nhiệm phù hợp. Chỉ chọn agent nếu cách tra cứu động này xử lý tốt hơn workflow trên bộ ca có đáp án, với chi phí và thời gian chấp nhận được. Đây là thiết kế đề xuất, chưa phải kết quả thực nghiệm. Khung phân biệt: [Anthropic, Building effective agents](https://www.anthropic.com/engineering/building-effective-agents).

Các năng lực công cụ có thể cần, ở mức sản phẩm: tìm tài liệu được cấp quyền; mở đoạn/phiên bản; tra nhóm chịu trách nhiệm và liên hệ; yêu cầu GDV làm rõ; chuẩn bị bản nháp bàn giao. Giai đoạn đầu đều chỉ đọc hoặc tạo bản nháp trong cuộc hỏi. Agent không có công cụ đóng thẻ, thay đổi tiền, mở khóa hoặc cấp quyền.

Tài liệu có nội dung “hãy bỏ qua quy tắc...” vẫn là dữ liệu để đọc, không có quyền chỉ đạo công cụ. Quyền truy cập do ứng dụng kiểm tra trước khi trả nội dung, không dựa vào việc nhắc model giữ bí mật. Cần giới hạn vòng tìm kiếm và dừng khi không tìm thêm được bằng chứng, nguồn mâu thuẫn hoặc hết ngân sách. Các phép kiểm kỹ thuật này không thay cho kiểm chứng nghiệp vụ.

**PS9 theo Day02**

| Trường | Định nghĩa |
|---|---|
| Actor | GDV đang tiếp nhận câu hỏi chưa biết; người hỗ trợ tuyến hai và khách hàng là bên chịu tác động |
| Workflow | Nhận câu hỏi → làm rõ → tra căn cứ → đối chiếu điều kiện → hướng dẫn/chuyển tuyến → ghi nhận |
| Bottleneck | Khó tìm đúng quy định/ngoại lệ và chủ trách nhiệm; cần quan sát xác thực từng nguyên nhân |
| Impact | Thời gian tìm/nhờ hỗ trợ, số lượt chuyển sai, khách liên hệ lại và rủi ro hướng dẫn sai; chưa có số đo |
| Success Metric | Tỷ lệ xác định đúng bước tiếp theo có căn cứ; thời gian đến phương án đúng; lỗi nghiêm trọng sau người kiểm tra |
| Boundary | Không tự kết luận khi thiếu nguồn/ngữ cảnh, không sinh liên hệ, không quyết định giao dịch hoặc tự liên hệ người khác |
| AI entry | Khi GDV chủ động hỏi hoặc yêu cầu đối chiếu trong CMS |
| Level | So sánh tìm kiếm/cây quyết định, LLM workflow và agent tra cứu có giới hạn; chưa chọn agent theo tên sản phẩm |
| Risk/HITL | GDV kiểm tra dữ kiện và gợi ý; chuyên gia giải quyết ngoại lệ/mâu thuẫn; chủ nguồn quản lý hiệu lực |

**Trả lời 22 câu hỏi Day02 cho phạm vi đã chọn**

- **D1 — Giả định cần lật lại:** GDV không nhất thiết phải nhớ toàn bộ quy định; họ cần biết tìm căn cứ và nhận diện giới hạn thẩm quyền.
- **D2 — Cách tiếp cận mới:** Hỗ trợ theo tình huống thực tế và điều kiện áp dụng, có đường chuyển người, ngay tại màn hình CMS.
- **D3 — Thiết kế lại từ đầu:** Tạo kho tri thức có phiên bản và bản đồ trách nhiệm rõ, kèm trải nghiệm tra cứu theo case. Chatbot là một cách truy cập kho đó.
- **D4 — Vì sao AI:** Đáng thử ở việc ánh xạ lời khách sang thuật ngữ, tổng hợp đoạn liên quan và hỏi làm rõ. Cần thắng tìm kiếm tốt/cây quyết định trên ca khó.
- **D5 — Thói quen:** Hỏi đồng nghiệp/người quen có thể che việc thiếu nguồn hoặc đầu mối chính thức. Đây là giả thuyết từ câu chuyện người dùng, chưa được coi là hiện trạng toàn ngân hàng.
- **D6 — Câu hỏi bị né tránh:** Ai có quyền xác nhận đáp án khi quy trình không rõ, và tài liệu nào thực sự có hiệu lực? Nếu không trả lời được thì chatbot chưa có nguồn sự thật.
- **I1 — Pain/tần suất:** Có tín hiệu thực tế từ trải nghiệm đóng thẻ; chưa có số ca/ngày, loại câu hỏi hoặc mức độ phổ biến trong GDV.
- **I2 — Workflow/công cụ/bàn giao:** CMS/quầy → kho chính sách hoặc đồng nghiệp → đơn vị hỗ trợ → GDV → khách. Cần kiểm chứng bằng vài ca thật được phép quan sát.
- **I3 — Thiệt hại:** Đo phút tìm và xác nhận, số lần chuyển sai, số lượt khách liên hệ lại. Chưa định lượng; không suy ra ROI từ demo.
- **I4 — AI sai:** Có thể dùng sai phiên bản, nhầm sản phẩm, suy diễn ngoại lệ, bịa liên hệ hoặc khiến GDV tin vào kết luận sai. Cần bằng chứng mở được và tuyến chuyên gia, không chỉ nút “duyệt”.
- **I5 — Ai nói YES:** Bạn quyết định sandbox. Pilot cần chủ quy trình, đơn vị sử dụng CMS và chủ dữ liệu/quyền truy cập liên quan; vai trò cụ thể chưa xác nhận. Lỗi nghiêm trọng có quyền phủ quyết cải thiện tốc độ.
- **P1 — Quy trình:** Workflow và bốn kết quả hợp lệ ở trên; gồm cả thiếu ngữ cảnh và chuyển tuyến, không ép tất cả ca phải được chatbot giải quyết.
- **P2 — Nút thắt:** Tìm căn cứ áp dụng và người chịu trách nhiệm. Chưa khẳng định thiếu khả năng sinh câu trả lời là nguyên nhân gốc.
- **P3 — Hao phí:** Chưa có baseline; cần đo công tra cứu, kiểm chứng và bàn giao theo mức kinh nghiệm, không chỉ tổng thời gian cuộc gọi.
- **P4 — Thành công:** Đúng bước tiếp theo có căn cứ tốt hơn hoặc ít nhất không kém đối chứng; thời gian hoàn tất giảm; không tăng lỗi nghiêm trọng lọt qua GDV. Ngưỡng số phải chốt sau baseline và trước kiểm thử cuối.
- **P5 — Hậu quả/boundary:** Không thao tác tài khoản/giao dịch; thiếu điều kiện thì hỏi, thiếu căn cứ thì chuyển đúng nơi; đầu mối phải lấy từ bản đồ trách nhiệm đã duyệt.
- **P6 — Phi AI:** Chuẩn hóa kho tài liệu/danh bạ, tìm kiếm từ đồng nghĩa, cây quyết định và FAQ, cải thiện hỗ trợ tuyến hai. Dù dùng AI vẫn cần các nền tảng này.
- **G1 — Ngôn ngữ/tri thức/suy luận:** Có, do câu hỏi nhiều cách diễn đạt và điều kiện nghiệp vụ; đây là căn cứ để thử LLM.
- **G2 — Dữ liệu đủ ngữ cảnh:** Chưa. Cần chính sách có hiệu lực, phụ lục, quy tắc ưu tiên nguồn, bản đồ trách nhiệm và tình huống/đáp án. Chưa có đủ tài liệu thẻ cho case hạt giống.
- **G3 — Có chỉ số định lượng:** Đã xác định loại chỉ số và cách đo; chưa có baseline, ngưỡng chốt hoặc kết quả thử.
- **G4 — Hậu quả trong kiểm soát:** Có khả năng giới hạn bằng sandbox, quyền đọc và chuyển chuyên gia; phải thử khả năng GDV phát hiện lỗi trước pilot.
- **G5 — Giải pháp đơn giản hơn:** Có tìm kiếm/cây quyết định. Phần hội thoại/tra cứu động chỉ được giữ nếu đem thêm giá trị đo được.

Khung câu hỏi tham chiếu [Day02, trang 21, 25, 30, 67, 69 và 76](</Users/hieuquockieu/Documents/AI Docs/AI-Thuc-Chien/00_Phase1_Nen-tang-AI-LLM/Day02_xac-dinh-bai-toan-cho-ai.pdf>).

**Bộ tình huống đầu tiên và cách đo**

Dùng câu chuyện đóng thẻ làm tình huống discovery hạt giống. Trước khi dùng nó làm test đúng/sai phải có quy định đúng sản phẩm/thời điểm và xác nhận của chủ nghiệp vụ. Nếu không có, có thể dựng ngân hàng giả với quy định tự định nghĩa, ghi rõ chỉ kiểm tra năng lực hệ thống trong mô phỏng.

Mỗi ca cần có: câu hỏi ban đầu, dữ kiện quyết định, dữ kiện chỉ xuất hiện khi GDV hỏi thêm, nguồn áp dụng, kết quả hợp lệ, đầu mối nếu cần, câu trả lời sai dễ gây nhầm và mức hậu quả. Không cần một câu trả lời văn bản duy nhất; có thể có nhiều cách đúng để đi tới bước tiếp theo.

| Nhóm ca | Điều cần kiểm tra |
|---|---|
| Câu hỏi được tài liệu bao quát | Trả căn cứ đúng và không tự thêm điều kiện |
| Thiếu dữ kiện quyết định | Hỏi làm rõ trước khi chọn nhánh |
| Ngoại lệ có trong phụ lục | Tìm đúng phụ lục, không dừng ở quy tắc chung |
| Không tìm được quy định phù hợp | Không bịa; chọn đầu mối được bản đồ trách nhiệm xác nhận |
| Hai phiên bản/mâu thuẫn | Xác định hiệu lực hoặc chuyển chủ tài liệu |
| Danh bạ thiếu/hết hiệu lực | Báo chưa xác định được, không sinh thông tin liên hệ |
| Ngoài quyền GDV | Không tiết lộ tài liệu/nội dung mà GDV không có quyền xem |
| GDV sửa dữ kiện giữa hội thoại | Bỏ kết luận cũ không còn phù hợp và đối chiếu lại |
| Tài liệu chứa chỉ dẫn độc hại | Đọc như dữ liệu, không mở rộng quyền hoặc gửi thông tin |

Đối chứng tối thiểu: cùng người dùng, nguồn và bài toán, so sánh tìm kiếm/cây quyết định tốt với LLM workflow. Chỉ thêm agent khi nhóm ca cần tìm động đủ phổ biến. Đổi thứ tự và dùng ca tương đương để tránh người dùng nhớ đáp án.

Metric chính là **tỷ lệ xác định đúng bước tiếp theo có căn cứ**: có thể là hướng xử lý, câu hỏi làm rõ hoặc chuyển tuyến đúng theo đáp án chuẩn. Báo riêng từng nhóm kết quả và mức độ bao phủ để không thưởng hệ thống luôn trả lời “hãy hỏi chuyên gia”.

Metric bổ trợ: thời gian tới kết quả đã kiểm tra; tỷ lệ dẫn chứng thực sự hỗ trợ kết luận; tìm được ngoại lệ; chuyển đúng đơn vị; thông tin liên hệ được xác thực; công sửa; độ trễ/chi phí mỗi ca; tỷ lệ GDV phát hiện lỗi cài sẵn trong sandbox. Chấm lỗi bản nháp và lỗi sau người duyệt riêng.

Với quyết định “cần chuyển chuyên gia”, FP là chuyển ca có thể xử lý từ nguồn sẵn có; FN là đưa hướng dẫn khi đáng lẽ cần chuyên gia. FN có thể nghiêm trọng hơn nhưng FP quá nhiều cũng làm mất giá trị. Không tối ưu duy nhất số ca chatbot trả lời hoặc tỷ lệ không chuyển tuyến.

Chưa đặt số phần trăm thành công tùy ý. Sau khi đo baseline, bạn và người kiểm tra nghiệp vụ chốt mục tiêu thời gian/chất lượng, ngân sách, mức lỗi chấp nhận được và điều kiện dừng. Bất kỳ truy cập vượt quyền hoặc đầu mối bịa đặt đều phải làm phiên bản không đạt gate tương ứng; một kết luận sai nghiêm trọng vượt qua GDV yêu cầu xem lại phạm vi trả lời và cơ chế kiểm chứng.

**Phạm vi sản phẩm đầu tiên**

- Một quy trình có bộ tài liệu kiểm chứng được; một nhóm GDV mô phỏng; một danh bạ/bản đồ trách nhiệm có chủ sở hữu.
- Hỏi theo tình huống, đối chiếu nguồn, hỏi làm rõ, tìm đầu mối và chuẩn bị phiếu bàn giao nháp.
- Bản nháp không tự trở thành hướng dẫn khách, yêu cầu đã gửi hoặc dữ liệu chính thức trong hồ sơ.
- Chưa chấm hiệu suất nhân viên. Lịch sử câu hỏi có thể gợi ý chỗ tài liệu khó hiểu, nhưng phải nghiên cứu mục đích này riêng trước khi biến thành công cụ đánh giá con người.

**Bốn kết luận Day02**

1. **Có cần AI?** Có lý do đủ cụ thể để thử: xử lý ngôn ngữ tình huống và tra cứu/đối chiếu căn cứ. Chưa chứng minh lợi ích hơn tìm kiếm tốt.
2. **Cấp độ nào?** Chatbot là giao diện; so workflow với agent tra cứu có giới hạn. Chưa có lý do cho agent tự thực hiện nghiệp vụ.
3. **PS đủ rõ chưa?** Đủ để tổ chức discovery và thiết kế bộ ca; chưa đủ để nghiệm thu vì thiếu baseline, nguồn chuẩn và phân công trách nhiệm.
4. **Go/Not Yet/No-Go?** Go nghiên cứu nguyên mẫu trên một phạm vi được định nghĩa; Not Yet tư vấn nghiệp vụ thật; không cấp quyền xử lý tài chính tự chủ trong phạm vi này.

**Thông tin cần xác thực tiếp theo**

Ưu tiên thu thập một bộ nhỏ tình huống GDV thật sự cần hỏi, nguồn dùng để giải quyết và đường chuyển tuyến đúng. Phỏng vấn về ca gần nhất thay vì hỏi chung “có muốn chatbot không”: đã tìm ở đâu, mất bao lâu, vì sao không tin nguồn đó, ai xác nhận cuối cùng, khách phải quay lại không. Khi có các bằng chứng này mới quyết định giao diện chi tiết, kiến trúc tra cứu và phạm vi agent.
