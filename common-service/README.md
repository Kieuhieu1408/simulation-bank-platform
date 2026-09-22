# Common Service

Module `common-service` cung cấp các thư viện và cấu trúc dùng chung cho toàn bộ Simulation Bank Platform. Việc sử dụng `common-service` giúp giảm thiểu duplicate code và đồng nhất kiến trúc (Security, CQRS, Idempotency, Outbox, Exception Handling) giữa các dịch vụ như `corebank` và `money-bank`.

## Cấu trúc thư mục chính

- `com.hieu.common.cqrs`: Định nghĩa các interface lõi cho mô hình CQRS (`Command`, `Query`, `CommandHandler`, `QueryHandler`, `Dispatcher`, `SpringDispatcher`).
- `com.hieu.common.idempotency`: Cung cấp cơ chế Idempotency Pattern để đảm bảo các thao tác chuyển tiền, tạo tài khoản không bị xử lý trùng lặp do retry từ phía client. Bao gồm: `IdempotencyRecord`, `IdempotencyService`, `IdempotencyRecordRepository`.
- `com.hieu.common.outbox`: Cung cấp cơ chế Transactional Outbox Pattern (`OutboxEvent`, `OutboxDispatcher`, `OutboxDispatchStore`) đảm bảo tính toàn vẹn dữ liệu khi publish các event (Kafka, MQ) từ các domain aggregate.
- `com.hieu.common.security`: Chứa cấu hình bảo mật chuẩn (`SecurityConfig`, `CommonSecurityProperties`) dùng cho các Resource Server (JWT verification, xác minh token issuer, audience).
- `com.hieu.common.exception`: Các exception chuẩn cho toàn hệ thống (`NotFoundException`, `IdempotencyConflictException`, v.v.).

## Hướng dẫn sử dụng

### 1. Thêm dependency

```xml
<dependency>
    <groupId>com.hieu</groupId>
    <artifactId>common-service</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. Cấu hình ComponentScan và EntityScan

Để Spring Boot của dịch vụ sử dụng tự động nhận diện các bean và JPA Entity từ `common-service`, bạn cần thêm khai báo quét package trong file chạy chính của ứng dụng:

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.hieu.your_service", "com.hieu.common"})
@EntityScan(basePackages = {"com.hieu.your_service", "com.hieu.common"})
@EnableJpaRepositories(basePackages = {"com.hieu.your_service", "com.hieu.common"})
public class YourServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(YourServiceApplication.class, args);
    }
}
```

### 3. Cấu hình ứng dụng (application.yaml)

Các bean trong `common-service` sẽ yêu cầu một số properties bắt buộc để có thể khởi chạy (Ví dụ: `money-bank.security`). Bạn cần cung cấp chúng:

```yaml
money-bank:
  security:
    jwk-set-uri: ${KEYCLOAK_ISSUER_URI}/protocol/openid-connect/certs
    issuer: ${KEYCLOAK_ISSUER_URI}
    audience: your-service-audience
  idempotency:
    resultRetention: 24h
```

### 4. CQRS Pattern

Các Use Case/Handler trong hệ thống nên triển khai các interface từ `common-service`:

```java
@Component
public class CreateAccountCommandHandler implements CommandHandler<CreateAccountCommand, AccountResponseDTO> {
    @Override
    public AccountResponseDTO handle(CreateAccountCommand command) {
        // Implementation
    }
}
```

Và sử dụng `Dispatcher` để gọi lệnh từ RestController:

```java
@RestController
@RequiredArgsConstructor
public class AccountController {
    private final Dispatcher dispatcher;

    @PostMapping
    public AccountResponseDTO create(@RequestBody CreateAccountCommand command) {
        return dispatcher.dispatch(command);
    }
}
```
