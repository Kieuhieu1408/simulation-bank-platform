---
document_id: "NV3-004"
title: "Biểu phí Paygate Standard cho doanh nghiệp F&B năm 2026"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV3"
document_type: "tariff"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2025-12-15"
effective_from: "2026-01-01"
effective_to: "2027-01-01"
product_scope: ["paygate_standard"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "merchant_operations"
related_documents: ["NV3-001", "NV3-003", "NV3-005", "NV3-006", "NV3-007", "NV3-009"]
supersedes: []
---

# Biểu phí Paygate Standard cho doanh nghiệp F&B năm 2026

Đây là bảng phí phân khúc mô phỏng, có hiệu lực cho các tháng lịch năm 2026. Ngành nghề suy đoán từ tên doanh nghiệp hoặc nội dung trao đổi không đủ xác nhận phân khúc F&B.

<a id="NV3-004-D01"></a>
## 1. Điều kiện áp dụng phân khúc

Merchant phải có Paygate Standard, phân khúc F&B được ghi nhận hiệu lực trong hồ sơ merchant, đồng thời không bị điều khoản fixed riêng điều chỉnh. Biểu áp cho tháng từ 01/2026 đến hết 12/2026, khoảng [2026-01-01, 2027-01-01). Mức Standard retail/default 1,8% từ tháng 06/2026 không thay bảng này. GROWTH_PROGRESSIVE là sản phẩm riêng; việc doanh nghiệp có nhà hàng không đủ chuyển gói đó sang bảng F&B.

Nếu phân khúc thay đổi giữa tháng, chưa được tự chọn bảng thuận lợi hơn hoặc chia tháng theo ngày. Cần quyết định hiệu lực chuyển gói theo hồ sơ merchant. Tài liệu này chỉ quy định chuyển phân khúc vào ngày đầu tháng kế tiếp sau khi được phê duyệt; quyết định ghi ngày khác phải được chủ sở hữu làm rõ trước khi tính. Không có căn cứ giá cho F&B năm 2027 trong tài liệu này.

<a id="NV3-004-D02"></a>
## 2. Bậc phí trên toàn doanh số

B là doanh số thuần tháng của một merchant, đơn vị VND. Chọn một bậc theo B sau khi loại hoàn cùng kỳ, rồi áp tỷ lệ tương ứng cho toàn B.

| Khoảng doanh số thuần tháng B (VND) | Tỷ lệ trước thuế | Phạm vi nhân tỷ lệ |
|---|---:|---|
| 0 ≤ B < 100.000.000 | 2,0% | Toàn bộ B |
| 100.000.000 ≤ B ≤ 500.000.000 | 1,7% | Toàn bộ B |
| B > 500.000.000 | 1,4% | Toàn bộ B |

Đúng 100 triệu và đúng 500 triệu cùng thuộc bậc 1,7%; trên 500 triệu mới chuyển 1,4%. Không nhân 100 triệu đầu với 2,0% rồi phần còn lại với 1,7%; đó là cách tính khác. Biểu không có phí tối thiểu hay trần phí; B = 0 tạo phí bằng 0.

<a id="NV3-004-D03"></a>
## 3. Dữ liệu doanh số và hoàn tiền

Kỳ tính theo thời điểm quyết toán thành công từ đầu tháng đến trước đầu tháng sau, múi giờ Asia/Ho_Chi_Minh. B cộng POS và ONLINE của chính merchant, gồm tiền hàng khách thanh toán và không gồm phí Paygate. Không cộng doanh số của chi nhánh có merchant_id riêng. Hoàn tiền được xác nhận cùng kỳ cho giao dịch cùng kỳ giảm B trước khi chọn bậc; khi đó bậc có thể thay đổi.

Hoàn ở tháng sau không làm giảm B tháng mới và không tính lại bậc đã chốt. Điều chỉnh phí hoàn dùng tỷ lệ đã phân bổ cho giao dịch gốc, giới hạn tổng điều chỉnh không vượt phí đã thu. Thiếu dữ liệu quyết toán hoặc lịch sử phân bổ phí thì yêu cầu đối soát trước khi tính chính thức. B âm hoặc hoàn vượt giao dịch không hợp lệ; không tự làm tròn thành 0.

<a id="NV3-004-D04"></a>
## 4. Thuế và quan hệ hợp đồng

F = round_half_up(B × r, 0), T = round_half_up(F × 10%, 0), tổng phí = F + T, đơn vị VND. Thuế 10% là giả định mô phỏng. Làm tròn nửa lên một lần trên số phí tháng và một lần trên thuế; không làm tròn từng giao dịch. Chưa chốt tháng thì doanh số và mức bậc chỉ là ước tính.

Công ty Minh An thuộc F&B không có nghĩa mọi khoản phí đều dùng bảng này: hợp đồng NV3-007 và phụ lục NV3-009 quy định mức fixed riêng cho ENT-SIM-001/MRC-SIM-001. Người không có quyền đọc hồ sơ riêng chỉ có thể giải thích nguyên tắc chung, không suy luận điều khoản từ tài liệu công khai. Campaign tháng 09/2026 NV3-006 chỉ dành retail Standard nên không áp cho F&B. Ghi rõ điều khoản chọn phí trong câu trả lời để tránh nhầm mức 1,4% F&B với mức 1,4% xuất hiện ở một phụ lục kênh ONLINE.
