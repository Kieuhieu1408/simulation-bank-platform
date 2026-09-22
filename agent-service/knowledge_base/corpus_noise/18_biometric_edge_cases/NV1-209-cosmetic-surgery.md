---
domain: "NV1"
document_id: "NV1-209"
---
# Xác minh danh tính cho khách hàng phẫu thuật thẩm mỹ toàn diện

<a id="NV1-209-D01"></a>
## 1. Lỗi nhận diện do thay đổi nhân trắc học
Trường hợp khách hàng thực hiện phẫu thuật thẩm mỹ toàn diện (gọt hàm, nâng mũi, cắt mí), cấu trúc nhân trắc học (Anthropometric structure) thay đổi hoàn toàn so với ảnh gốc trên CCCD. Thuật toán AI của Ngân hàng Mô phỏng sẽ trả về Matching Score dưới 40%, được phân loại là rủi ro mạo danh (Impersonation Risk) cao.

<a id="NV1-210-D02"></a>
## 2. Quy trình cập nhật dữ liệu sinh trắc học
Paygate lập tức đóng băng chức năng đăng nhập sinh trắc học của tài khoản. Khách hàng bắt buộc phải xin cấp lại CCCD mới từ cơ quan công an để cập nhật ảnh gốc. Sau khi có CCCD mới, khách hàng phải đến quầy dịch vụ của Ngân hàng Mô phỏng. Giao dịch viên thực hiện quy trình "Biometric Reset", xóa bỏ vector đặc trưng cũ, quét NFC thẻ mới và đồng bộ lại hình ảnh lên server Paygate. Mọi giấy tờ y tế chứng minh phẫu thuật thẩm mỹ chỉ mang tính tham khảo, không có giá trị thay thế CCCD mới.
