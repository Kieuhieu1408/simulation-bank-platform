---
document_id: "NV3-009"
title: "Phụ lục sửa đổi hợp đồng ENT-SIM-001"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV3"
document_type: "amendment"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-06-15"
effective_from: "2026-07-01"
effective_to: null
product_scope: ["merchant_acquiring"]
customer_scope: ["ENT-SIM-001"]
access_roles: ["gdv", "approver", "operations"]
unit_scope: ["UNIT-SIM-01"]
owner_role: "customer_operations"
related_documents: ["NV3-007", "NV3-006", "NV3-004"]
supersedes: []
---

# Phụ lục sửa đổi hợp đồng ENT-SIM-001

 <a id="NV3-009-D01"></a> 
## 1. Thông tin ký kết Phụ lục

Phụ lục này (Mã hiệu: AMD-ENT-SIM-001-01) là một phần không thể tách rời của Hợp đồng cung cấp dịch vụ cổng thanh toán số NV3-007, ký kết giữa Ngân hàng Mô phỏng và Đối tác:
- **Tên Doanh nghiệp:** Công ty Minh An
- **Mã hồ sơ (CIF):** ENT-SIM-001
- **Mã Merchant:** MRC-SIM-001 (F&B)

Phụ lục này không làm vô hiệu hóa các điều khoản khác trong hợp đồng gốc NV3-007, ngoại trừ những điểm được chỉ định sửa đổi trực tiếp tại Mục 2 của văn bản này.

 <a id="NV3-009-D02"></a> 
## 2. Nội dung sửa đổi cơ chế áp dụng khuyến mãi

Nhằm hỗ trợ Công ty Minh An trong giai đoạn thúc đẩy tiêu dùng ngành F&B, hai bên đồng ý sửa đổi cơ chế áp dụng phí giao dịch như sau:
- **Sửa đổi Điều 2 của Hợp đồng gốc (NV3-007):** Bãi bỏ quy định "KHÔNG tự động được áp dụng các Chương trình Khuyến mãi định kỳ". 
- **Quy định mới:** Từ ngày hiệu lực của Phụ lục này (01/07/2026), Công ty Minh An được phép tham gia và áp dụng các chiến dịch khuyến mãi (Campaign) dành riêng cho lĩnh vực F&B do Ngân hàng Mô phỏng ban hành.
- Khi tham gia khuyến mãi (ví dụ: CMP-FNB-2026), mức phí khuyến mãi sẽ **ghi đè** (override) mức phí cố định 1.45% của hợp đồng gốc, miễn là mức phí khuyến mãi thấp hơn. Sau khi kết thúc thời gian khuyến mãi, hệ thống Paygate tự động khôi phục mức phí 1.45% theo hợp đồng.

 <a id="NV3-009-D03"></a> 
## 3. Cập nhật yêu cầu duy trì doanh số

Đổi lại việc mở khóa tính năng áp dụng khuyến mãi trên Hợp đồng cố định, Công ty Minh An cam kết cập nhật định mức doanh số duy trì (KPI):
- **Cam kết mới:** Tổng doanh số thanh toán tối thiểu qua Paygate phải đạt 350.000.000 VNĐ (Ba trăm năm mươi triệu đồng) mỗi tháng (tăng so với mức 200 triệu VNĐ tại Hợp đồng gốc).
- Nếu Doanh nghiệp không đạt mức cam kết này trong 2 tháng liên tiếp (rút ngắn từ 3 tháng), Ngân hàng Mô phỏng sẽ chuyển hợp đồng về biểu phí F&B tiêu chuẩn (NV3-004) tại thời điểm đó.
- Phí xử lý giao dịch quốc tế giữ nguyên ở mức 2.5%, không được áp dụng ghi đè bởi bất kỳ chương trình khuyến mãi nội địa nào.

 <a id="NV3-009-D04"></a> 
## 4. Điều khoản thực thi trên hệ thống

- GDV trực thuộc đơn vị quản lý (UNIT-SIM-01) có trách nhiệm đệ trình yêu cầu cập nhật Phụ lục này lên hệ thống CMS để lưu vết hồ sơ.
- Việc phê duyệt phụ lục chỉ cần thực hiện bởi Cấp duyệt đơn vị (Unit Approver), không yêu cầu chuyển lên cấp Vùng, do NĐDPL không thay đổi và tài khoản đối soát giữ nguyên.
- Ngay khi trạng thái trên CMS chuyển sang `APPROVED`, bộ phận Operations sẽ tiếp nhận và tiến hành cập nhật logic tính phí vào Paygate (`EXECUTED`). Trạng thái phê duyệt của GDV và Approver không đồng nghĩa với việc phí đã thay đổi trên thực tế.
- Các điều khoản không được đề cập trong Phụ lục này vẫn giữ nguyên hiệu lực theo Hợp đồng NV3-007.
