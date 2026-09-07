package com.hieu.moneybank.shared.error;

/**
 * Một dependency bắt buộc không khả dụng và có bằng chứng là chưa gây side
 * effect (ví dụ Keycloak token exchange lỗi, connect refused trước khi gửi lệnh).
 *
 * <p>Không dùng exception này cho tình huống đã gửi lệnh xuống Corebank rồi mới
 * mất response. Trường hợp đó là {@link ErrorCategory#AMBIGUOUS} và phải chuyển
 * transaction sang {@code UNKNOWN}, không phải trả lỗi kỹ thuật.
 */
public class DependencyUnavailableException extends MoneyBankException {

    private final String dependency;

    public DependencyUnavailableException(String dependency, Throwable cause) {
        super(ErrorCode.DEPENDENCY_UNAVAILABLE, "Dependency không khả dụng: " + dependency, cause);
        this.dependency = dependency;
    }

    public String dependency() {
        return dependency;
    }
}
