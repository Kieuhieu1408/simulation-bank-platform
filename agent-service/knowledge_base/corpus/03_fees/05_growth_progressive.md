---
document_id: "NV3-005"
title: "Biểu phí Paygate Growth tính lũy tiến theo phần doanh số"
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
product_scope: ["paygate_growth_progressive"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "merchant_operations"
related_documents: ["NV3-001", "NV3-004", "NV3-006"]
supersedes: []
---

# Biểu phí Paygate Growth tính lũy tiến theo phần doanh số

Đây là sản phẩm mô phỏng độc lập, mã GROWTH_PROGRESSIVE. Tên gọi “bậc thang doanh số” của sản phẩm này không đồng nghĩa cách tính toàn doanh số trong Paygate Standard.

<a id="NV3-005-D01"></a>
## 1. Điều kiện sử dụng gói Growth

Áp dụng từ 01/2026 đến hết 12/2026 cho merchant có hợp đồng hoặc xác nhận chuyển gói GROWTH_PROGRESSIVE vào ngày đầu tháng. Không tự chuyển gói dựa trên doanh số cao, ngành nhà hàng hoặc mức phí thấp hơn. Không áp bảng này đồng thời với Standard trong cùng một tháng của cùng merchant. Nếu tài liệu chỉ ghi “Paygate”, cần xác nhận product_code trước khi tính.

Tài liệu điều chỉnh phí xử lý thanh toán; không quy định phí tài khoản doanh nghiệp, thẻ, trả góp hoặc phí thiết bị. Gói Growth không tham gia campaign Standard NV3-006. Hợp đồng fixed có mức khác vẫn cần xét theo NV3-001. Thay đổi gói có hiệu lực tháng sau phải lưu quyết định; việc GDV chuẩn bị hồ sơ chưa làm thay đổi gói đang dùng.

<a id="NV3-005-D02"></a>
## 2. Các phần doanh số và công thức lũy tiến

B là doanh số thuần tháng của chính merchant, tính VND. Chia B thành ba phần liên tiếp; tỷ lệ mỗi phần chỉ nhân phần doanh số tương ứng.

| Phần doanh số | Giá trị phần tính phí (VND) | Tỷ lệ trước thuế |
|---|---|---:|
| Phần đầu đến 100.000.000 VND | B1 = min(B, 100.000.000) | 2,0% |
| Phần trên 100.000.000 đến 500.000.000 VND | B2 = min(max(B − 100.000.000, 0), 400.000.000) | 1,7% |
| Phần trên 500.000.000 VND | B3 = max(B − 500.000.000, 0) | 1,4% |

Phí chưa làm tròn F* = B1 × 2,0% + B2 × 1,7% + B3 × 1,4%. Tại đúng 100 triệu, B2 = B3 = 0; tại đúng 500 triệu, B3 = 0. Mức 1,4% không áp toàn B khi B vượt 500 triệu. Tỷ lệ bình quân F*/B chỉ dùng để giải thích và không thay tỷ lệ biểu phí; khi B = 0 không tính tỷ lệ bình quân.

<a id="NV3-005-D03"></a>
## 3. Kỳ phí và điều chỉnh doanh số

Tháng tính theo Asia/Ho_Chi_Minh, từ 00:00 đầu tháng đến trước 00:00 đầu tháng kế tiếp. B gồm tiền hàng thanh toán đã quyết toán thành công, không gồm phí Paygate; trừ hoàn tiền xác nhận cùng kỳ cho giao dịch cùng kỳ. Cộng POS và ONLINE của merchant nhưng không gộp merchant khác. Doanh số chưa quyết toán hoặc giao dịch thất bại không được dùng đạt bậc thấp hơn.

Hoàn ở kỳ sau dùng tỷ lệ phí phân bổ bình quân của giao dịch trong kỳ gốc, lấy từ bảng chốt kỳ gốc; không lấy bậc cuối hoặc tỷ lệ của tháng hoàn. Phân bổ phí kỳ gốc cho giao dịch theo tỷ trọng số tiền quyết toán thuần, phần chênh làm tròn gán giao dịch có giá trị thuần lớn nhất, nếu bằng nhau theo transaction_id tăng dần. Không tính lại các phần doanh số kỳ đã chốt. Thiếu bảng phân bổ thì khoản điều chỉnh cần đối soát, không tự tính từ ảnh chụp số tổng.

<a id="NV3-005-D04"></a>
## 4. Làm tròn, thuế và kiểm soát

F = round_half_up(F*, 0); T = round_half_up(F × 10%, 0); tổng phí = F + T, đơn vị VND. Cộng các phần doanh số trước khi làm tròn F; không làm tròn từng phần rồi cộng. Thuế 10% chỉ là giả định mô phỏng. B = 0 thì F = T = 0; biểu không có phí tối thiểu hoặc trần phí. Hoàn lũy kế không vượt phí giao dịch đã được phân bổ, dữ liệu B âm hoặc hoàn vượt tiền gốc phải đối soát.

Biên bản giải thích phải ghi rõ “lũy tiến theo phần doanh số”, product_code, kỳ, B1/B2/B3 và số tiền trước thuế. Không suy từ bảng F&B có cùng ba tỷ lệ để kết luận Growth cũng áp toàn doanh số. Tài liệu hết hiệu lực sau 2026-12-31; thiếu bảng kỳ sau thì nêu thiếu căn cứ, không tự kéo dài chính sách.
