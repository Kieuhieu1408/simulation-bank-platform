**Trợ lý tri thức và hỗ trợ chuẩn bị proposal trong CMS — định nghĩa v0.3**

Ngày cập nhật: 21/09/2026. Bản này mở rộng v0.2 với luồng chuẩn bị proposal, thay phạm vi chỉ dành cho GDV trong [v0.1](./gdv-policy-assistant-problem-definition.md). Yêu cầu chi tiết và đối chiếu Day02 hiện hành nằm trong [BRD v1.0](../brd/cms-knowledge-and-proposal-assistant-brd-v1.0.md). Giai đoạn: xác định sản phẩm và thử nghiệm tính khả thi; chưa triển khai chatbot.

**Quyết định sản phẩm từ trao đổi với người dùng**

- Người dùng là toàn bộ nhân viên được cấp tài khoản CMS; GDV, kế toán và nhân sự/CTV tín dụng có tài khoản phù hợp là những nhóm ví dụ.
- Chatbot chỉ tích hợp tại CMS. Chức năng chính: tìm tài liệu, giải thích, đối chiếu quy trình/sản phẩm và cung cấp căn cứ để người dùng tự kiểm tra khi quan trọng.
- Ví dụ: kế toán hỏi quy trình khóa thẻ; nhân sự tín dụng tìm hiểu mở thẻ, hạn mức sản phẩm và điều kiện được tài liệu quy định; GDV đối chiếu một tình huống khách hàng. Quyền hỏi/đọc không đồng nghĩa quyền thực hiện nghiệp vụ.
- Người dùng không muốn mã hóa trước mọi case. Nguồn chuẩn là tài liệu đang có hiệu lực; tình huống mẫu phục vụ đánh giá, không phải toàn bộ luật vận hành của chatbot.
- Giao diện mặc định nền trắng; chỉ ngoại lệ/cảnh báo được highlight. Không thiết kế bảng màu trạng thái phủ khắp giao diện.
- Tài liệu được đưa vào hệ thống đã qua quy trình xem xét nghiệp vụ của tổ chức. LLM rà soát thêm khi nhập để tìm ngoại lệ, tham chiếu, khả năng mâu thuẫn và tác động có thể chưa được đề cập. Đây là lớp kiểm tra bổ sung.
- Văn bản chưa có hiệu lực không được đưa vào kho tri thức trả lời. Văn bản hết hiệu lực bị loại khỏi kho đang sử dụng khi cập nhật nguồn.
- Tổ chức kho theo cây và các liên kết có ý nghĩa; kết hợp tìm kiếm với đọc file/chương đầy đủ. Chưa mặc định vector database hoặc một kích thước chunk.
- Bổ sung chuẩn bị proposal qua chat và mở form CMS đã điền trước. Người dùng kiểm tra, sửa và bấm tạo; CMS kiểm tra quyền, validation và giữ nguyên luồng phê duyệt. Agent không nhận token và không điều khiển trình duyệt để tự tạo.

**Bổ sung phạm vi proposal**

Trợ lý chỉ chuẩn bị các trường được phép, hỏi dữ kiện còn thiếu và hiển thị bản xem trước. Nội dung chuẩn bị chưa phải proposal đã lưu, chưa có mã proposal. Frontend CMS dùng phiên hiện tại để mở form; token không đi qua LLM. Không ghi đè chỉnh sửa chưa lưu của người dùng. Chỉ khi backend xác nhận thành công mới hiển thị mã/liên kết thật; xử lý timeout và gửi lặp phải tránh tạo trùng. Tạo và gửi duyệt nếu là các bước riêng thì giữ riêng. Loại proposal, schema, danh mục, API, trạng thái và tuyến duyệt cần được xác nhận; không coi các chi tiết đó đã được kiểm tra từ mã nguồn.

**Problem Statement**

Nhân viên có nhu cầu tra cứu quy trình và sản phẩm ngoài phạm vi họ thường làm, nhưng khó tìm đúng tài liệu và đọc đủ điều kiện/ngoại lệ. Cần một trợ lý trong CMS giúp tìm nguồn đang áp dụng, giải thích có trích dẫn và nhận biết phần chưa đủ căn cứ, giảm công tìm kiếm và phụ thuộc vào việc hỏi người quen.

Giả thuyết giá trị là tiết kiệm thời gian tìm/đọc và cải thiện khả năng tiếp cận tri thức giữa các bộ phận. Tần suất, baseline, mức sẵn sàng đọc nguồn và hiệu quả chưa được đo. Định vị “công cụ tìm kiếm tài liệu thông minh” giúp giới hạn kỳ vọng; không tự chứng minh mọi người sẽ luôn nhận ra lỗi AI.

**Hai nhu cầu cần xử lý khác nhau**

| Nhu cầu | Ví dụ | Cách phục vụ |
|---|---|---|
| Tra cứu kiến thức chung | Quy trình khóa thẻ là gì? Các sản phẩm thẻ có những hạn mức nào theo tài liệu? | Trả quy trình/thông tin và các điều kiện được nguồn nêu; không đòi dữ liệu một khách hàng nếu câu hỏi chưa cần |
| Đối chiếu tình huống cụ thể | Trường hợp khách này có thuộc điều kiện X không? | Đọc điều kiện áp dụng, đối chiếu dữ kiện, hỏi phần còn thiếu và phân biệt điều đã biết với suy luận |

Không tự biến giới hạn sản phẩm được công bố trong tài liệu thành hạn mức được phê duyệt cho một khách hàng. Khi chưa có căn cứ đủ rõ, trả phần thông tin có nguồn và điểm chưa xác định, hoặc đầu mối chính thức nếu danh bạ có dữ liệu phù hợp.

**Giao diện và trích dẫn**

Nền trắng là trạng thái bình thường. Highlight chỉ xuất hiện khi có điều kiện ngoại lệ, nguồn thiếu/mâu thuẫn, nội dung cần xác nhận hoặc trích dẫn trong lịch sử đã bị thay thế. Màu đi cùng nhãn ngắn để ý nghĩa vẫn rõ khi không phân biệt được màu.

Citation gắn từng nhận định cần kiểm chứng. Hover xem nhanh nguyên văn; bấm để giữ popup hoặc mở tài liệu tại vị trí gốc. Popup có tên nguồn, điều khoản/trang, phiên bản/ngày hiệu lực, đoạn liên quan và ngữ cảnh cần thiết. Trích đoạn lấy từ nguồn đã định vị, không để mô hình tự viết thành “nguyên văn”. Trạng thái/màu lấy từ metadata được hệ thống quản lý, không do LLM tự chọn.

**Luồng nhập và cập nhật tri thức**

Admin chọn tài liệu đã qua xem xét nghiệp vụ → khai báo hiệu lực, phạm vi, nguồn bị thay thế → LLM đọc/rà soát bổ sung → admin xem phát hiện cần xử lý → công bố một phiên bản kho tri thức mới.

LLM đề xuất: điều kiện, ngoại lệ, định nghĩa, liên kết phụ lục, khác biệt với nguồn hiện hành và điểm nghi ngờ mâu thuẫn. Mỗi phát hiện có đoạn nguồn và lý do ngắn. Phát hiện chưa được xác nhận không tự trở thành quy định. Không cam kết rằng thêm một lượt LLM đọc sẽ tìm hết ngoại lệ; cần đo tỷ lệ phát hiện trên những thay đổi đã có đáp án kiểm tra.

Khi cập nhật nguồn:

1. Loại văn bản hết hiệu lực khỏi phạm vi truy xuất đang hoạt động, gồm tìm kiếm từ khóa, chỉ mục ngữ nghĩa nếu có và liên kết dẫn tới nguồn áp dụng.
2. Cập nhật cache và dữ liệu trích xuất phụ thuộc: điều kiện, tóm tắt, quan hệ tham chiếu, trích dẫn.
3. Kiểm tra phiên bản kho ở lượt hỏi kế tiếp; nguồn cũ trong ngữ cảnh hội thoại không được tiếp tục làm căn cứ hiện hành. Đọc lại nguồn mới khi cần và đánh dấu câu trả lời lịch sử đã cũ.
4. Công bố thay đổi theo một phiên bản nhất quán, tránh khoảng thời gian trộn bản cũ và mới hoặc mất cả hai.
5. Nếu sửa một phần, admin xác định phạm vi thay thế; không tự xóa toàn bộ tài liệu còn các điều khoản hiệu lực. Nguồn áp dụng có thể là văn bản gốc kèm sửa đổi đã xác nhận.

“Xóa khỏi tri thức hoạt động” là yêu cầu sản phẩm. Việc giữ bản gốc/snapshot riêng cho truy vết hoặc xóa vật lý theo chính sách lưu trữ là một quyết định khác; bản lưu đó không tham gia tìm kiếm trả lời thông thường. Tài liệu chưa hiệu lực có thể nằm trong vùng quản trị nếu cần chuẩn bị, nhưng không nằm trong tập nguồn chatbot được dùng.

Log ghi phiên bản nguồn, đoạn đọc, công cụ, phát hiện, quyết định admin và lỗi thực thi. Đây là dấu vết có thể kiểm chứng, không yêu cầu lưu chuỗi suy nghĩ nội bộ của model. Chỉ người được phép mới xem log; hạn chế lưu thông tin không cần cho chẩn đoán.

**Dùng LangChain/LangGraph quản lý state**

LangChain có agent với custom state; LangGraph cung cấp cơ chế điều phối trực tiếp bằng State, Nodes và Edges. LangGraph phù hợp để thử khi cần chủ động thiết kế nhánh đọc tiếp, hỏi thêm và tiếp tục sau câu trả lời của người dùng. Đây là lựa chọn kỹ thuật đề xuất, chưa phải quyết định thay công nghệ CMS. Nguồn: [LangChain agents](https://docs.langchain.com/oss/python/langchain/agents), [LangGraph Graph API](https://docs.langchain.com/oss/python/langgraph/graph-api).

State sản phẩm đề xuất:

| Thành phần | Nội dung và mục đích |
|---|---|
| Intent | Tra cứu chung, so sánh tài liệu hoặc đối chiếu một case; có thể được sửa khi hội thoại làm rõ |
| Facts | Dữ kiện người dùng/CMS cung cấp, nguồn của dữ kiện, dữ kiện bị sửa hoặc chưa xác nhận |
| Sources | Tài liệu/mục đã đọc, phiên bản kho, liên kết liên quan chưa đọc hoặc không truy cập được |
| Conditions | Điều kiện/ngoại lệ tìm được, đoạn nguồn, phạm vi và trạng thái đáp ứng/chưa rõ/không đáp ứng |
| Open questions | Dữ kiện còn thiếu có thể làm thay đổi kết luận, tránh hỏi điều không cần cho câu tra cứu chung |
| Conflicts | Mâu thuẫn nguồn/dữ kiện hoặc tham chiếu còn thiếu |
| Answer claims | Các nhận định dự kiến và bằng chứng hỗ trợ từng nhận định |
| Progress | Trạng thái công việc và ngân sách đọc/tra cứu để biết lúc nào dừng |

Luồng đề xuất: xác định nhu cầu → tìm nguồn trong quyền truy cập → đọc và mở rộng liên kết cần thiết → xây/đối chiếu điều kiện → kiểm tra căn cứ → trả lời, hỏi thêm hoặc chỉ ra giới hạn. Các nhánh có thể quay lại tra cứu khi có dữ kiện mới. Dùng code kiểm tra các trạng thái đã biết; không phó mặc quyết định chuyển nhánh hoàn toàn cho lời hứa trong prompt.

LangGraph hỗ trợ interrupt và tiếp tục khi có đầu vào bổ sung, với persistence/checkpointer được cấu hình; không tự có độ bền lưu trữ chỉ vì khai báo một state. [LangGraph interrupts](https://docs.langchain.com/oss/python/langgraph/interrupts).

Giới hạn quan trọng: state theo dõi được điều kiện đã được ghi nhận. Nếu bước đọc không tìm ra Y, cấu trúc state không tự phát hiện Y vắng mặt. Hai lớp giảm lỗi đề xuất là: rà soát điều kiện/ngoại lệ khi nhập tài liệu, rồi đối chiếu lại nguyên văn/phụ lục khi trả lời. Danh mục điều kiện trích xuất là dữ liệu dẫn đường, không thay tài liệu gốc và không chứng minh đã đầy đủ. Đo riêng lỗi bỏ sót lúc nhập, lỗi tìm thiếu nguồn và lỗi áp dụng khi hỏi.

Không định nghĩa một state cho từng case đóng/mở/khóa thẻ. State mô tả quá trình giải quyết câu hỏi; điều kiện nghiệp vụ đến từ nguồn. Khi người dùng sửa dữ kiện hoặc tài liệu đổi phiên bản, phải vô hiệu hóa kết luận phụ thuộc và đối chiếu lại.

**Phạm vi nội bộ và giới hạn hành động**

Nhân viên có tài khoản CMS là nhóm sử dụng mục tiêu. Tài liệu chia sẻ toàn công ty có thể được tra cứu xuyên bộ phận; các nguồn hạn chế vẫn theo quyền CMS. CTV có quyền truy cập nào dùng phạm vi đó, không mặc nhiên có cùng quyền với mọi nhân viên.

Chatbot đọc, giải thích tài liệu và chuẩn bị dữ liệu proposal; người dùng kiểm tra rồi tạo qua chức năng CMS hiện có. Chatbot không có quyền khóa/mở thẻ, phê duyệt hạn mức hoặc sửa giao dịch. Điều này giới hạn tác động trực tiếp của lỗi. Môi trường nội bộ giảm một phần bề mặt tiếp xúc so với public, nhưng không bảo đảm thiệt hại rò rỉ tài liệu hoặc sử dụng thông tin sai luôn nhỏ. Áp dụng kiểm soát tương xứng: quyền đọc hiện có, tách dữ liệu khỏi chỉ dẫn và trích dẫn mở được; không thêm bước phê duyệt vào mọi câu hỏi đơn giản.

**Problem Statement — 9 trường Day02**

| Trường | Định nghĩa hiện hành |
|---|---|
| Actor | Nhân viên được cấp tài khoản CMS, gồm nhiều vai trò; GDV là một nhóm |
| Workflow | Có nhu cầu → tìm/hỏi tài liệu → đọc điều kiện → đối chiếu hoặc hỏi người phụ trách → sử dụng thông tin; nếu cần, chuẩn bị và tạo proposal qua form CMS |
| Bottleneck | Khó định vị đúng nguồn và tổng hợp điều kiện/ngoại lệ ngoài chuyên môn thường ngày |
| Impact | Công tìm/đọc, thời gian hỏi đồng nghiệp, nguy cơ dùng nguồn sai; chưa có baseline |
| Success Metric | Hoàn thành tra cứu đúng có căn cứ, thời gian gồm đọc/xác minh, dẫn chứng đúng, ngoại lệ không bị bỏ sót |
| Boundary | CMS-only; nguồn hiện hành trong quyền truy cập; đọc/giải thích/chuẩn bị proposal; người dùng kiểm tra và bấm tạo, CMS giữ quyền và luồng phê duyệt |
| AI entry | Nhân viên chủ động hỏi; thêm lượt rà soát tài liệu ở luồng admin |
| Level | Workflow có state và khả năng agent tra cứu động khi cần; so sánh trên cùng bộ ca trước khi chốt |
| Risk/HITL | Admin xử lý phát hiện lúc nhập; nhân viên kiểm nguồn khi sử dụng; chủ nghiệp vụ giải đáp phần không rõ |

**22 câu hỏi Day02 được cập nhật theo phạm vi mới**

Các câu trả lời dưới đây ghi lại lập luận cho phần tra cứu ở v0.2. Bản đối chiếu đầy đủ đã bao gồm luồng proposal tại mục 4.5.1–4.5.2 của BRD v1.0.

- **D1:** Nhân viên không cần biết tài liệu nằm ở bộ phận nào hoặc nhớ thuật ngữ trước khi tìm được nguồn.
- **D2:** Cho hỏi bằng ngôn ngữ công việc, nhận câu trả lời cùng vị trí nguồn, dùng liên kết để đọc tiếp.
- **D3:** Kho tri thức có cấu trúc, hiệu lực và người phụ trách; một lối truy cập trong CMS cho nhiều vai trò.
- **D4:** AI đáng thử ở tìm nghĩa, tổng hợp và giải thích; tìm kiếm thông thường vẫn là đối chứng.
- **D5:** Phụ thuộc việc nhớ đường dẫn hoặc hỏi người quen có thể là thói quen; cần quan sát để xác minh.
- **D6:** Người dùng thật sự khó tìm tài liệu, khó hiểu nội dung hay thiếu quyền/trách nhiệm xử lý? Chatbot không giải quyết cả ba giống nhau.
- **I1:** Có ví dụ GDV, kế toán, tín dụng; tần suất câu hỏi và mức khó của từng nhóm chưa được đo.
- **I2:** CMS/kho tài liệu/đồng nghiệp là các điểm truy cập dự kiến; cần thu ca gần nhất và các bước bàn giao thực tế.
- **I3:** Hao phí là phút tìm, đọc, xác nhận và hỏi lại; chưa có số, không gán ROI giả.
- **I4:** Lỗi có thể dẫn người dùng tới nguồn/điều kiện sai; phạm vi chỉ đọc giảm tác động trực tiếp nhưng thông tin vẫn có thể được dùng sai.
- **I5:** Bạn quyết định sandbox; pilot cần chủ CMS, chủ tài liệu và đại diện nhóm sử dụng. Hiệu quả và lỗi theo nhóm là căn cứ, không chỉ lượt chat.
- **P1:** Nhu cầu tra cứu → tìm/đọc → kiểm nguồn → sử dụng. Chỉ thêm hỏi dữ kiện case khi câu hỏi thật sự cần áp dụng cụ thể.
- **P2:** Tìm đúng nguồn, giữ điều kiện/ngoại lệ khi giải thích và đối chiếu.
- **P3:** Chưa có baseline; đo với công cụ tìm kiếm tài liệu tốt và cùng quyền truy cập.
- **P4:** Đo tỷ lệ hoàn thành đúng, thời gian kể cả kiểm chứng, chất lượng citation và điều kiện bị bỏ sót; ngưỡng chưa chốt.
- **P5:** Giới hạn quyền đọc/giải thích; nguồn hết hiệu lực không tham gia trả lời; chưa đủ căn cứ thì nêu giới hạn/hỏi thêm.
- **P6:** Tìm kiếm từ khóa/từ đồng nghĩa, cây thư mục, wiki có liên kết, FAQ và đầu mối chính thức.
- **G1:** Có nhu cầu xử lý ngôn ngữ và tổng hợp đa nguồn; không suy ra mọi câu đều cần agent.
- **G2:** Chưa đủ để pilot: cần kho hiện hành, quyền, liên kết/metadata và nguồn chuẩn cho bộ câu hỏi.
- **G3:** Đã có kế hoạch chỉ số; chưa có đo baseline, bộ đáp án và ngưỡng được chấp nhận.
- **G4:** Giới hạn hành động giúp kiểm soát hậu quả; khả năng nhân viên kiểm chứng cần thử, không giả định tuyệt đối.
- **G5:** Có giải pháp đơn giản hơn; LLM phải cho lợi ích so với tìm kiếm/wiki tốt trên những câu người dùng thật cần.

**Kiểm chứng tính khả thi**

Sản phẩm có phạm vi người dùng rộng từ đầu, nhưng thử nghiệm theo vài miền tài liệu được chọn để đo được. Đây là giới hạn của phép thử, không phải loại kế toán/nhân sự tín dụng khỏi sản phẩm.

Các nhóm đánh giá cần có: tra cứu quy trình đơn giản; câu hỏi cùng ý khác từ; ngoại lệ ở phụ lục; câu hỏi cần thêm dữ kiện; văn bản được thay thế giữa hai lượt hỏi; nguồn sửa một phần; tài liệu chưa hiệu lực; câu hỏi vượt quyền; nguồn thiếu hoặc mâu thuẫn; và nhân viên khác chuyên môn đọc hiểu câu trả lời.

So tìm kiếm thông thường, đọc toàn bộ tài liệu phù hợp và tìm kiếm kết hợp đọc mở rộng trên cùng nguồn. Đo lỗi trước/sau cập nhật tri thức, chi phí/độ trễ và công kiểm chứng; không chỉ đo thời gian sinh câu trả lời. Với lượt rà soát nhập tài liệu, đo phát hiện đúng, bỏ sót và báo thừa trên thay đổi có đáp án xác nhận.

Theo Day02: Go cho nguyên mẫu kho tri thức có kiểm soát, chatbot và chuẩn bị dữ liệu proposal trong CMS; Not Yet cho khẳng định hiệu quả vận hành toàn doanh nghiệp. LangGraph có thể hỗ trợ cơ chế state và quan sát, nhưng khả thi nghiệp vụ phải được chứng minh bằng dữ liệu và cách sử dụng thực tế. Luồng tạo proposal chỉ tích hợp sau khi xác nhận hợp đồng dữ liệu và cơ chế CMS tại D02 của BRD.
