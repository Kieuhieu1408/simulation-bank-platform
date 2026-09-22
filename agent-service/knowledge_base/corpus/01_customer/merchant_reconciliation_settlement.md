---
document_id: "NV1-004"
title: "Đối soát giao dịch và thanh toán tiền cho merchant"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV1"
document_type: "procedure"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_adaptation"
source_refs: ["RAW-REG-02"]
status: "published"
published_at: "2026-01-01"
effective_from: "2026-01-01"
effective_to: null
product_scope: ["merchant_settlement"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "customer_operations"
related_documents: ["NV1-003", "NV1-005", "NV1-006", "NV1-008", "NV1-009", "NV1-010", "NV3-001"]
supersedes: []
---

# Đối soát giao dịch và thanh toán tiền cho merchant

> Quy trình vận hành giả lập của Ngân hàng Mô phỏng. Mọi thời gian, chu kỳ và ngưỡng trong tài liệu chỉ áp dụng cho gói Paygate thông thường trong thế giới mô phỏng.

<a id="NV1-004-D01"></a>
## D01. Ba đối tượng khác nhau trong đối soát

Giao dịch thanh toán ghi nhận tiền mua hàng; phiên thanh toán ghi khoản phải chuyển cho merchant; báo cáo phí ghi cơ sở và số phí của một kỳ. “Giao dịch thành công” không chứng minh tiền đã về tài khoản merchant, và “phiên đã chốt” không chứng minh người mua đã nhận hoàn tiền. Khi tra cứu phải ghi rõ đang hỏi đối tượng nào và khóa tương ứng: `transaction_id`, `settlement_batch_id` hoặc kỳ báo cáo.

Ngày giao dịch tính theo Asia/Ho_Chi_Minh. Chu kỳ tháng bắt đầu lúc 00:00 ngày đầu tháng, kết thúc trước 00:00 ngày đầu tháng kế tiếp. Ngày làm việc trong lịch mô phỏng này là thứ Hai đến thứ Sáu; chưa cấu hình ngày nghỉ bổ sung. Với thời hạn n ngày làm việc kể từ một sự kiện, không tính ngày xảy ra sự kiện. Khi hợp đồng quy định lịch khác, cần dẫn điều khoản và áp dụng đúng merchant đó, không trộn lịch của hai hợp đồng.

<a id="NV1-004-D02"></a>
## D02. Quy trình đối chiếu hằng ngày

| Bước | Bên thực hiện | Dữ liệu và kết quả cần ghi |
|---|---|---|
| 1 | Merchant có quyền xem báo cáo | Lấy danh sách giao dịch theo merchant và ngày; đối chiếu với đơn hàng nội bộ |
| 2 | Đầu mối đối soát được đăng ký | Gửi các dòng sai lệch: mã giao dịch, trạng thái hai bên, giá trị VND, lý do và chứng từ |
| 3 | Operations Paygate | Kiểm tra trạng thái, giao dịch trùng, thời điểm chuyển kỳ và tham chiếu hoàn tiền |
| 4 | Hai bên có thẩm quyền xác nhận | Lưu kết quả phù hợp hoặc trạng thái chưa giải quyết, mã điều chỉnh nếu có |

Không gửi lại toàn bộ danh sách khách mua hàng khi chỉ một giao dịch sai lệch. Người được quyền tải báo cáo chưa chắc được quyền xác nhận chênh lệch. Nếu không đủ quyền, bảng so sánh có thể chuẩn bị nhưng chưa coi là kết quả đã được merchant chấp nhận.

Khi hai bên chưa xác định nguyên nhân trước mốc chốt phiên, dữ liệu Paygate là căn cứ tạm thời cho phần không bị giữ hoặc tranh chấp. Sai lệch được theo dõi bằng hồ sơ riêng và điều chỉnh vào phiên tiếp theo sau khi có kết luận. Từ “tạm thời” không có nghĩa quyền khiếu nại đã chấm dứt.

<a id="NV1-004-D03"></a>
## D03. Điều kiện và lịch thanh toán

Đối với gói thông thường không có điều khoản riêng, phiên ngày D được xem xét thanh toán vào ngày làm việc tiếp theo sau D. Lịch này chỉ áp dụng phần giao dịch đã xác nhận đủ điều kiện, tài khoản nhận tiền hợp lệ và không có trạng thái chờ xác minh. Tổng tiền phải trả dương nhưng nhỏ hơn 50.000 VND được chuyển sang phiên kế tiếp; ngưỡng này là ngưỡng chuyển tiền, không phải doanh số tối thiểu để chọn bậc phí.

Giá trị thanh toán bằng tiền hàng đủ điều kiện, trừ tiền hoàn/bồi hoàn đã được xác nhận cần bù trừ, trừ phí và thuế đã xác nhận, cộng hoặc trừ các điều chỉnh có chứng từ. Doanh số tính phí không được mặc nhiên lấy bằng số tiền ròng thực trả. Cơ sở doanh số, hoàn tiền cùng kỳ, khoản hoàn kỳ trước, mức phí và làm tròn thuộc NV3-001 cùng hợp đồng liên quan.

Nếu số tiền ròng bằng hoặc nhỏ hơn 0 VND, ghi nghĩa vụ chuyển tiếp theo hợp đồng; không tạo giao dịch chuyển tiền âm. Nếu tài khoản đang thay đổi, tuân thủ mốc áp dụng và hướng xử lý từng phiên theo NV1-003. Thông tin người đại diện đổi CCCD không tự làm dừng mọi khoản thanh toán nếu không có điều kiện khác yêu cầu giữ.

<a id="NV1-004-D04"></a>
## D04. Đối soát tháng và các mốc phản hồi

Trong mô hình thông thường, operations chuẩn bị báo cáo tháng vào ngày làm việc thứ tư của tháng kế tiếp. Đầu mối được đăng ký phản hồi trong một ngày làm việc kể từ khi báo cáo được gửi qua kênh đã xác nhận. Nếu không phản hồi, báo cáo được ghi là “chưa có ý kiến”, không tự suy thành mọi giao dịch hợp lệ hoặc mọi khiếu nại đã được giải quyết.

Khi có chênh lệch, hai bên phối hợp xác định phương án trong hai ngày làm việc kể từ khi tiếp nhận đủ dữ kiện. Phần còn thiếu được ghi rõ thay vì tự đóng hồ sơ khi hết thời hạn. Báo cáo có thể chốt tạm phần đã xác nhận, còn dòng tranh chấp tiếp tục theo dõi. Điều chỉnh tháng đã chốt phải có tham chiếu giao dịch gốc và phiên điều chỉnh; không sửa âm thầm dữ liệu kỳ trước để làm mất lịch sử.

<a id="NV1-004-D05"></a>
## D05. Giữ khoản liên quan và xử lý giao dịch nghi ngờ

Một giao dịch chưa rõ trạng thái, trùng lặp hoặc đang bị khiếu nại có thể được đưa vào diện xác minh theo quyết định của operations có quyền. Đặc biệt, nếu giao dịch có dấu hiệu bất thường (doanh số đột biến, chia nhỏ giao dịch liên tục), hệ thống sẽ treo (hold) khoản tiền đó để rà soát rủi ro phòng chống rửa tiền (AML). Việc tiền của merchant bị giữ lại trong luồng đối soát, thanh toán lúc này có thể do vướng cảnh báo AML chứ không phải do lỗi tính sai phí hay sai lệch hệ thống.

Phạm vi giữ phải xác định bằng giao dịch và giá trị liên quan. Không giữ toàn bộ doanh số của pháp nhân chỉ vì một giao dịch có sai lệch thông thường, trừ khi có căn cứ riêng hoặc cảnh báo AML áp dụng cho toàn bộ hoạt động của merchant.

Merchant cung cấp chứng từ giao hàng, dịch vụ, thông tin đơn hàng và dữ liệu phản hồi theo yêu cầu trong ba ngày làm việc. Quá hạn thiếu chứng từ làm hồ sơ chuyển trạng thái cần xử lý, không tự biến giao dịch thành thất bại hoặc tự phát sinh bồi hoàn. Kết quả xác minh cần nêu nguồn dữ liệu, căn cứ, người quyết định và ảnh hưởng đến phiên thanh toán.

<a id="NV1-004-D06"></a>
## D06. Ranh giới trả lời và bàn giao

Trợ lý được giải thích lịch, phân biệt trạng thái, chuẩn bị bảng sai lệch và nêu chứng từ. Trợ lý không chốt đối soát, ghi có, tự tính lại số dư hoặc báo tiền đã chuyển nếu chưa có kết quả hệ thống. Nếu chỉ có snapshot NV1-008 đến NV1-010, trả lời đúng thời điểm ảnh chụp và nêu chưa có dữ liệu phiên hiện tại. Hoàn tiền thực hiện theo NV1-005; đổi đầu mối báo cáo theo NV1-006 không làm chuyển quyền xác nhận đối soát.
