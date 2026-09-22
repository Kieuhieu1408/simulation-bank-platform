---
domain: "NV1"
document_id: "NV1-215"
---
# Cấp quyền ghi đè (override) cho Giao dịch viên cấp cao trong hệ thống Paygate

<a id="NV1-215-D01"></a>
## 1. Ma trận phân quyền (Authorization Matrix)
Việc xử lý các edge case về sinh trắc học (vân tay hỏng, phẫu thuật thẩm mỹ, thẻ hỏng) tại Ngân hàng Mô phỏng đòi hỏi một cơ chế kiểm soát chặt chẽ. Ma trận phân quyền cho chức năng "Manual Biometric Override" trên hệ thống Paygate Admin được chia thành 3 cấp: Teller (Cấp 1 - Khởi tạo), Supervisor (Cấp 2 - Phê duyệt hạn mức thấp), và Branch Manager (Cấp 3 - Phê duyệt hạn mức cao và rủi ro cao).

<a id="NV1-215-D02"></a>
## 2. Điều kiện kích hoạt quyền Ghi đè Cấp 3
Trong trường hợp khách hàng gặp sự cố sinh trắc học đồng thời có số dư tài khoản trên 2 tỷ VNĐ hoặc thuộc nhóm khách hàng VIP (Priority), Supervisor Cấp 2 không đủ thẩm quyền phê duyệt ghi đè. Hệ thống Paygate sẽ yêu cầu Branch Manager đăng nhập bằng sinh trắc học tĩnh mạch ngón tay (Finger Vein Biometrics) vào máy trạm nội bộ của Ngân hàng Mô phỏng để xác nhận (Approve). Hành động này được log trực tiếp vào hệ thống Core Audit và gửi báo cáo tự động đến Ban Giám Đốc Khối Bán Lẻ vào cuối ngày.
