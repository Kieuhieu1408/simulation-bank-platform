---
document_id: "NV1-005"
title: "Hoàn tiền, bồi hoàn và chấm dứt dịch vụ merchant"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV1"
document_type: "policy"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_adaptation"
source_refs: ["RAW-REG-02"]
status: "published"
published_at: "2026-01-01"
effective_from: "2026-01-01"
effective_to: null
product_scope: ["merchant_refund", "merchant_termination"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "customer_operations"
related_documents: ["NV1-002", "NV1-003", "NV1-004", "NV2-001", "NV2-006", "NV3-001"]
supersedes: []
---

# Hoàn tiền, bồi hoàn và chấm dứt dịch vụ merchant

> Chính sách giả lập của Ngân hàng Mô phỏng cho hoàn tiền Paygate. Đây là quy tắc dữ liệu của dự án, không phải cam kết xử lý thanh toán thực tế.

<a id="NV1-005-D01"></a>
## D01. Phân biệt hoàn tiền, hủy giao dịch và bồi hoàn

Hoàn tiền là yêu cầu của merchant hoàn lại một phần hoặc toàn bộ giá trị giao dịch mua hàng đã thành công. Hủy giao dịch chưa chốt chỉ áp dụng khi hệ thống nghiệp vụ xác nhận giao dịch còn hỗ trợ hủy. Bồi hoàn là quy trình xử lý nghĩa vụ sau tra soát hoặc tranh chấp, cần quyết định và căn cứ riêng. Không đổi tên bồi hoàn thành hoàn tiền để tránh hồ sơ giải trình.

Khi người mua nói đã bị trừ tiền nhưng Paygate không có giao dịch thành công, trước hết tra soát trạng thái và chứng từ theo NV1-004. Không tạo một giao dịch mua hàng mới chỉ để có mã tham chiếu hoàn tiền. Trạng thái trên thiết bị tại cửa hàng, trạng thái đơn hàng và trạng thái thanh toán có thể khác nhau; cần xác định nguồn nào đang được mô tả.

<a id="NV1-005-D02"></a>
## D02. Điều kiện tiếp nhận yêu cầu hoàn tiền

Mỗi yêu cầu phải có merchant, mã giao dịch gốc, số tiền VND, lý do, chứng từ liên quan đến đơn hàng, người yêu cầu và phạm vi quyền còn hiệu lực. Số tiền đề nghị phải lớn hơn 0 VND. Tổng tiền hoàn đã hoàn tất cộng tiền hoàn đang xử lý cộng yêu cầu mới không được vượt tiền hàng gốc trừ phần bồi hoàn đã xác nhận cho cùng giao dịch. Phần đang xử lý được giữ trong phép kiểm tra để tránh hai yêu cầu song song hoàn trùng.

Trong gói Paygate thông thường của corpus này, merchant được đề nghị hoàn trong 90 ngày lịch kể từ ngày giao dịch thành công; ngày giao dịch là ngày 0 và yêu cầu trong ngày 90 vẫn thuộc cửa sổ tiếp nhận. Sau đó cần operations kiểm tra cơ chế xử lý ngoại lệ và căn cứ hợp đồng, không hứa tự động từ chối hoặc hoàn được. Cửa sổ tiếp nhận hoàn tiền không phải thời hạn bảo hành hàng hóa hay thời hạn khiếu nại của mọi sản phẩm.

| Kiểm tra | Đủ điều kiện tiếp tục | Trường hợp phải bổ sung hoặc chuyển xử lý |
|---|---|---|
| Giao dịch gốc | Thành công, thuộc đúng merchant | Không tìm thấy, chưa rõ trạng thái hoặc thuộc merchant khác |
| Giá trị | Còn phần tiền hàng chưa hoàn/bồi hoàn | Vượt dư còn lại, trùng yêu cầu hoặc thiếu trạng thái yêu cầu trước |
| Quyền người yêu cầu | Có quyền đề nghị hoàn, còn hiệu lực | Chỉ có quyền tải báo cáo, đã bị thu hồi hoặc hết hạn |
| Phương thức | Trở về nguồn thanh toán của giao dịch gốc | Đề nghị chuyển vào tài khoản mới do người mua cung cấp |

<a id="NV1-005-D03"></a>
## D03. Xử lý và theo dõi hoàn tiền

Người được quyền của merchant gửi yêu cầu trên kênh vận hành được cấu hình. Paygate kiểm tra trạng thái, quyền và hạn mức tiền hàng còn lại, rồi xác định nguồn bù trừ từ phiên thanh toán hoặc khoản nộp bổ sung theo hợp đồng. Nếu chưa đủ nguồn, ghi trạng thái chờ bổ sung và lý do; không coi yêu cầu đã hoàn tất.

Hoàn tiền đi theo tuyến thanh toán gốc. Không trả bằng tiền mặt hoặc chuyển khoản ngoài luồng để rồi đồng thời gửi yêu cầu hoàn trên Paygate. Nếu nguồn thanh toán gốc không còn nhận được tiền, chuyển operations xác minh cơ chế thay thế được phép; chatbot không chọn tài khoản nhận tiền khác. Mã tham chiếu hoàn tiền do hệ thống cấp và trạng thái thực tế phải được theo dõi riêng với trạng thái proposal CMS.

Hoàn tiền hàng không tự chứng minh merchant được hoàn phí dịch vụ. Cách điều chỉnh doanh số, hoàn phí cùng kỳ hoặc khoản phí của kỳ trước theo NV3-001 và điều khoản hợp đồng. Khi chưa có dữ kiện phân bổ phí gốc, không lấy biểu phí hôm nay nhân tiền hoàn để đoán số phí hoàn. Trợ lý chỉ diễn giải quy tắc và phép tính có đủ căn cứ, không khẳng định khoản ghi có đã được thực hiện.

<a id="NV1-005-D04"></a>
## D04. Bồi hoàn và đối chiếu tránh trùng

Hồ sơ bồi hoàn ghi mã giao dịch, bên yêu cầu, lý do, số tiền tranh chấp, chứng từ đã nhận và thời hạn phản hồi cụ thể của hồ sơ. Không áp dụng cửa sổ 90 ngày của hoàn tiền chủ động làm thời hạn mặc định cho bồi hoàn. Nếu đã có yêu cầu hoàn một phần, số tiền đó phải được đối chiếu trước khi xác định nghĩa vụ bồi hoàn nhằm tránh bù trừ hai lần cho cùng phần tiền hàng.

Quyết định bồi hoàn và phạm vi giữ tiền do operations có quyền thực hiện, không do nội dung khiếu nại tự phát sinh. Merchant có trách nhiệm cung cấp chứng từ theo yêu cầu; việc gửi chứng từ chưa chứng minh đã thắng tranh chấp. Nếu hồ sơ thiếu ngày phản hồi hoặc số tiền được yêu cầu, trợ lý phải chỉ rõ phần thiếu thay vì suy ra từ vụ việc khác.

<a id="NV1-005-D05"></a>
## D05. Tạm ngừng và chấm dứt merchant

Tạm ngừng nhận giao dịch mới, chấm dứt hợp đồng merchant và đóng tài khoản thanh toán là ba việc riêng. Chấm dứt merchant không đóng tài khoản doanh nghiệp, không hủy thẻ và không xóa quan hệ khách hàng. Hồ sơ cần văn bản yêu cầu của người có thẩm quyền, merchant bị ảnh hưởng, mốc dừng, trạng thái thiết bị nếu có và bảng nghĩa vụ còn tồn.

Trước khi đóng vận hành cần rà soát các phiên chưa thanh toán, yêu cầu hoàn còn xử lý, bồi hoàn đang mở, phí chưa đối soát và quyền truy cập cần thu hồi. Nghĩa vụ đã phát sinh vẫn được theo dõi sau mốc dừng nhận giao dịch; không dùng trạng thái INACTIVE để kết luận số dư nghĩa vụ bằng 0. Với nhiều merchant cùng pháp nhân, chấm dứt một merchant không mặc nhiên chấm dứt các merchant còn lại.

<a id="NV1-005-D06"></a>
## D06. Ranh giới trợ lý và nội dung bàn giao

Trợ lý có thể giúp kiểm tra danh sách dữ kiện, nêu điểm chưa đủ và chuẩn bị nội dung để người có thẩm quyền thao tác. Trợ lý không hoàn tiền, khóa merchant, xác nhận đã chấm dứt hay gửi yêu cầu ngoài CMS. Phản hồi trạng thái phải có nguồn và thời điểm; lời xác nhận của người mua rằng đã nhận tiền là thông tin cần đối chiếu, chưa thay kết quả thực thi của hệ thống. Chính sách đóng tài khoản và thẻ tra cứu riêng NV2-006 và NV2-001.
