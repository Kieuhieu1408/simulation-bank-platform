package com.hieu.moneybank.service.otp;

public interface OtpService {

    /**
     * Sinh và lưu mã OTP cho proposal chuyển tiền.
     * Mặc định cho môi trường test trả về 111111.
     *
     * @param proposalId ID của proposal
     * @return mã OTP
     */
    String generateOtp(String proposalId);

    /**
     * Xác thực mã OTP của proposal.
     *
     * @param proposalId ID của proposal
     * @param inputOtp mã OTP người dùng nhập
     * @throws com.hieu.moneybank.exception.BusinessException nếu OTP sai
     */
    void validateOtp(String proposalId, String inputOtp);
}
