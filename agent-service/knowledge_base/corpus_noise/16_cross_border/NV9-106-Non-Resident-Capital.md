---
domain: "NV9"
document_id: "NV9-106"
---
# Quản lý Tài khoản Vốn của Người Không Cư Trú (Non-Resident Capital Accounts)

<a id="NV9-106-D01"></a>
## 1. Giới hạn Giao dịch đối với Người không cư trú

Theo quy định của Ngân hàng Mô phỏng, cá nhân hoặc tổ chức nước ngoài không cư trú (Non-Resident) có nhu cầu đầu tư gián tiếp vào thị trường tài chính nội địa phải mở Tài khoản Vốn Đầu tư Gián tiếp (FII Account). Khác với tài khoản FDI, tài khoản FII chỉ được nhận tiền chuyển từ nước ngoài vào để mua bán chứng khoán, trái phiếu, hoặc góp vốn vào các doanh nghiệp không thuộc diện đầu tư trực tiếp. Việc chuyển tiền từ tài khoản FII sang nền tảng thanh toán Paygate để mua sắm hàng hóa hoặc thanh toán dịch vụ (e-commerce) bị nghiêm cấm tuyệt đối. Mọi nỗ lực liên kết thẻ ghi nợ nội địa phát hành từ tài khoản FII với Paygate sẽ bị hệ thống AML tự động từ chối với mã lỗi `ERR-AML-NRES-091`.

<a id="NV9-106-D02"></a>
## 2. Kiểm soát Chuyển tiền thu nhập ra nước ngoài

Khi nhà đầu tư không cư trú muốn chuyển thu nhập (cổ tức, lãi trái phiếu) ra nước ngoài, hệ thống Core Banking yêu cầu một chuỗi quy trình xác thực chéo. Đầu tiên, số tiền chuyển đi phải tương ứng với chứng từ chứng minh thu nhập hợp pháp đã nộp đủ thuế. Thứ hai, tỷ giá quy đổi sang ngoại tệ phải áp dụng tỷ giá bán (Ask Rate) của Central FX Hub tại thời điểm phê duyệt lệnh. Nếu lệnh chuyển tiền có giá trị trên 100,000 USD, hệ thống AML sẽ áp dụng chính sách "Cooling-off Period" 24 giờ. Trong khoảng thời gian này, khoản tiền bị phong tỏa tạm thời; nếu phát hiện hoạt động gom mua ngoại tệ bất thường trên thị trường, Ngân hàng Mô phỏng có quyền đơn phương hủy bỏ giao dịch và yêu cầu khách hàng thực hiện lại vào chu kỳ tiếp theo nhằm bảo vệ thanh khoản ngoại tệ của ngân hàng.
