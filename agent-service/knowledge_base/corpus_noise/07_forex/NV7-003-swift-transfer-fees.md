---
document_id: "NV7-003"
title: "Biểu phí chuyển tiền quốc tế qua điện SWIFT"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV7"
document_type: "tariff"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-01-01"
effective_from: "2026-06-01"
effective_to: null
product_scope: ["swift_transfer", "fee_schedule"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "operations"
related_documents: ["NV3-002", "NV3-003"]
supersedes: []
---

# Biểu phí chuyển tiền quốc tế qua điện SWIFT

 <a id="NV7-003-D01"></a> 
## 1. Cấu trúc phí chuyển tiền SWIFT ra nước ngoài (OUR, SHA, BEN)
Giao dịch chuyển tiền quốc tế SWIFT tại Ngân hàng Mô phỏng áp dụng ba loại phí theo tùy chọn của người chuyển: OUR (người chuyển chịu mọi phí), SHA (chia sẻ phí), và BEN (người nhận chịu mọi phí). Không giống với phí chuyển khoản nội địa cố định, phí SWIFT bao gồm Phí xử lý giao dịch (0.2% số tiền chuyển, tối thiểu 10 USD, tối đa 200 USD) cộng với Điện phí SWIFT (5 USD/điện). Đối với các giao dịch cấn trừ đối soát Paygate, nếu chuyển trả tiền cho đối tác quốc tế, phí mặc định áp dụng là OUR.

 <a id="NV7-003-D02"></a> 
## 2. Ưu đãi phí SWIFT cho khách hàng F&B có doanh số cao
Từ ngày 2026-06-01, các khách hàng thuộc phân khúc F&B như Công ty Minh An (ENT-SIM-001) duy trì doanh số thanh toán Paygate trên 500 triệu VND/tháng sẽ được miễn hoàn toàn Điện phí SWIFT và giảm 50% Phí xử lý giao dịch khi chuyển tiền thanh toán nhượng quyền thương mại ra nước ngoài. Bảng phí ưu đãi này độc lập với bảng phí thẻ tín dụng theo [NV2] và chỉ được hệ thống tự động nhận diện nếu Merchant ID đang có trạng thái hoạt động trên cổng thanh toán.

 <a id="NV7-003-D03"></a> 
## 3. Phí tra soát và chỉnh sửa điện SWIFT
Khi có yêu cầu chỉnh sửa thông tin người nhận (Amend) hoặc tra soát dòng tiền (Trace) đối với điện SWIFT đã gửi, mức phí áp dụng là 30 USD/yêu cầu, cộng phí ngân hàng đại lý (nếu có). Trong trường hợp lỗi phát sinh từ hệ thống cổng thanh toán Paygate dẫn đến việc hạch toán sai tên người nhận, Ngân hàng Mô phỏng sẽ miễn trừ hoàn toàn phí tra soát này. Yêu cầu miễn trừ phải được thiết lập trạng thái PENDING_APPROVAL và do operations cấp trung tâm duyệt.

 <a id="NV7-003-D04"></a> 
## 4. Phí ghi có báo có tiền chuyển đến (Inward Remittance)
Đối với tiền chuyển từ nước ngoài về thông qua mạng lưới SWIFT, Ngân hàng Mô phỏng thu phí báo có 0.05% (tối thiểu 2 USD, tối đa 50 USD) trên số tiền ghi có. Nếu khoản tiền đến là từ việc hoàn trả (Refund) của giao dịch ngoại hối gốc do đối tác nước ngoài trả lại, khách hàng được miễn thu phí báo có nếu cung cấp đủ hồ sơ đối soát (reconciliation records) khớp với giao dịch ban đầu đã ghi nhận trên hệ thống.
