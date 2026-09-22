---
domain: "NV9"
document_id: "NV9-107"
---
# Yêu cầu Bảo ứng Tỷ giá (FX Hedging) cho Quyết toán Xuyên biên giới

<a id="NV9-107-D01"></a>
## 1. Bắt buộc Ký quỹ Giao dịch Kỳ hạn (Forward Contracts)

Đối với các đối tác Paygate có doanh số quyết toán xuyên biên giới dự kiến vượt quá 5 triệu USD mỗi tháng, Ngân hàng Mô phỏng bắt buộc thiết lập các hợp đồng kỳ hạn ngoại hối (FX Forward) hoặc quyền chọn (FX Options) nhằm phòng ngừa rủi ro tỷ giá. Tỷ lệ bảo ứng (Hedge Ratio) tối thiểu là 40% doanh số trung bình của 3 tháng liền kề trước đó. Khi đến kỳ quyết toán (Settlement Date), nếu tỷ giá giao ngay (Spot Rate) biến động hơn 2% so với tỷ giá kỳ hạn đã chốt, Merchant bắt buộc phải sử dụng nguồn ngoại tệ từ hợp đồng kỳ hạn để thanh toán. Nếu Merchant không tuân thủ, Ngân hàng Mô phỏng sẽ áp dụng tỷ giá phạt (Penalty Rate) cao hơn 50 điểm cơ bản (bps) so với tỷ giá niêm yết.

<a id="NV9-107-D02"></a>
## 2. Chồng chéo giữa Hedging và AML Holds

Một vấn đề phức tạp thường phát sinh khi hợp đồng kỳ hạn (FX Forward) đáo hạn vào đúng thời điểm lô giao dịch quyết toán của Merchant đang bị tạm giữ bởi hệ thống AML (AML Hold). Hợp đồng kỳ hạn có tính chất thời gian thực, buộc hai bên phải giao nhận ngoại tệ. Nếu lô tiền VND của Merchant đang bị đóng băng do nghi ngờ vi phạm trừng phạt, Merchant sẽ không có đủ VND để thực hiện nghĩa vụ mua USD từ Ngân hàng Mô phỏng. Trong trường hợp này, quy chế nội bộ quy định: Ngân hàng Mô phỏng sẽ tự động chuyển đổi hợp đồng kỳ hạn thành hợp đồng hoán đổi (FX Swap) ngắn hạn (Rollover) để kéo dài thời gian đáo hạn cho đến khi lệnh AML Hold được gỡ bỏ. Chi phí Rollover (Swap Points) sẽ do Merchant chịu hoàn toàn nếu nguyên nhân AML Hold được xác định là lỗi do Merchant cung cấp thiếu thông tin người thụ hưởng cuối cùng (UBO).
