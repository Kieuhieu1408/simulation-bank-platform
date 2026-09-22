---
document_id: "REF-002"
title: "Ghi chú Pháp lý: Hoạt động Thẻ Ngân hàng"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV2"
document_type: "reference"
organization: "Nhà nước"
source_type: "external_regulation"
source_refs: ["TT-18-2024", "TT-19-2016"]
status: "published"
published_at: "2024-07-01"
effective_from: "2024-07-01"
effective_to: null
product_scope: ["credit_card", "debit_card"]
customer_scope: ["all"]
access_roles: ["knowledge_admin"]
unit_scope: ["all"]
owner_role: "card_operations"
related_documents: ["NV2-001", "NV2-003"]
supersedes: []
---

# Ghi chú Pháp lý: Hoạt động Thẻ Ngân hàng

> Mục đích: Cung cấp khung pháp lý thực tế để làm giàu cho nghiệp vụ Thẻ (NV2) tại Ngân hàng Mô phỏng, hỗ trợ sinh tình huống nhiễu về độ tuổi, hạn mức rút tiền và phát hành thẻ phụ.

<a id="REF-002-D01"></a>
## D01. Quy định chung về phát hành thẻ (Thông tư 18/2024/TT-NHNN)

- **Chuyển đổi thẻ từ sang thẻ chip:** Ngân hàng phải dừng hoàn toàn việc phát hành thẻ từ nội địa và bắt buộc phát hành thẻ có gắn chip tuân thủ tiêu chuẩn cơ sở của NHNN.
- **Định danh điện tử (eKYC) với thẻ:** Tương tự như tài khoản, phát hành thẻ điện tử yêu cầu đối khớp sinh trắc học khuôn mặt khách hàng với thẻ CCCD gắn chip.
- **Ứng dụng vào Mô phỏng:** Sinh ra các tình huống khách hàng khiếu nại "Tại sao tôi không dùng được thẻ từ cũ?", chatbot cần căn cứ quy định này để giải thích thẻ từ đã hết hiệu lực, phải đổi thẻ chip.

<a id="REF-002-D02"></a>
## D02. Điều kiện độ tuổi và năng lực hành vi

- **Chủ thẻ chính:** Phải đủ 18 tuổi trở lên có năng lực hành vi dân sự đầy đủ. Người từ đủ 15 tuổi đến chưa đủ 18 tuổi được phát hành thẻ ghi nợ không thấu chi, hoặc thẻ trả trước.
- **Chủ thẻ phụ:**
  - Từ đủ 15 tuổi: được dùng thẻ ghi nợ, thẻ tín dụng, thẻ trả trước mà không cần người đại diện theo pháp luật đồng ý bằng văn bản.
  - Từ đủ 6 đến chưa đủ 15 tuổi: được dùng thẻ nhưng cần văn bản đồng ý của người đại diện. Không được phát hành thẻ tín dụng cho độ tuổi này.
- **Ứng dụng vào Mô phỏng:** Dùng làm nhiễu cho quy trình mở thẻ phụ (NV2-003). Chatbot phải biết từ chối hồ sơ phát hành thẻ tín dụng phụ cho người 14 tuổi, hoặc yêu cầu văn bản cha mẹ đối với thẻ ghi nợ phụ cho người 12 tuổi.

<a id="REF-002-D03"></a>
## D03. Hạn mức giao dịch và Rút tiền mặt

- **Hạn mức rút tiền mặt thẻ tín dụng:** Tối đa không quá số tiền hoặc tỷ lệ phần trăm hạn mức tín dụng thỏa thuận (thường do Ngân hàng tự định nhưng NHNN có giám sát).
- **Hạn mức eKYC:** Đối với thẻ phát hành qua phương thức điện tử không có gặp mặt trực tiếp, hạn mức giao dịch tính toán (debit/credit) không được vượt quá 100 triệu VND/tháng, trừ khi đã thực hiện xác thực sinh trắc học khớp với CCCD chip.
- **Ứng dụng vào Mô phỏng:** Nếu hồ sơ phát hành trực tuyến, hạn mức tín dụng tự động bị block ở mức 100 triệu nếu khách chưa scan mặt (Face ID).

<a id="REF-002-D04"></a>
## D04. Xử lý tra soát, khiếu nại thẻ

- **Thời hạn xử lý:** Tối đa 30 ngày làm việc kể từ ngày tiếp nhận khiếu nại (đối với thẻ có BIN do TCTQT cấp) và 45 ngày đối với một số giao dịch phức tạp. 
- **Bồi thường:** Nếu ngân hàng xác định lỗi không thuộc về khách hàng, ngân hàng phải hoàn tiền/bồi thường trong vòng 05 ngày làm việc.
- **Ứng dụng vào Mô phỏng:** Làm dữ liệu đối soát khi đóng thẻ (NV2-001). Nếu đang có tra soát chưa kết thúc, không được tất toán và đóng thẻ tín dụng.
