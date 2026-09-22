---
document_id: "NV2-005"
title: "Xử lý mất thẻ và phát hành lại"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV2"
document_type: "procedure"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-01-15"
effective_from: "2026-02-01"
effective_to: null
product_scope: ["credit_card", "debit_card"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "card_operations"
related_documents: ["NV2-001", "NV2-002"]
supersedes: []
---

# Xử lý mất thẻ và phát hành lại

Tài liệu này hướng dẫn quy trình nghiệp vụ khi khách hàng của Ngân hàng Mô phỏng thông báo mất thẻ (bao gồm cả thẻ tín dụng và thẻ ghi nợ) và quy định các điều kiện, thủ tục liên quan đến việc phát hành lại thẻ thay thế.

<a id="NV2-005-D01"></a>
## Tiếp nhận thông báo mất thẻ và khóa thẻ khẩn cấp

Khi khách hàng phát hiện mất thẻ, quy trình xử lý khẩn cấp được thực hiện như sau:
- Khách hàng có thể thông báo mất thẻ qua các kênh: Ứng dụng ngân hàng điện tử, tổng đài tự động hoặc trực tiếp tại quầy giao dịch của Ngân hàng Mô phỏng.
- Ngay khi nhận được yêu cầu, hệ thống thẻ tự động áp dụng trạng thái `LOCKED_LOST` cho số thẻ tương ứng. Đối với Chatbot, AI chỉ hướng dẫn quy trình hoặc tiếp nhận thông tin, KHÔNG được phép trực tiếp thực hiện lệnh khóa thẻ trên hệ thống mà phải tạo yêu cầu chuyển đến CMS.
- Trạng thái `LOCKED_LOST` có hiệu lực ngay lập tức (real-time). Mọi giao dịch thanh toán trực tuyến hoặc tại máy POS, ATM bằng thẻ này sẽ bị hệ thống Paygate từ chối với lý do "Thẻ đã báo mất".

<a id="NV2-005-D02"></a>
## Quy trình xử lý các giao dịch phát sinh trong thời gian báo mất

Việc xử lý rủi ro đối với các giao dịch phát sinh gần thời điểm báo mất được quy định chặt chẽ:
- Ngân hàng Mô phỏng không chịu trách nhiệm đối với bất kỳ giao dịch nào thực hiện thành công và được ghi nhận vào hệ thống trước thời điểm khách hàng chính thức thông báo mất thẻ (time-stamp trên hệ thống).
- Trong trường hợp khách hàng khiếu nại về các giao dịch xảy ra trước thời điểm báo mất, GDV phải hướng dẫn khách hàng tạo một "Yêu cầu tra soát khiếu nại" (Dispute Request).
- Khối Vận Hành (Operations) sẽ xem xét lịch sử giao dịch. Nếu giao dịch không có xác thực sinh trắc học hoặc OTP mà chỉ thực hiện qua dải từ hoặc chạm (contactless) dưới mức cho phép không cần PIN, ngân hàng sẽ phối hợp với tổ chức thẻ để rà soát nhưng không đảm bảo hoàn tiền cho khách hàng.

<a id="NV2-005-D03"></a>
## Điều kiện và thủ tục phát hành lại thẻ thay thế

Khách hàng có nhu cầu sử dụng lại dịch vụ sau khi báo mất thẻ cần thực hiện quy trình phát hành lại:
- Yêu cầu phát hành lại thẻ phải được khách hàng xác nhận qua ứng dụng ngân hàng hoặc ký biểu mẫu tại quầy. Hệ thống CMS tạo yêu cầu `CARD_REPLACEMENT_REQUEST`.
- Điều kiện phê duyệt: Thẻ cũ phải ở trạng thái đã khóa (`LOCKED_LOST` hoặc `CLOSED`). Đối với thẻ tín dụng, khách hàng không có nợ quá hạn từ nhóm 2 trở lên tại thời điểm yêu cầu.
- **Tiêu chuẩn công nghệ thẻ:** Mọi thẻ thay thế phát hành mới bắt buộc phải là thẻ có gắn chip. Ngân hàng Mô phỏng từ chối hoàn toàn việc phát hành hoặc cấp lại dưới dạng thẻ từ (magnetic stripe) nhằm đảm bảo tiêu chuẩn bảo mật. Nếu thẻ bị mất trước đó là thẻ từ, quá trình cấp lại sẽ đồng thời là quá trình chuyển đổi sang thẻ chip.
- Thẻ mới sẽ được tạo với một dãy số thẻ (PAN) hoàn toàn mới và mã bảo mật (CVV) mới. Tuy nhiên, hạn mức tín dụng và các số dư tài khoản liên kết (đối với thẻ ghi nợ) vẫn giữ nguyên theo hồ sơ định danh hợp nhất của khách hàng (`CIF-SIM-xxx`).

<a id="NV2-005-D04"></a>
## Phí phát hành lại thẻ và các trường hợp miễn giảm

Chi phí phát hành lại thẻ thay thế do mất thẻ được quy định tại biểu phí thẻ hiện hành của ngân hàng (miền NV3/NV4), tuân thủ các nguyên tắc sau:
- Mức phí tiêu chuẩn để in và phát hành thẻ vật lý mới được tính theo bảng phí chuẩn ban hành cho từng hạng thẻ (Standard, Gold, Platinum).
- Phí này được trích nợ tự động từ tài khoản thanh toán của khách hàng (đối với thẻ ghi nợ) hoặc cộng vào dư nợ kỳ tiếp theo (đối với thẻ tín dụng).
- Các trường hợp được miễn phí phát hành lại bao gồm: Khách hàng thuộc phân khúc khách hàng ưu tiên (VIP) hoặc khách hàng chuyển sang sử dụng thẻ ảo (Virtual Card) mà không yêu cầu phát hành thẻ cứng. Các ngoại lệ khác cần được Approver phê duyệt trên CMS.
