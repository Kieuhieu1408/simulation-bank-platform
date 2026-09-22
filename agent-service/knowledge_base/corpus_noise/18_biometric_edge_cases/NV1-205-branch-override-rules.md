---
domain: "NV1"
document_id: "NV1-205"
---
# Quy định ghi đè kết quả sinh trắc học tại quầy (Manual Override)

<a id="NV1-205-D01"></a>
## 1. Nguyên tắc Bốn mắt (Four-Eyes Principle)
Tại Ngân hàng Mô phỏng, mọi nỗ lực ghi đè kết quả nhận diện khuôn mặt thất bại (Manual Biometric Override) của Paygate đều phải tuân thủ nguyên tắc "Bốn mắt". Một Giao dịch viên (Teller) khởi tạo yêu cầu ghi đè trên hệ thống, và một Kiểm soát viên (Supervisor - Level 3) phải kiểm tra hồ sơ, đối chiếu khách hàng thực tế tại quầy, sau đó duyệt bằng mã PIN cứng hoặc Smart Token của chính Kiểm soát viên.

<a id="NV1-205-D02"></a>
## 2. Ma trận rủi ro và Audit Logs
Khi luồng ghi đè được kích hoạt, hệ thống Paygate tạo ra một bản ghi không thể thay đổi (Immutable Audit Log) chứa thông tin: ID Giao dịch viên, ID Kiểm soát viên, Timestamp, Hình ảnh chụp tại quầy, và Lý do (ví dụ: Phẫu thuật, Đeo băng y tế, Lỗi NFC). Ma trận rủi ro của hệ thống sẽ tăng điểm rủi ro của tài khoản này lên mức "Theo dõi 7 ngày", trong đó các giao dịch rút tiền mặt giá trị lớn bị giám sát chặt chẽ.
