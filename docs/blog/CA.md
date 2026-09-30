# Clean Architecture trong Corebank

## 1. Giới thiệu
Clean Architecture (CA), được Robert C. Martin (Uncle Bob) giới thiệu, là một triết lý thiết kế phần mềm hướng tới việc tách biệt các mối quan tâm (separation of concerns). Mục tiêu tối thượng của CA là bảo vệ Business Logic (Domain) khỏi các tác động của công nghệ bên ngoài (Framework, Database, UI).

Trong CA, kiến trúc được chia thành các vòng tròn đồng tâm:
1. **Entities (Enterprise Business Rules):** Chứa core business logic, hoàn toàn không biết về bất kỳ công nghệ nào khác.
2. **Use Cases (Application Business Rules):** Chứa logic điều phối ứng dụng, định nghĩa các interfaces (Ports).
3. **Interface Adapters:** Chuyển đổi dữ liệu giữa Use Cases và External Agencies (Web, DB).
4. **Frameworks & Drivers:** Chứa các công cụ, database, UI framework.

Quy tắc cốt lõi là **The Dependency Rule**: Phụ thuộc source code chỉ được trỏ vào trong (từ lớp ngoài vào lớp trong).

## 2. Thực trạng Corebank hiện tại (V1 & V2)
Dự án đang trong quá trình chuyển đổi, tuy nhiên vẫn còn tồn đọng những "anti-pattern" so với chuẩn Clean Architecture:

*   **Entities bị ô nhiễm (Leaky Entities):** Các class như `Customer`, `BankCard` gắn chặt với `@Entity`, `@Table` của JPA. Đây là các Anemic Data Models, không phải Domain Entities thực sự.
*   **Application Layer bị phụ thuộc Infrastructure:** `CreateAccountCommandHandler` gọi trực tiếp `JdbcClient` để thực thi câu lệnh SQL Oracle (`SELECT ... FROM DUAL`). Điều này vi phạm nghiêm trọng The Dependency Rule.
*   **Bỏ qua Ports & Adapters:** Các Use Case (Handlers) inject trực tiếp Spring Data Repositories (`CustomerRepository`). Không có sự đảo ngược phụ thuộc (Dependency Inversion).
*   **Sự phân mảnh:** Một nửa dự án dùng Event Sourcing (Account, Transfer), nửa còn lại dùng CRUD thuần túy.

## 3. Hướng đi cho V3: Làm sạch kiến trúc
Trong phiên bản V3, chúng ta sẽ áp dụng triệt để Clean Architecture:

1.  **Tách biệt hoàn toàn Core Domain:**
    *   Tạo package `domain` chứa các Pure Java class (Aggregate, Entity, Value Object) không chứa bất kỳ annotation nào của Framework (Spring, JPA).
2.  **Áp dụng Ports & Adapters:**
    *   Application layer định nghĩa các Interfaces (vd: `AccountRepositoryPort`, `SequenceGeneratorPort`).
    *   Infrastructure layer implement các interface này (vd: `JpaAccountRepositoryAdapter`).
3.  **Tách Use Case rõ ràng:**
    *   Mỗi Command/Query Handler chỉ đóng vai trò Use Case: Nhận Request, gọi Port lấy Aggregate, gọi hàm nghiệp vụ trên Aggregate, lưu Aggregate thông qua Port. Không tự viết logic nghiệp vụ hay query SQL.