---
document_id: "NV3-011"
title: "Biểu phí quản lý tài khoản doanh nghiệp"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV3"
document_type: "tariff"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-01-01"
effective_from: "2026-01-01"
effective_to: null
product_scope: ["corporate_account"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "corporate_operations"
related_documents: []
supersedes: []
---

# Biểu phí quản lý tài khoản doanh nghiệp

 <a id="NV3-011-D01"></a> 
## 1. Phí mở và duy trì tài khoản

Tài liệu này quy định các loại phí liên quan đến việc quản lý tài khoản thanh toán của Khách hàng Doanh nghiệp (KHDN) tại Ngân hàng Mô phỏng. KHDN bao gồm cả các Đối tác sử dụng cổng Paygate.
- **Phí mở tài khoản:** Miễn phí cho mọi loại tài khoản VNĐ và ngoại tệ.
- **Số dư tối thiểu ban đầu:** Yêu cầu nộp 1.000.000 VNĐ (Một triệu đồng) đối với tài khoản VNĐ, hoặc 100 USD đối với tài khoản ngoại tệ. Khoản này không bị phong tỏa nhưng tài khoản cần duy trì số dư lớn hơn hoặc bằng mức này để không bị thu phí phạt.
- **Phí duy trì tài khoản hàng tháng:** 
  - Nếu số dư bình quân tháng >= 10.000.000 VNĐ: Miễn phí.
  - Nếu số dư bình quân tháng < 10.000.000 VNĐ: Thu 50.000 VNĐ/tháng/tài khoản. Phí này tự động cấn trừ vào ngày làm việc cuối cùng của tháng.

 <a id="NV3-011-D02"></a> 
## 2. Phí dịch vụ Ngân hàng số Doanh nghiệp

Hệ thống Ngân hàng số Doanh nghiệp (Corporate CMS/e-Banking) cho phép KHDN thực hiện các nghiệp vụ quản lý dòng tiền trực tuyến.
- **Phí đăng ký và cài đặt dịch vụ:** Miễn phí.
- **Phí cấp thiết bị bảo mật phần cứng (Hard Token):** 300.000 VNĐ/thiết bị/lần (Chỉ áp dụng khi khách hàng yêu cầu thay vì dùng Soft Token).
- **Phí duy trì dịch vụ Ngân hàng số:** 
  - Gói Cơ bản (Chỉ truy vấn và đối soát): 100.000 VNĐ/tháng.
  - Gói Nâng cao (Bao gồm chuyển khoản, chi lương, quản lý nhóm người dùng): 300.000 VNĐ/tháng.
- Các khoản phí trên chưa bao gồm 10% thuế VAT.

 <a id="NV3-011-D03"></a> 
## 3. Phí giao dịch thanh toán và chuyển khoản

Mức phí áp dụng cho từng giao dịch chuyển tiền khởi tạo từ tài khoản Doanh nghiệp:
- **Chuyển tiền nội bộ Ngân hàng Mô phỏng:** Miễn phí, bất kể số tiền giao dịch.
- **Chuyển tiền liên ngân hàng 24/7 (hệ thống NAPAS):** 
  - Giao dịch dưới 50.000.000 VNĐ: 5.000 VNĐ/giao dịch.
  - Giao dịch từ 50.000.000 VNĐ trở lên: Không áp dụng qua NAPAS 24/7, phải chuyển qua hệ thống CITAD.
- **Chuyển tiền liên ngân hàng qua hệ thống CITAD (thông thường):**
  - Mức phí: 0.02% giá trị giao dịch.
  - Phí tối thiểu: 10.000 VNĐ/giao dịch. Phí tối đa: 1.000.000 VNĐ/giao dịch.
- Phí dịch vụ chi hộ lương (Payroll): 2.000 VNĐ/tài khoản nhận lương nội bộ; 7.000 VNĐ/tài khoản nhận lương khác ngân hàng.

 <a id="NV3-011-D04"></a> 
## 4. Chính sách miễn giảm và ngoại lệ

- **Tích hợp cổng Paygate:** Đối với KHDN ký hợp đồng sử dụng cổng thanh toán Paygate (như hợp đồng NV3-007, NV3-008), Ngân hàng Mô phỏng miễn hoàn toàn phí duy trì tài khoản hàng tháng và phí duy trì Gói Cơ bản Ngân hàng số, bất kể số dư bình quân.
- **Trường hợp tài khoản không hoạt động (Dormant):** Nếu tài khoản không phát sinh giao dịch chủ động trong 12 tháng liên tục và số dư bằng 0, Ngân hàng có quyền tự động đóng tài khoản. Mọi thay đổi NĐDPL hay cập nhật hồ sơ doanh nghiệp (CIF) phải được duyệt bởi cấp Vùng và sẽ không làm sống lại (reactivate) tài khoản đã đóng nếu không có yêu cầu mở mới chính thức.
- Biểu phí này độc lập với Biểu phí giao dịch Paygate quy định tại miền NV3. Việc tính thuế VAT đối với phí tài khoản được thực hiện trên hóa đơn riêng biệt vào cuối tháng.
