---
document_id: "NV2-008"
title: "Sản phẩm thẻ tương lai và chính sách dự kiến"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV2"
document_type: "policy"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-09-15"
effective_from: "2027-06-01"
effective_to: null
product_scope: ["credit_card", "virtual_card", "biometric_card"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "card_operations"
related_documents: ["NV2-001", "NV1-010"]
supersedes: []
---

# Sản phẩm thẻ tương lai và chính sách dự kiến

Tài liệu này thiết lập bộ khung chính sách và định hướng thiết kế cho các sản phẩm thẻ thế hệ mới của Ngân hàng Mô phỏng, dự kiến chính thức áp dụng và triển khai từ ngày 01/06/2027. Tài liệu hiện tại đã được ban hành (published) để làm cơ sở chuẩn bị cho hệ thống CNTT và vận hành.

<a id="NV2-008-D01"></a>
## Định hướng phát triển các dòng thẻ ảo (Virtual Card) và thẻ phi vật lý

Trong giai đoạn tới, Ngân hàng Mô phỏng sẽ đẩy mạnh việc chuyển đổi từ thẻ vật lý sang thẻ ảo (Virtual Card) đối với toàn bộ các sản phẩm thẻ phát hành mới.
- Khách hàng khi đăng ký thẻ tín dụng hay thẻ ghi nợ sẽ mặc định được cấp thẻ ảo trên ứng dụng ngân hàng di động, sẵn sàng để thanh toán trên cổng Paygate hoặc gắn vào các ví điện tử.
- Thẻ vật lý chỉ được in và gửi đi nếu khách hàng có yêu cầu riêng biệt và chấp nhận chịu phí in thẻ (trừ phân khúc khách hàng ưu tiên).
- Các dòng thẻ ảo được hỗ trợ khả năng thay đổi số CVV/CVC động (Dynamic CVV) định kỳ (mỗi 10 phút hoặc theo giao dịch) nhằm giảm thiểu tối đa nguy cơ gian lận trực tuyến.

<a id="NV2-008-D02"></a>
## Tích hợp công nghệ thanh toán sinh trắc học

Sản phẩm thẻ cao cấp tương lai sẽ được thiết kế hướng tới công nghệ thanh toán bằng sinh trắc học, cho phép khách hàng loại bỏ hoàn toàn mã PIN.
- Dòng thẻ vật lý tích hợp cảm biến vân tay (Biometric Card) dự kiến ra mắt giới hạn cho tập khách hàng doanh nghiệp (`ENT-SIM-xxx`) quản lý chi phí cấp cao và khách hàng cá nhân phân khúc Platinum trở lên.
- Quá trình xác thực sinh trắc học diễn ra cục bộ trên con chip của thẻ. Ngân hàng Mô phỏng KHÔNG lưu trữ hình ảnh vân tay gốc của khách hàng trên hệ thống CMS để đảm bảo các tiêu chuẩn bảo mật dữ liệu.
- Mọi trường hợp hư hỏng cảm biến hoặc cập nhật dấu vân tay mới đều yêu cầu khách hàng phải đến các điểm giao dịch để tạo yêu cầu đổi thẻ mới (`CARD_REPLACEMENT_REQUEST`).

<a id="NV2-008-D03"></a>
## Chính sách khách hàng thân thiết áp dụng cho dòng thẻ mới

Đồng bộ với Khung quản lý khách hàng hợp nhất (NV1-010), chương trình khách hàng thân thiết của thẻ tương lai cũng sẽ được nâng cấp.
- Điểm thưởng (Loyalty Points) và chính sách hoàn tiền (Cashback) sẽ được tính lũy kế tập trung trên `UNIFIED-SIM-ID` thay vì lưu trữ rời rạc trên từng thẻ như trước đây.
- Khách hàng có thể sử dụng điểm thưởng của thẻ tín dụng cá nhân để quy đổi thành phí thường niên hoặc cấn trừ vào phí quản lý tài khoản doanh nghiệp (nếu có ủy quyền chéo giữa tài khoản doanh nhân và tài khoản doanh nghiệp do họ làm NĐDPL).
- Mức độ tỷ lệ hoàn tiền hoặc tích lũy điểm sẽ chịu ảnh hưởng bởi yếu tố hành vi (như sử dụng thẻ ảo thay cho thẻ cứng, số lượng giao dịch contactless nhiều hơn giao dịch quẹt từ).

<a id="NV2-008-D04"></a>
## Đánh giá rủi ro và khung quản lý an toàn bảo mật dự kiến

Các sản phẩm thẻ mới mang đến nhiều tiện ích nhưng cũng đòi hỏi các tiêu chuẩn giám sát rủi ro nghiêm ngặt hơn:
- Hệ thống quản trị rủi ro mới sẽ áp dụng trí tuệ nhân tạo để nhận dạng các mẫu giao dịch bất thường (ví dụ: giao dịch ảo số lượng lớn qua thẻ Virtual liên tục trong vài giây).
- Khách hàng có quyền tự tinh chỉnh giới hạn rủi ro cá nhân ngay trên ứng dụng, như việc đặt giới hạn mức giao dịch tối đa một lần quẹt hay hạn chế giao dịch ngoại tệ. Tuy nhiên, các cài đặt này không được vượt quá giới hạn chuẩn của hạng thẻ theo quy định của ngân hàng.
- AI và Chatbot sẽ được sử dụng để tương tác và cảnh báo tức thời khi phát hiện giao dịch đáng ngờ, yêu cầu khách hàng xác nhận lại thay vì khóa thẻ cứng nhắc như quy trình cũ.
