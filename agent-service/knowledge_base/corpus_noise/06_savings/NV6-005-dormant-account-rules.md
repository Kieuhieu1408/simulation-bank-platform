---
document_id: "NV6-005"
title: "Xử lý tài khoản tiền gửi ngủ quên"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV6"
document_type: "policy"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_adaptation"
source_refs: []
status: "published"
published_at: "2026-05-01"
effective_from: "2026-05-01"
effective_to: null
product_scope: ["savings", "current_account"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "customer_operations"
related_documents: []
supersedes: []
---

# Xử lý tài khoản tiền gửi ngủ quên

 <a id="NV6-005-D01"></a> 
## 1. Khái niệm tài khoản ngủ quên (Dormant Account)

Tài khoản tiền gửi tiết kiệm (hoặc thanh toán liên kết) được coi là ngủ quên khi không phát sinh bất kỳ giao dịch chủ động nào từ phía khách hàng (nộp/rút) trong vòng 24 tháng liên tục. Quá trình xử lý tài khoản này độc lập với nghiệp vụ đóng thẻ tín dụng hoặc hủy thẻ do không sử dụng.

 <a id="NV6-005-D02"></a> 
## 2. Phí quản lý tài khoản ngủ quên

Sau thời gian 24 tháng, tài khoản sẽ bắt đầu bị tính phí duy trì tài khoản ngủ quên mức 20,000 VND/tháng. Phí này sẽ tự động trừ vào số dư tài khoản đến khi số dư bằng 0. Phí này không áp dụng chung với biểu phí merchant tiêu chuẩn hay các loại account closure fee.

 <a id="NV6-005-D03"></a> 
## 3. Kích hoạt lại tài khoản

Khách hàng có thể kích hoạt lại tài khoản ngủ quên bằng cách thực hiện một giao dịch gửi tiền hoặc rút tiền hợp lệ qua cổng Paygate hoặc tại quầy. Không yêu cầu mở thẻ mới hay cấp lại mã PIN như đối với nghiệp vụ thẻ. Mọi khoản phí đã thu trước đó sẽ không được hoàn lại (no refund).

 <a id="NV6-005-D04"></a> 
## 4. Ngoại lệ đối với doanh nghiệp

Đối với tài khoản tiền gửi của doanh nghiệp như `Công ty Minh An` (ENT-SIM-001) có kèm hợp đồng ký quỹ, tài khoản không được phân loại là ngủ quên trong suốt thời gian hiệu lực của hợp đồng ký quỹ, bất kể việc không phát sinh giao dịch.
