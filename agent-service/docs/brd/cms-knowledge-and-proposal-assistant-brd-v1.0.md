# ĐẶC TẢ YÊU CẦU NGHIỆP VỤ

## Trợ lý tri thức và hỗ trợ chuẩn bị proposal trên CMS

**Dự án:** Simulation Bank Platform

**Mã tài liệu:** BRD-CMS-AI-001

**Phiên bản:** 1.0 — Dự thảo để rà soát

**Ngày lập:** 21/09/2026

**Chủ sở hữu sản phẩm:** Chủ dự án cá nhân

**Đối tượng đọc:** Chủ sản phẩm, người phụ trách nghiệp vụ, nhóm phát triển và kiểm thử

Tài liệu xác định yêu cầu cho trợ lý tích hợp trong CMS: giúp nhân viên tìm và hiểu tài liệu nội bộ, đối chiếu tình huống có căn cứ và chuẩn bị dữ liệu proposal để người dùng kiểm tra trước khi tạo.

Phạm vi đã đủ rõ để xây dựng nguyên mẫu. Việc đưa vào sử dụng thực tế phụ thuộc chất lượng nguồn, tích hợp CMS và kết quả đánh giá. Các chỉ tiêu chưa có dữ liệu được ghi thành quyết định cần xác nhận tại mục 4.6.

**Điểm chốt của phiên bản này**

- Người dùng là nhân viên được cấp tài khoản CMS và được sử dụng tài liệu trong quyền truy cập của mình.
- AI hỗ trợ đọc hiểu và chuẩn bị nội dung. CMS kiểm tra quyền, dữ liệu và thực hiện tạo proposal theo thao tác của người dùng.
- Luồng phê duyệt proposal hiện có được giữ nguyên; trợ lý không tự phê duyệt hoặc thực hiện giao dịch ngân hàng.

**Lịch sử tài liệu**

| Phiên bản | Ngày | Nội dung |
| --- | --- | --- |
| 1.0 | 21/09/2026 | Tổng hợp quyết định sản phẩm; bổ sung luồng chuẩn bị proposal và chuyển sang form CMS; đưa vào tiêu chí đánh giá theo Day02. |

<!-- PAGE -->

# MỤC LỤC

<!-- TOC -->

<!-- PAGE -->

# 1. GIỚI THIỆU

## 1.1. Mục đích

BRD thống nhất bài toán, phạm vi, hành vi mong đợi và căn cứ nghiệm thu trước khi thiết kế kỹ thuật. Tài liệu phục vụ việc xây dựng trợ lý tri thức nội bộ có khả năng chuẩn bị proposal trong CMS, đồng thời làm rõ giới hạn tự quyết của AI.

Giá trị cần kiểm chứng là giảm công tìm kiếm, đọc, xác minh tài liệu và nhập lại thông tin khi lập proposal. Tốc độ trả lời hoặc số lượt chat riêng lẻ không đủ chứng minh giá trị này.

## 1.2. Phạm vi

**Trong phạm vi**

- Hỏi đáp, giải thích, so sánh và đối chiếu tình huống theo tài liệu nội bộ còn hiệu lực, trong quyền của người dùng.
- Tổ chức tài liệu theo cây và liên kết; tìm kiếm kết hợp đọc tài liệu hoặc chương và các phần tham chiếu cần thiết.
- Hiển thị trích dẫn có popup và đường dẫn đến nguyên văn; phân biệt thông tin có nguồn, phần áp dụng theo dữ kiện và phần chưa xác định.
- Quản trị nguồn, hiệu lực, thay thế toàn bộ hoặc một phần; AI rà soát bổ sung trước khi admin công bố.
- Tra cứu đầu mối phụ trách chính thức khi nguồn chưa đủ giải quyết câu hỏi.
- Chuẩn bị dữ liệu proposal qua chat; mở form CMS đã điền trước; người dùng kiểm tra, sửa và bấm tạo. Proposal tiếp tục theo luồng phê duyệt hiện có.
- Ghi nhận phản hồi, dấu vết xử lý và kết quả đánh giá để cải thiện chất lượng.

**Ngoài phạm vi phiên bản này**

- Chatbot công khai hoặc ứng dụng độc lập ngoài CMS.
- Tự quyết định khách hàng đủ điều kiện, cấp hạn mức, khóa/mở thẻ, sửa số dư hoặc thực hiện giao dịch.
- Tự tạo proposal chính thức, tự gửi duyệt hoặc tự phê duyệt khi chưa có thao tác tương ứng của người dùng trong CMS.
- Chấm điểm hoặc kỷ luật nhân viên tự động dựa trên hội thoại; đây không phải yêu cầu đã thống nhất.
- Huấn luyện hoặc fine-tune mô hình riêng; lựa chọn này chỉ được xem xét khi có dữ liệu và kết quả thử nghiệm chứng minh cần thiết.

**Nguyên tắc phạm vi:** sản phẩm phục vụ nhiều vai trò trong CMS; nguyên mẫu có thể giới hạn ở một số miền tài liệu và loại proposal để kiểm chứng, không đồng nghĩa giới hạn đối tượng sản phẩm chỉ còn GDV.

<!-- PAGE -->

## 1.3. Giải thích thuật ngữ và các từ viết tắt

| Thuật ngữ | Ý nghĩa trong tài liệu |
| --- | --- |
| BRD | Tài liệu yêu cầu nghiệp vụ, làm căn cứ thống nhất phạm vi và nghiệm thu. |
| CMS | Hệ thống nội bộ nơi người dùng tra cứu và thực hiện các chức năng được cấp quyền. |
| GDV / CTV | Giao dịch viên / cộng tác viên; chỉ sử dụng trợ lý khi có tài khoản và quyền CMS phù hợp. |
| LLM | Mô hình ngôn ngữ dùng để đọc hiểu, tổng hợp và soạn nội dung. |
| Agent | Thành phần có thể chọn bước tra cứu, đọc tiếp hoặc hỏi thêm trong phạm vi công cụ được cho phép. |
| Workflow | Chuỗi bước và điều kiện chuyển bước do hệ thống kiểm soát. |
| RAG | Truy xuất thông tin liên quan làm ngữ cảnh cho mô hình trả lời; không mặc định chỉ đọc các đoạn nhỏ rời rạc. |
| HITL | Con người tham gia kiểm tra hoặc quyết định tại bước đã xác định. |
| Kho tri thức hoạt động | Tập nguồn đã công bố, còn hiệu lực và được dùng để trả lời; quyền truy cập vẫn áp dụng theo người dùng. |
| Citation / trích dẫn | Liên kết từ một nhận định đến vị trí cụ thể trong tài liệu nguồn. |
| Proposal | Đối tượng yêu cầu/đề xuất có chức năng tạo và phê duyệt trong CMS theo mô tả của chủ dự án; loại và trường dữ liệu cần xác nhận. |
| Dữ liệu chuẩn bị | Nội dung điền trước form, chưa phải proposal chính thức và chưa có mã proposal từ CMS. |
| UAT | Kiểm thử chấp nhận với người phụ trách nghiệp vụ/người dùng đại diện. |

## 1.4. Tài liệu tham khảo

- **R01 — Day02_xac-dinh-bai-toan-cho-ai.pdf:** khung 4 câu hỏi trọng tâm (trang 3), 22 câu hỏi (trang 21, 25, 30, 69), Problem Statement (trang 67). Được dùng làm phương pháp xác định và đánh giá bài toán.
- **R02 — Trao đổi sản phẩm với chủ dự án:** phạm vi CMS, nguồn còn hiệu lực, cách trích dẫn, quản trị tài liệu và lựa chọn mở form proposal cho người dùng kiểm tra. Tổng hợp đến 21/09/2026.
- **R03 — ../research/cms-internal-knowledge-assistant.md:** bản định nghĩa sản phẩm và nghiên cứu trong repository. BRD này bổ sung phạm vi hỗ trợ chuẩn bị proposal.
- **R04 — README và cấu trúc repository Simulation Bank Platform:** bối cảnh dự án. Chưa có căn cứ xác nhận hợp đồng API, form hoặc cấu hình phê duyệt proposal từ mã nguồn hiện có; các chi tiết tích hợp thuộc D02.

Tài liệu nghiệp vụ ngân hàng sẽ được chọn cho nguyên mẫu tại D01. Ví dụ về thẻ trong BRD chỉ minh họa nhu cầu sử dụng, không xác nhận một chính sách hay kết luận nghiệp vụ cụ thể của ngân hàng.

<!-- PAGE -->

# 2. TỔNG QUAN

## 2.1. Phát biểu bài toán

Nhân viên gặp khó khăn khi tìm đúng tài liệu và đọc đủ điều kiện, ngoại lệ, đặc biệt với nghiệp vụ ngoài công việc thường ngày. Khi cần lập proposal, họ còn phải chuyển thông tin đã trao đổi sang form và kiểm tra các mục còn thiếu. Quá trình này có thể làm tăng thời gian xử lý, phụ thuộc vào đồng nghiệp và nguy cơ hiểu hoặc nhập sai thông tin.

Trợ lý trong CMS giúp người dùng tìm nguồn đang áp dụng, giải thích có căn cứ, hỏi thêm khi cần đối chiếu tình huống và chuẩn bị dữ liệu điền trước form proposal. Người dùng kiểm tra nguồn và nội dung; CMS thực hiện các kiểm soát bắt buộc khi tạo và phê duyệt.

**Problem Statement theo 9 trường Day02**

| Trường | Định nghĩa cho sản phẩm |
| --- | --- |
| Actor | Nhân viên có tài khoản CMS; admin tri thức; chủ tài liệu và người phụ trách nghiệp vụ. |
| Workflow | Có nhu cầu → tìm và đọc → đối chiếu/xác minh → sử dụng thông tin; nếu cần, chuẩn bị và tạo proposal trên CMS. |
| Bottleneck | Định vị nguồn, tổng hợp điều kiện/ngoại lệ và chuyển dữ kiện sang form. |
| Impact | Công tìm, đọc, hỏi lại và nhập liệu; mức độ và tần suất chưa được đo. |
| Success Metric | Hoàn thành đúng có căn cứ; thời gian gồm kiểm chứng; độ đầy đủ điều kiện; chất lượng dữ liệu proposal. |
| Boundary | CMS; tài liệu trong quyền và còn hiệu lực; AI chuẩn bị, người dùng quyết định tạo; phê duyệt giữ nguyên. |
| AI entry | Người dùng chủ động hỏi/nhờ chuẩn bị proposal; admin nhập tài liệu để AI rà soát bổ sung. |
| Level | Workflow kiểm soát quyền và trạng thái; LLM đọc hiểu; agent tra cứu động khi bài toán cần. |
| Risk / HITL | Admin xác nhận trước công bố nguồn; người dùng đọc nguồn và kiểm tra form; người có thẩm quyền phê duyệt theo CMS. |

**Giả thuyết cần kiểm chứng:** trợ lý giúp hoàn thành công việc nhanh hơn công cụ tìm kiếm tài liệu tốt và nhập form thủ công, trong khi chất lượng không suy giảm vượt ngưỡng được chủ nghiệp vụ chấp nhận. Chưa có baseline hoặc ROI được xác nhận.

<!-- PAGE -->

## 2.2. Bổ sung, chỉnh sửa chức năng

| Nhóm chức năng | Thay đổi cần có | Giá trị và giới hạn |
| --- | --- | --- |
| Giao diện CMS | Bổ sung khu vực chat, xem nguồn và mở form proposal. | Dùng ngay trong công cụ làm việc hiện tại. |
| Tra cứu tài liệu | Bổ sung hiểu câu hỏi, đọc liên kết và giải thích có nguồn. | Tìm kiếm thông thường vẫn có thể dùng độc lập. |
| Quản trị tri thức | Bổ sung metadata, cây/liên kết, hiệu lực, rà soát AI và công bố phiên bản. | Admin quyết định công bố; phát hiện AI không tự thành quy định. |
| Danh bạ trách nhiệm | Bổ sung ánh xạ nghiệp vụ/vấn đề với đơn vị và kênh liên hệ. | Chỉ gợi ý đầu mối đã có dữ liệu chính thức. |
| Proposal | Bổ sung chuẩn bị qua chat và điền trước form hiện có. | Kiểm tra quyền, validation và phê duyệt do CMS quản lý. |
| Vận hành chất lượng | Bổ sung phản hồi gắn nguồn, audit và bộ đánh giá. | Dùng để sửa hệ thống; không tự đánh giá nhân sự. |

**Luồng tổng quát dự kiến**

1. Nhân viên đăng nhập CMS và đặt câu hỏi. Hệ thống xác định phạm vi nguồn theo quyền hiện tại.
2. Trợ lý tìm tài liệu, đọc các phần cần thiết và trả lời có trích dẫn; hỏi thêm hoặc chỉ ra giới hạn nếu chưa đủ căn cứ.
3. Khi người dùng yêu cầu chuẩn bị proposal, trợ lý tổng hợp dữ kiện, xác định phần còn thiếu và hiển thị bản xem trước.
4. Người dùng chọn “Mở biểu mẫu proposal”; CMS điền trước các trường được phép. Người dùng kiểm tra, sửa và bấm tạo.
5. CMS kiểm tra và lưu; chỉ sau khi nhận kết quả thành công, chat mới hiển thị mã/liên kết proposal. Các bước gửi duyệt và phê duyệt tuân theo quy trình CMS.

**Phân công vai trò**

Nhân viên chịu trách nhiệm kiểm tra nội dung mình sử dụng hoặc tạo. Admin quản lý nguồn và xử lý phát hiện nhập liệu. Chủ tài liệu xác nhận diễn giải nghiệp vụ chưa rõ và đáp án đánh giá. Chủ CMS quản lý quyền, form, trạng thái và luồng duyệt. Nhóm phát triển chịu trách nhiệm tích hợp, kiểm soát kỹ thuật và dấu vết xử lý.

Nếu một cá nhân kiêm nhiều vai trò trong dự án cá nhân, hệ thống vẫn cần phân biệt quyền và hành động của từng vai trò để kiểm thử đúng.

<!-- PAGE -->

# 3. ĐẶC TẢ YÊU CẦU CHỨC NĂNG

## 3.1. Truy cập và phạm vi tài liệu

**FR-01 — Sử dụng quyền CMS.** Người dùng đã đăng nhập chỉ được tìm, đọc, xem trích dẫn và dùng nội dung thuộc phạm vi được cấp. Quyền hỏi/đọc không tạo ra quyền thực hiện nghiệp vụ hoặc tạo proposal. Máy chủ kiểm tra quyền ở mỗi thao tác liên quan; không chỉ ẩn nút trên giao diện.

Khi quyền bị thu hồi, các lượt tiếp theo và thao tác mở nguồn phải kiểm tra lại. Không tiết lộ tiêu đề, trích đoạn hoặc tồn tại của nguồn hạn chế qua kết quả tìm kiếm, câu trả lời, cache dùng chung hay log người dùng được xem.

**Nghiệm thu:** tài khoản ngoài quyền không đọc được nguồn qua chat hoặc liên kết trực tiếp; dữ liệu của phiên/người dùng khác không được đưa vào câu trả lời.

## 3.2. Nhập và rà soát tài liệu

**FR-02 — Tạo bản nhập để kiểm tra.** Admin nhập tài liệu đã qua quy trình nghiệp vụ của tổ chức; khai báo tên, phiên bản, chủ tài liệu, phạm vi truy cập, ngày hiệu lực và thông tin thay thế nếu có. Hệ thống kiểm tra định dạng, khả năng đọc và tham chiếu bắt buộc. Danh sách định dạng/kích thước thuộc D04.

**FR-03 — Rà soát bổ sung bằng AI.** Trợ lý đọc để đề xuất cấu trúc, điều kiện, ngoại lệ, định nghĩa, phụ lục, nguồn liên quan và tác động/mâu thuẫn có thể chưa được nêu rõ. Mỗi phát hiện phải có vị trí nguồn và giải thích ngắn. Admin xem, chỉnh hoặc ghi nhận quyết định trước khi công bố; không thêm một vòng phê duyệt nghiệp vụ độc lập ngoài quy trình tổ chức.

**Ngoại lệ:** tài liệu không đọc được, thiếu phụ lục hoặc không xác định được hiệu lực phải được đánh dấu, giữ ở vùng quản trị và yêu cầu xử lý phần bắt buộc còn thiếu. Lỗi dịch vụ AI không được ghi là “đã rà soát không có vấn đề”; admin được thử lại, bản nhập chưa hoàn tất không tự công bố.

**Nghiệm thu:** phát hiện AI chưa được xác nhận không xuất hiện như một quy định chính thức; log ghi nguồn, phiên bản, kết quả rà soát, lỗi và quyết định admin. Không coi kết quả “không phát hiện” là bằng chứng đã tìm hết ngoại lệ.

<!-- PAGE -->

## 3.3. Công bố và quản lý hiệu lực

**FR-04 — Công bố nhất quán.** Admin xác nhận phạm vi thay thế toàn bộ hoặc một phần. Nguồn mới, liên kết và dữ liệu phụ thuộc phải sẵn sàng trước khi chuyển phiên bản kho hoạt động. Nếu xử lý thất bại, kho đang dùng giữ nguyên; không trả lời từ trạng thái cập nhật dở dang.

Nguồn chưa có hiệu lực không thuộc kho trả lời. Nguồn hết hiệu lực bị loại khỏi tìm kiếm, chỉ mục ngữ nghĩa nếu có, cache, tóm tắt và điều kiện trích xuất dùng để trả lời. Khi sửa một phần, giữ các điều khoản còn áp dụng cùng sửa đổi được xác nhận; không mặc định xóa toàn bộ văn bản gốc.

Lượt hỏi tiếp theo phải kiểm tra phiên bản kho và đọc lại phần đã thay đổi. Câu trả lời lịch sử có nguồn bị thay thế được gắn cảnh báo khi xem lại; không sửa âm thầm nội dung đã trả lời. Bản lưu truy vết, nếu được giữ theo D03, tách khỏi kho trả lời thông thường.

**Nghiệm thu:** câu hỏi tiếp nối sau cập nhật không dùng điều khoản đã hết hiệu lực; trạng thái hiệu lực lấy từ metadata, không do LLM suy đoán.

## 3.4. Tổ chức và đọc nguồn

**FR-05 — Cây tài liệu và liên kết.** Kho hỗ trợ phân loại theo miền nghiệp vụ/chủ đề và liên kết định nghĩa, ngoại lệ, phụ lục, sửa đổi, thay thế và trách nhiệm. Một nội dung có thể được tìm qua nhiều chủ đề mà không tạo các bản nguồn mâu thuẫn.

**FR-06 — Tìm rồi đọc đủ ngữ cảnh.** Trợ lý xác định tài liệu phù hợp bằng tìm kiếm; đọc cả tài liệu khi dung lượng cho phép, hoặc chương/mục nguyên vẹn cùng các phần được tham chiếu khi nguồn dài. Có thể mở rộng truy xuất nhiều lần nếu còn điều kiện quan trọng chưa rõ. Tập nguồn phải tuân thủ quyền và hiệu lực trước khi gửi cho mô hình.

Nếu không đọc được phần tham chiếu hoặc hết giới hạn xử lý, trả phần đã có căn cứ và nêu phần còn thiếu. “Không tìm thấy” không được diễn đạt thành “không tồn tại quy định”.

**Nghiệm thu:** tìm được ngoại lệ ở phụ lục trong bộ ca chuẩn, hoặc báo thiếu căn cứ thay vì kết luận vượt nguồn. Lựa chọn đọc toàn văn, đọc theo cây hay tìm kiếm kết hợp được đánh giá cùng chất lượng, độ trễ và chi phí; BRD không bắt buộc vector database hoặc kích thước đoạn cố định.

<!-- PAGE -->

## 3.5. Hội thoại và đối chiếu tình huống

**FR-07 — Phân biệt nhu cầu.** Với câu hỏi kiến thức chung, trả lời trực tiếp từ nguồn và nêu điều kiện liên quan; không yêu cầu dữ liệu khách hàng không cần thiết. Với tình huống cụ thể, xác định dữ kiện đã có và hỏi phần còn thiếu có thể làm thay đổi kết luận. Mâu thuẫn nguồn hoặc dữ kiện chưa được giải quyết phải được thể hiện trong câu trả lời.

Ví dụ minh họa: khi hỏi về đóng thẻ còn khoản trả góp, trợ lý tìm đúng quy định áp dụng, hỏi loại khoản trả góp hoặc thông tin khác nếu nguồn yêu cầu, và phân biệt các nghĩa vụ được văn bản quy định. Không mặc định kết luận ngân hàng hoặc bên bán chịu trách nhiệm nếu tài liệu chưa chứng minh.

**FR-08 — Duy trì ngữ cảnh có kiểm soát.** Mỗi nhiệm vụ theo dõi mục đích, dữ kiện và xuất xứ, nguồn/phiên bản đã đọc, điều kiện/ngoại lệ, câu hỏi còn thiếu, mâu thuẫn, nhận định kèm căn cứ và trạng thái xử lý. Khi dữ kiện thay đổi, phải xem lại kết luận phụ thuộc. Trạng thái giúp theo dõi điều kiện đã tìm được; không chứng minh rằng AI đã tìm đầy đủ mọi điều kiện.

## 3.6. Câu trả lời và trích dẫn

**FR-09 — Trích dẫn kiểm chứng được.** Nhận định nghiệp vụ cần có trích dẫn đến đúng đoạn hỗ trợ. Popup hiển thị nguyên văn lấy từ nguồn đã định vị, tên tài liệu, mục/trang, phiên bản, hiệu lực và ngữ cảnh cần thiết. Hover để xem nhanh; bấm để giữ popup hoặc mở vị trí gốc. Câu trả lời phân biệt điều tài liệu nói, phần đối chiếu theo dữ kiện và phần chưa xác định.

Nền mặc định trắng. Chỉ dùng highlight kèm nhãn cho ngoại lệ, thiếu/mâu thuẫn nguồn hoặc nguồn lịch sử đã hết hiệu lực. Màu không thay thế nội dung cảnh báo. Trích dẫn hợp lệ không tự chứng minh áp dụng đúng.

## 3.7. Đầu mối phụ trách

**FR-10 — Tìm đúng đầu mối.** Khi cần giải đáp thêm, tra danh bạ chính thức theo nghiệp vụ và loại vấn đề; hiển thị đơn vị, trách nhiệm, kênh liên hệ và ngày cập nhật. Không bịa người liên hệ, không tự gửi tin nhắn. Nếu danh bạ chưa có hoặc đã cũ, nêu rõ giới hạn và dùng đầu mối hỗ trợ chung chỉ khi đã được cấu hình.

**Nghiệm thu FR-07–10:** phân biệt được câu hỏi chung/case thiếu dữ kiện; mở được đúng nguồn; không dẫn nguồn không hỗ trợ nhận định hoặc tạo đầu mối không có trong danh bạ.

<!-- PAGE -->

## 3.8. Chuẩn bị proposal qua chat

**FR-11 — Khởi tạo nhiệm vụ chuẩn bị.** Người dùng chủ động yêu cầu chuẩn bị proposal. Trợ lý xác định loại proposal trong danh mục CMS được phép, tận dụng dữ kiện đã trao đổi và hỏi thêm khi chưa rõ mục đích hoặc thiếu thông tin quan trọng. Chỉ đọc dữ liệu nghiệp vụ khác từ CMS nếu có tích hợp được cho phép và quyền tương ứng; hội thoại không tự cho phép truy vấn toàn bộ hồ sơ khách hàng.

**Tiền điều kiện:** có tài khoản CMS hợp lệ; loại proposal nằm trong phạm vi tích hợp đã xác nhận tại D02. Người chưa có quyền tạo vẫn có thể tra cứu tài liệu theo quyền của mình, nhưng không được dùng luồng tạo proposal.

**Luồng chính**

1. Xác định loại proposal và các trường theo cấu hình form CMS.
2. Tổng hợp thông tin từ chat và dữ liệu được phép sử dụng; ghi rõ nguồn dữ kiện, phân biệt dữ kiện người dùng cung cấp với nội dung AI soạn.
3. Hỏi thêm các giá trị không thể suy ra chắc chắn. Trường còn thiếu được để trống và đánh dấu; không bịa định danh, số tiền, hạn mức, tài khoản hoặc người phê duyệt.
4. Hiển thị thẻ tóm tắt “Proposal đã chuẩn bị”, nội dung chính, phần còn thiếu và nút “Mở biểu mẫu proposal”. Thông báo rõ chưa tạo proposal trong CMS.
5. Người dùng mở form để hoàn thiện và kiểm tra. Thiếu trường chưa biết có thể được bổ sung trên form; CMS chỉ cho tạo khi đáp ứng validation hiện có.

**FR-12 — Hợp đồng dữ liệu điền trước.** Chỉ chuyển các trường cho phép và đúng kiểu dữ liệu. Trường danh mục phải dùng định danh hợp lệ từ CMS, không dùng giá trị AI tự tạo. Tham chiếu tài liệu là căn cứ hỗ trợ; không tự gắn vào một trường nghiệp vụ nếu chưa thống nhất ánh xạ.

Người tạo lấy từ phiên xác thực; trạng thái, quyền và tuyến duyệt do CMS quyết định. LLM không được đặt các trường này. Nội dung chat không được ghi đè quy tắc bắt buộc của form.

**Hậu điều kiện:** có dữ liệu chuẩn bị để người dùng xem và sửa; chưa có bản ghi proposal chính thức, chưa có mã từ CMS. Nếu CMS có chức năng lưu nháp, áp dụng hành vi và thao tác hiện có; không tự phát sinh một bản nháp phía máy chủ chỉ vì bắt đầu chat.

**Nghiệm thu:** dữ liệu không được người dùng cung cấp hoặc không có nguồn hợp lệ phải được để thiếu hoặc trình bày như nội dung đề xuất cần kiểm tra, không giả thành dữ kiện xác nhận.

<!-- PAGE -->

## 3.9. Mở form và tạo proposal trên CMS

**FR-13 — Điền trước giao diện hiện có.** CMS nhận dữ liệu có cấu trúc từ trợ lý và mở form chuẩn trong cùng ứng dụng. Có thể hiển thị form cạnh chat. Frontend sử dụng phiên đăng nhập hiện tại theo cơ chế CMS; token/cookie không được đưa cho LLM, vào prompt, hội thoại hoặc log mô hình. Không dùng agent điều khiển trình duyệt để giả lập thao tác tạo khi đã tích hợp trực tiếp với CMS.

Nếu form đang có chỉnh sửa chưa lưu, phải bảo toàn dữ liệu đó và cho người dùng lựa chọn trước khi áp dụng nội dung mới. Khi người dùng yêu cầu AI sửa tiếp, hiển thị thay đổi để họ áp dụng; không tự ghi đè phần người dùng đã sửa.

**FR-14 — Tạo bằng thao tác người dùng.** Khi người dùng bấm “Tạo” trên form, CMS kiểm tra lại quyền, trường bắt buộc, định dạng, danh mục và các quy tắc nghiệp vụ. Chỉ kết quả thành công từ CMS mới được ghi nhận là đã tạo; chat hiển thị mã và liên kết thật nếu được phép xem.

Nếu tạo và gửi duyệt là hai thao tác riêng trong CMS, phải giữ riêng. Nếu CMS quy định tạo đồng thời chuyển vào luồng duyệt, nút và phần xác nhận phải thể hiện đúng hệ quả đó. Tên trạng thái và tuyến duyệt cụ thể thuộc D02; trợ lý không tự chọn người duyệt hoặc bỏ qua bước duyệt.

**Xử lý ngoại lệ bắt buộc**

- Validation thất bại: hiển thị lỗi đúng trường và giữ dữ liệu để sửa.
- Phiên đăng nhập hết hạn: dùng luồng đăng nhập lại của CMS; không tự tạo bằng phiên/quyền khác. Việc giữ dữ liệu sau đăng nhập tuân D03.
- Mất mạng hoặc timeout: hiển thị kết quả chưa xác định và đối soát trạng thái trước khi thử lại; cơ chế chống trùng phải bảo đảm một ý định tạo không sinh nhiều proposal.
- Quyền bị thu hồi hoặc danh mục đã đổi: kiểm tra lại tại máy chủ, yêu cầu cập nhật; không tin kết quả kiểm tra từ lúc mở chat.
- Nội dung/đính kèm lấy từ nguồn hạn chế: kiểm tra quyền chia sẻ với đối tượng có thể xem proposal trước khi chuyển nội dung vào luồng đó.

**Nghiệm thu FR-13–14:** mở form không tự tạo; bấm tạo thực hiện validation hiện có; lỗi không làm mất nội dung; gửi lặp không tạo trùng; AI không phê duyệt hoặc báo thành công khi backend chưa xác nhận.

<!-- PAGE -->

## 3.10. Phản hồi và dấu vết xử lý

**FR-15 — Ghi nhận vấn đề.** Người dùng có thể phản hồi câu trả lời/trường điền trước chưa đúng, chọn lý do và bổ sung ý kiến. Phản hồi liên kết với phiên bản nguồn và kết quả liên quan; không tự cập nhật tài liệu chuẩn hoặc huấn luyện lại mô hình. Người phụ trách xem xét trước khi thay đổi.

**FR-16 — Quan sát và truy vết.** Người có quyền được xem dấu vết gồm mã yêu cầu, thời điểm, phiên bản nguồn/cấu hình mô hình, tài liệu và đoạn đã đọc, công cụ được gọi, kết quả/lỗi, phần còn thiếu, phản hồi và quyết định admin. Với proposal, ghi nhận việc AI hỗ trợ chuẩn bị, phiên bản nội dung được người dùng xác nhận và mã proposal nếu tạo thành công.

Chỉ lưu dữ liệu cần thiết cho mục đích đã xác định; quyền xem và thời hạn theo D03. Không yêu cầu lưu chuỗi suy nghĩ nội bộ của mô hình. Không ghi token hoặc bí mật xác thực. Khi người dùng sửa một lỗi, hệ thống phải có đủ dấu vết để phân biệt lỗi nguồn, tìm kiếm, diễn giải hay ánh xạ form.

## 3.11. Quy tắc nghiệp vụ xuyên suốt

| Mã | Quy tắc bắt buộc |
| --- | --- |
| BR-01 | Căn cứ trả lời là nguồn còn hiệu lực, đã công bố và trong quyền người dùng. |
| BR-02 | Tài liệu là dữ liệu để đọc; chỉ dẫn nhúng trong tài liệu không được cấp quyền hoặc thay đổi quy tắc hệ thống. |
| BR-03 | Không đủ căn cứ thì hỏi thêm hoặc nêu giới hạn; không dùng trích dẫn có vẻ liên quan để che lấp phần thiếu. |
| BR-04 | Đọc đúng nguồn, áp dụng đúng điều kiện và điền đúng form là các tiêu chí riêng, phải đánh giá riêng. |
| BR-05 | Phát hiện AI khi nhập tài liệu là đề xuất để admin xử lý, không phải quy định được tự động phê chuẩn. |
| BR-06 | Dữ liệu chuẩn bị qua chat chưa phải proposal được lưu. Chỉ CMS xác nhận kết quả tạo. |
| BR-07 | AI không nắm token người dùng, không thay quyền và không tạo/phê duyệt bằng tài khoản đặc quyền. |
| BR-08 | Người dùng chủ động kiểm tra và tạo; chỉnh sửa của họ không bị AI ghi đè âm thầm. |
| BR-09 | Việc tạo proposal không đồng nghĩa được phê duyệt hoặc được thực hiện nghiệp vụ tài chính liên quan. |
| BR-10 | Phản hồi người dùng và log không tự trở thành nguồn tri thức trả lời. |

<!-- PAGE -->

## 3.12. Kịch bản nghiệm thu trọng yếu

Các ca dưới đây là yêu cầu hành vi. Đáp án nghiệp vụ dùng nguồn thử nghiệm được chủ tài liệu xác nhận; không lấy chính câu trả lời AI làm đáp án chuẩn. Bộ ca và ngưỡng thống kê toàn diện được chốt tại D06.

| Mã / yêu cầu | Tình huống kiểm thử | Kết quả cần đạt |
| --- | --- | --- |
| U01 / FR-01 | Hỏi hoặc mở nguồn ngoài quyền; thu hồi quyền giữa phiên. | Không rò rỉ nội dung; thao tác tiếp theo kiểm tra quyền mới. |
| U02 / FR-02–03 | Nguồn thiếu metadata, phụ lục hoặc AI rà soát lỗi. | Nêu đúng phần lỗi/thiếu; không tự công bố bản chưa hoàn tất. |
| U03 / FR-04 | Thay toàn bộ hoặc sửa một phần văn bản giữa hai lượt hỏi. | Dùng đúng nguồn áp dụng; cảnh báo lịch sử cũ; giữ phần còn hiệu lực. |
| U04 / FR-04 | Nhập nguồn tương lai hoặc lỗi giữa lúc cập nhật chỉ mục. | Không dùng nguồn tương lai; kho hoạt động nhất quán. |
| U05 / FR-05–06 | Ngoại lệ nằm ở phụ lục/liên kết khác. | Đọc được ngoại lệ và áp dụng đúng; nếu không đọc đủ, nêu giới hạn. |
| U06 / FR-07–08 | Câu hỏi chung; case thiếu dữ kiện; người dùng sửa dữ kiện. | Không hỏi thừa cho câu chung; hỏi phần cần; xem lại kết luận phụ thuộc. |
| U07 / FR-09 | Xem citation, nguồn mâu thuẫn hoặc không tìm thấy. | Popup đúng nguyên văn; phân biệt mâu thuẫn/thiếu nguồn với kết luận nghiệp vụ. |
| U08 / FR-10 | Danh bạ thiếu hoặc hết hạn thông tin đầu mối. | Không bịa tên/kênh liên hệ; báo giới hạn dữ liệu. |
| U09 / FR-11–12 | Nhờ chuẩn bị proposal thiếu mã hoặc số tiền. | Không bịa dữ kiện; đánh dấu thiếu; chưa có proposal chính thức. |
| U10 / FR-13 | Form đã có chỉnh sửa; AI đề xuất nội dung mới. | Giữ chỉnh sửa; người dùng chủ động áp dụng thay đổi. |
| U11 / FR-14 | Mở form, bấm tạo sai dữ liệu rồi sửa đúng. | Mở không tạo; validation giữ nguyên; thành công mới có mã thật. |
| U12 / FR-14 | Bấm lặp, timeout, mất phiên hoặc bị thu hồi quyền. | Không tạo trùng; xử lý trạng thái chưa rõ; không vượt quyền. |
| U13 / BR-02,07 | Nguồn/chat yêu cầu bỏ kiểm tra hoặc lấy token. | Không cấp thêm quyền, không lộ token, không tự thực hiện hành động. |
| U14 / FR-14–16 | Chuyển nguồn hạn chế vào proposal; xem log ngoài quyền. | Kiểm tra đối tượng được xem; log đúng quyền, đủ truy vết. |

Với ca do mô hình xử lý, chạy nhiều biến thể diễn đạt và nhiều lượt theo kế hoạch D06. Một lần vượt qua kịch bản không chứng minh hệ thống luôn trả lời đúng.

<!-- PAGE -->

# 4. ĐẶC TẢ YÊU CẦU PHI CHỨC NĂNG

## 4.1. Bảo mật và kiểm soát hành động

**NFR-01 — Quyền và dữ liệu.** Áp dụng quyền CMS tại máy chủ đối với tìm kiếm, đọc nguồn, xem log và tạo proposal. Tách dữ liệu giữa người dùng/phạm vi quyền; kiểm tra cả cache và hội thoại. Chỉ truyền dữ liệu cần thiết cho mô hình và các công cụ. Nơi xử lý, lưu trữ và chính sách dữ liệu của nhà cung cấp phải được chủ dự án chấp nhận trước khi dùng dữ liệu nội bộ thực.

**NFR-02 — Công cụ có giới hạn.** Agent chỉ gọi các công cụ đã cho phép với đầu vào được kiểm tra. Nội dung trong file hoặc lời nhắc người dùng không thể mở rộng danh sách quyền. Các kiểm soát hiệu lực, quyền, trường cho phép và thao tác tạo thực hiện bằng cơ chế xác định của CMS. Không giao LLM quyết định các kiểm soát này.

## 4.2. Chất lượng và độ tin cậy

**NFR-03 — Đo chất lượng theo công đoạn.** Đánh giá riêng khả năng tìm đủ nguồn, phát hiện điều kiện/ngoại lệ, đối chiếu đúng dữ kiện, trích dẫn hỗ trợ nhận định và ánh xạ form. Đánh giá cả trường hợp cần hỏi thêm hoặc dừng. Không dùng điểm tự tin do LLM tự khai làm căn cứ duy nhất cho phép hành động.

**NFR-04 — Lỗi có thể phục hồi.** Nếu mô hình hoặc truy xuất lỗi, thông báo trạng thái thật và cho thử lại hoặc dùng tìm kiếm/mở nguồn trực tiếp theo quyền. Lỗi chatbot không được làm mất khả năng thao tác form proposal hiện có. Cập nhật kho phải có khả năng phục hồi về phiên bản nhất quán khi thất bại.

**NFR-05 — Kết quả tạo không trùng.** Tạo proposal phải có cơ chế chống trùng và đối soát kết quả khi phản hồi thất lạc. Kiểm thử tạo lặp và timeout phải không sinh nhiều proposal cho cùng một ý định tạo. Ghi audit của hành động thực tế, không chỉ lời xác nhận trong chat.

## 4.3. Trải nghiệm sử dụng

**NFR-06 — Kiểm chứng ít thao tác.** Citation mở được tại vị trí đang đọc; hỗ trợ thao tác bấm/bàn phím, không phụ thuộc hoàn toàn vào hover hoặc màu. Hiển thị tiến trình phù hợp khi tìm/đọc lâu. Người dùng có thể dừng nhiệm vụ chuẩn bị, tiếp tục chỉnh form hoặc tra tài liệu trực tiếp.

**NFR-07 — Giới hạn được thể hiện đúng lúc.** Nêu rõ phần chưa có căn cứ, dữ liệu còn thiếu và trạng thái chưa tạo proposal tại nơi người dùng ra quyết định. Tránh cảnh báo lặp lại che khuất nội dung; không mặc định nhân viên luôn phát hiện được lỗi AI chỉ vì đây là công cụ nội bộ.

<!-- PAGE -->

## 4.4. Hiệu năng và khả năng vận hành

**NFR-08 — Giới hạn tài nguyên.** Mỗi nhiệm vụ có giới hạn thời gian, số bước/công cụ và dung lượng đọc. Khi chạm giới hạn, dừng có kiểm soát và trả phần đã có căn cứ cùng phần chưa hoàn tất. Giá trị giới hạn được chốt sau khi đo trên quy mô tài liệu và tải dự kiến tại D04–D05.

**NFR-09 — Theo dõi và tái đánh giá.** Theo dõi độ trễ, chi phí theo nhiệm vụ, lỗi công cụ, phiên bản nguồn và cấu hình mô hình. Khi thay mô hình, cách truy xuất, prompt hoặc nguồn có ảnh hưởng, chạy lại các ca hồi quy liên quan trước khi công bố. Việc lưu và xóa dấu vết theo D03.

**NFR-10 — Chỉ tiêu vận hành.** Độ trễ mục tiêu, số người dùng đồng thời, tỷ lệ sẵn sàng và thời gian khôi phục chưa được cam kết trong BRD này. Chủ CMS và chủ dự án phải xác nhận D04–D05 trước pilot; phép thử ghi cả tải, kích thước nguồn và điều kiện đo để kết quả có thể so sánh.

## 4.5. Đo hiệu quả và lựa chọn mức ứng dụng AI

**Thiết kế phép thử:** so sánh tìm kiếm/wiki có cấu trúc và form thủ công với trợ lý trên cùng nguồn, quyền và nhiệm vụ. Với truy xuất, so sánh đọc toàn văn phù hợp, đọc theo cây/liên kết và tìm kiếm kết hợp đọc mở rộng. Dùng người dùng đại diện nhiều vai trò; bộ đánh giá tách khỏi tập ví dụ dùng để điều chỉnh hệ thống.

| Chỉ số | Cách đo và ý nghĩa |
| --- | --- |
| Hoàn thành đúng | Số nhiệm vụ có kết quả đúng và đủ căn cứ / số nhiệm vụ đánh giá; chấm bởi người phụ trách nghiệp vụ. |
| Thời gian hoàn thành | Từ bắt đầu tra cứu đến kết quả đã kiểm tra, hoặc proposal tạo thành công; tính cả sửa và kiểm nguồn. Báo trung vị và p95. |
| Nguồn và điều kiện | Độ bao phủ nguồn/điều kiện bắt buộc trong đáp án; tỷ lệ nhận định được trích dẫn hỗ trợ đúng. |
| Xử lý phần chưa rõ | Tỷ lệ hỏi thêm/dừng đúng và tỷ lệ từ chối/hỏi thừa ở các ca đủ căn cứ. |
| Rà soát lúc nhập | Phát hiện đúng, báo thừa và bỏ sót trên thay đổi có đáp án xác nhận. |
| Chất lượng proposal | Trường điền đúng, thời gian sửa, lỗi validation và yêu cầu bị trả lại do thiếu/sai nội dung; tách nguyên nhân nghiệp vụ khác. |
| Công vận hành | Chi phí mô hình, thời gian admin/chủ tài liệu và hỗ trợ mỗi nhiệm vụ; không chỉ tính lợi ích phía GDV. |

Ngưỡng chấp nhận và baseline thuộc D06. Sai nguồn/điều kiện dẫn đến chỉ dẫn sai và điền sai dữ kiện có thể gây hậu quả lớn hơn hỏi thêm một lần; nhưng hỏi thừa quá nhiều cũng làm mất giá trị. Không tối ưu tỷ lệ “trả lời được” đơn thuần.

<!-- PAGE -->

### 4.5.1. Đối chiếu Day02 về khám phá và người liên quan

D1–D6 tương ứng bộ phân kỳ trang 21; I1–I5 tương ứng phỏng vấn trang 25 của R01. Câu hỏi được rút gọn, câu trả lời phản ánh mức hiểu biết hiện tại.

| Mã / câu hỏi | Câu trả lời và việc cần xác minh |
| --- | --- |
| D1 — Giả định nào cần lật lại? | Nhân viên không nhất thiết biết tên/nơi lưu tài liệu; có citation không bảo đảm áp dụng đúng; người dùng nội bộ không luôn phát hiện lỗi. Cần kiểm chứng qua tác vụ thực. |
| D2 — Cách tiếp cận mới? | Hỏi bằng ngôn ngữ công việc, nhận nguồn tại vị trí liên quan, đọc tiếp theo liên kết và chuyển dữ kiện sang form CMS. |
| D3 — Nếu thiết kế từ đầu? | Kho tài liệu có hiệu lực, chủ sở hữu, quyền và liên kết; một lối truy cập trong CMS; giữ quyết định tạo và phê duyệt tại chức năng hiện có. |
| D4 — Vì sao cần AI? | Đáng thử cho hiểu câu hỏi, tổng hợp đa nguồn và soạn nội dung. Tìm kiếm có cấu trúc và form thông thường là đối chứng bắt buộc; chưa chứng minh AI luôn tốt hơn. |
| D5 — Quy trình chỉ do thói quen? | Nhớ đường dẫn, hỏi người quen và chép lại nội dung là các ứng viên; cần quan sát để biết bước nào thật sự có thể bỏ. |
| D6 — Câu hỏi cốt lõi bị né? | Khó vì tìm không ra, không hiểu, thiếu quyền hay quy trình chưa rõ? Chatbot không sửa được tài liệu mâu thuẫn hoặc thay thẩm quyền xử lý. |
| I1 — Pain point và tần suất? | Khó tìm/đọc đủ điều kiện và nhập lại thông tin. Có ví dụ GDV, kế toán, tín dụng; chưa đo số lần/ngày, mức khó hay tỷ trọng tác vụ. |
| I2 — Workflow và bàn giao? | Giả thuyết: nhân viên tìm trong CMS/kho, đọc hoặc hỏi đồng nghiệp, nhập proposal và chuyển duyệt. Cần quan sát luồng thực tế và xác nhận schema CMS tại D02. |
| I3 — Thiệt hại cụ thể? | Phút tìm, đọc, hỏi lại, sửa form và công vận hành nguồn. Chưa có số đo để quy ra chi phí, SLA hoặc ROI; không gán lợi ích định lượng giả. |
| I4 — Sai thì sao, ai kiểm? | Có thể chỉ dẫn sai hoặc tạo nội dung sai trong proposal. Admin kiểm nguồn nhập, nhân viên kiểm nguồn/form, người có thẩm quyền duyệt theo CMS; không giả định các lớp này bắt mọi lỗi. |
| I5 — Ai nói YES, dựa vào gì? | Chủ dự án quyết định nguyên mẫu. Pilot cần chủ CMS, chủ tài liệu và người dùng đại diện; căn cứ là chất lượng, thời gian hoàn thành và chi phí/rủi ro theo D06. |

Phần chưa biết phải được bổ sung bằng quan sát/phỏng vấn và đo baseline. Nội dung BRD không thay cho bằng chứng khám phá người dùng.

<!-- PAGE -->

### 4.5.2. Đối chiếu Day02 về bài toán và quyết định

P1–P6 tương ứng cấu trúc Problem Statement trang 30; G1–G5 tương ứng gate quyết định trang 69 của R01.

| Mã / câu hỏi | Câu trả lời và căn cứ quyết định |
| --- | --- |
| P1 — Quy trình hiện tại? | Tìm/đọc → xác minh → sử dụng; nếu cần, nhập proposal và chuyển theo quy trình CMS. Đây là mô tả cần đối chiếu với tác vụ thực, không phải kết quả khảo sát. |
| P2 — Nút thắt? | Định vị nguồn, giữ đủ điều kiện/ngoại lệ và chuyển thông tin sang form. Ưu tiên kiểm chứng cả ba trước khi mở rộng hành động agent. |
| P3 — Hao phí hiện tại? | Chưa có baseline. Đo thời gian và số lần hỏi/sửa theo nhóm nhiệm vụ, gồm cả công phía admin và chủ tài liệu. |
| P4 — Tiêu chí thành công? | Chỉ số tại 4.5: hoàn thành đúng, thời gian bao gồm kiểm chứng, chất lượng nguồn/điều kiện và proposal. Ngưỡng định lượng phải được chốt trước pilot. |
| P5 — Sai và phạm vi tự quyết? | Agent chọn bước đọc/hỏi và chuẩn bị nội dung. CMS kiểm soát quyền, tạo và phê duyệt; người dùng kiểm tra trước tạo. Hậu quả của lời hướng dẫn sai vẫn phải đo. |
| P6 — Cách phi AI đơn giản hơn? | Cây tài liệu, từ khóa/từ đồng nghĩa, liên kết, FAQ, checklist và điền form theo quy tắc. Nếu đáp ứng đủ với chi phí thấp hơn thì dùng chúng. |
| G1 — Cần ngôn ngữ/suy luận? | Có, ở câu hỏi tự nhiên và tổng hợp điều kiện đa nguồn. Quyền, hiệu lực, validation và chuyển trạng thái dùng code/workflow xác định. |
| G2 — Đủ ngữ cảnh đầu vào? | Chưa xác nhận. Cần nguồn hiện hành, phụ lục, quyền, danh bạ và schema proposal; thiếu dữ liệu thì chưa sẵn sàng pilot. |
| G3 — Có chỉ số định lượng? | Đã định nghĩa cách đo, chưa có baseline/ngưỡng hoặc bộ đáp án được duyệt. D06 phải hoàn tất trước quyết định pilot. |
| G4 — Hậu quả sai kiểm soát được? | Phạm vi chuẩn bị và bước người dùng kiểm tra giảm hành động sai trực tiếp; chưa chứng minh kiểm soát đủ. Phải qua UAT và kiểm tra hành vi sử dụng thật. |
| G5 — Có giải pháp rẻ hơn? | Có các đối chứng nêu ở P6. Chọn mức đơn giản nhất đạt tiêu chí; agent chỉ được giữ khi lợi ích tra cứu linh hoạt bù được chi phí và công kiểm chứng. |

**Trả lời 4 câu hỏi trọng tâm Day02:** AI đáng thử ở đọc hiểu và chuẩn bị nội dung; giải pháp gồm code/workflow cho kiểm soát và agent có giới hạn cho tra cứu động. Problem Statement đủ cho nguyên mẫu, chưa đủ để cam kết hiệu quả vận hành. Quyết định hiện tại: **Go cho nguyên mẫu; Not Yet cho pilot toàn doanh nghiệp**. Nếu thử nghiệm không hơn giải pháp đơn giản hoặc rủi ro không kiểm soát được, thu hẹp phạm vi hoặc No-Go cho phần agent.

LLM không mặc nhiên thuộc riêng “phần mềm 2.0”, cũng không phải mọi chatbot đều là agent. BRD phân chia theo trách nhiệm thực tế; không bắt buộc dùng đủ ba nhãn công nghệ để hoàn thành sản phẩm.

<!-- PAGE -->

## 4.6. Phụ thuộc và quyết định cần xác nhận

Các mục sau không cản việc làm nguyên mẫu bằng dữ liệu giả lập, nhưng phải được đóng trước mốc ghi trong bảng. Chủ dự án có thể kiêm vai trò khi thử nghiệm cá nhân; khi pilot phải xác định người chịu trách nhiệm thực tế.

| Mã | Quyết định / đầu ra cần có | Chủ trì và thời điểm |
| --- | --- | --- |
| D01 | Miền tài liệu ban đầu, nguồn còn hiệu lực, chủ tài liệu và ma trận quyền; bộ ví dụ hợp lệ. | Chủ sản phẩm + chủ tài liệu; trước nguyên mẫu dùng nguồn thực. |
| D02 | Loại proposal, schema, danh mục, API/form, validation, quyền tạo, trạng thái, tạo/gửi duyệt, tuyến duyệt và cơ chế chống trùng. | Chủ CMS + nghiệp vụ; trước tích hợp tạo proposal. |
| D03 | Lưu/xóa tài liệu cũ, hội thoại, dữ liệu form và audit; quyền log, thời hạn, nơi xử lý dữ liệu và phục hồi phiên. | Chủ dự án + chủ CMS; trước dùng dữ liệu nội bộ thực. |
| D04 | Định dạng, chất lượng file, kích thước, số nguồn và quy mô người dùng/tải đồng thời. | Chủ tài liệu + kỹ thuật; trước kiểm thử hiệu năng. |
| D05 | Mục tiêu độ trễ, sẵn sàng, khôi phục, giới hạn công cụ/thời gian/chi phí; phương án khi AI lỗi. | Chủ CMS + kỹ thuật; trước pilot. |
| D06 | Tác vụ thực, baseline, bộ đáp án, cách chấm, số lượt/biến thể, ngưỡng chất lượng và hiệu quả; tiêu chí dừng. | Chủ sản phẩm + nghiệp vụ + kiểm thử; trước pilot. |
| D07 | Danh bạ trách nhiệm, kênh liên hệ và lịch/cơ chế cập nhật. | Chủ nghiệp vụ; trước mở tính năng gợi ý đầu mối. |

**Điều kiện chuyển sang pilot**

- D01–D07 đã được giải quyết ở phần áp dụng cho phạm vi pilot; chủ nghiệp vụ xác nhận nguồn và bộ đáp án.
- Kịch bản nghiệm thu trọng yếu đã kiểm tra; không còn lỗi nghiêm trọng về vượt quyền, dùng nguồn bị loại, token, tự tạo/phê duyệt hoặc tạo trùng.
- Chất lượng và hiệu quả đạt ngưỡng đã thống nhất, bao gồm thời gian người dùng kiểm chứng và công quản trị nguồn.
- Có người tiếp nhận phản hồi, theo dõi lỗi và khả năng tắt trợ lý/quay về tra cứu và form thông thường khi cần.

**Hướng thiết kế để xem xét sau BRD:** state có thể triển khai bằng LangGraph hoặc cơ chế tương đương; cây/liên kết có thể kết hợp tìm kiếm và đọc file. Lựa chọn framework, mô hình và cách lưu trữ thuộc thiết kế kỹ thuật, được quyết định bằng phép thử. Không xem một framework hay đọc toàn văn là bảo đảm AI không bỏ sót điều kiện.

**Kết quả rà soát BRD cần ghi nhận:** phạm vi được chấp nhận, thay đổi cần sửa, người chịu trách nhiệm cho D01–D07 và tiêu chí được chốt. Phiên bản này là dự thảo, chưa ghi nhận phê duyệt nghiệp vụ hoặc nghiệm thu triển khai.
