---
document_id: "NV3-003"
title: "Biểu phí Paygate Standard retail và default từ tháng 06/2026"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV3"
document_type: "tariff"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-03-15"
effective_from: "2026-06-01"
effective_to: "2027-01-01"
product_scope: ["paygate_standard"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "merchant_operations"
related_documents: ["NV3-001", "NV3-002", "NV3-004", "NV3-006", "NV3-008", "NV3-010"]
supersedes: ["NV3-002"]
---

# Biểu phí Paygate Standard retail và default từ tháng 06/2026

Biểu phí của Ngân hàng Mô phỏng dành cho kỳ phí từ 06/2026 đến hết 12/2026. Các giá trị trong tài liệu là giả định nghiệp vụ phục vụ dự án.

<a id="NV3-003-D01"></a>
## 1. Phạm vi và quan hệ thay thế

Khoảng áp dụng là [2026-06-01, 2027-01-01), theo tháng quyết toán của Paygate Standard phân khúc retail/default. Tài liệu thay toàn bộ NV3-002 trong phạm vi Standard retail/default kể từ 2026-06-01; không thay biểu F&B NV3-004 hoặc hợp đồng fixed. Merchant không có hợp đồng mức riêng được áp khi hồ sơ đã xác nhận phân khúc default hoặc retail. Thiếu hợp đồng trong kết quả tìm kiếm không được coi là bằng chứng không có hợp đồng.

Merchant floating như đối tượng hợp đồng NV3-008 áp mức Standard của kỳ này theo điều kiện hợp đồng. Với merchant fixed, cần đọc điều khoản và phụ lục riêng trước khi dùng con số 1,8%. Cụm “mức Standard hiện hành” chỉ đề cập sản phẩm, phân khúc và thời kỳ ở đây; không bao gồm mọi merchant của Ngân hàng Mô phỏng.

<a id="NV3-003-D02"></a>
## 2. Tỷ lệ và kênh thanh toán

| Sản phẩm | Phân khúc đã xác nhận | Kênh | Phí trước thuế trên doanh số thuần tháng |
|---|---|---|---:|
| Paygate Standard | Retail/default | POS và ONLINE | 1,8% |

Mức 1,8% áp toàn doanh số, không phụ thuộc bậc 500 triệu hoặc 2 tỷ của biểu cũ. Không có mức phí tối thiểu, phí duy trì merchant hoặc trần thu trong tariff này. B bằng 0 thì F, T và tổng phí bằng 0. Không áp 1,8% cho dư nợ thẻ, khoản trả góp hoặc lệnh chuyển tiền từ tài khoản doanh nghiệp.

Nếu đủ điều kiện NV3-006, phần ONLINE được ưu đãi tách thành nhóm riêng; phần vượt hạn mức và toàn bộ POS vẫn dùng 1,8%. Tài liệu campaign không làm đổi mức nền trong hợp đồng hay kéo dài ưu đãi sang tháng khác.

<a id="NV3-003-D03"></a>
## 3. Doanh số thuần và thời điểm

B tính bằng VND, là tổng thanh toán quyết toán thành công trong một tháng lịch theo Asia/Ho_Chi_Minh, trừ hoàn tiền xác nhận trong cùng tháng của chính giao dịch tháng đó. Giá trị gồm tiền hàng khách thực trả, không gồm phí Paygate. Doanh số các kênh của một merchant được tổng hợp; merchant khác vẫn tách biệt dù cùng doanh nghiệp. Hủy trước quyết toán và giao dịch thất bại không tính doanh số.

Hoàn tiền của kỳ đã chốt được điều chỉnh theo mức phí từng giao dịch gốc, không lấy 1,8% mặc định cho giao dịch vốn đã hưởng campaign. Không trừ khoản hoàn cũ vào B tháng mới, không tính lại bậc hoặc ưu đãi kỳ cũ. Nếu chưa có phân bổ phí của giao dịch, cần đối soát. Khoản điều chỉnh lũy kế không vượt phí đã thu tương ứng; hoàn vượt số tiền gốc là dữ liệu lỗi cần làm rõ.

<a id="NV3-003-D04"></a>
## 4. Công thức và chứng từ phí

Với nhóm áp mức r, Fg = round_half_up(Bg × r, 0), Tg = round_half_up(Fg × 10%, 0); tổng phải thu là tổng Fg + Tg theo từng nhóm, tính bằng VND. Thuế 10% và cách làm tròn nửa lên chỉ là thiết lập bài mô phỏng. Khi không có ưu đãi hoặc mức riêng, toàn tháng là một nhóm r = 1,8%. Không làm tròn phí từng giao dịch trước khi tổng hợp tháng.

Bảng tính cần lưu merchant_id, tháng, kênh nếu có ưu đãi, căn cứ hợp đồng, doanh số trước và sau hoàn cùng kỳ, mức trước thuế và tiền thuế. Biểu phí tương lai NV3-010 có ngày công bố trước ngày áp dụng; không dùng mức đó cho tháng 09/2026. Từ kỳ 01/2027, kiểm tra NV3-010 và điều kiện hợp đồng. Chatbot chỉ giải thích kết quả; thông báo đã thu hoặc hoàn phí chỉ có giá trị khi do adapter mô phỏng xác nhận.
