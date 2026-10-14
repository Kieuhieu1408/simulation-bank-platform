package com.hieu.moneybank.service.otp;

import com.hieu.moneybank.config.OtpProperties;
import com.hieu.moneybank.exception.BusinessException;
import com.hieu.moneybank.service.otp.impl.OtpServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OtpServiceImplTest {

    private OtpProperties otpProperties;
    private OtpServiceImpl otpService;

    @BeforeEach
    void setUp() {
        otpProperties = new OtpProperties();
        otpProperties.setDefaultTestOtp("111111");
        otpProperties.setExpiresInSeconds(300);
        otpService = new OtpServiceImpl(otpProperties);
    }

    @Test
    @DisplayName("Sinh OTP mặc định trả về 111111 cho môi trường test")
    void shouldGenerateDefaultTestOtp() {
        String proposalId = "prop-test-1";
        String otp = otpService.generateOtp(proposalId);
        assertThat(otp).isEqualTo("111111");
    }

    @Test
    @DisplayName("Xác thực thành công với OTP 111111")
    void shouldValidateSuccessfullyWithDefaultOtp() {
        String proposalId = "prop-test-2";
        otpService.generateOtp(proposalId);

        assertThatCode(() -> otpService.validateOtp(proposalId, "111111"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Xác thực thành công khi không truyền OTP (mặc định dùng test OTP)")
    void shouldValidateSuccessfullyWhenOtpNotProvided() {
        String proposalId = "prop-test-3";
        otpService.generateOtp(proposalId);

        assertThatCode(() -> otpService.validateOtp(proposalId, null))
                .doesNotThrowAnyException();
        assertThatCode(() -> otpService.validateOtp(proposalId, ""))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Ném BusinessException khi nhập sai OTP")
    void shouldThrowExceptionWhenOtpWrong() {
        String proposalId = "prop-test-4";
        otpService.generateOtp(proposalId);

        assertThatThrownBy(() -> otpService.validateOtp(proposalId, "999999"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Mã OTP không chính xác");
    }
}
