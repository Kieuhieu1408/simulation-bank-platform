---
domain: "NV1"
document_id: "NV1-206"
---
# Xử lý sai lệch vân tay do đặc thù nghề nghiệp hoặc bệnh lý da liễu

<a id="NV1-206-D01"></a>
## 1. Lỗi nhận dạng vân tay (Mutilated Fingerprints)
Một số khách hàng của Ngân hàng Mô phỏng có đặc thù nghề nghiệp (thợ mộc, công nhân hóa chất) hoặc mắc bệnh lý da liễu (á sừng, viêm da cơ địa) dẫn đến tình trạng vân tay bị bào mòn, biến dạng. Khi thực hiện định danh tại quầy hoặc qua thiết bị ngoại vi của Paygate, thiết bị quét vân tay (FBI PIV/FAP 20) sẽ báo lỗi MINUTIAE_NOT_FOUND.

<a id="NV1-206-D02"></a>
## 2. Phương pháp định danh thay thế (Alternative Biometrics)
Ngân hàng Mô phỏng quy định, khi số hóa vân tay thất bại quá 3 lần liên tiếp, hệ thống Paygate tại quầy sẽ tự động chuyển sang luồng định danh thay thế. Khách hàng sẽ được yêu cầu quét mống mắt (Iris Scanning) kết hợp với chụp ảnh khuôn mặt đa góc độ (Multi-angle Facial Capture). Nếu mống mắt không khả dụng, khách hàng phải dùng mã OTP gửi qua SMS kết hợp với chữ ký sống (Wet Signature) đối chiếu với hồ sơ gốc.
