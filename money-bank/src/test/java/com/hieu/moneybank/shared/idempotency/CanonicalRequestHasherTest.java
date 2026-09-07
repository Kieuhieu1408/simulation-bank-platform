package com.hieu.moneybank.shared.idempotency;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Kiểm chứng {@code AD-DC-T04}: hash phải đổi khi bất kỳ field nghiệp vụ có ý
 * nghĩa nào đổi, và phải giữ nguyên khi chỉ khác cách biểu diễn.
 */
class CanonicalRequestHasherTest {

    private final CanonicalRequestHasher hasher = new CanonicalRequestHasher();

    private CanonicalPayload transferPayload() {
        return CanonicalPayload.builder()
                .text("sourceAccount", "1234567890")
                .text("destinationAccount", "9876543210")
                .money("amount", new BigDecimal("1500000"), "VND")
                .text("description", "Chuyen tien");
    }

    @Test
    @DisplayName("Cùng dữ liệu cho cùng hash")
    void sameDataProducesSameHash() {
        assertThat(hasher.hash(transferPayload()))
                .isEqualTo(hasher.hash(transferPayload()));
    }

    @Test
    @DisplayName("Thứ tự thêm field không ảnh hưởng hash")
    void fieldOrderDoesNotMatter() {
        String reversed = hasher.hash(CanonicalPayload.builder()
                .text("description", "Chuyen tien")
                .money("amount", new BigDecimal("1500000"), "VND")
                .text("destinationAccount", "9876543210")
                .text("sourceAccount", "1234567890"));

        assertThat(reversed).isEqualTo(hasher.hash(transferPayload()));
    }

    @Test
    @DisplayName("Đổi số tiền thì hash đổi")
    void amountChangeChangesHash() {
        String modified = hasher.hash(CanonicalPayload.builder()
                .text("sourceAccount", "1234567890")
                .text("destinationAccount", "9876543210")
                .money("amount", new BigDecimal("1500001"), "VND")
                .text("description", "Chuyen tien"));

        assertThat(modified).isNotEqualTo(hasher.hash(transferPayload()));
    }

    @Test
    @DisplayName("Đổi tài khoản đích thì hash đổi")
    void destinationChangeChangesHash() {
        String modified = hasher.hash(CanonicalPayload.builder()
                .text("sourceAccount", "1234567890")
                .text("destinationAccount", "9876543211")
                .money("amount", new BigDecimal("1500000"), "VND")
                .text("description", "Chuyen tien"));

        assertThat(modified).isNotEqualTo(hasher.hash(transferPayload()));
    }

    @Test
    @DisplayName("Đổi description thì hash đổi vì description thuộc contract")
    void descriptionChangeChangesHash() {
        // Nếu nghiệp vụ chốt description KHÔNG thuộc canonical hash
        // (AD-API-OPEN-01) thì phải sửa cả test này cùng lúc, không sửa lặng lẽ
        // phía implementation.
        String modified = hasher.hash(CanonicalPayload.builder()
                .text("sourceAccount", "1234567890")
                .text("destinationAccount", "9876543210")
                .money("amount", new BigDecimal("1500000"), "VND")
                .text("description", "Chuyen tien hoc phi"));

        assertThat(modified).isNotEqualTo(hasher.hash(transferPayload()));
    }

    @Test
    @DisplayName("Khác cách viết số tiền nhưng cùng giá trị thì cùng hash")
    void equivalentAmountRepresentationsProduceSameHash() {
        String withDecimals = hasher.hash(CanonicalPayload.builder()
                .money("amount", new BigDecimal("100.00"), "USD"));
        String withoutDecimals = hasher.hash(CanonicalPayload.builder()
                .money("amount", new BigDecimal("100"), "USD"));

        assertThat(withDecimals).isEqualTo(withoutDecimals);
    }

    @Test
    @DisplayName("Số tiền có độ chính xác cao hơn đơn vị tiền tệ bị từ chối")
    void amountBeyondCurrencyScaleIsRejected() {
        // 100.5 VND không tồn tại. Nếu im lặng làm tròn, hash sẽ khác số tiền
        // thực sự gửi xuống Corebank.
        assertThatThrownBy(() -> CanonicalPayload.builder()
                .money("amount", new BigDecimal("100.5"), "VND"))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    @DisplayName("Khoảng trắng đầu/cuối không tạo hash khác")
    void whitespaceIsNormalized() {
        String padded = hasher.hash(CanonicalPayload.builder()
                .text("sourceAccount", "  1234567890  "));
        String plain = hasher.hash(CanonicalPayload.builder()
                .text("sourceAccount", "1234567890"));

        assertThat(padded).isEqualTo(plain);
    }

    @Test
    @DisplayName("Field null và field rỗng là hai ý định khác nhau")
    void nullDiffersFromEmpty() {
        String nullValue = hasher.hash(CanonicalPayload.builder().text("description", null));
        String emptyValue = hasher.hash(CanonicalPayload.builder().text("description", ""));

        assertThat(nullValue).isNotEqualTo(emptyValue);
    }

    @Test
    @DisplayName("Hash là SHA-256 hex đúng độ dài")
    void hashHasExpectedLength() {
        assertThat(hasher.hash(transferPayload()))
                .hasSize(CanonicalRequestHasher.HASH_LENGTH)
                .matches("[0-9a-f]{64}");
    }

    @Test
    @DisplayName("Canonical form mang version để đổi quy tắc là breaking change tường minh")
    void canonicalFormCarriesVersion() {
        assertThat(transferPayload().canonicalForm())
                .startsWith(CanonicalPayload.CANONICAL_VERSION);
    }
}
