---
domain: "NV9"
document_id: "NV9-108"
---
# Rà soát Giao dịch với Khu vực Pháp lý Rủi ro cao (High-Risk Jurisdictions)

<a id="NV9-108-D01"></a>
## 1. Phân loại và Áp dụng Định tuyến Thanh toán

Ngân hàng Mô phỏng duy trì một danh sách động các khu vực pháp lý rủi ro cao (High-Risk Jurisdictions - HRJ) dựa trên khuyến nghị của FATF và cảnh báo từ các ngân hàng đại lý. Khi một giao dịch qua cổng Paygate có IP khởi tạo, thẻ phát hành, hoặc tài khoản thụ hưởng liên quan đến một quốc gia trong danh sách HRJ, hệ thống định tuyến (Routing Engine) sẽ lập tức chuyển luồng xử lý từ "Straight-Through Processing" (STP) sang "Manual Enhanced Due Diligence" (EDD). Lệnh chuyển tiền ra nước ngoài đối với các giao dịch có liên đới HRJ không được phép sử dụng mạng lưới điện báo Swift tiêu chuẩn (MT103) mà phải kèm theo điện thông tin bổ sung (MT202 COV) mô tả chi tiết nguồn gốc và mục đích giao dịch để ngân hàng đại lý phê duyệt trước.

<a id="NV9-108-D02"></a>
## 2. Xử lý Tạm giữ Dây chuyền (Cascading Holds)

Đối với dòng tiền liên quan đến các quốc gia HRJ, thời gian tạm giữ AML không chỉ phụ thuộc vào Ngân hàng Mô phỏng mà còn phụ thuộc vào ngân hàng đại lý (Correspondent Bank). Nếu ngân hàng đại lý tại Mỹ phát lệnh phong tỏa (Freeze) một khoản chuyển tiền bằng USD do nghi ngờ rửa tiền, Ngân hàng Mô phỏng bắt buộc phải áp dụng lệnh "Tạm giữ Dây chuyền" (Cascading Hold) ngược trở lại tài khoản nội địa của khách hàng. Khách hàng sẽ bị khóa toàn bộ các thẻ tín dụng, tài khoản thanh toán và tài khoản FDI liên quan cho đến khi ngân hàng đại lý đưa ra phán quyết cuối cùng, quy trình này có thể kéo dài từ 45 đến 90 ngày. Bất kỳ yêu cầu tất toán hay rút tiền mặt nào trong thời gian Cascading Hold đều bị hệ thống AML từ chối tự động.
