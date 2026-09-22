---
domain: "NV1"
document_id: "NV1-211"
---
# Xử lý thẻ CCCD bị mờ/trầy xước không thể quét MRZ

<a id="NV1-211-D01"></a>
## 1. Không thể giải mã MRZ
Vùng đọc máy (MRZ - Machine Readable Zone) trên thẻ CCCD đóng vai trò quan trọng trong việc trích xuất khóa mật mã (BAC/PACE) để mở khóa chip. Trong các trường hợp thẻ bị trầy xước vật lý nghiêm trọng, hệ thống camera OCR của Ngân hàng Mô phỏng không thể đọc đúng chuỗi ký tự, khiến ứng dụng Paygate liên tục báo lỗi "Lỗi giải mã thẻ".

<a id="NV1-211-D02"></a>
## 2. Giao thức NFC Fallback và Yêu cầu tại quầy
Khi luồng OCR cho MRZ thất bại, ứng dụng Paygate cung cấp tùy chọn "NFC Direct Scan" (Yêu cầu nhập thủ công chuỗi MRZ để tạo khóa hoặc sử dụng mã CAN nếu có hỗ trợ). Nếu khách hàng nhập sai quá 3 lần, thẻ bị tạm khóa giao tiếp NFC trên ứng dụng. Khách hàng phải đến trực tiếp quầy giao dịch Ngân hàng Mô phỏng, nơi nhân viên có quyền sử dụng mã CAN gốc trên hệ thống kết nối với cơ sở dữ liệu Bộ Công An để kích hoạt lại luồng sinh trắc học, hoặc phát hành thẻ phụ tạm thời cho đến khi khách hàng làm thẻ mới.
