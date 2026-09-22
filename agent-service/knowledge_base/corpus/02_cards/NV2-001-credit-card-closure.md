---
document_id: "NV2-001"
title: "Chấm dứt thẻ tín dụng chính và xử lý nghĩa vụ còn lại"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV2"
document_type: "procedure"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_adaptation"
source_refs: ["RAW-INT-02"]
status: "published"
published_at: "2026-01-01"
effective_from: "2026-01-01"
effective_to: null
product_scope: ["personal_credit_card"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "card_operations"
related_documents: ["NV2-002", "NV2-003", "NV2-005", "NV2-006", "NV4-001"]
supersedes: []
---

# Chấm dứt thẻ tín dụng chính và xử lý nghĩa vụ còn lại

Đây là quy trình do Ngân hàng Mô phỏng biên soạn cho môi trường mô phỏng. Mọi xác nhận số dư hoặc trạng thái thẻ phải có kết quả từ hệ thống nghiệp vụ có thẩm quyền.

<a id="NV2-001-D01"></a>
## D01. Phạm vi yêu cầu đóng thẻ

Áp dụng khi chủ thẻ chính cá nhân đề nghị chấm dứt hợp đồng thẻ tín dụng và toàn bộ thẻ phụ gắn với hợp đồng đó. “Hủy thẻ”, “đóng thẻ”, “không dùng thẻ nữa” phải được làm rõ là chấm dứt hợp đồng, ngừng một thẻ phụ hay chỉ khóa tạm thời. Không áp dụng điều kiện dư nợ bằng không để trì hoãn khóa khẩn cấp khi mất thẻ; khóa khẩn cấp theo NV2-005.

Đóng thẻ tín dụng không tự đóng tài khoản thanh toán dùng để trích nợ, không hủy hợp đồng Paygate của doanh nghiệp có liên quan và không xóa hồ sơ cá nhân. Dừng riêng thẻ phụ làm ngừng phương tiện sử dụng; nghĩa vụ đã phát sinh vẫn thuộc hợp đồng của chủ thẻ chính.

<a id="NV2-001-D02"></a>
## D02. Điều kiện hoàn tất chấm dứt

Chỉ kết luận đủ điều kiện hoàn tất khi đồng thời đáp ứng các điều kiện sau tại thời điểm xử lý:

| Nội dung kiểm tra | Điều kiện cần đạt | Nếu chưa đạt |
| --- | --- | --- |
| Gốc, lãi, phí đã ghi nhận | Tất cả nghĩa vụ phải trả bằng 0 VND | Lập bảng nghĩa vụ và hướng dẫn thanh toán |
| Khoản trả góp đang hoạt động | Đã hoàn tất hoặc tất toán theo NV2-002 | Thu thập xác nhận tất toán từng khoản |
| Giao dịch chờ ghi sổ, giữ chỗ | Không còn khoản chưa xử lý | Chờ kết quả ghi sổ hoặc giải phóng giữ chỗ |
| Tranh chấp chưa có kết quả | Có kết luận và hạch toán liên quan đã hoàn tất | Chuyển bộ phận xử lý tranh chấp |
| Số dư có khách hàng nộp thừa | Bằng 0 VND sau hoàn trả hoặc xử lý được chấp thuận | Lấy chỉ dẫn hoàn trả của chủ thẻ |
| Thẻ phụ và thanh toán định kỳ | Đã rà soát, xác định phương án ngừng sử dụng | Thông báo chủ thẻ chính xử lý cam kết định kỳ |

Sao kê kỳ trước bằng không chưa chứng minh hiện tại không còn nghĩa vụ. Ảnh chụp màn hình của khách hàng và lời xác nhận của chatbot không thay thế dữ liệu nghiệp vụ có dấu thời gian.

<a id="NV2-001-D03"></a>
## D03. Có trả góp thì xử lý từng khoản

Không có cơ chế mặc định “chuyển cả dư nợ trả góp thành thanh toán một lần miễn phí” để đóng thẻ. GDV xác định mã gói, ngày đăng ký, gốc còn lại và ngày dự kiến tất toán từng khoản; áp dụng NV2-002 hoặc sửa đổi đúng phạm vi thời gian. Phải cộng cả lãi, phí đã phát sinh nếu hệ thống xác nhận có.

Tổng ba khoản là 45.000.000 VND chưa đủ tính phí: thiếu mã gói và phân bổ gốc thì chưa xác định được mức 2%, 3% hay 4%. Khi đã có đủ dữ kiện và chấp thuận của khách hàng, nghiệp vụ tất toán được xử lý trước. Chỉ sau kết quả hạch toán mới kiểm tra lại điều kiện đóng; dự kiến số tiền cần nộp không phải xác nhận đã tất toán.

<a id="NV2-001-D04"></a>
## D04. Hồ sơ cần chuẩn bị

| Mã yêu cầu hồ sơ | Nội dung | Khi bắt buộc |
| --- | --- | --- |
| CARD_CLOSURE_REQUEST | Đề nghị chấm dứt có xác nhận chủ thẻ chính, định danh hợp đồng thẻ bằng mã nội bộ | Mọi hồ sơ |
| CARD_LIABILITY_SNAPSHOT | Bảng nghĩa vụ, trả góp, giữ chỗ, tranh chấp và dấu thời gian của hệ thống | Mọi hồ sơ |
| INSTALLMENT_SETTLEMENT_CONSENT | Danh sách khoản, gốc, phí dự kiến, ngày dự kiến và chấp thuận tất toán | Còn trả góp |
| CARD_SURPLUS_REFUND_INSTRUCTION | Chỉ dẫn xử lý số dư có, người nhận phù hợp với quyền của chủ thẻ | Có tiền nộp thừa |

Không ghi toàn bộ số thẻ, mã xác thực hoặc mã bí mật vào hội thoại. Mã nội bộ dùng trong môi trường mô phỏng là đủ để nối hồ sơ.

<a id="NV2-001-D05"></a>
## D05. Vai trò và trạng thái xử lý

Chatbot được giải thích điều kiện, liệt kê thiếu sót và chuẩn bị nội dung rà soát. `CARD_CLOSURE_REVIEW` là loại hướng dẫn; `PROPOSAL-CARD-02` là tên cũ của loại này, không phải quyền thao tác của chatbot. Hồ sơ chuẩn bị chưa có `proposal_id`.

Nếu CMS hỗ trợ luồng này, GDV thực hiện tạo và gửi duyệt trên CMS theo NV4-001. `PENDING_APPROVAL` chỉ xác nhận đã gửi; `APPROVED` chỉ xác nhận quyết định duyệt. Phải có kết quả thực thi của hệ thống nghiệp vụ mới được thông báo hợp đồng thẻ đã chấm dứt. Yêu cầu bị trả lại cần sửa trên cùng hồ sơ theo quy tắc phiên bản CMS; không tự tạo hồ sơ khác để bỏ qua thiếu sót.

<a id="NV2-001-D06"></a>
## D06. Phân biệt với đóng tài khoản thanh toán

Thẻ ghi nợ sử dụng tiền trên tài khoản thanh toán nên không áp dụng bảng phí tất toán trả góp của thẻ tín dụng. Yêu cầu đóng tài khoản thanh toán theo NV2-006 cần kiểm tra số dư và dịch vụ liên kết riêng. Nếu khách hàng yêu cầu đồng thời đóng cả thẻ tín dụng và tài khoản trích nợ, xác định thứ tự xử lý nghĩa vụ thẻ trước khi loại bỏ phương tiện thanh toán còn cần dùng.

<a id="NV2-001-D07"></a>
## D07. Quy định xử lý tra soát và khiếu nại trước khi đóng thẻ

Nếu thẻ đang có tra soát chưa kết thúc, hệ thống tuyệt đối không cho phép tất toán và đóng thẻ tín dụng. Quá trình tra soát tuân thủ các mốc thời gian sau:
- **Thời hạn xử lý:** Tối đa 30 ngày làm việc kể từ ngày tiếp nhận khiếu nại đối với thẻ quốc tế (BIN do Tổ chức thẻ quốc tế cấp). Với một số giao dịch phức tạp cần xác minh với nhiều bên, thời hạn có thể kéo dài tối đa đến 45 ngày.
- **Hoàn tiền và bồi thường:** Nếu kết quả tra soát xác định lỗi không thuộc về khách hàng, ngân hàng thực hiện hoàn tiền hoặc bồi thường vào thẻ chậm nhất trong vòng 05 ngày làm việc kể từ ngày có kết luận.
Chỉ khi tiền hoàn/bồi thường (nếu có) đã được hạch toán đầy đủ và dư nợ cuối cùng được làm rõ thì hợp đồng thẻ mới đủ điều kiện tất toán.
