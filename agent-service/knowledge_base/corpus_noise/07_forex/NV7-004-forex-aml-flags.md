---
document_id: "NV7-004"
title: "Kiểm soát phòng chống rửa tiền (AML) trong giao dịch ngoại hối"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV7"
document_type: "policy"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-05-01"
effective_from: "2026-05-15"
effective_to: null
product_scope: ["forex", "aml_compliance"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "operations"
related_documents: []
supersedes: []
---

# Kiểm soát phòng chống rửa tiền (AML) trong giao dịch ngoại hối

 <a id="NV7-004-D01"></a> 
## 1. Cảnh báo AML đối với giao dịch thanh toán xuyên biên giới
Hệ thống Ngân hàng Mô phỏng thiết lập ngưỡng cảnh báo tự động (AML Flag) cho các giao dịch ngoại hối có tổng giá trị quy đổi vượt 50,000 USD/ngày đối với cá nhân, hoặc 200,000 USD/ngày đối với tổ chức. Cảnh báo này có nhiều điểm trùng lặp với bộ lọc chặn giao dịch nội địa đáng ngờ trên cổng Paygate, tuy nhiên yêu cầu cung cấp chứng từ chứng minh mục đích sử dụng ngoại tệ là bắt buộc. Hồ sơ sẽ bị chuyển sang trạng thái RETURNED_FOR_CORRECTION nếu GDV không đính kèm hóa đơn thương mại hợp lệ.

 <a id="NV7-004-D02"></a> 
## 2. Thời hạn tạm giữ và xử lý giao dịch khả nghi
Khi một giao dịch chuyển tiền SWIFT hoặc hoàn tiền Paygate bị gắn cờ AML, số tiền sẽ bị tạm giữ (hold) tối đa 72 giờ làm việc để rà soát. Tuy nhiên, nếu cờ AML được kích hoạt từ danh sách cấm vận quốc tế (Sanctions List), giao dịch sẽ bị đóng băng ngay lập tức với thời hạn xử lý kéo dài đến 30 ngày. Điều khoản 30 ngày này áp dụng độc lập và ghi đè lên SLA 72 giờ tiêu chuẩn của giao dịch ngoại hối nội bộ ngân hàng.

 <a id="NV7-004-D03"></a> 
## 3. Miễn trừ rà soát AML cho Merchant đối soát định kỳ
Đối với các đơn vị chấp nhận thanh toán như Công ty Minh An Thương mại (ENT-SIM-002) có lịch sử đối soát Paygate hoàn hảo trong 12 tháng liên tiếp, hệ thống cho phép nâng ngưỡng kích hoạt AML ngoại hối lên gấp 3 lần hạn mức thông thường. Quyền miễn trừ này chỉ có hiệu lực với luồng tiền thanh toán thương mại điện tử, không áp dụng cho luồng tiền chuyển vốn đầu tư trực tiếp (FDI) hay thanh toán cổ tức.

 <a id="NV7-004-D04"></a> 
## 4. Xử lý trùng lặp cảnh báo giữa Thẻ và AML Ngoại hối
Trong trường hợp một khách hàng cá nhân (CIF-SIM-001) dùng thẻ tín dụng quốc tế thanh toán và bị hệ thống thẻ nghi ngờ gian lận (Fraud hold) đồng thời bị hệ thống AML ngoại hối cảnh báo do giao dịch chuyển tiền ra nước ngoài, quy trình giải tỏa (release) phải được thực hiện tuần tự. Bộ phận Thẻ giải tỏa mã Fraud trước, sau đó bộ phận Tuân thủ mới thẩm định cờ AML. Thời hạn thẩm định tổng hợp không vượt quá 5 ngày làm việc dù thời hạn xử lý thẻ thông thường chỉ là 2 ngày.
