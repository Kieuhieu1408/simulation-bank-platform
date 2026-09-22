---
document_id: "NV1-010"
title: "Khung quản lý khách hàng hợp nhất (Bản dự thảo)"
version: "1.0"
corpus_version: "1.0.0"
domain: "NV1"
document_type: "policy"
organization: "Ngân hàng Mô phỏng"
source_type: "synthetic_enrichment"
source_refs: []
status: "published"
published_at: "2026-09-01"
effective_from: "2027-01-01"
effective_to: null
product_scope: ["customer_profile", "merchant_management"]
customer_scope: ["all"]
access_roles: ["gdv", "approver", "knowledge_admin", "operations"]
unit_scope: ["all"]
owner_role: "customer_operations"
related_documents: ["NV1-001", "NV1-003"]
supersedes: []
---

# Khung quản lý khách hàng hợp nhất (Bản dự thảo)

Tài liệu này mô tả định hướng thiết kế và các quy định dự kiến áp dụng cho Khung quản lý khách hàng hợp nhất của Ngân hàng Mô phỏng. Mặc dù ở trạng thái đã ban hành (published), khung quy định này chỉ bắt đầu có hiệu lực đối với các hoạt động vận hành vào tương lai, cụ thể là từ ngày 01/01/2027.

<a id="NV1-010-D01"></a>
## Mục tiêu và nguyên tắc của khung quản lý hợp nhất

Khung quản lý hợp nhất hướng tới việc xóa bỏ sự rời rạc giữa dữ liệu khách hàng cá nhân, khách hàng doanh nghiệp và đơn vị chấp nhận thẻ (Merchant) sử dụng cổng Paygate.
- **Mục tiêu:** Cung cấp góc nhìn 360 độ về khách hàng, từ đó tối ưu hóa các chiến dịch tiếp thị thẻ, chính sách biểu phí và đánh giá rủi ro đồng bộ trên một nền tảng duy nhất.
- **Nguyên tắc:** Mỗi thực thể pháp lý (cá nhân hoặc tổ chức) sẽ được định danh bằng một mã định danh hợp nhất duy nhất (`UNIFIED-SIM-ID`). Các vai trò khác như Merchant, Chủ thẻ, Người bảo lãnh sẽ được gắn như là các "thuộc tính vai trò" dưới mã định danh duy nhất này.

<a id="NV1-010-D02"></a>
## Cấu trúc dữ liệu khách hàng đa chiều (Khách hàng, Merchant, Đơn vị chấp nhận thẻ)

Theo cấu trúc mới dự kiến, dữ liệu khách hàng sẽ được chuẩn hóa thành các thực thể phân cấp:
- Mức 1: Định danh gốc của pháp nhân hoặc cá nhân. Tại mức này chứa các thông tin pháp lý bắt buộc (Tên, CCCD/Mã số thuế, Ngày sinh/Ngày thành lập).
- Mức 2: Các tài khoản ngân hàng (Tài khoản thanh toán, Tài khoản thẻ tín dụng).
- Mức 3: Các vai trò dịch vụ mở rộng. Ví dụ, Công ty Minh An (`ENT-SIM-001`) ngoài việc là khách hàng vay vốn, còn là một Merchant (`MRC-SIM-001`) sử dụng Paygate. Cả hai mã này sẽ được ánh xạ về một `UNIFIED-SIM-ID`.
- Mức 4: Các điểm giao dịch vật lý hoặc chi nhánh (Ví dụ: `UNIT-SIM-01` đại diện cho một cửa hàng cụ thể của Merchant). Việc tách biệt này cho phép áp dụng mức phí hoặc chương trình khuyến mãi (campaign) cụ thể cho từng địa điểm giao dịch.

<a id="NV1-010-D03"></a>
## Quy trình định danh và xác thực tập trung

Để đảm bảo tính nhất quán của dữ liệu khi nâng cấp lên hệ thống hợp nhất, quy trình định danh và xác thực sẽ thay đổi:
- Bất kỳ yêu cầu thay đổi thông tin pháp lý nào ở Mức 1 (Ví dụ: Yêu cầu `CUSTOMER_PERSONAL_INFORMATION_CHANGE`) sẽ tự động được hệ thống đánh giá mức độ ảnh hưởng đến các dịch vụ ở Mức 2 và Mức 3.
- Hệ thống CMS sẽ sinh ra các thông báo rà soát (Review Tasks) gửi đến các bộ phận tương ứng. Tuy nhiên, AI hoặc hệ thống không tự động thay đổi dữ liệu của Merchant nếu chưa có xác nhận từ bộ phận quản lý rủi ro hoặc Operations.
- Việc xác thực đa yếu tố sẽ được yêu cầu đối với bất kỳ thay đổi nào ảnh hưởng đến thông tin người đại diện hoặc tài khoản nhận tiền thanh toán (settlement account).

<a id="NV1-010-D04"></a>
## Lộ trình triển khai và chuyển đổi dữ liệu

Việc triển khai khung quản lý hợp nhất sẽ diễn ra theo từng giai đoạn để không làm gián đoạn nghiệp vụ hiện tại:
- **Giai đoạn 1 (Đến hết 2026):** Rà soát và làm sạch dữ liệu cũ. Các trường hợp xung đột dữ liệu (ví dụ giữa CIF cá nhân của người đại diện và CIF doanh nghiệp) sẽ được xử lý thủ công bởi các GDV.
- **Giai đoạn 2 (Dự kiến 01/01/2027):** Kích hoạt hệ thống lõi định danh hợp nhất. Các mã CIF cũ (`CIF-SIM-xxx`) và mã Merchant cũ (`MRC-SIM-xxx`) vẫn được duy trì như là định danh phụ (alias) nhằm đảm bảo tương thích ngược với các hợp đồng cũ chưa được làm mới.
- **Giai đoạn 3 (Từ tháng 06/2027):** Ngừng cung cấp việc tạo mã phân mảnh mới. Mọi hồ sơ mở mới đều bắt buộc tạo theo luồng `UNIFIED-SIM-ID`.
