---
domain: "NV1"
document_id: "NV1-210"
---
# Quy trình eKYC đối với người cao tuổi suy giảm hệ thần kinh vận động

<a id="NV1-210-D01"></a>
## 1. Thách thức trong Active Liveness
Đối với người cao tuổi mắc chứng Parkinson hoặc suy giảm chức năng vận động (Motor Impairment), việc thực hiện các cử chỉ Active Liveness của Paygate (như cười, quay đầu nhanh sang trái/phải, giữ điện thoại cố định không rung lắc) là bất khả thi. Điều này dẫn đến tỷ lệ rớt (Drop-off rate) ở bước Liveness Checks của Ngân hàng Mô phỏng đối với phân khúc khách hàng này lên tới 80%.

<a id="NV1-210-D02"></a>
## 2. Passive Liveness và Branch-Assisted eKYC
Để giải quyết bài toán edge case này, Ngân hàng Mô phỏng áp dụng công nghệ Passive Liveness (không yêu cầu hành động) chạy ẩn dưới nền để phân tích kết cấu da và chiều sâu khuôn mặt. Nếu điện thoại rung lắc quá mạnh khiến ảnh mờ, ứng dụng Paygate sẽ đề xuất "Branch-Assisted eKYC". Khách hàng đến quầy cùng với người giám hộ (Authorized Proxy), nhân viên ngân hàng sẽ dùng thiết bị chuyên dụng được chống rung quang học để chụp ảnh định danh cho khách hàng, sau đó phê duyệt đè luồng từ chối của hệ thống tự động.
