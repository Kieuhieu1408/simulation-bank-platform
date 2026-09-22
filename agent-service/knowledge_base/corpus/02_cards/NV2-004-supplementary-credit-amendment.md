---
document_id: "NV2-004"
title: "Sửa đổi tuổi phát hành mới thẻ phụ tín dụng từ tháng 06 năm 2026"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV2"
document_type: "amendment"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-05-15"
effective_from: "2026-06-01"
effective_to: null
product_scope: ["supplementary_credit_card"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "card_operations"
related_documents: ["NV2-003", "NV2-005", "NV2-007"]
supersedes: []
---

# Sửa đổi tuổi phát hành mới thẻ phụ tín dụng từ tháng 06 năm 2026

Đây là sửa đổi một phần trong bộ chính sách của Ngân hàng Mô phỏng. Chỉ các nội dung được nêu cụ thể dưới đây thay đổi; không hủy toàn bộ quy định NV2-003.

<a id="NV2-004-D01"></a>
## D01. Điều khoản được sửa và phạm vi loại trừ

Sửa phần tuổi tối thiểu của **thẻ phụ tín dụng cá nhân** tại NV2-003-D02 đối với quyết định chấp thuận phát hành mới từ 2026-06-01, bao gồm cấp lại để gia hạn sang kỳ thẻ mới. Không thay tuổi tối thiểu của thẻ phụ ghi nợ, không sửa quy tắc hạn mức hoặc trách nhiệm thanh toán, không áp dụng cho người đại diện doanh nghiệp chỉ vì họ đang có quan hệ tín dụng.

Tài liệu không thay toàn bộ NV2-003, vì vậy trường `supersedes` để trống. Khi viện dẫn kết luận tuổi, dùng điều khoản sửa đổi cùng phạm vi sản phẩm; khi viện dẫn hồ sơ hoặc trách nhiệm nghĩa vụ vẫn dùng phần tương ứng của NV2-003.

<a id="NV2-004-D02"></a>
## D02. Điều kiện tuổi từ ngày hiệu lực

Người sử dụng thẻ phụ tín dụng phải **từ đủ 18 tuổi tại ngày chấp thuận phát hành mới**. Không dùng ngày nộp đề nghị để giữ điều kiện cũ nếu ngày chấp thuận đã từ 2026-06-01. Tuổi tính theo ngày sinh đã xác minh, theo ngày lịch tại Asia/Ho_Chi_Minh.

Chấp thuận của người đại diện hoặc cam kết trả nợ của chủ thẻ chính không phải ngoại lệ cho ngưỡng 18 tuổi. Nếu người dùng chưa đủ 18 tuổi, có thể giải thích phạm vi thẻ phụ ghi nợ theo NV2-003 khi khách quan tâm; không tự chuyển loại sản phẩm trong hồ sơ đã chuẩn bị.

| Tình huống mô phỏng | Kết luận về điều kiện tuổi |
| --- | --- |
| 16 tuổi, phát hành mới thẻ phụ tín dụng ngày 2026-09-22 | Chưa đạt tuổi tối thiểu |
| 16 tuổi, phát hành mới thẻ phụ ghi nợ ngày 2026-09-22 | Đạt ngưỡng tuổi gốc; vẫn cần hồ sơ đại diện và điều kiện khác |
| Vừa đủ 18 vào ngày chấp thuận thẻ phụ tín dụng | Đạt ngưỡng tuổi; chưa có nghĩa đã được phát hành |
| Chỉ biết “mở thẻ phụ cho con” | Chưa đủ dữ kiện về loại thẻ và tuổi tại ngày xét |

<a id="NV2-004-D03"></a>
## D03. Thẻ đã phát hành và chuyển tiếp

Thẻ phụ tín dụng đã phát hành hợp lệ trước 2026-06-01 cho người từ đủ 15 đến dưới 18 tuổi được tiếp tục sử dụng đến ngày hết hạn đã ghi nhận, nếu không có sự kiện phải dừng theo hợp đồng. Không tự khóa hoặc chấm dứt chỉ vì ngày hiệu lực của sửa đổi đã đến.

Cấp lại do mất/hỏng để thay phương tiện cùng hợp đồng, cùng người dùng và **không kéo dài ngày hết hạn** được xem là thay thế thẻ đã phát hành. Tuổi áp dụng theo trạng thái được giữ chuyển tiếp; vẫn kiểm tra định danh và điều kiện cấp lại. Nếu cấp lại kéo dài hạn thẻ hoặc mở hợp đồng khác thì được xem là quyết định phát hành cho kỳ mới, phải đáp ứng từ đủ 18 tuổi.

Thay đổi người dùng thẻ phụ luôn là hồ sơ phát hành mới, không dùng quyền chuyển tiếp của người dùng trước. Hệ thống cần lưu quyết định phát hành ban đầu và hạn thẻ để chứng minh trường hợp chuyển tiếp; nếu thiếu thì chuyển rà soát nghiệp vụ, không mặc định được hưởng điều kiện cũ.

<a id="NV2-004-D04"></a>
## D04. Hồ sơ giữ nguyên và thông tin bổ sung

Giữ yêu cầu `SUPPLEMENTARY_CARD_APPLICATION` và `SUPPLEMENTARY_HOLDER_ID_EVIDENCE` theo NV2-003. Với hồ sơ phát hành mới thẻ phụ tín dụng từ 2026-06-01, `GUARDIAN_CONSENT` không thay được bằng chứng đủ tuổi. Với cấp lại không kéo dài hạn cho thẻ được chuyển tiếp, ghi mã quyết định phát hành gốc, ngày hết hạn cũ và lý do cấp lại trong phần mô tả nghiệp vụ.

Phải phân biệt ngày chuẩn bị, ngày tiếp nhận, ngày chấp thuận và ngày thực thi phát hành. Chatbot không chọn ngày hiệu lực có lợi hơn để hợp thức hóa hồ sơ. Dữ liệu lịch sử dùng chứng minh sự kiện đã xảy ra, không làm thay đổi ngày chấp thuận mới.

<a id="NV2-004-D05"></a>
## D05. Cách kết luận khi đọc hai văn bản

Nếu đã biết đây là thẻ phụ tín dụng phát hành mới vào tháng 09/2026 cho người 16 tuổi thì đủ căn cứ kết luận chưa đạt tuổi tối thiểu, không cần coi hai tài liệu là mâu thuẫn chưa giải quyết. Nếu là thẻ ghi nợ thì tiếp tục dùng NV2-003. Nếu chưa biết loại thẻ, yêu cầu bổ sung loại sản phẩm trước.

Các phần về hạn mức, nghĩa vụ của chủ thẻ chính và quyền thao tác không thay đổi. Chatbot không phê duyệt ngoại lệ tuổi, không phát hành, không báo đã khóa thẻ chuyển tiếp. Trường hợp có văn bản riêng ngoài corpus được cung cấp nhưng chưa xác minh phải chuyển người có quyền rà soát nguồn trước khi sử dụng.
