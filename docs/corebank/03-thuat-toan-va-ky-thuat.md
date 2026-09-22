# 3. Thuật toán & Giải quyết Yêu cầu Kỹ thuật hóc búa

Quá trình phát triển Core Banking luôn phải đối mặt với các bài toán kỹ thuật "hóc búa" về độ tin cậy và khả năng xử lý đồng thời. Dưới đây là các kỹ thuật mà dự án đã áp dụng để vượt qua thử thách.

## 1. Xử lý đồng thời (Concurrency Control) & Race Condition

**Bài toán:** Điều gì sẽ xảy ra nếu một khách hàng sử dụng 2 thiết bị khác nhau, bấm nút "Rút tiền" cùng một phần nghìn giây, trong khi số dư chỉ vừa đủ cho 1 giao dịch? Nếu code đọc số dư và kiểm tra điều kiện không được bảo vệ, khách hàng sẽ lách luật rút được gấp đôi số tiền họ có.

**Cách giải quyết:**
- **Pessimistic Locking (Khóa bi quan):** Sử dụng khóa ở cấp độ Database (`SELECT ... FOR UPDATE`). Khi giao dịch thứ 1 chạm vào tài khoản A, tài khoản này sẽ bị khóa lại. Giao dịch thứ 2 phải đứng chờ cho đến khi giao dịch thứ 1 hoàn thành (có số dư mới) thì mới được tiếp tục.
- **Optimistic Locking (Khóa lạc quan):** Dùng trường `version` trong bảng tài khoản. Bất cứ khi nào số dư đổi, `version` tăng lên. Nếu 2 giao dịch cùng đọc 1 `version`, giao dịch update sau sẽ thất bại do `version` đã cũ.

**Ưu điểm:** Loại bỏ 100% rủi ro bị "âm tiền" trong hệ thống.

## 2. Distributed Transactions (Giao dịch phân tán)

**Bài toán:** Khi hệ thống áp dụng Microservices (chia nhỏ thành Account Service, Notification Service, Reward Service...), làm sao để Rollback (hoàn tác) nếu Account Service đã trừ tiền thành công nhưng Reward Service lại bị crash không cộng được điểm thưởng? Database của 2 service là độc lập, không thể dùng hàm `rollback()` thông thường.

**Cách giải quyết:**
- **Saga Pattern (Choreography / Orchestration):** Chúng ta chia giao dịch lớn thành chuỗi các giao dịch nhỏ cục bộ.
- Nếu một mắt xích thất bại, hệ thống sẽ tự động phát ra các **Compensating Transactions (Giao dịch đền bù)** để chạy ngược lại các thao tác đã thành công trước đó (ví dụ: lệnh hoàn tiền).
- Áp dụng thêm **Outbox Pattern** kết hợp với Message Broker (như Kafka/RabbitMQ) để đảm bảo event chắc chắn được gửi đi.

## 3. Idempotency (Tính luỹ đẳng) trong gọi API

**Bài toán:** Do lỗi mạng lưới, ứng dụng khách hàng (hoặc app của đối tác bên thứ 3) gặp timeout nên đã "Retry" (thử gọi lại) API chuyển tiền 3 lần. Làm sao để không trừ tiền của người dùng 3 lần?

**Cách giải quyết:**
- Bắt buộc phải có **Idempotency Key (Khóa lũy đẳng)** (thường là một chuỗi UUID duy nhất, vd: `request_id`) cho mỗi nghiệp vụ thay đổi số dư.
- Cache/Lưu trữ lại trạng thái của Key này (ví dụ dùng Redis). Nếu cùng một Key được gọi lần 2, hệ thống từ chối thực hiện lại logic và trả về ngay kết quả của lần xử lý trước đó.

**Ưu điểm:** Tạo ra một hệ thống API cực kỳ an toàn, thân thiện với các hệ thống phân tán, mang lại trải nghiệm êm ái kể cả khi mạng chập chờn.
