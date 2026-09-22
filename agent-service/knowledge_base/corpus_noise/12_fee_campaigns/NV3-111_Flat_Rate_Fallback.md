---
domain: "NV3"
document_id: "NV3-111"
title: "Cơ chế Fallback về Flat Rate"
---

# Cơ chế Fallback về Flat Rate

<a id="NV3-111-D01"></a>
## 1. Kích hoạt Fallback Tự động
Cơ chế Fallback là một chốt chặn an toàn (Failsafe) của **Ngân hàng Mô phỏng**. Khi hệ thống đối soát dữ liệu (Reconciliation System) của **Paygate** không thể xác định được hợp đồng Progressive Rate nào đang có hiệu lực (do lỗi timeout API hoặc hết hạn hợp đồng chưa update), hệ thống sẽ tự động gán nhãn Flat Rate 1.5% cho tất cả các giao dịch trong mẻ (batch) đó.

<a id="NV3-111-D02"></a>
## 2. Giải quyết Hậu Fallback và Biên Rủi ro
Nếu một giao dịch của **Công ty Minh An** bị dính Fallback, ngân hàng sẽ thu phí 1.5% ở ngày T+1. Sau khi hệ thống vận hành bình thường trở lại, khoản chênh lệch (giữa 1.5% và tỷ lệ Progressive Rate thực tế) sẽ được hoàn lại thông qua thủ tục Dispute nội bộ (được quy định tại NV3-113). Điều này nhằm đảm bảo không bao giờ vi phạm Margin Risk Boundary dưới 0.8% ngay tại thời điểm thanh toán thời gian thực (Real-time Settlement).
