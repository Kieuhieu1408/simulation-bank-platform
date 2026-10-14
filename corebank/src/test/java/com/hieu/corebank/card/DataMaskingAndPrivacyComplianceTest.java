package com.hieu.corebank.card;

import com.hieu.corebank.account.api.dto.request.AccountCreateRequestDTO;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;
import com.hieu.corebank.account.application.command.AccountHandler;
import com.hieu.corebank.card.api.dto.request.CardGetRequestDTO;
import com.hieu.corebank.card.api.dto.request.IssueCardRequestDTO;
import com.hieu.corebank.card.api.dto.response.IssueCardResponseDTO;
import com.hieu.corebank.card.application.command.CardHandler;
import com.hieu.corebank.card.application.query.GetCardByIdQueryHandler;
import com.hieu.corebank.customer.api.dto.request.CustomerCreateRequestDTO;
import com.hieu.corebank.customer.api.dto.request.CustomerGetRequestDTO;
import com.hieu.corebank.customer.api.dto.response.CustomerResponseDTO;
import com.hieu.corebank.customer.application.command.CustomerHandler;
import com.hieu.corebank.customer.application.query.GetCustomerQueryHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class DataMaskingAndPrivacyComplianceTest {

    @Autowired
    private CustomerHandler customerHandler;

    @Autowired
    private GetCustomerQueryHandler getCustomerQueryHandler;

    @Autowired
    private AccountHandler accountHandler;

    @Autowired
    private CardHandler cardHandler;

    @Autowired
    private GetCardByIdQueryHandler getCardByIdHandler;

    @Test
    @DisplayName("AC-CB-11: Tuân thủ Nghị định 13/2023/NĐ-CP: Masking CCCD 012345****01 và PAN thẻ 4111-22XX-XXXX-3344")
    void testDataMaskingCompliance() {
        // 1. Tạo Customer với CCCD thật
        String realCcd = "012345678901";
        CustomerCreateRequestDTO custReq = CustomerCreateRequestDTO.builder()
                .nationalId(realCcd)
                .fullName("Nguyen Van Masking")
                .phoneNumber("0912345678")
                .email("masking@bank.com")
                .build();

        CustomerResponseDTO custRes = customerHandler.handle(custReq);
        assertThat(custRes.getNationalId()).isEqualTo("012345****01");

        // Truy vấn lại qua CustomerGetRequestDTO cũng phải masked
        CustomerResponseDTO queriedCust = getCustomerQueryHandler.handle(new CustomerGetRequestDTO(custRes.getCifNumber()));
        assertThat(queriedCust.getNationalId()).isEqualTo("012345****01");

        // 2. Tạo Account
        AccountResponseDTO account = accountHandler.handle(
                new AccountCreateRequestDTO(custRes.getCifNumber(), "VND", new BigDecimal("5000000.00")));

        // 3. Phát hành Thẻ và kiểm tra masking số thẻ
        IssueCardRequestDTO cardReq = IssueCardRequestDTO.builder()
                .accountId(account.getAccountId())
                .build();

        IssueCardResponseDTO cardRes = cardHandler.handle(cardReq);
        assertThat(cardRes).isNotNull();
        assertThat(cardRes.getCardId()).isNotNull();

        // Định dạng PAN phải đúng chuẩn PCI DSS: 4111-22XX-XXXX-3344 (hoặc 9704-XXXX-XXXX-XXXX)
        // Ví dụ: 9704-12XX-XXXX-3456 (4 số đầu - 2 số tiếp theo + XX-XXXX - 4 số cuối)
        assertThat(cardRes.getMaskedCardNumber()).matches("^[0-9]{4}-[0-9]{2}XX-XXXX-[0-9]{4}$");

        // Truy vấn lại qua CardGetRequestDTO cũng phải masked
        IssueCardResponseDTO queriedCard = getCardByIdHandler.handle(new CardGetRequestDTO(cardRes.getCardId()));
        assertThat(queriedCard.getMaskedCardNumber()).isEqualTo(cardRes.getMaskedCardNumber());

        // Đảm bảo không có CVV trong Response DTO
        // IssueCardResponseDTO không chứa field cvv
        boolean hasCvvField = false;
        try {
            IssueCardResponseDTO.class.getDeclaredField("cvv");
            hasCvvField = true;
        } catch (NoSuchFieldException ignored) {}
        assertThat(hasCvvField).isFalse();
    }
}
