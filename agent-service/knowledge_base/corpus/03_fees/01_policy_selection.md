---
document_id: "NV3-001"
title: "Chọn chính sách và tính phí Paygate theo phạm vi"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV3"
document_type: "policy"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_adaptation"
source_refs: ["RAW-INT-03"]
status: "published"
published_at: "2024-12-15"
effective_from: "2025-01-01"
effective_to: null
product_scope: ["paygate_standard", "paygate_growth_progressive", "business_deposit_account"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "merchant_operations"
related_documents: ["NV3-002", "NV3-003", "NV3-004", "NV3-005", "NV3-006", "NV3-007", "NV3-008", "NV3-009", "NV3-010", "NV3-011", "NV2-002", "COM-002"]
supersedes: []
---

# Chọn chính sách và tính phí Paygate theo phạm vi

Đây là quy định tự xây dựng cho Ngân hàng Mô phỏng. Các tỷ lệ, thời hạn và thuế dưới đây là dữ liệu mô phỏng, không xác nhận chính sách tài chính thực tế.

<a id="NV3-001-D01"></a>
## 1. Nhận diện đối tượng và kỳ nghiệp vụ

Trước khi chọn phí, xác định pháp nhân, merchant_id, mã sản phẩm, phân khúc đã được duyệt, kênh POS hoặc ONLINE và tháng tính phí theo Asia/Ho_Chi_Minh. Tên doanh nghiệp gần giống không chứng minh cùng pháp nhân. Không cộng doanh số hai merchant kể cả chung chủ sở hữu. Tài liệu riêng chỉ sử dụng khi người tra cứu có cả quyền vai trò, đơn vị và khách hàng phù hợp.

Kỳ phí merchant là tháng lịch, tính từ 00:00 ngày đầu tháng đến trước 00:00 ngày đầu tháng sau. Thời điểm được tính vào kỳ là thời điểm giao dịch ghi nhận quyết toán thành công; không phải ngày khách đặt hàng, ngày ký hợp đồng hoặc ngày tra cứu. Kỳ chưa chốt chỉ cho kết quả ước tính.

<a id="NV3-001-D02"></a>
## 2. Trình tự chọn điều khoản

| Bước | Kiểm tra | Kết quả sử dụng |
|---|---|---|
| 1 | Hợp đồng đúng pháp nhân, merchant và thời hạn | Xác định fixed hay floating; không thấy hợp đồng thì chưa suy đoán loại |
| 2 | Phụ lục hiệu lực và điều khoản bị sửa | Chỉ thay đúng sản phẩm, kênh hoặc nội dung được chỉ định |
| 3 | Fixed còn hiệu lực | Dùng mức hợp đồng sau sửa đổi; bảng chung không tự thay thế |
| 4 | Floating hoặc hồ sơ đã xác nhận không có mức riêng | Chọn tariff đúng sản phẩm, phân khúc và kỳ |
| 5 | Campaign được hợp đồng cho phép, đủ điều kiện và có bằng chứng đăng ký | Áp dụng trong phạm vi campaign; phần còn lại giữ mức đã chọn |

Không có quy tắc “khuyến mãi đè lên tất cả”. Nếu hai phụ lục cùng phạm vi, cùng hiệu lực nhưng không xác định quan hệ thay thế, dừng tính phần xung đột và yêu cầu chủ sở hữu xác nhận. Không tự chọn văn bản có ngày tải lên mới hơn. Có hợp đồng nhưng thiếu trang phí cũng là thiếu căn cứ, không phải không có hợp đồng.

**Quy định về thông báo phí và quản lý rủi ro:**
- Các thay đổi về phí (tăng phí, áp dụng phí mới) phải được thông báo cho khách hàng tối thiểu 15 ngày trước khi áp dụng. Nếu chưa nhận được thông báo đủ 15 ngày, hợp đồng sẽ tiếp tục áp dụng biểu phí cũ cho đến khi đủ thời hạn.
- Ngân hàng định kỳ rà soát doanh số và tỷ lệ hoàn trả/tra soát (chargeback) của Merchant. Đối với các hợp đồng phí cố định (fixed-rate), nếu merchant vi phạm các giới hạn rủi ro hoặc không đạt KPI cam kết, ngân hàng có quyền chấm dứt thỏa thuận cước phí này, chuyển sang biểu phí thả nổi hoặc ngừng cung cấp dịch vụ Paygate.

<a id="NV3-001-D03"></a>
## 3. Cơ sở tính và hoàn tiền

B là tổng tiền thanh toán đã quyết toán trong kỳ, trừ hoàn tiền gắn với chính các giao dịch đó đã được xác nhận trong cùng kỳ. B tính bằng VND; gồm giá trị hàng hóa khách thực trả nhưng không gồm phí dịch vụ Paygate. Giao dịch thất bại, hủy trước quyết toán, khoản đặt cọc chưa quyết toán và doanh số merchant khác không thuộc B. B = 0 thì phí tỷ lệ và thuế bằng 0, không tự áp phí tối thiểu.

Hoàn tiền ở kỳ sau được ghi khoản điều chỉnh riêng theo mức phí và thuế của giao dịch gốc; không trừ vào B kỳ hiện tại và không tính lại bậc đã chốt. Chỉ hoàn phần phí tương ứng số tiền hoàn được xác nhận, lũy kế không vượt phí đã thu của giao dịch. Thiếu phân bổ phí gốc thì chưa tính khoản điều chỉnh. Dữ liệu hoàn vượt số tiền giao dịch hoặc B âm cần đối soát; không tự đổi thành 0 để che sai lệch.

<a id="NV3-001-D04"></a>
## 4. Công thức, thuế và làm tròn

Với mỗi nhóm g có cùng mức r và cùng căn cứ: Fg = round_half_up(Bg × r, 0); Tg = round_half_up(Fg × 10%, 0); tổng phải thu bằng tổng (Fg + Tg), tính VND. Thuế 10% chỉ là giả định bài mô phỏng. Làm tròn nửa lên tại đơn vị đồng; không làm tròn từng giao dịch trước khi cộng nhóm. Biểu toàn doanh số chọn một bậc rồi nhân toàn B. Biểu lũy tiến cộng các phần doanh số từng bậc trước khi làm tròn phí; không hoán đổi hai cách.

<a id="NV3-001-D05"></a>
## 5. Phạm vi tài liệu và kết quả giải thích

NV3-002 là Standard cũ; NV3-003 là Standard retail/default từ tháng 06/2026; NV3-004 là F&B năm 2026; NV3-005 chỉ dành sản phẩm GROWTH_PROGRESSIVE. NV3-010 là Standard tương lai. Phí tài khoản doanh nghiệp NV3-011 có cơ sở riêng; phí thẻ và tất toán trả góp thuộc NV2-002, không lấy tỷ lệ merchant để tính thay.

Kết quả tra cứu phải nêu kỳ, product_scope, nguồn đã chọn, nhóm doanh số, mức phí, thuế giả định và trạng thái ước tính hay đã chốt. Chatbot có thể giải thích hoặc chuẩn bị thông tin; không ghi nợ, thu phí, cập nhật hợp đồng hoặc tuyên bố đã điều chỉnh hệ thống.
