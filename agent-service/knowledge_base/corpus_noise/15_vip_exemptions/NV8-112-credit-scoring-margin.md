---
domain: "NV8"
document_id: "NV8-112"
document_type: "amendment"
---
# Chấm Điểm Tín Dụng Và Ký Quỹ Giao Dịch Chuyên Biệt

<a id="NV8-112-D01"></a>
## 1. Bỏ Qua Lịch Sử Tín Dụng Nhóm 2 Tạm Thời
Trong mô hình chấm điểm tín dụng chung (NV6-088), khách hàng có nợ cần chú ý (Nhóm 2) trên CIC sẽ bị từ chối cấp thẻ tín dụng mới. Ngoại lệ tại Ngân hàng Mô phỏng: Khách hàng Private nếu phát sinh nợ Nhóm 2 do nguyên nhân kỹ thuật (quên đóng phí thường niên, lỗi hệ thống trích nợ) với dư nợ dưới 5 triệu VND, ngân hàng vẫn duyệt cấp hạn mức tín dụng mới lên đến 2 tỷ VND mà không cần chờ CIC cập nhật lại.

<a id="NV8-112-D02"></a>
## 2. Ký Quỹ Giao Dịch Phái Sinh
Hệ thống Paygate tích hợp tính năng giao dịch phái sinh tiền tệ. Tỷ lệ ký quỹ (Margin Requirement) thông thường là 15%. Tuy nhiên, khách hàng Diamond được giảm tỷ lệ ký quỹ xuống còn 8%, và khách hàng Private được giảm xuống còn 5%, giúp tối ưu hóa đòn bẩy tài chính.

<a id="NV8-112-D03"></a>
## 3. Ngoại Lệ Lệnh Gọi Bổ Sung Ký Quỹ (Margin Call)
Khi tài khoản giao dịch phái sinh của khách VIP chạm ngưỡng Margin Call, thay vì tự động cắt lỗ (Force Sell) lập tức, Paygate sẽ tạo độ trễ 4 tiếng để Relationship Manager liên hệ khách hàng nộp thêm tiền hoặc dùng hạn mức thấu chi đặc quyền (quy định tại NV8-108) bù đắp ký quỹ.
