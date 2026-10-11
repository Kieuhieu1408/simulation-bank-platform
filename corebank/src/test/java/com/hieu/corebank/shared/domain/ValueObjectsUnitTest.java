package com.hieu.corebank.shared.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Value Objects Pure Unit Tests (Make illegal states unrepresentable)")
public class ValueObjectsUnitTest {

    @Nested
    @DisplayName("Currency Value Object")
    class CurrencyTests {
        @Test
        void validCurrencyCodes() {
            Currency vnd = Currency.of("VND");
            assertThat(vnd.code()).isEqualTo("VND");
            assertThat(vnd.fractionDigits()).isEqualTo(0);

            Currency usd = Currency.of("USD");
            assertThat(usd.code()).isEqualTo("USD");
            assertThat(usd.fractionDigits()).isEqualTo(2);

            Currency eur = Currency.of("eur"); // auto upper
            assertThat(eur.code()).isEqualTo("EUR");
        }

        @Test
        void invalidCurrencyCodes_ThrowException() {
            assertThatThrownBy(() -> Currency.of(null))
                    .isInstanceOf(DomainException.class);
            assertThatThrownBy(() -> Currency.of("VN"))
                    .isInstanceOf(DomainException.class);
            assertThatThrownBy(() -> Currency.of("VIETNAM"))
                    .isInstanceOf(DomainException.class);
            assertThatThrownBy(() -> Currency.of("123"))
                    .isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("Money Value Object")
    class MoneyTests {
        @Test
        void validMoneyCreationAndScaling() {
            Money m1 = Money.of(new BigDecimal("100000"), Currency.VND);
            assertThat(m1.amount().scale()).isEqualTo(0);
            assertThat(m1.amount()).isEqualByComparingTo("100000");

            Money m2 = Money.of(new BigDecimal("50.5"), Currency.USD);
            assertThat(m2.amount().scale()).isEqualTo(2);
            assertThat(m2.amount()).isEqualByComparingTo("50.50");
        }

        @Test
        void scaleViolation_ThrowsException() {
            // VND không được có số thập phân
            assertThatThrownBy(() -> Money.of(new BigDecimal("100.5"), Currency.VND))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("fraction digits");

            // USD không được có quá 2 số thập phân
            assertThatThrownBy(() -> Money.of(new BigDecimal("10.123"), Currency.USD))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("fraction digits");
        }

        @Test
        void nullInputs_ThrowException() {
            assertThatThrownBy(() -> Money.of(null, Currency.VND))
                    .isInstanceOf(DomainException.class);
            assertThatThrownBy(() -> Money.of(BigDecimal.TEN, (Currency) null))
                    .isInstanceOf(DomainException.class);
        }

        @Test
        void requirePositive_EnforcesPositive() {
            Money positive = Money.of(100, Currency.VND).requirePositive();
            assertThat(positive.isPositive()).isTrue();

            Money zero = Money.zero(Currency.VND);
            assertThatThrownBy(zero::requirePositive)
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("must be positive");

            Money negative = Money.of(-50, Currency.VND);
            assertThatThrownBy(negative::requirePositive)
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("must be positive");
        }

        @Test
        void mathOperations_SameCurrency() {
            Money a = Money.of(100, Currency.VND);
            Money b = Money.of(50, Currency.VND);

            assertThat(a.add(b)).isEqualTo(Money.of(150, Currency.VND));
            assertThat(a.subtract(b)).isEqualTo(Money.of(50, Currency.VND));
            assertThat(a.isGreaterThan(b)).isTrue();
            assertThat(a.isGreaterThanOrEqual(b)).isTrue();
            assertThat(b.isLessThan(a)).isTrue();
            assertThat(a.compareTo(b)).isPositive();
        }

        @Test
        void mathOperations_DifferentCurrency_ThrowsException() {
            Money vnd = Money.of(100, Currency.VND);
            Money usd = Money.of(10, Currency.USD);

            assertThatThrownBy(() -> vnd.add(usd))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Currency mismatch");

            assertThatThrownBy(() -> vnd.subtract(usd))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Currency mismatch");

            assertThatThrownBy(() -> vnd.isGreaterThanOrEqual(usd))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Currency mismatch");
        }
    }

    @Nested
    @DisplayName("NationalId Value Object")
    class NationalIdTests {
        @Test
        void validCccdAndMasking() {
            NationalId id = NationalId.of("012345000001");
            assertThat(id.value()).isEqualTo("012345000001");
            assertThat(id.mask()).isEqualTo("012345****01");
        }

        @Test
        void validPassport() {
            NationalId passport = NationalId.of("B1234567");
            assertThat(passport.value()).isEqualTo("B1234567");
            assertThat(passport.mask()).isEqualTo("B1****67");
        }

        @Test
        void invalidNationalId_ThrowsException() {
            assertThatThrownBy(() -> NationalId.of(null))
                    .isInstanceOf(DomainException.class);
            assertThatThrownBy(() -> NationalId.of("12345")) // too short
                    .isInstanceOf(DomainException.class);
            assertThatThrownBy(() -> NationalId.of("012345000001234")) // too long
                    .isInstanceOf(DomainException.class);
            assertThatThrownBy(() -> NationalId.of("01234500000A")) // contains letter in CCCD
                    .isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("CardNumber Value Object")
    class CardNumberTests {
        @Test
        void validCardNumberAndLuhn() {
            // Sinh số thẻ hợp lệ qua Luhn generator
            CardNumber card = CardNumber.generate("9704");
            assertThat(card.value()).hasSize(16);
            assertThat(card.value()).startsWith("9704");
            assertThat(CardNumber.isValidLuhn(card.value())).isTrue();

            // Test mask D13 format: 4111-22XX-XXXX-3344
            // Dùng số thẻ Visa test chuẩn Luhn: 4111 2212 3456 3344 -> tính checksum
            CardNumber card2 = CardNumber.of(card.value());
            assertThat(card2.mask()).matches("^\\d{4}-\\d{2}XX-XXXX-\\d{4}$");
        }

        @Test
        void invalidLuhnChecksum_ThrowsException() {
            // 16 chữ số nhưng sai checksum
            assertThatThrownBy(() -> CardNumber.of("1234567812345678"))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Luhn check failed");
        }

        @Test
        void invalidFormat_ThrowsException() {
            assertThatThrownBy(() -> CardNumber.of(null))
                    .isInstanceOf(DomainException.class);
            assertThatThrownBy(() -> CardNumber.of("1234"))
                    .isInstanceOf(DomainException.class);
            assertThatThrownBy(() -> CardNumber.of("411122223333444A"))
                    .isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("Id Value Objects")
    class IdValueObjectsTests {
        @Test
        void accountIdValidation() {
            AccountId id = AccountId.of("ACC-12345");
            assertThat(id.value()).isEqualTo("ACC-12345");
            assertThat(AccountId.generate().value()).isNotBlank();

            assertThatThrownBy(() -> AccountId.of(null)).isInstanceOf(DomainException.class);
            assertThatThrownBy(() -> AccountId.of("  ")).isInstanceOf(DomainException.class);
        }

        @Test
        void accountNumberValidation() {
            AccountNumber num = AccountNumber.of("100000000001");
            assertThat(num.value()).isEqualTo("100000000001");

            assertThatThrownBy(() -> AccountNumber.of(null)).isInstanceOf(DomainException.class);
            assertThatThrownBy(() -> AccountNumber.of("12345")).isInstanceOf(DomainException.class); // < 10 digits
            assertThatThrownBy(() -> AccountNumber.of("10000000000A")).isInstanceOf(DomainException.class); // contains letter
        }

        @Test
        void transferIdValidation() {
            TransferId tid = TransferId.of("TX-999");
            assertThat(tid.value()).isEqualTo("TX-999");
            assertThat(TransferId.generate().value()).isNotBlank();

            assertThatThrownBy(() -> TransferId.of(null)).isInstanceOf(DomainException.class);
            assertThatThrownBy(() -> TransferId.of("")).isInstanceOf(DomainException.class);
        }

        @Test
        void customerIdValidation() {
            CustomerId cid = CustomerId.of("CIF00000001");
            assertThat(cid.value()).isEqualTo("CIF00000001");

            assertThatThrownBy(() -> CustomerId.of(null)).isInstanceOf(DomainException.class);
            assertThatThrownBy(() -> CustomerId.of("   ")).isInstanceOf(DomainException.class);
        }

        @Test
        void emailValidation() {
            Email email = Email.of("test.user@bank.com");
            assertThat(email.value()).isEqualTo("test.user@bank.com");

            assertThatThrownBy(() -> Email.of(null)).isInstanceOf(DomainException.class);
            assertThatThrownBy(() -> Email.of("invalid-email")).isInstanceOf(DomainException.class);
            assertThatThrownBy(() -> Email.of("@bank.com")).isInstanceOf(DomainException.class);
        }

        @Test
        void phoneNumberValidation() {
            PhoneNumber phone1 = PhoneNumber.of("0901234567");
            assertThat(phone1.value()).isEqualTo("0901234567");

            PhoneNumber phone2 = PhoneNumber.of("+84901234567");
            assertThat(phone2.value()).isEqualTo("+84901234567");

            assertThatThrownBy(() -> PhoneNumber.of(null)).isInstanceOf(DomainException.class);
            assertThatThrownBy(() -> PhoneNumber.of("0123456")).isInstanceOf(DomainException.class); // too short
            assertThatThrownBy(() -> PhoneNumber.of("0201234567")).isInstanceOf(DomainException.class); // prefix not mobile
        }
    }
}
