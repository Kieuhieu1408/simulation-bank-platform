---
document_id: "NV3-006"
title: "Chính sách khuyến mãi và nguyên tắc ghi đè"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV3"
document_type: "policy"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_adaptation"
source_refs: ["RAW-INT-03"]
status: "published"
published_at: "2026-02-15"
effective_from: "2026-03-01"
effective_to: null
product_scope: ["merchant_acquiring"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "customer_operations"
related_documents: ["NV3-001", "NV3-002", "NV3-003"]
supersedes: []
---

# Chính sách khuyến mãi và nguyên tắc ghi đè

 <a id="NV3-006-D01"></a> 
## 1. Nguyên tắc áp dụng ghi đè khuyến mãi

Chính sách khuyến mãi (Campaign) do Ngân hàng Mô phỏng ban hành chỉ áp dụng ghi đè lên Biểu phí tiêu chuẩn và Hợp đồng của Đơn vị chấp nhận thanh toán (Merchant) khi thỏa mãn toàn bộ các điều kiện sau:
- Chính sách khuyến mãi đang trong thời gian hiệu lực.
- Merchant thuộc đối tượng (customer_scope) hoặc nhóm sản phẩm (product_scope) của khuyến mãi.
- Merchant phải đăng ký tham gia chương trình qua hệ thống Paygate và được phê duyệt bởi cấp có thẩm quyền, trừ trường hợp khuyến mãi ghi rõ "áp dụng tự động".
- Hợp đồng của Merchant không có điều khoản "Không áp dụng ghi đè khuyến mãi định kỳ" (No-campaign-override).

Trường hợp một giao dịch thỏa mãn nhiều chương trình khuyến mãi cùng lúc, hệ thống Paygate sẽ ưu tiên áp dụng chương trình có mức phí giao dịch thấp nhất cho Merchant. Thuế VAT luôn được tính dựa trên mức phí cuối cùng sau khuyến mãi.

 <a id="NV3-006-D02"></a> 
## 2. Các chiến dịch khuyến mãi đang hiệu lực trong năm 2026

Dưới đây là danh sách các chiến dịch khuyến mãi trọng điểm hiện hành:

1. **Chiến dịch "Bứt phá Doanh thu mùa Hè" (Mã: CMP-SUM-2026)**
   - **Thời gian áp dụng**: 01/05/2026 đến hết 31/08/2026.
   - **Phạm vi áp dụng**: Merchant thuộc lĩnh vực Bán lẻ (Retail).
   - **Mức phí ưu đãi**: Giảm 0.2% trên mức phí giao dịch tiêu chuẩn hiện hành cho các giao dịch nội địa, mức phí sàn sau giảm không thấp hơn 1.0%. Không áp dụng ghi đè cho hợp đồng fixed-rate (cố định).

2. **Chiến dịch "Đồng hành F&B" (Mã: CMP-FNB-2026)**
   - **Thời gian áp dụng**: 15/09/2026 đến hết 31/12/2026.
   - **Phạm vi áp dụng**: Các Merchant thuộc lĩnh vực F&B có doanh số tháng liền trước đạt trên 500 triệu VNĐ.
   - **Mức phí ưu đãi**: Áp dụng mức đồng giá 1.3% (chưa VAT) cho toàn bộ doanh số (whole-volume). Chiến dịch này được phép ghi đè mọi hợp đồng floating-rate.

 <a id="NV3-006-D03"></a> 
## 3. Xử lý xung đột giữa Hợp đồng và Chính sách khuyến mãi

Trong thực tế vận hành, các xung đột có thể xảy ra giữa mức phí theo hợp đồng và mức phí khuyến mãi. Các nguyên tắc xử lý:
- **Đối với hợp đồng cố định (Fixed-rate Contracts):** Khuyến mãi chỉ được phép ghi đè (override) nếu điều khoản hợp đồng hoặc phụ lục có ghi rõ "Đồng ý áp dụng các chính sách ưu đãi linh hoạt từ Ngân hàng Mô phỏng". Nếu không có điều khoản này, hợp đồng giữ nguyên mức đã ký, không tự động hưởng khuyến mãi.
- **Đối với hợp đồng thả nổi (Floating-rate Contracts):** Hợp đồng floating luôn dẫn chiếu bảng phí tại thời điểm phát sinh giao dịch. Khuyến mãi sẽ tự động ghi đè bảng phí chuẩn (Standard Tariff) hiện hành, qua đó áp dụng gián tiếp lên các hợp đồng floating, miễn là Merchant thỏa mãn điều kiện khuyến mãi (như lĩnh vực hoặc doanh số).
- Nếu có sự cố hệ thống Paygate khiến phí bị tính sai lệch với quy định ghi đè, GDV tiếp nhận yêu cầu từ Merchant và chuẩn bị hồ sơ Tra soát. Phê duyệt hoàn phí (Refund) phải do cấp Vùng quyết định.

 <a id="NV3-006-D04"></a> 
## 4. Quy định về đăng ký và ghi nhận hệ thống

Để khuyến mãi có hiệu lực, quy trình ghi nhận trên hệ thống Paygate phải tuân thủ:
- **Bước 1:** GDV nhận yêu cầu đăng ký tham gia chiến dịch từ NĐDPL hoặc Người được ủy quyền hợp lệ của Doanh nghiệp. Yêu cầu có thể gửi qua email định danh hoặc văn bản.
- **Bước 2:** GDV khởi tạo yêu cầu gán mã chiến dịch (Ví dụ: `CMP-SUM-2026`) vào hồ sơ Merchant trên CMS. Trạng thái chuyển thành `PENDING_APPROVAL`.
- **Bước 3:** Cấp duyệt (Approver) kiểm tra điều kiện chiến dịch. Nếu hợp lệ, chuyển trạng thái `APPROVED`. Nếu sai lệch thông tin hoặc thiếu chữ ký, chọn `RETURNED_FOR_CORRECTION`. Trả lại giữ nguyên `proposal_id` và tăng `version` khi GDV cập nhật lại.
- **Lưu ý nghiệp vụ:** Trạng thái `APPROVED` trên CMS chỉ mang ý nghĩa phê duyệt nghiệp vụ, không phải là `EXECUTED` (Đã cập nhật vào Paygate). Chăm sóc khách hàng (Chatbot/GDV) không được tự ý báo với khách hàng rằng hệ thống đã tính phí mới cho đến khi có xác nhận `EXECUTED` từ bộ phận Operations. Mọi ngoại lệ do hệ thống trễ hẹn cập nhật được tính hồi tố (backdate) từ ngày phê duyệt.
