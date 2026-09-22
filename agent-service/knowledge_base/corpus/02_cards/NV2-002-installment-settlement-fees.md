---
document_id: "NV2-002"
title: "Tất toán trả góp thẻ cá nhân và phí theo mã gói"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV2"
document_type: "tariff"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_adaptation"
source_refs: ["RAW-INT-02"]
status: "published"
published_at: "2026-01-01"
effective_from: "2026-01-01"
effective_to: null
product_scope: ["INSTALLMENT_MERCHANT", "INSTALLMENT_CONVERSION", "INSTALLMENT_CASH"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "card_operations"
related_documents: ["NV2-001", "NV2-008"]
supersedes: []
---

# Tất toán trả góp thẻ cá nhân và phí theo mã gói

Biểu phí mô phỏng này cụ thể hóa khoảng phí trả góp trong dữ liệu đầu vào. Các mã gói, phép tính và tham số thuế là thiết kế của Ngân hàng Mô phỏng, không phải biểu phí của tổ chức thực.

<a id="NV2-002-D01"></a>
## D01. Phạm vi và thời điểm lựa chọn

Áp dụng cho tất toán trước hạn khoản trả góp trên thẻ tín dụng cá nhân được đăng ký trước ngày 2027-01-01. Khoản đăng ký trước mốc đó giữ biểu phí này kể cả khi tất toán từ năm 2027, trừ khi có thỏa thuận sửa đổi cụ thể được phép áp dụng và có bằng chứng. Khoản đăng ký từ ngày 2027-01-01 áp dụng NV2-008. Thời điểm đăng ký lấy từ kết quả xác lập gói trong hệ thống, không lấy ngày khách mua hàng nếu hai ngày khác nhau.

Tên gói có chữ merchant nói đến nơi chuyển đổi giao dịch thành trả góp cho chủ thẻ. Nó không phải phí dịch vụ doanh nghiệp chấp nhận thanh toán trên Paygate; không áp dụng mức phần trăm này cho doanh số merchant.

<a id="NV2-002-D02"></a>
## D02. Ba mức phí và căn cứ tính

| Mã gói | Tình huống đăng ký | Phí tất toán trước hạn | Căn cứ tính |
| --- | --- | --- | --- |
| INSTALLMENT_MERCHANT | Trả góp được xác lập tại đơn vị chấp nhận thanh toán | 2% | Gốc chưa thanh toán của khoản tại ngày chốt |
| INSTALLMENT_CONVERSION | Chuyển giao dịch mua hàng đã ghi sổ sang trả góp | 3% | Gốc chưa thanh toán của khoản tại ngày chốt |
| INSTALLMENT_CASH | Khoản ứng tiền được đăng ký trả góp trong gói riêng | 4% | Gốc chưa thanh toán của khoản tại ngày chốt |

Không tính phí trên giá trị giao dịch ban đầu, hạn mức tín dụng hoặc tổng doanh số thẻ. Không tự chọn mức thấp nhất khi chỉ biết khách hàng còn trả góp. Hạng thẻ cao hơn không tự tạo quyền miễn hoặc giảm phí. Khoản đã hoàn tất theo lịch không bị thu phí tất toán trước hạn lần nữa.

<a id="NV2-002-D03"></a>
## D03. Công thức, thuế và làm tròn

Với mỗi khoản `i`, gọi `G_i` là gốc chưa thanh toán tính bằng VND, `r_i` là tỷ lệ phù hợp mã gói. Phí `F_i = ROUND_HALF_UP(G_i × r_i, 0)` VND. Không có phí tối thiểu hoặc trần phí trong biểu này. Làm tròn từng khoản đến 1 VND rồi mới cộng, không cộng số chưa làm tròn để tính một khoản phí chung.

Mức phí đã bao gồm tham số thuế mô phỏng; không cộng VAT lần thứ hai. Đây là cách định nghĩa giá trong bài mô phỏng, không kết luận về chính sách thuế thực. Tổng số tiền để giải phóng nghĩa vụ bằng tổng gốc còn lại, phí tất toán từng khoản và các khoản lãi/phí khác đã xác nhận, trừ khoản tiền có sẵn thực sự được hệ thống cho phép cấn trừ. Không tự suy lãi tương lai phải thu khi không có căn cứ.

<a id="NV2-002-D04"></a>
## D04. Minh họa có đủ dữ kiện

Ba khoản đều đăng ký ngày 2026-05-10, tất toán ngày 2026-09-22, chưa phát sinh khoản lãi/phí khác và chưa có số tiền được cấn trừ:

| Khoản mô phỏng | Mã gói | Gốc còn lại (VND) | Tỷ lệ | Phí (VND) |
| --- | --- | ---: | ---: | ---: |
| INS-SIM-001 | INSTALLMENT_MERCHANT | 20.000.000 | 2% | 400.000 |
| INS-SIM-002 | INSTALLMENT_CONVERSION | 15.000.000 | 3% | 450.000 |
| INS-SIM-003 | INSTALLMENT_CASH | 10.000.000 | 4% | 400.000 |
| Tổng | Ba mã gói khác nhau | 45.000.000 | Không dùng một tỷ lệ chung | 1.250.000 |

Tổng cần thanh toán trong ví dụ là 46.250.000 VND. Đây là minh họa công thức, không phải số dư hiện tại của một khách hàng. Một khoản INSTALLMENT_CONVERSION còn 1.234.567 VND tạo phí 37.037 VND vì 37.037,01 được làm tròn đến đơn vị VND.

<a id="NV2-002-D05"></a>
## D05. Dữ kiện và chấp thuận

Cần có mã khoản, mã gói, thời điểm đăng ký, gốc còn lại tại ngày chốt, ngày dự kiến tất toán, nghĩa vụ bổ sung và tình trạng thỏa thuận riêng. Nếu chỉ có tổng gốc thì nêu rõ chưa đủ để xác định tổng phí, đồng thời yêu cầu phân bổ gốc theo khoản. Không đánh đồng thiếu dữ kiện tính toán với không có chính sách.

Hồ sơ `INSTALLMENT_SETTLEMENT_CONSENT` ghi từng khoản, công thức và tổng dự kiến để chủ thẻ xem xét. Số dự kiến phải được tính lại nếu ngày chốt hoặc gốc thay đổi trước thao tác nghiệp vụ. Chatbot không ghi nhận đã thu phí hoặc đã tất toán; kết quả phải đến từ hệ thống theo quy trình được phân quyền. Hoàn tất trả góp là một điều kiện của đóng thẻ, không thay thế các kiểm tra còn lại trong NV2-001.
