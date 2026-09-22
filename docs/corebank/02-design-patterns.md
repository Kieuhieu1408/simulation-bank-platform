# 2. Các Design Pattern được sử dụng trong Core Banking

Một hệ thống ngân hàng không chỉ cần chạy đúng, mà còn cần phải dễ bảo trì, dễ mở rộng và kiểm soát chặt chẽ. Dưới đây là các Design Pattern chủ đạo chúng ta sử dụng và lý do tại sao người mới cần nắm vững chúng.

## CQRS (Command Query Responsibility Segregation)

**CQRS là gì?** 
Là pattern chia tách hoàn toàn logic của luồng **Ghi/Sửa đổi dữ liệu (Command)** ra khỏi luồng **Đọc/Truy vấn dữ liệu (Query)**.

**Tại sao dự án dùng CQRS?**
- **Đặc thù nghiệp vụ:** Trong ngân hàng, lượng người dùng mở app lên xem số dư và xem lịch sử giao dịch (Query) nhiều gấp hàng trăm lần so với số lần họ thực sự bấm nút "Chuyển tiền" (Command).
- **Tránh Deadlock:** Logic chuyển tiền sẽ khóa (lock) các bản ghi tài khoản lại để tính toán. Nếu việc truy vấn lịch sử chung một luồng/database với ghi, hệ thống sẽ bị treo khi tải cao.

**Ưu điểm:**
1. **Tối ưu hóa hiệu năng độc lập:** Chúng ta có thể scale các service đọc (Read Replica, Elasticsearch) thoải mái mà không ảnh hưởng đến database ghi chứa logic giao dịch.
2. **Bảo mật & Toàn vẹn:** Luồng Command (Ghi) được đóng gói chặt chẽ với các logic nghiệp vụ phức tạp, tránh việc vô tình chỉnh sửa số dư khi đang viết các câu query báo cáo.

## AOP (Aspect-Oriented Programming)

**AOP là gì?**
Lập trình hướng khía cạnh giúp chúng ta tách các logic "cắt ngang" (cross-cutting concerns) ra khỏi logic nghiệp vụ chính yếu.

**Tại sao dự án dùng AOP?**
Thử tưởng tượng hàm `transferMoney()`:
Nếu không dùng AOP, hàm này sẽ chứa hàng đống code tạp lố nhố như: Kiểm tra quyền (Auth) -> Mở Transaction -> Ghi Log Request -> Thực hiện chuyển tiền -> Ghi Audit log -> Đóng Transaction... Điều này làm hàm nghiệp vụ chính phình to và rất khó đọc.

**Ưu điểm khi dùng AOP:**
1. **Tập trung nghiệp vụ cốt lõi:** Hàm chuyển tiền giờ đây thuần túy chỉ có code nghiệp vụ tài chính. Code trong sạch và dễ test hơn.
2. **Khả năng tái sử dụng:** Dễ dàng gắn các logic lặp lại dưới dạng Annotation. Ví dụ chỉ cần thêm `@AuditLog`, `@RequirePIN` hoặc `@Transactional` trên đầu method, AOP sẽ tự động bọc logic lại. Developer mới có thể dễ dàng tuân thủ chuẩn của dự án mà không sợ quên viết code ghi log.

---
**👉 Đọc tiếp:** [Thuật toán và Giải quyết Yêu cầu Kỹ thuật hóc búa](./03-thuat-toan-va-ky-thuat.md)
