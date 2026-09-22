---
document_id: "NV3-008"
title: "Hợp đồng phí thả nổi ENT-SIM-002"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV3"
document_type: "contract"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-02-10"
effective_from: "2026-03-01"
effective_to: null
product_scope: ["merchant_acquiring"]
customer_scope: ["ENT-SIM-002"]
access_roles: ["gdv", "approver", "operations"]
unit_scope: ["UNIT-SIM-01"]
owner_role: "customer_operations"
related_documents: ["NV3-002", "NV3-003", "NV3-006"]
supersedes: []
---

# Hợp đồng phí thả nổi ENT-SIM-002

 <a id="NV3-008-D01"></a> 
## 1. Thông tin pháp nhân và hệ thống thanh toán

Ngân hàng Mô phỏng đồng ý cung cấp dịch vụ chấp nhận thanh toán (Merchant Acquiring) qua cổng Paygate cho đối tác với thông tin chi tiết:
- **Tên Doanh nghiệp:** Công ty Minh An Thương mại
- **Mã hồ sơ Doanh nghiệp (CIF):** ENT-SIM-002
- **Mã điểm chấp nhận thanh toán (Merchant ID):** MRC-SIM-002
- **Lĩnh vực hoạt động (MCC):** Bán lẻ (Retail)
- **Đại diện pháp luật:** Theo hồ sơ cập nhật mới nhất lưu trữ trong hệ thống Ngân hàng Mô phỏng. Mặc dù có nét tương đồng về tên gọi với pháp nhân ENT-SIM-001, đây là một chủ thể pháp lý độc lập.

 <a id="NV3-008-D02"></a> 
## 2. Cơ chế biểu phí thả nổi (Floating-rate Mechanism)

Đây là hợp đồng áp dụng cơ chế tính phí thả nổi (Floating-rate Contract). Theo đó:
- Công ty Minh An Thương mại sẽ không đóng cứng một mức phí cố định nào trong toàn bộ vòng đời hợp đồng.
- Mức phí áp dụng cho từng giao dịch sẽ tự động dẫn chiếu đến **Biểu phí Tiêu chuẩn (Standard Tariff)** dành cho phân khúc Bán lẻ (Retail) do Ngân hàng Mô phỏng ban hành tại thời điểm giao dịch được thực hiện.
- Ví dụ theo lịch sử chính sách mô phỏng: Các giao dịch trước ngày 01/06/2026 sẽ tính theo bảng Standard cũ (lũy tiến toàn phần từ 1.0% đến 1.5%), trong khi các giao dịch từ ngày 01/06/2026 sẽ tự động cập nhật mức 1.8% theo bảng Standard mới. Thuế VAT 10% tính trên phần phí ngân hàng thu.

 <a id="NV3-008-D03"></a> 
## 3. Chính sách khuyến mãi và ghi đè tự động

Nhờ tính chất thả nổi, Hợp đồng này cho phép Công ty Minh An Thương mại tự động hưởng các chương trình khuyến mãi (Campaign) của Ngân hàng Mô phỏng mà không cần ký thêm phụ lục, miễn là:
- Chương trình khuyến mãi áp dụng cho đối tượng phân khúc Retail (như chiến dịch CMP-SUM-2026).
- Doanh nghiệp thực hiện thao tác đăng ký tham gia trên hệ thống Paygate nếu chính sách khuyến mãi có yêu cầu.
- Khi có xung đột giữa biểu phí chuẩn và chính sách khuyến mãi đã duyệt, hệ thống Paygate sẽ ưu tiên mức phí thấp nhất mang lại lợi ích cho Merchant theo nguyên tắc ghi đè quy định tại NV3-006.

 <a id="NV3-008-D04"></a> 
## 4. Quyền hạn cập nhật và bảo mật thông tin

- **Cập nhật hồ sơ:** Doanh nghiệp có trách nhiệm thông báo cho Ngân hàng Mô phỏng mọi thay đổi về Người đại diện pháp luật hoặc tài khoản nhận tiền (Settlement account). Mọi thay đổi tài khoản nhận tiền phải được ký xác nhận bởi NĐDPL hiện hành và được cấp duyệt chuyên trách (cấp Vùng) phê duyệt trên hệ thống CMS.
- **Tính trọn vẹn của dữ liệu:** Hợp đồng này là tài liệu nghiệp vụ đóng, chỉ GDV, Approver và bộ phận Operations mới có quyền truy xuất dữ liệu chi tiết của ENT-SIM-002. Quyền quản trị viên tri thức (Knowledge Admin) không được cấp quyền đọc các điều khoản nội dung này.
- **Trường hợp ngắt kết nối:** Ngân hàng Mô phỏng có quyền tạm dừng dịch vụ Paygate với thông báo trước 24 giờ nếu phát hiện MRC-SIM-002 phát sinh các giao dịch có dấu hiệu gian lận thẻ hoặc tỷ lệ chargeback vượt quá 1.5% tổng số lượng giao dịch trong 30 ngày liên tiếp.
