# Các Vấn Đề Cần Giải Quyết (Problem Notes)

*Ghi nhận bởi PO — Ngày: 22/09/2026*
*Tham chiếu: BRD-CMS-AI-001, brd/, research/*

---

## Vấn đề 1: Quản lý Vòng đời Tài liệu (Document Versioning & Effective Dates)

### Mô tả vấn đề
Tài liệu BRD hiện có đề cập *"Văn bản hết hiệu lực bị loại khỏi kho"*, nhưng thực tế vận hành ngân hàng phức tạp hơn: **các hợp đồng cũ (ký trước thời điểm đổi chính sách) vẫn phải áp dụng quy chế tại thời điểm ký hợp đồng**, không phải quy chế hiện hành.

### Rủi ro cụ thể
- LLM có thể lấy quy chế mới nhất để áp dụng cho hợp đồng được ký dưới quy chế cũ → sai nghiệp vụ.
- Nếu chỉ "loại văn bản hết hiệu lực khỏi kho" mà không có cơ chế tra cứu ngược, Chatbot sẽ không thể xử lý câu hỏi dạng: *"Hợp đồng ký năm 2023 áp dụng biểu phí nào?"*

### Yêu cầu cần làm rõ
- [x] Hệ thống Metadata của kho tri thức cần bắt buộc có các trường: `ngay_hieu_luc`, `ngay_het_hieu_luc`, `doi_tuong_ap_dung`, `phien_ban`.
- [x] Cần định nghĩa rõ chiến lược lưu trữ tài liệu hết hiệu lực: *loại khỏi kho trả lời thông thường* nhưng *vẫn giữ trong kho lưu trữ (archive)* để phục vụ tra cứu theo ngữ cảnh hợp đồng cụ thể.
- [x] Khi người dùng hỏi liên quan đến một hợp đồng cũ, Chatbot cần có cơ chế nhận diện ngữ cảnh thời điểm và tra cứu đúng phiên bản tài liệu có hiệu lực vào lúc đó.
- [x] Cần xác nhận với đội nghiệp vụ: Có bao nhiêu trường hợp thực tế cần tra cứu theo "thời điểm ký hợp đồng"? Đây có phải use case phổ biến không?

### 💡 Đề xuất Giải pháp (Proposed Solutions)
**1. Chiến lược "Time-Aware RAG" (Truy xuất dữ liệu nhận thức thời gian):**
- **Thiết kế Database:** Vector Database chia làm 2 phân vùng (hoặc filter cứng): **Active (Hiện hành)** và **Archive (Lưu trữ)**.
- **Metadata bắt buộc:** Mỗi chunk thông tin phải gắn `effective_start_date` và `effective_end_date`. Nếu còn hiệu lực, `end_date` để trống.
- **Quy tắc truy xuất:** 
  - *Mặc định:* Chatbot chỉ tìm kiếm trong vùng **Active**.
  - *Kích hoạt Archive:* Nếu câu hỏi có mốc thời gian (VD: "Hợp đồng tháng 5/2023"), LLM tự trích xuất mốc thời gian, truyền cho Backend để query thêm dữ liệu từ vùng **Archive** có mốc thời gian tương ứng.

**2. Nguyên tắc Trả lời (Prompt Engineering):**
- Bắt buộc LLM luôn nêu rõ mốc thời gian của tài liệu khi trả lời. VD: *"Theo Quyết định số 123 có hiệu lực từ ngày 01/01/2022..."*.
- Nếu AI tìm thấy nhiều phiên bản tài liệu (cũ và mới) mà không chắc chắn, AI **bắt buộc phải hỏi ngược lại GDV**: *"Anh/chị vui lòng cho biết ngày ký hợp đồng để đối chiếu quy định chính xác nhất?"*.

### Mức độ ưu tiên
**Cao** — Nếu không giải quyết, Chatbot có thể đưa ra căn cứ sai cho các hợp đồng/giao dịch cũ, gây rủi ro nghiệp vụ và pháp lý.

---

## Vấn đề 2: Flow xử lý khi AI "Bó tay" hoặc Trả lời Sai (AI Failure & Feedback Loop)

### Mô tả vấn đề
Tài liệu hiện chưa đề cập đến **Feedback Loop** (Vòng lặp phản hồi) và **Human Handoff Flow** (Luồng chuyển tiếp sang hỗ trợ con người). Trong thực tế, Chatbot AI sẽ có những tình huống:
1. **Không đủ căn cứ** → nên từ chối trả lời (đã có trong BRD).
2. **Trả lời sai** → người dùng phát hiện sau khi nhận câu trả lời.
3. **Tình huống vượt phạm vi** → cần escalate lên chuyên viên nghiệp vụ.

### Câu hỏi chưa được giải đáp trong tài liệu
- Nếu Chatbot trả lời sai, người dùng sẽ **báo cáo (report / thumbs down)** như thế nào?
- Sau khi nhận phản hồi sai, dữ liệu đó có được thu thập để **cải thiện hệ thống** (fine-tuning / RAG update) không? Ai chịu trách nhiệm review và xử lý?
- **Flow chuyển tiếp (Handoff)** từ Chatbot sang "Người hỗ trợ nghiệp vụ (Human Support)" thực tế sẽ diễn ra như thế nào trên UI?
  - Chatbot tự động gợi ý "Liên hệ bộ phận X" hay người dùng phải tự tìm?
  - Có tích hợp với hệ thống ticket/yêu cầu hỗ trợ nội bộ không?

### Yêu cầu cần làm rõ
- [x] Thiết kế **UI/UX cho Feedback Mechanism**: Nút Thumbs Up / Thumbs Down, hoặc "Báo cáo câu trả lời không chính xác" với dropdown lý do.
- [x] Xác định **người tiếp nhận và xử lý feedback**: Ai là người review các câu trả lời bị đánh dấu sai? Bộ phận nào?
- [x] Thiết kế **Human Handoff Flow**: Khi nào Chatbot tự chủ động gợi ý escalate, khi nào đợi người dùng yêu cầu? Nội dung được chuyển tiếp bao gồm những gì (lịch sử chat, câu hỏi, ngữ cảnh)?
- [ ] Xem xét tích hợp **Audit Log**: Mọi câu trả lời của Chatbot (kèm trích dẫn nguồn) cần được log lại để phục vụ tra soát khi có tranh chấp nghiệp vụ.

### 💡 Đề xuất Giải pháp (Proposed Solutions)
**1. Thiết kế UI/UX cho Vòng lặp phản hồi (Feedback Loop):**
- **Nút Đánh giá (Inline Feedback):** Dưới mỗi câu trả lời luôn có nút 👍 (Hữu ích) và 👎 (Chưa chính xác).
- **Form Báo lỗi Nhanh:** Khi bấm 👎, hiện popup nhỏ với các tick-box có sẵn (VD: *Sai nghiệp vụ, Tài liệu hết hiệu lực, Trả lời chung chung*) và 1 ô nhập text bổ sung.
- **Dashboard cho QA:** Các lượt 👎 được đẩy về Dashboard riêng. Team Quản lý tri thức review định kỳ. (Nếu tài liệu sai -> sửa tài liệu; nếu AI ngốc -> thêm vào tập test/fine-tuning).

**2. Flow Chuyển tiếp Hỗ trợ (Human Handoff):**
- **Trigger (Điểm kích hoạt):**
  - *AI chủ động:* Quét không ra kết quả / tự tin thấp -> AI báo: *"Tài liệu hiện hành không quy định rõ. Bạn có muốn gửi Ticket hỗ trợ không?"* kèm nút **[Tạo Ticket]**.
  - *Người dùng chủ động:* Nút "Gặp chuyên viên" thường trực trên giao diện.
- **Trải nghiệm Chuyển tiếp:**
  - Bấm **[Tạo Ticket]**, CMS mở form tạo Yêu cầu hỗ trợ.
  - **Automation:** Tự động đính kèm lịch sử chat, câu hỏi gốc, các tài liệu AI đã trích dẫn vào Ticket để chuyên viên không phải hỏi lại.
  - **Phân luồng:** Dựa trên context chat, Ticket tự động được gán về đúng Phòng/Ban hỗ trợ (VD: Thẻ, Tín dụng).

### Mức độ ưu tiên
**Cao** — Không có Feedback Loop, hệ thống sẽ không có cơ chế tự cải thiện và không có phương án xử lý khi AI mắc lỗi trong môi trường ngân hàng (rủi ro tuân thủ rất cao).

---

## Ghi chú tổng hợp

| # | Vấn đề | Mức ưu tiên | Giai đoạn cần giải quyết |
|---|--------|-------------|--------------------------|
| 1 | Quản lý Vòng đời Tài liệu (Versioning & Effective Dates) | 🔴 Cao | Trước khi xây dựng Knowledge Base |
| 2 | Feedback Loop & Human Handoff Flow | 🔴 Cao | Trước khi ra mắt Prototype |

*Cập nhật tiếp theo: Bổ sung sau khi có kết quả thảo luận với đội nghiệp vụ và kỹ thuật.*