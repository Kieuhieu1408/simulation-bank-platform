---
document_id: "NV7-001"
title: "Quy định đối soát Paygate xuyên biên giới"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV7"
document_type: "policy"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-01-01"
effective_from: "2026-01-01"
effective_to: null
product_scope: ["forex_settlement", "paygate"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "operations"
related_documents: ["NV1-004", "NV3-001"]
supersedes: []
---

# Quy định đối soát Paygate xuyên biên giới

 <a id="NV7-001-D01"></a> 
## 1. Nguyên tắc đối soát giao dịch ngoại tệ qua Paygate
Các giao dịch thanh toán từ thẻ quốc tế hoặc tài khoản ngoại tệ qua cổng thanh toán Paygate phải được đối soát theo tỷ giá hạch toán cuối ngày (EOD). Khác với đối soát Paygate nội địa thông thường theo [NV1-004], giao dịch xuyên biên giới yêu cầu Merchant phải chịu rủi ro tỷ giá nếu chọn mô hình T+3. Đối với các đơn vị như Công ty Minh An Thương mại (MRC-SIM-002), kỳ đối soát được thực hiện theo nguyên tắc chốt sổ vào lúc 16:30 giờ chiều mỗi ngày. 

 <a id="NV7-001-D02"></a> 
## 2. Quy trình xử lý chênh lệch tỷ giá T+N
Khi Merchant chọn nhận tiền VND nhưng giao dịch gốc bằng USD hoặc EUR, hệ thống Paygate sẽ tạm tính theo tỷ giá Spot tại thời điểm giao dịch (T+0). Khi quyết toán vào T+N (ví dụ T+3), nếu tỷ giá mua chuyển khoản của Ngân hàng Mô phỏng thay đổi quá 1.5% so với T+0, hệ thống tự động ghi nhận chênh lệch vào tài khoản chờ xử lý. Operations có trách nhiệm rà soát các khoản mục này và thực hiện cấn trừ vào chu kỳ thanh toán tiếp theo. 

 <a id="NV7-001-D03"></a> 
## 3. Miễn trừ phí quy đổi ngoại tệ cho VIP Merchants
Các Merchant đạt doanh số trên 10 triệu USD/năm (ví dụ Công ty Minh An - MRC-SIM-001) được miễn trừ hoàn toàn phí quy đổi ngoại tệ qua Paygate. Tuy nhiên, việc miễn trừ này chỉ áp dụng đối với các giao dịch thanh toán thẻ tín dụng, không áp dụng cho thanh toán qua ví điện tử quốc tế. Yêu cầu miễn trừ phải được thiết lập trên hệ thống CMS dưới dạng ngoại lệ hợp đồng, ghi đè lên mức phí tiêu chuẩn của [NV3]. Yêu cầu phải được cấp vùng phê duyệt (PENDING_APPROVAL sang APPROVED).

 <a id="NV7-001-D04"></a> 
## 4. Xử lý hoàn trả (Refund) có yếu tố ngoại hối
Khi Merchant phát sinh yêu cầu hoàn trả toàn phần hoặc một phần cho khách hàng nước ngoài, tỷ giá áp dụng cho giao dịch Refund là tỷ giá bán chuyển khoản tại thời điểm thực hiện Refund, không phải tỷ giá lúc giao dịch gốc (Purchase). Điều này có thể dẫn đến việc Merchant phải chịu khoản lỗ tỷ giá. Quy định này tương đồng với chính sách hoàn tiền nội địa của cổng thanh toán, nhưng có thêm bước kiểm tra hạn mức ngoại tệ gộp (Gross FX Limit) trước khi CMS sinh mã chuẩn bị xử lý.
