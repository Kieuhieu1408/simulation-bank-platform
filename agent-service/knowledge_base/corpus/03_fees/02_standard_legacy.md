---
document_id: "NV3-002"
title: "Biểu phí Paygate Standard trước tháng 06/2026"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV3"
document_type: "tariff"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_adaptation"
source_refs: ["RAW-INT-03"]
status: "published"
published_at: "2024-12-15"
effective_from: "2025-01-01"
effective_to: "2026-06-01"
product_scope: ["paygate_standard"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "merchant_operations"
related_documents: ["NV3-001", "NV3-003", "NV3-004", "NV3-008"]
supersedes: []
---

# Biểu phí Paygate Standard trước tháng 06/2026

Đây là biểu phí mô phỏng được lưu để tra cứu nghiệp vụ quá khứ. Nhãn published không có nghĩa biểu phí còn hiệu lực tại ngày chốt corpus 2026-09-22.

<a id="NV3-002-D01"></a>
## 1. Hiệu lực và khách hàng áp dụng

Áp dụng Paygate Standard phân khúc retail/default cho các kỳ tháng từ 01/2025 đến hết 05/2026; khoảng hiệu lực [2025-01-01, 2026-06-01). Hồ sơ phải xác nhận không có mức fixed riêng hoặc hợp đồng floating dẫn chiếu biểu Standard hiện hành. Hợp đồng ghi mức cố định 1,5% không trở thành hợp đồng bậc thang chỉ vì biểu này cũng có mức 1,5%.

Từ tháng 06/2026, Standard retail/default chuyển sang NV3-003. Phân khúc F&B đã đăng ký có biểu NV3-004 riêng từ 01/2026; không dùng bảng Standard này cho F&B trong khoảng đó. Biểu này không cung cấp căn cứ F&B trước 2026: cần văn bản cùng thời kỳ. Mã sản phẩm GROWTH_PROGRESSIVE, tài khoản thanh toán doanh nghiệp và thẻ tín dụng không thuộc phạm vi.

<a id="NV3-002-D02"></a>
## 2. Bậc toàn doanh số tháng

B là doanh số thuần quyết toán của một merchant trong một tháng lịch, tính VND. Sau khi xác định B, chọn đúng một dòng rồi áp mức r lên toàn bộ B; không chia doanh số sang nhiều bậc.

| Doanh số thuần B trong tháng (VND) | Mức phí trước thuế r | Kiểu tính |
|---|---:|---|
| 0 ≤ B < 500.000.000 | 1,5% | Toàn doanh số |
| 500.000.000 ≤ B ≤ 2.000.000.000 | 1,2% | Toàn doanh số |
| B > 2.000.000.000 | 1,0% | Toàn doanh số |

Đúng 500.000.000 VND và đúng 2.000.000.000 VND đều thuộc mức 1,2%. Chỉ khi vượt 2.000.000.000 VND mới chọn 1,0%. Việc phí tuyệt đối giảm tại điểm chuyển bậc là đặc tính chủ ý của chính sách toàn doanh số này; không sửa thành lũy tiến để làm đường phí liên tục.

<a id="NV3-002-D03"></a>
## 3. Số liệu quyết toán và hoàn tiền

Kỳ tính từ 00:00 ngày đầu tháng đến trước 00:00 tháng kế tiếp theo Asia/Ho_Chi_Minh. Giao dịch tính theo ngày quyết toán thành công, không theo ngày tạo đơn. B gồm tiền khách thực trả cho hàng hóa, loại phí Paygate và trừ hoàn tiền đã xác nhận trong cùng kỳ của giao dịch cùng kỳ. Cộng các kênh POS, ONLINE thuộc chính merchant; không cộng merchant khác hoặc doanh số giao dịch chưa thành công.

B = 0 tạo phí bằng 0. Hoàn giao dịch kỳ đã chốt tạo điều chỉnh riêng theo tỷ lệ đã áp cho giao dịch gốc, không thay bậc quá khứ và không hạ doanh số kỳ hiện tại. Tổng hoàn phí không vượt khoản đã thu. Nếu thiếu lịch sử phân bổ phí gốc hoặc số tiền hoàn vượt giao dịch, phải đối soát trước khi báo số tiền cuối cùng; chatbot không tự tạo số âm hay ghi có.

<a id="NV3-002-D04"></a>
## 4. Thuế, làm tròn và đối chiếu

F = round_half_up(B × r, 0); thuế giả định T = round_half_up(F × 10%, 0); tổng phí gồm thuế = F + T, đơn vị VND. Mức 10% là tham số mô phỏng, không khẳng định nghĩa vụ thuế thực. Không có phí tối thiểu hoặc trần phí trong biểu này. Làm tròn nửa lên một lần trên phí tháng; không cộng các khoản phí giao dịch đã làm tròn trước.

Khi nhận bảng đối soát khác mức, kiểm tra trước tháng nghiệp vụ, mã sản phẩm, loại hợp đồng và phụ lục. Một màn hình hiển thị 1,8% ở tháng 09/2026 không sửa kết quả tháng 05/2026; ngược lại ký hợp đồng năm 2025 không đủ để giữ bảng cũ nếu hợp đồng floating. NV3-001 quy định trình tự chọn nguồn; NV3-003 quy định giai đoạn kế tiếp. Kết quả tính từ dữ liệu chưa chốt phải được ghi là ước tính.
