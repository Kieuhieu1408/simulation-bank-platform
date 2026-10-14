package com.hieu.moneybank.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "money-bank.otp")
@Data
public class OtpProperties {
    /**
     * Mã OTP mặc định dùng cho môi trường test (mặc định: 111111).
     */
    private String defaultTestOtp = "111111";

    /**
     * Thời gian hết hạn của OTP (tính bằng giây).
     */
    private long expiresInSeconds = 300;
}
