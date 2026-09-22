---
document_id: "NV1-003"
title: "Đăng ký merchant và thay đổi hồ sơ Paygate"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV1"
document_type: "procedure"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_adaptation"
source_refs: ["RAW-REG-02", "RAW-INT-01"]
status: "published"
published_at: "2026-01-01"
effective_from: "2026-01-01"
effective_to: null
product_scope: ["merchant_profile", "customer_profile"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "customer_operations"
related_documents: ["NV1-001", "NV1-002", "NV1-004", "NV1-005", "NV1-006", "NV3-001", "NV4-003", "NV4-004"]
supersedes: []
---

# Đăng ký merchant và thay đổi hồ sơ Paygate

> Quy trình merchant mô phỏng của Ngân hàng Mô phỏng. Các bước và thời hạn là thiết kế cho dự án; không xác nhận Paygate đã có kết nối thực thi tự động.

<a id="NV1-003-D01"></a>
## D01. Khóa hồ sơ và điều kiện đăng ký

Một merchant gắn với một pháp nhân ký hợp đồng, một hoặc nhiều điểm chấp nhận được khai báo và một cấu hình nhận thanh toán. `merchant_id` không đồng nghĩa `enterprise_id`. Nếu một doanh nghiệp có nhiều ngành kinh doanh, phân khúc dùng để chọn biểu phí phải được xác nhận cho sản phẩm và merchant tương ứng; tên cửa hàng không đủ xác định F&B hoặc bán lẻ.

Đăng ký cần hồ sơ pháp nhân giả lập, người có quyền ký, mô tả hàng hóa/dịch vụ, danh sách điểm chấp nhận, tài khoản nhận tiền thuộc pháp nhân theo hợp đồng, phương án đối soát và thông tin liên hệ. Tài khoản đăng nhập của nhân viên cửa hàng chỉ là tài khoản sử dụng dịch vụ, không được coi là tài khoản nhận thanh toán. Kích hoạt merchant phải có xác nhận riêng của hệ thống vận hành; phê duyệt proposal hồ sơ không tự kích hoạt.

<a id="NV1-003-D02"></a>
## D02. Ma trận thay đổi và chứng từ

| Thay đổi được yêu cầu | Chứng từ chính ngoài hồ sơ cá nhân nếu có | Phạm vi kiểm tra |
|---|---|---|
| Cùng người ký thay số CCCD | `MERCHANT_CHANGE_REQUEST`, `IDENTITY_EVIDENCE`, `AUTHORITY_PROOF`; bằng chứng cùng người nếu thiếu | Hợp đồng và hồ sơ merchant nào đang lưu giấy tờ cũ |
| Thay người NĐDPL hoặc người ký hợp đồng | `ENTERPRISE_CHANGE_NOTICE`, `MERCHANT_CHANGE_REQUEST`, `AUTHORITY_PROOF`, định danh người mới | Hiệu lực bổ nhiệm, phạm vi ký, quyền của người cũ cần xử lý |
| Đổi tài khoản nhận thanh toán | `MERCHANT_CHANGE_REQUEST`, `SETTLEMENT_ACCOUNT_PROOF`, `AUTHORITY_PROOF` | Chủ tài khoản, pháp nhân, merchant, thời điểm chuyển và các phiên đang chờ |
| Thay đầu mối nhận báo cáo | `MERCHANT_CHANGE_REQUEST`, xác nhận kênh liên hệ theo NV1-006 | Quyền nhận thông báo; không tăng quyền giao dịch |
| Thêm điểm bán cùng pháp nhân | `MERCHANT_CHANGE_REQUEST`, thông tin địa điểm và ngành hàng | Phạm vi hợp đồng, cấu hình thiết bị, phân khúc và sản phẩm |

Tên nhóm chứng từ không phải mã upload. Định danh có thể tham chiếu bản đã kiểm tra nếu còn phù hợp và người nhận được quyền xem; không cần nhân bản mọi chứng từ vào từng hội thoại. Không sử dụng ảnh chụp màn hình số tài khoản không xác nhận chủ tài khoản thay cho `SETTLEMENT_ACCOUNT_PROOF`.

<a id="NV1-003-D03"></a>
## D03. Điều kiện phát sinh từ thay CCCD

Khi một NĐDPL đổi CCCD nhưng vẫn là cùng người, thực hiện cập nhật cá nhân theo NV1-001, rà soát doanh nghiệp theo NV1-002, sau đó chuẩn bị thay đổi merchant nếu Paygate lưu số giấy tờ cũ của người đó. Có thể kiểm kê các trường và chuẩn bị chứng từ trước, nhưng phải ghi phụ thuộc vào kết quả cập nhật cá nhân và doanh nghiệp; không báo đã đồng bộ.

Nếu người đổi CCCD chỉ là cổ đông không có quyền và Paygate không lưu định danh của họ, không phát sinh yêu cầu merchant từ sự kiện này. Nếu merchant chỉ lưu mã liên kết tới người có quyền mà không lưu hoặc sử dụng số giấy tờ thay đổi, operations cần xác nhận cơ chế dữ liệu trước khi kết luận không phát sinh. Không suy “không cần” chỉ vì giao diện không hiển thị số CCCD. Bản ghi chưa xác nhận phải được nêu rõ là thiếu căn cứ.

<a id="NV1-003-D04"></a>
## D04. Đổi tài khoản nhận tiền và thời điểm chuyển

Tài khoản mới phải được xác minh đúng pháp nhân và phạm vi hợp đồng. Không thay bằng tài khoản cá nhân của NĐDPL, kế toán trưởng hoặc cổ đông chỉ vì họ có quyền nhận báo cáo. Thay đổi này thuộc tuyến cấp vùng theo NV4-004 và cần mốc áp dụng được operations xác nhận. Không hứa chuyển ngay trong ngày tiếp nhận.

Các phiên thanh toán đã chốt với tài khoản cũ được xử lý theo phương án phiên đó. Phiên mới chỉ sử dụng tài khoản mới sau kết quả thực thi thành công và mốc áp dụng. Nếu tài khoản cũ không nhận được tiền, ghi trạng thái chờ xử lý và chuyển vận hành xác minh; không tự chuyển lại vào tài khoản mới hoặc tài khoản khác. Thay CCCD đơn thuần không đổi cấu hình tài khoản nhận tiền, lịch đối soát, biểu phí hay thông tin xác thực.

<a id="NV1-003-D05"></a>
## D05. Quyền merchant và điều kiện tiếp nhận

Quyền xem giao dịch, tải báo cáo, yêu cầu hoàn tiền, xác nhận đối soát và yêu cầu thay hồ sơ là những phạm vi riêng. Người có quyền hoàn tiền vẫn phải đáp ứng điều kiện giao dịch tại NV1-005; quyền tải báo cáo không thay thế quyền xác nhận bảng đối soát. Quyền được kiểm tra tại lúc nộp và lúc xử lý, kể cả khi văn bản ủy quyền hết hạn trong thời gian chờ.

Đơn vị tiếp nhận đối chiếu yêu cầu, chứng từ và dữ liệu hiện có. Nếu tên pháp nhân không thống nhất nhưng merchant_id trùng, phải xác minh nguyên nhân; không sửa tên trong chứng từ để khớp dữ liệu. Nếu hai công ty có tên gần giống, giữ hai hồ sơ riêng và hỏi mã pháp nhân khi người dùng chưa chỉ rõ.

<a id="NV1-003-D06"></a>
## D06. Kết quả, phụ thuộc và tài liệu liên quan

Bản chuẩn bị ghi trường trước/sau, merchant bị ảnh hưởng, thời điểm nghiệp vụ, chứng từ còn thiếu và hệ thống cần xác nhận. `APPROVED` của CMS không chứng minh Paygate đã thay đổi; chỉ kết quả thực thi nghiệp vụ mới làm căn cứ thông báo hoàn tất. Nếu MVP chưa hỗ trợ loại yêu cầu merchant, trợ lý chỉ cung cấp nội dung bàn giao và hướng dẫn xử lý thủ công, không nhét yêu cầu merchant vào loại thay thông tin cá nhân.

Đối soát và thanh toán theo NV1-004; hoàn tiền/chấm dứt theo NV1-005; mức phí và điều khoản hợp đồng theo NV3-001 cùng các tài liệu được dẫn chiếu. Hồ sơ định danh không tự chọn biểu phí, và biểu phí mới không tự thay quyền người ký hợp đồng.
