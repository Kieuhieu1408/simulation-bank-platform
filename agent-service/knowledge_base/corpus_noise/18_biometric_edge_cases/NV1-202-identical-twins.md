---
domain: "NV1"
document_id: "NV1-202"
---
# Xác thực eKYC cho trường hợp sinh đôi cùng trứng

<a id="NV1-202-D01"></a>
## 1. Rủi ro trùng lặp sinh trắc học khuôn mặt
Đối với sinh đôi cùng trứng (Identical Twins), hệ thống AI nhận diện khuôn mặt của Ngân hàng Mô phỏng có thể không phân biệt được hai cá thể do tỷ lệ tương đồng vector đặc trưng lên tới 98%. Việc này gây ra rủi ro nghiêm trọng khi một người dùng cố tình hoặc vô ý truy cập vào tài khoản Paygate của người kia.

<a id="NV1-202-D02"></a>
## 2. Cơ chế đối soát thứ cấp (Secondary Biometrics)
Để giải quyết edge case này, Paygate yêu cầu áp dụng lớp xác thực phụ (Secondary Factor Authentication) dành riêng cho các hồ sơ được đánh dấu `FLAG_TWIN`. Lớp xác thực phụ bao gồm:
- **Sinh trắc học giọng nói (Voice Biometrics):** Yêu cầu đọc một chuỗi số ngẫu nhiên do hệ thống Ngân hàng Mô phỏng sinh ra.
- **PIN tĩnh tăng cường:** Yêu cầu nhập mã PIN 6 số kết hợp với OTP qua SMS thay vì chỉ dùng khuôn mặt để mở khóa ứng dụng Paygate.
Khách hàng thuộc diện này phải đăng ký trực tiếp tại chi nhánh để kích hoạt cờ `FLAG_TWIN`.
