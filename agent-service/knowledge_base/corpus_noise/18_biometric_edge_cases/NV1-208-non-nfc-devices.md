---
domain: "NV1"
document_id: "NV1-208"
---
# Giao dịch qua Paygate với thiết bị không hỗ trợ đọc NFC

<a id="NV1-208-D01"></a>
## 1. Giới hạn phần cứng của End-user
Nhiều khách hàng của Ngân hàng Mô phỏng sử dụng điện thoại thông minh đời cũ hoặc thiết bị bị hỏng anten NFC. Khi đó, quá trình đăng ký tài khoản Paygate không thể thực hiện phương thức đọc chip CCCD (NFC-enabled eKYC) vốn là chuẩn bảo mật cao nhất hiện nay (EAL4+).

<a id="NV1-208-D02"></a>
## 2. Chuyển đổi trạng thái (Downgrade Flow)
Hệ thống Paygate tự động nhận diện thiết bị không có NFC thông qua hàm API hệ thống. Quy trình được downgrade xuống OCR (Optical Character Recognition) để đọc thông tin thẻ. Tuy nhiên, khách hàng gặp tình trạng này sẽ bị gắn cờ `LIMIT_NON_NFC`. Để thực hiện các giao dịch trên 100 triệu VNĐ, họ phải đến chi nhánh Ngân hàng Mô phỏng để nhân viên dùng đầu đọc chuyên dụng (NFC Reader) xác minh con chip tĩnh, sau đó gỡ bỏ cờ giới hạn trên tài khoản Paygate.
