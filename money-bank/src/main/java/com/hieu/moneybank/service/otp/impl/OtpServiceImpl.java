package com.hieu.moneybank.service.otp.impl;

import com.hieu.moneybank.config.OtpProperties;
import com.hieu.moneybank.exception.BusinessException;
import com.hieu.moneybank.service.otp.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final OtpProperties otpProperties;

    // Cache lưu trữ OTP tạm thời: proposalId -> OtpEntry
    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

    private record OtpEntry(String otp, Instant expiresAt) {}

    @Override
    public String generateOtp(String proposalId) {
        String otp = otpProperties.getDefaultTestOtp();
        Instant expiresAt = Instant.now().plusSeconds(otpProperties.getExpiresInSeconds());
        otpStore.put(proposalId, new OtpEntry(otp, expiresAt));
        log.info("eventName=OTP_GENERATED proposalId={}", proposalId);
        return otp;
    }

    @Override
    public void validateOtp(String proposalId, String inputOtp) {
        if (inputOtp == null || inputOtp.isBlank()) {
            throw new BusinessException("Mã OTP không được để trống");
        }

        String defaultOtp = otpProperties.getDefaultTestOtp();
        OtpEntry entry = otpStore.get(proposalId);

        // Kiểm tra hết hạn nếu có trong store
        if (entry != null && Instant.now().isAfter(entry.expiresAt())) {
            otpStore.remove(proposalId);
            log.warn("eventName=OTP_EXPIRED proposalId={}", proposalId);
            throw new BusinessException("Mã OTP đã hết hạn");
        }

        String actualOtp = inputOtp.trim();
        String expectedOtp = (entry != null) ? entry.otp() : defaultOtp;

        if (!expectedOtp.equals(actualOtp) && !defaultOtp.equals(actualOtp)) {
            log.warn("eventName=OTP_VALIDATION_FAILED proposalId={}", proposalId);
            throw new BusinessException("Mã OTP không chính xác");
        }

        // Dùng xong xóa khỏi store để tránh replay
        otpStore.remove(proposalId);
        log.info("eventName=OTP_VALIDATION_SUCCESS proposalId={}", proposalId);
    }
}
