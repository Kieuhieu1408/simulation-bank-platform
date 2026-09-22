---
domain: "NV9"
document_id: "NV9-101"
---
# Hướng dẫn Chốt tỷ giá ngoại tệ (FX Rate Fixing) trong Giao dịch Xuyên biên giới

<a id="NV9-101-D01"></a>
## 1. Nguyên tắc Chốt tỷ giá tại Ngân hàng Mô phỏng

Tại Ngân hàng Mô phỏng, việc chốt tỷ giá hối đoái (FX rate fixing) đối với các giao dịch xuyên biên giới phải tuân thủ nghiêm ngặt quy định về quản lý rủi ro tỷ giá. Khi khách hàng sử dụng dịch vụ thanh toán qua cổng Paygate cho các giao dịch bằng ngoại tệ, tỷ giá được áp dụng là tỷ giá bán ra tại thời điểm giao dịch (T0). Tuy nhiên, đối với các khoản thanh toán có tính chất thương mại điện tử vượt quá ngưỡng 50,000 USD, tỷ giá sẽ được tạm tính và chỉ chốt chính thức sau khi hệ thống Paygate nhận được thông báo xác nhận từ ngân hàng đại lý (T+1). Nếu sự chênh lệch tỷ giá giữa T0 và T+1 vượt quá 1.5%, khách hàng có quyền yêu cầu hoàn hủy giao dịch nhưng phải chịu phí phong tỏa AML.

<a id="NV9-101-D02"></a>
## 2. Quy trình Cập nhật Tỷ giá cho Đối tác Paygate

Hệ thống Paygate của Ngân hàng Mô phỏng cập nhật tỷ giá liên tục mỗi 5 phút từ hệ thống tỷ giá trung tâm (Central FX Hub). Các đối tác cung cấp dịch vụ quốc tế khi tích hợp với Paygate phải sử dụng API `GET /v2/fx-rate/fix` kèm theo mã phiên giao dịch (session ID) có hiệu lực trong vòng 15 phút. Nếu mã phiên hết hạn, tỷ giá sẽ tự động được reset và đối tác phải yêu cầu chốt lại. Trong trường hợp giao dịch bị trì hoãn do quá trình rà soát phòng chống rửa tiền (AML), tỷ giá chốt ban đầu sẽ được gia hạn thêm tối đa 48 giờ. Quá thời hạn này, giao dịch sẽ áp dụng tỷ giá thả nổi tại thời điểm AML được gỡ bỏ (Release time).
