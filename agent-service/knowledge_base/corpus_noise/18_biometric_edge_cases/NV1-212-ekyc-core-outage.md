---
domain: "NV1"
document_id: "NV1-212"
---
# Trình tự định danh ngoại tuyến khi hệ thống eKYC Core bị gián đoạn

<a id="NV1-212-D01"></a>
## 1. Downtime của Core Biometric
Trong các trường hợp thảm họa mạng hoặc mất kết nối diện rộng giữa Data Center của Ngân hàng Mô phỏng và cụm máy chủ eKYC, Paygate không thể thực hiện quá trình đối chiếu khuôn mặt thời gian thực (Real-time Face Matching). Đây là một edge case về hạ tầng (Infrastructure Edge Case) ảnh hưởng nghiêm trọng đến onboarding khách hàng.

<a id="NV1-212-D02"></a>
## 2. Hàng đợi phi đồng bộ (Asynchronous Queue Processing)
Quy trình dự phòng rủi ro (Contingency Plan) của Ngân hàng Mô phỏng cho phép ứng dụng Paygate chuyển sang chế độ thu thập ngoại tuyến (Offline Onboarding). Hình ảnh chụp thẻ, ảnh liveness, và gói dữ liệu NFC sẽ được mã hóa chuẩn AES-256 lưu tạm tại thiết bị (Secure Enclave). Khi hệ thống eKYC khôi phục, Paygate đẩy dữ liệu lên qua một Async Queue. Tài khoản trong thời gian này thuộc trạng thái `PENDING_BIOMETRIC`, chỉ được nhận tiền vào mà không được phép chuyển tiền ra (Block Debit) cho đến khi batch job định danh chạy thành công.
