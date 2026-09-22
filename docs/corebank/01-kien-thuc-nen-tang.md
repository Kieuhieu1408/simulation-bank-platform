# 1. Kiến thức nền tảng để xây dựng Core Banking

Để phát triển một hệ thống Ngân hàng lõi (Core Banking), nắm vững công nghệ là chưa đủ. Các kỹ sư phần mềm cần phải hiểu về các nguyên tắc nghiệp vụ tài chính khắt khe nhất để đảm bảo không một đồng tiền nào bị thất thoát.

## Core Banking là gì?
Core Banking là "trái tim" của một ngân hàng, hệ thống back-end xử lý các nghiệp vụ ngân hàng cơ bản như:
- Tạo và quản lý tài khoản khách hàng.
- Xử lý các giao dịch gửi tiền, rút tiền, chuyển khoản.
- Tính toán lãi suất.
- Lưu trữ Sổ cái (Ledger) của ngân hàng.

## Tính toàn vẹn của Giao dịch (ACID)
Trong hệ thống tài chính, sai số dù chỉ 1 đồng cũng là thảm họa. Mọi giao dịch làm thay đổi số dư đều phải tuân thủ nghiêm ngặt **nguyên tắc ACID** của Database:

1. **Atomicity (Tính nguyên tử):** Hoặc là tất cả các bước cùng thành công (trừ tiền người gửi VÀ cộng tiền người nhận), hoặc không có bước nào được thực hiện.
2. **Consistency (Tính nhất quán):** Tổng số dư của toàn hệ thống trước và sau khi thực hiện giao dịch phải luôn đồng nhất, không tự nhiên sinh ra hay mất đi.
3. **Isolation (Tính độc lập):** Các giao dịch diễn ra đồng thời (cùng lúc) không được phép can thiệp hay ghi đè lên nhau (giải quyết triệt để lỗi Race Condition).
4. **Durability (Tính bền vững):** Một khi giao dịch báo "Thành công", nó phải tồn tại vĩnh viễn dù sau đó server có bị sập nguồn hay mất điện.

## Sổ cái kép (Double-entry Bookkeeping)
Mọi ngân hàng trên thế giới đều hoạt động dựa trên nguyên lý Kế toán kép. Mỗi một giao dịch tài chính phải ảnh hưởng đến ít nhất hai tài khoản: 
- Một khoản ghi **Nợ (Debit)**
- Một khoản ghi **Có (Credit)**

Nguyên tắc này giúp hệ thống luôn trong trạng thái tự cân bằng, hỗ trợ tối đa cho việc **Đối soát (Reconciliation)** vào cuối ngày.

---
**👉 Đọc tiếp:** [Design Patterns: Tại sao áp dụng AOP và CQRS?](./02-design-patterns.md)
