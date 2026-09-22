---
document_id: "REF-003"
title: "Ghi chú Pháp lý: Thanh toán không dùng tiền mặt và Phí"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV3"
document_type: "reference"
organization: "Nhà nước"
source_type: "external_regulation"
source_refs: ["ND-52-2024", "TT-40-2024"]
status: "published"
published_at: "2024-07-01"
effective_from: "2024-07-01"
effective_to: null
product_scope: ["merchant_acquiring", "payment_fees"]
customer_scope: ["all"]
access_roles: ["knowledge_admin"]
unit_scope: ["all"]
owner_role: "fee_management"
related_documents: ["NV3-001", "NV1-003"]
supersedes: []
---

# Ghi chú Pháp lý: Thanh toán không dùng tiền mặt và Phí

> Mục đích: Bổ sung kiến thức nghiệp vụ cho mảng cổng thanh toán, Đơn vị chấp nhận thanh toán (Merchant) và cơ chế thu phí (NV3).

<a id="REF-003-D01"></a>
## D01. Thanh toán không dùng tiền mặt (Nghị định 52/2024/NĐ-CP)

- **Dịch vụ trung gian thanh toán:** Quy định chặt chẽ hơn về ví điện tử, dịch vụ chuyển mạch tài chính, dịch vụ bù trừ điện tử, cổng thanh toán điện tử (như Paygate), và dịch vụ hỗ trợ thu hộ/chi hộ.
- **Tiền điện tử (E-money):** Định nghĩa rõ tiền điện tử bao gồm ví điện tử, thẻ trả trước, và tiền di động (Mobile Money). Tiền điện tử phải được đảm bảo bằng 100% giá trị tiền thật lưu giữ tại tài khoản đảm bảo thanh toán của tổ chức cung ứng.
- **Ứng dụng vào Mô phỏng:** Trong nghiệp vụ Paygate, cần xác định rõ Merchant đang sử dụng cổng thanh toán cho giao dịch thẻ hay ví điện tử, từ đó áp biểu phí khác nhau.

<a id="REF-003-D02"></a>
## D02. Minh bạch Biểu phí Thanh toán

- **Thông báo biểu phí:** Các TCTD và tổ chức cung ứng dịch vụ TGTT phải niêm yết công khai biểu phí. Mọi thay đổi về phí (tăng phí, áp phí mới) phải được thông báo cho khách hàng tối thiểu 15 ngày trước khi áp dụng (qua email, SMS, website).
- **Ứng dụng vào Mô phỏng:** Quy tắc áp dụng biểu phí tương lai (NV3-010). Chatbot phải kiểm tra: "Khách hàng đã nhận thông báo đủ 15 ngày chưa?" trước khi xác nhận biểu phí mới có hiệu lực cho một hợp đồng merchant cũ.

<a id="REF-003-D03"></a>
## D03. Quản lý rủi ro Merchant (Đơn vị chấp nhận thanh toán)

- **Thẩm định hồ sơ Merchant (Onboarding):** Phải kiểm tra sự tồn tại hợp pháp của pháp nhân, ngành nghề kinh doanh không thuộc danh mục cấm, và có địa điểm kinh doanh rõ ràng. Cấm các merchant sử dụng cổng thanh toán cho cờ bạc, tiền ảo.
- **Rà soát định kỳ:** Ngân hàng phải định kỳ rà soát doanh số và tỷ lệ hoàn trả/tra soát (chargeback) của Merchant.
- **Ứng dụng vào Mô phỏng:** Giải thích tại sao một hợp đồng phí cố định siêu rẻ (NV3-007) yêu cầu KPI doanh số 350 triệu. Nếu merchant có dấu hiệu rủi ro cao hoặc chargeback lớn, ngân hàng có quyền chấm dứt Paygate.

<a id="REF-003-D04"></a>
## D04. Quy định về Giao dịch Đáng ngờ (AML)

- **Báo cáo giao dịch:** Các giao dịch điện tử, thanh toán qua cổng Paygate nếu có dấu hiệu bất thường (doanh số đột biến, chia nhỏ giao dịch liên tục) phải bị hold tiền và báo cáo Cục PCRT.
- **Ứng dụng vào Mô phỏng:** Gây nhiễu trong luồng đối soát, thanh toán (NV1-004). Tiền của merchant bị treo không phải do tính sai phí mà do vướng cờ AML (Anti-Money Laundering).
