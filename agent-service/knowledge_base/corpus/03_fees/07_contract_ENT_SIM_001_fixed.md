---
document_id: "NV3-007"
title: "Hợp đồng phí cố định ENT-SIM-001"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV3"
document_type: "contract"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2025-10-15"
effective_from: "2025-11-01"
effective_to: null
product_scope: ["merchant_acquiring"]
customer_scope: ["ENT-SIM-001"]
access_roles: ["gdv", "approver", "operations"]
unit_scope: ["UNIT-SIM-01"]
owner_role: "customer_operations"
related_documents: ["NV3-009"]
supersedes: []
---

# Hợp đồng phí cố định ENT-SIM-001

 <a id="NV3-007-D01"></a> 
## 1. Thông tin pháp nhân và điểm chấp nhận thanh toán

Hợp đồng này được ký kết giữa Ngân hàng Mô phỏng và Đối tác Doanh nghiệp với các thông tin chi tiết như sau:
- **Tên Doanh nghiệp:** Công ty Minh An
- **Mã hồ sơ Doanh nghiệp (CIF):** ENT-SIM-001
- **Mã điểm chấp nhận thanh toán chính (Merchant ID):** MRC-SIM-001
- **Lĩnh vực hoạt động (MCC):** Dịch vụ Ăn uống (F&B)
- **Đại diện pháp luật:** Được xác thực qua hồ sơ ID-SIM-001 lưu trữ tại hệ thống nội bộ.

Hợp đồng cung cấp dịch vụ cổng thanh toán Paygate áp dụng cho toàn bộ các giao dịch phát sinh từ máy POS và kênh thanh toán trực tuyến thuộc sở hữu hợp pháp của Công ty Minh An đăng ký với Ngân hàng.

 <a id="NV3-007-D02"></a> 
## 2. Biểu phí giao dịch cố định (Fixed-rate)

Ngân hàng Mô phỏng đồng ý cung cấp cho Công ty Minh An mức phí giao dịch ưu đãi theo cơ chế cố định (fixed), không bị ảnh hưởng bởi biến động của biểu phí tiêu chuẩn (Standard Tariff). Các điều khoản cụ thể:

- **Phí xử lý giao dịch nội địa:** Áp dụng mức phí cố định 1.45% (Chưa bao gồm 10% VAT) trên tổng giá trị mỗi giao dịch thành công.
- **Phí xử lý giao dịch quốc tế:** Áp dụng mức phí 2.5% (Chưa bao gồm 10% VAT).
- **Cơ chế tính phí:** Phí được khấu trừ trực tiếp (Net-settlement) vào từng giao dịch trước khi Ngân hàng thanh toán (settlement) tiền về tài khoản của Doanh nghiệp. Nguyên tắc làm tròn áp dụng theo chuẩn toán học lên số nguyên đồng gần nhất.
- **Quy định ghi đè (Override):** Đây là hợp đồng phí cố định, do đó Công ty Minh An sẽ KHÔNG tự động được áp dụng các Chương trình Khuyến mãi (Campaign) định kỳ của Ngân hàng Mô phỏng, trừ khi hai bên ký kết Phụ lục hợp đồng (Amendment) đồng ý áp dụng.

 <a id="NV3-007-D03"></a> 
## 3. Quy định đối soát và thanh toán tiền hàng (Settlement)

- **Chu kỳ thanh toán:** T+1. Ngân hàng Mô phỏng thực hiện thanh toán tổng số tiền giao dịch hợp lệ sau khi đã trừ đi phí dịch vụ (Net-settlement) vào tài khoản thanh toán của Công ty Minh An tại Ngân hàng Mô phỏng. Việc thanh toán diễn ra vào ngày làm việc tiếp theo của Ngân hàng.
- **Đối soát:** Ngân hàng cung cấp báo cáo đối soát điện tử hàng ngày qua cổng thông tin dành cho Merchant. Công ty Minh An có trách nhiệm rà soát và phản hồi về bất kỳ sai sót nào trong vòng 3 ngày làm việc kể từ ngày phát sinh giao dịch.
- **Giao dịch tra soát (Chargeback/Refund):** Với các giao dịch khách hàng yêu cầu hoàn trả, Ngân hàng không hoàn lại khoản phí xử lý giao dịch ban đầu (1.45% hoặc 2.5%) đã thu.

 <a id="NV3-007-D04"></a> 
## 4. Điều khoản duy trì và chấm dứt hợp đồng

- **Hiệu lực:** Hợp đồng có hiệu lực kể từ ngày 01/11/2025 và kéo dài vô thời hạn cho đến khi một trong hai bên có thông báo chấm dứt bằng văn bản trước ít nhất 30 ngày.
- **Điều kiện duy trì mức phí cố định:** Để duy trì mức phí ưu đãi cố định 1.45%, Công ty Minh An cam kết duy trì tổng doanh số thanh toán qua cổng Paygate tối thiểu đạt 200.000.000 VNĐ (Hai trăm triệu đồng) mỗi tháng. 
- **Chế tài vi phạm cam kết:** Nếu Doanh nghiệp không đạt mức doanh số cam kết trong 3 tháng liên tiếp, Ngân hàng Mô phỏng có quyền đơn phương chấm dứt mức phí cố định và tự động chuyển Merchant về biểu phí tiêu chuẩn dành cho ngành F&B (Biểu phí NV3-004) tại thời điểm phát sinh. Quyết định chuyển đổi phải được phê duyệt bởi cấp Vùng và thông báo cho NĐDPL của Doanh nghiệp.
