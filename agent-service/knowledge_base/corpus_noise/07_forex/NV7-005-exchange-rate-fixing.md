---
document_id: "NV7-005"
title: "Quy định chốt tỷ giá giao dịch ngoại hối"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV7"
document_type: "policy"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-02-15"
effective_from: "2026-03-01"
effective_to: null
product_scope: ["forex_trading", "exchange_rate"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "operations"
related_documents: []
supersedes: []
---

# Quy định chốt tỷ giá giao dịch ngoại hối

 <a id="NV7-005-D01"></a> 
## 1. Tỷ giá áp dụng cho giao dịch Spot và Forward
Tại Ngân hàng Mô phỏng, giao dịch ngoại hối giao ngay (Spot) áp dụng tỷ giá yết bảng tại đúng thời điểm hệ thống ghi nhận phê duyệt (APPROVED), không phải thời điểm khởi tạo (PENDING_APPROVAL). Giao dịch kỳ hạn (Forward) sẽ áp dụng tỷ giá kỳ hạn được tính toán dựa trên tỷ giá Spot thời điểm ký hợp đồng cộng với điểm kỳ hạn (Forward points). Đối soát giao dịch Merchant Paygate thanh toán bằng ngoại tệ sẽ áp dụng quy tắc tỷ giá Spot cuối ngày (EOD), chứ không phải tỷ giá thực tế từng giao dịch.

 <a id="NV7-005-D02"></a> 
## 2. Chốt tỷ giá ngoài giờ hành chính
Nếu khách hàng thực hiện chuyển khoản quốc tế thông qua ngân hàng số vào thứ Bảy hoặc Chủ Nhật, tỷ giá tạm tính trên ứng dụng là tỷ giá đóng cửa ngày làm việc gần nhất. Khi hệ thống xử lý giao dịch vào sáng thứ Hai kế tiếp, tỷ giá thực tế sẽ được chốt tại phiên mở cửa. Nếu tỷ giá thực tế chênh lệch quá 2% so với tỷ giá tạm tính, giao dịch sẽ tự động bị RETURNED_FOR_CORRECTION và yêu cầu khách hàng xác nhận lại mức tỷ giá mới.

 <a id="NV7-005-D03"></a> 
## 3. Khóa tỷ giá (Rate Lock) cho hợp đồng vay ngoại tệ
Doanh nghiệp (ENT-SIM-001) có hợp đồng vay bằng USD (Floating) có quyền mua quyền chọn khóa tỷ giá (Rate Lock) đối với khoản giải ngân chuẩn bị thanh toán cho nhà cung cấp nước ngoài. Khi sử dụng tính năng khóa tỷ giá, mức tỷ giá được cố định trong 48 giờ. Tuy nhiên, nếu trong 48 giờ đó, doanh nghiệp dùng khoản USD này để đối soát thanh toán Paygate thay vì trả nợ vay, hợp đồng khóa tỷ giá sẽ bị vô hiệu hóa và áp dụng lại biểu phí chuyển đổi tiêu chuẩn.

 <a id="NV7-005-D04"></a> 
## 4. Xung đột tỷ giá trong giao dịch hoàn tiền (Refund)
Trường hợp khách hàng sử dụng tính năng khóa tỷ giá nhưng giao dịch bị hủy và hoàn tiền (Refund), tỷ giá áp dụng để quy đổi lại VND sẽ là tỷ giá mua chuyển khoản tại thời điểm Refund hoặc tỷ giá khóa ban đầu, tùy thuộc vào mức nào thấp hơn (có lợi hơn cho ngân hàng). Quy định này gây khó hiểu do đối nghịch trực tiếp với nguyên tắc bảo vệ tỷ giá đối soát (Reconciliation Rate Protection) áp dụng cho Merchant VIP, nơi Merchant VIP được bồi hoàn chênh lệch tỷ giá bằng quỹ rủi ro của Ngân hàng.
