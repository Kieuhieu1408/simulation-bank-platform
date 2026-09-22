---
domain: "NV9"
document_id: "NV9-112"
---
# Quản lý Thanh toán Thương mại Điện tử Xuyên biên giới

<a id="NV9-112-D01"></a>
## 1. Cơ chế Tài khoản Đảm bảo (Escrow) cho Sàn TMĐT

Đối với các Sàn thương mại điện tử quốc tế (Cross-Border E-commerce Platforms) tích hợp với cổng Paygate, Ngân hàng Mô phỏng yêu cầu mở một Tài khoản Đảm bảo (Escrow Account). Khi người mua tại nội địa thanh toán tiền hàng, khoản tiền bằng VND sẽ được lưu trữ trong Escrow Account. Chỉ khi có xác nhận giao hàng thành công từ hệ thống vận chuyển độc lập (Logistics API), khoản tiền này mới được chuyển đổi sang ngoại tệ và quyết toán (settle) cho người bán ở nước ngoài. Việc chốt tỷ giá hối đoái sẽ được tính tại thời điểm tiền vào Escrow (T0), do đó Ngân hàng Mô phỏng phải sử dụng các công cụ phái sinh để bảo hiểm rủi ro tỷ giá cho khoảng thời gian tiền nằm trong Escrow (thường từ 5 đến 15 ngày).

<a id="NV9-112-D02"></a>
## 2. Rủi ro Smurfing trong TMĐT Xuyên biên giới

Hệ thống AML của Ngân hàng Mô phỏng đặc biệt chú ý đến kỹ thuật "Smurfing" (chia nhỏ giao dịch) thông qua kênh TMĐT. Kịch bản phổ biến là một cá nhân tạo nhiều đơn hàng ảo có giá trị nhỏ (dưới ngưỡng báo cáo ngoại hối 1,000 USD) qua Paygate để chuyển một lượng lớn tiền ra nước ngoài. Để ngăn chặn, hệ thống áp dụng cơ chế "Aggregated Velocity Limit" (Giới hạn Tổng hợp). Nếu tổng giá trị thanh toán xuyên biên giới của một cá nhân (dựa trên số CCCD hoặc Device ID) vượt quá 10,000 USD trong vòng 30 ngày cho các đơn hàng TMĐT, mọi giao dịch tiếp theo sẽ bị từ chối tự động. Khách hàng phải đến quầy giao dịch, xuất trình hóa đơn chứng từ hải quan hợp lệ để yêu cầu gỡ bỏ giới hạn.
