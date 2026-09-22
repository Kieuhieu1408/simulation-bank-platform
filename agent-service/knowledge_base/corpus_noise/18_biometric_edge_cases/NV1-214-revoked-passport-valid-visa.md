---
domain: "NV1"
document_id: "NV1-214"
---
# Trường hợp khách hàng bị thu hồi hộ chiếu nhưng còn visa lao động hợp lệ

<a id="NV1-214-D01"></a>
## 1. Xung đột hồ sơ định danh ngoại kiều
Một edge case phức tạp đối với khách hàng nước ngoài tại Ngân hàng Mô phỏng là tình trạng Hộ chiếu (Passport) bị đại sứ quán nước sở tại báo mất/thu hồi, nhưng Thị thực (Visa) lao động tại Việt Nam hoặc Thẻ tạm trú (TRC) vẫn còn hiệu lực. Hệ thống Quản lý Rủi ro của Paygate khi thực hiện daily batch quét danh sách đen (Blacklist AML/KYC) sẽ phát ra cảnh báo (Red Flag).

<a id="NV1-214-D02"></a>
## 2. Quy trình đóng băng và Thẩm định pháp lý
Ngay khi phát hiện hộ chiếu bị thu hồi, Paygate tự động vô hiệu hóa (Deactivate) toàn bộ thông tin sinh trắc học đã liên kết, chặn khách hàng đăng nhập. Ngân hàng Mô phỏng nghiêm cấm nhân viên thực hiện thao tác Manual Override trong trường hợp này. Hồ sơ phải được chuyển lên Hội đồng Thẩm định Pháp chế (Compliance & Legal Committee). Tài khoản chỉ được mở lại khi khách hàng xuất trình được Hộ chiếu mới và thực hiện luồng Re-KYC từ đầu để ghi nhận dữ liệu khuôn mặt và ID mới vào core banking.
