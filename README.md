# Simulation Bank Platform

Nền tảng mô phỏng hệ sinh thái ngân hàng và thanh toán, được xây dựng theo kiến trúc microservice để phục vụ học tập, thử nghiệm nghiệp vụ và phát triển theo nhóm.

## 1. Tổng quan và chức năng các service

- `cms`: Cổng quản trị nội bộ cho vận hành, quản lý merchant và tra cứu giao dịch.
- `paygate`: Cổng thanh toán mô phỏng cho nghiệp vụ thu hộ, chi hộ và tính phí.
- `money-bank`: API ngân hàng số mô phỏng: tài khoản thanh toán, thẻ và chuyển tiền.
- `corebank`: Quản lý số tài khoản, số thẻ, số dư và lịch sử giao dịch – nguồn dữ liệu giao dịch trung tâm.
- `profile-service`: Quản lý hồ sơ người dùng, merchant và phân quyền.
- `notification-service`: Gửi thông báo về các sự kiện giao dịch.
- `common-service`: Module thư viện dùng chung chứa API/event DTO, kiểu dữ liệu nền tảng và tiện ích kỹ thuật thống nhất giữa các service; không chạy như một service độc lập và không sở hữu database.

## 2. Thông tin kỹ thuật sơ bộ

| Service | Công nghệ dự kiến | Design pattern dự kiến |
| --- | --- | --- |
| `cms` | Backend Java Spring Boot 4, frontend Angular; cơ sở dữ liệu sẽ xác định sau | Layered Architecture, RBAC |
| `paygate` | Java Spring Boot 4, database riêng, Kafka, Docker | Hexagonal Architecture, Saga, Strategy (tính phí) |
| `money-bank` | Java Spring Boot 4, database riêng, Kafka, Docker | Hexagonal Architecture, CQRS cơ bản |
| `corebank` | Java Spring Boot 4, Oracle Database cho bản demo, Kafka, Docker | Layered Architecture, Transaction Script, Outbox |
| `profile-service` | Java Spring Boot 4, PostgreSQL, Docker | Layered Architecture, RBAC |
| `notification-service` | Java Spring Boot 4, Kafka, Docker | Event-driven, Strategy (kênh gửi) |
| `common-service` | Java/Maven library, không có runtime và database riêng | Shared contracts, shared kernel tối thiểu |

### 2.1. Ranh giới của `common-service`

Được phép đặt trong module dùng chung:

- API DTO và event DTO đã có version, metadata chuẩn và quy tắc tương thích ngược.
- Kiểu dữ liệu nền tảng ổn định như money/currency, correlation ID, error envelope và pagination contract.
- Validation annotation, serialization convention và tiện ích kỹ thuật không phụ thuộc nghiệp vụ của một service cụ thể.

Không đặt trong module dùng chung:

- JPA entity, repository, database model hoặc migration của một service.
- Business workflow, state machine, authorization policy hoặc domain rule thuộc riêng Money Bank, Profile Service, Paygate hay Corebank.
- Internal implementation DTO của adapter/controller nếu không phải integration contract được nhiều service sử dụng.

Mỗi service vẫn sở hữu domain model và dữ liệu của mình. Thay đổi breaking trong shared contract phải tăng major version và được consumer contract test trước khi nâng phiên bản.

## 3. Corebank: phạm vi MVP và cách chạy

Corebank là nguồn sự thật duy nhất cho số dư và lịch sử giao dịch. Các service khác không tự cập nhật số dư; chúng gọi API của Corebank để tạo tài khoản, truy vấn hoặc ghi nhận giao dịch.

Corebank được đóng gói bằng Docker. Container khởi tạo Oracle Database cho bản demo và nạp sẵn tài khoản/mật khẩu cấu hình trong Docker Compose. Thông tin này chỉ dùng cho môi trường học tập, không dùng cho production.

Luồng chuyển tiền MVP: kiểm tra tài khoản nguồn/đích và số dư → trừ tiền nguồn, cộng tiền đích trong một transaction → ghi lịch sử giao dịch → trả mã giao dịch và trạng thái.

API chi tiết được chốt trong [corebank/README.md](corebank/README.md).

> Đây là bản khởi tạo. Công nghệ, API contract, dữ liệu và kiến trúc chi tiết sẽ được cập nhật dần.
