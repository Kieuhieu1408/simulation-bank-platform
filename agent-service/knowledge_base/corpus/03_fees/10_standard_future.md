---
document_id: "NV3-010"
title: "Biểu phí tiêu chuẩn (Dự kiến 2027)"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV3"
document_type: "tariff"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-09-01"
effective_from: "2027-01-01"
effective_to: null
product_scope: ["merchant_acquiring"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "customer_operations"
related_documents: ["NV3-003", "NV3-004"]
supersedes: []
---

# Biểu phí tiêu chuẩn (Dự kiến 2027)

 <a id="NV3-010-D01"></a> 
## 1. Mục đích và đối tượng áp dụng

Ngân hàng Mô phỏng ban hành Biểu phí tiêu chuẩn (Standard Tariff) dự kiến cho năm 2027 nhằm đáp ứng lộ trình số hóa hệ thống thanh toán và tối ưu hóa chi phí vận hành cổng Paygate.
- **Đối tượng áp dụng:** Áp dụng cho mọi Đối tác Doanh nghiệp (Merchant) ký mới hoặc đang duy trì hợp đồng thả nổi (floating-rate) thuộc mọi phân khúc (bao gồm Bán lẻ, F&B, Giáo dục).
- **Trạng thái tài liệu:** Tài liệu này đã được ban hành chính thức (published) trong nội bộ, nhưng ngày bắt đầu có hiệu lực (effective date) là 01/01/2027. Mọi giao dịch phát sinh trước ngày này tiếp tục áp dụng các bảng phí năm 2026.

 <a id="NV3-010-D02"></a> 
## 2. Thay đổi cấu trúc tính phí 2027

Kể từ năm 2027, Ngân hàng Mô phỏng loại bỏ toàn bộ cơ chế tính phí lũy tiến (progressive) và đồng giá khối lượng (whole-volume) đối với bảng phí Standard. Cơ chế mới sẽ tính phí theo "Phí sàn cố định + Phần trăm giao dịch" (Flat fee + Percentage).
- **Ưu điểm:** Minh bạch hơn cho các giao dịch nhỏ lẻ, bảo vệ biên lợi nhuận của Merchant.
- **Áp dụng:** Không phân biệt khối lượng giao dịch trong tháng, không yêu cầu chốt số liệu cuối kỳ để hoàn cấn trừ (refund netting) như cơ chế whole-volume trước đây.

 <a id="NV3-010-D03"></a> 
## 3. Chi tiết Biểu phí Tiêu chuẩn áp dụng từ 2027

Mức phí được quy định cho từng nhóm ngành (MCC) như sau, biểu giá chưa bao gồm thuế VAT:

| Lĩnh vực (MCC) | Phí cố định / Giao dịch | Tỷ lệ phí / Giao dịch | Ghi chú |
| :--- | :--- | :--- | :--- |
| Bán lẻ (Retail) | 1,000 VNĐ | 1.2% | Áp dụng thay thế mức 1.8% của năm 2026. |
| Dịch vụ Ăn uống (F&B) | 500 VNĐ | 1.1% | Thấp hơn nhờ tần suất giao dịch cao. |
| Dịch vụ Khác | 2,000 VNĐ | 1.5% | |
| Giao dịch Quốc tế (Mọi ngành) | 0 VNĐ | 2.8% | Phụ thu thêm phí quy đổi ngoại tệ. |

*Ví dụ tính phí:* Một giao dịch Bán lẻ trị giá 500.000 VNĐ sẽ chịu mức phí là: 1,000 + (500,000 * 1.2%) = 7,000 VNĐ (chưa VAT).

 <a id="NV3-010-D04"></a> 
## 4. Nguyên tắc chuyển đổi hợp đồng

Việc chuyển đổi sang biểu phí 2027 tuân theo nguyên tắc của hệ thống:
- **Tự động áp dụng:** Các hợp đồng tính phí thả nổi (Floating-rate) đang dẫn chiếu đến Standard Tariff sẽ tự động áp dụng biểu phí này kể từ 00:00 ngày 01/01/2027 trên hệ thống Paygate, với điều kiện Merchant đã được thông báo trước tối thiểu 15 ngày. Nếu chưa nhận được thông báo đủ 15 ngày, biểu phí cũ vẫn tiếp tục được áp dụng cho đến khi thỏa mãn thời hạn này. GDV không cần tạo Proposal trên CMS.
- **Hợp đồng cố định (Fixed-rate):** Các hợp đồng như NV3-007 (nếu vẫn còn hiệu lực) không bị ảnh hưởng, giữ nguyên mức phí đã ký kết trừ khi có thỏa thuận hoặc phụ lục khác. Tuy nhiên, ngân hàng có quyền chấm dứt hợp đồng cố định để chuyển sang biểu phí thả nổi nếu merchant vi phạm các giới hạn về quản lý rủi ro (như tỷ lệ chargeback quá cao hoặc không đạt KPI doanh số).
- Việc tư vấn biểu phí 2027 cho Merchant cần được thực hiện từ Quý 4/2026. Chatbot và công cụ hỗ trợ tri thức cần chú ý kỹ `effective_from` và kiểm tra điều kiện "đã nhận thông báo đủ 15 ngày" khi trả lời các câu hỏi về phí hiện hành để tránh cung cấp nhầm thông tin tương lai cho giao dịch hiện tại.
