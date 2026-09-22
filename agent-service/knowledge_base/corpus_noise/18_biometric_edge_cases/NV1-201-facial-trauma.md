---
domain: "NV1"
document_id: "NV1-201"
---
# Xử lý ngoại lệ nhận diện khuôn mặt do biến dạng vật lý

<a id="NV1-201-D01"></a>
## 1. Ngưỡng sai lệch khuôn mặt (Facial Matching Deviation)
Trong hệ thống của Ngân hàng Mô phỏng, khi khách hàng gặp tai nạn hoặc trải qua phẫu thuật dẫn đến biến dạng vật lý, thuật toán eKYC có thể trả về điểm số khớp khuôn mặt (Face Matching Score) dưới ngưỡng tiêu chuẩn 85%. Đối với các giao dịch thông thường qua Paygate, hệ thống sẽ tự động khóa luồng trực tuyến nếu điểm số này rơi xuống dưới 60%.

<a id="NV1-201-D02"></a>
## 2. Quy trình xác thực đè (Override Procedure) tại quầy
Khi khách hàng bị từ chối bởi hệ thống Paygate do lỗi sinh trắc học khuôn mặt, khách hàng bắt buộc phải đến quầy giao dịch vật lý. Giao dịch viên phải yêu cầu xuất trình Giấy xác nhận y tế (Medical Certificate) có dấu mộc đỏ. Giao dịch viên sau đó sử dụng quyền "Physical Branch Override" (Mã phân quyền: PBO-Level2) để cập nhật lại vector khuôn mặt mới vào cơ sở dữ liệu eKYC của Ngân hàng Mô phỏng, đồng thời ghi nhận log thay đổi với mã lý do `REASON_FACIAL_TRAUMA`.
