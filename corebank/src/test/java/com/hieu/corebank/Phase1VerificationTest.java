package com.hieu.corebank;

import com.hieu.common.constant.TransactionStatus;
import com.hieu.corebank.account.api.dto.request.AccountCreateRequestDTO;
import com.hieu.corebank.account.api.dto.response.AccountBalanceResponseDTO;
import com.hieu.corebank.account.api.dto.response.AccountResponseDTO;
import com.hieu.corebank.account.application.command.CreateAccountCommand;
import com.hieu.corebank.account.application.command.CreateAccountCommandHandler;
import com.hieu.corebank.account.application.query.*;
import com.hieu.corebank.account.domain.AccountAggregate;
import com.hieu.corebank.account.infrastructure.projection.AccountView;
import com.hieu.corebank.account.infrastructure.projection.AccountViewRepository;
import com.hieu.corebank.card.api.dto.request.IssueCardRequestDTO;
import com.hieu.corebank.card.api.dto.response.IssueCardResponseDTO;
import com.hieu.corebank.card.application.command.IssueCardCommand;
import com.hieu.corebank.card.application.command.IssueCardCommandHandler;
import com.hieu.corebank.card.application.query.GetCardByIdQuery;
import com.hieu.corebank.card.application.query.GetCardByIdQueryHandler;
import com.hieu.corebank.card.application.query.GetCardsByCifQuery;
import com.hieu.corebank.card.application.query.GetCardsByCifQueryHandler;
import com.hieu.corebank.card.domain.CardRepository;
import com.hieu.corebank.customer.api.dto.request.CustomerCreateRequestDTO;
import com.hieu.corebank.customer.api.dto.response.CustomerResponseDTO;
import com.hieu.corebank.customer.application.command.CreateCustomerCommand;
import com.hieu.corebank.customer.application.command.CreateCustomerCommandHandler;
import com.hieu.corebank.customer.domain.Customer;
import com.hieu.corebank.customer.domain.CustomerRepository;
import com.hieu.corebank.shared.domain.BusinessException;
import com.hieu.corebank.shared.domain.ConflictException;
import com.hieu.corebank.shared.infrastructure.eventsourcing.EventStore;
import com.hieu.corebank.shared.infrastructure.web.RequestLoggingConfig;
import com.hieu.corebank.transfer.api.dto.request.TransferRequestDTO;
import com.hieu.corebank.transfer.api.dto.response.TransferResponseDTO;
import com.hieu.corebank.transfer.application.command.InitiateTransferCommand;
import com.hieu.corebank.transfer.application.command.InitiateTransferHandler;
import com.hieu.corebank.transfer.infrastructure.projection.TransferViewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest
@ActiveProfiles("test")
public class Phase1VerificationTest {

    @Autowired
    private CreateCustomerCommandHandler createCustomerHandler;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CreateAccountCommandHandler createAccountHandler;

    @Autowired
    private GetAccountByIdQueryHandler getAccountByIdHandler;

    @Autowired
    private GetAccountsByCifQueryHandler getAccountsByCifHandler;

    @Autowired
    private GetAccountBalanceQueryHandler getAccountBalanceHandler;

    @Autowired
    private IssueCardCommandHandler issueCardHandler;

    @Autowired
    private GetCardByIdQueryHandler getCardByIdHandler;

    @Autowired
    private GetCardsByCifQueryHandler getCardsByCifHandler;

    @MockitoSpyBean
    private EventStore eventStore;

    @Autowired
    private InitiateTransferHandler transferHandler;

    @Autowired
    private AccountViewRepository accountViewRepository;

    @Autowired
    private TransferViewRepository transferViewRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private RequestLoggingConfig requestLoggingConfig;

    @Test
    @DisplayName("Fix B3: Atomic Transaction - Nếu append(destination) lỗi, append(source) cũng bị rollback, tiền không bị mất")
    void testB3_AtomicTransactionRollsBackOnDestinationFailure() {
        String cif = ("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase();
        createCustomerHandler.handle(new CreateCustomerCommand(cif));

        AccountResponseDTO src = createAccountHandler.handle(new CreateAccountCommand(cif, "VND", new BigDecimal("1000000.00")));
        AccountResponseDTO dst = createAccountHandler.handle(new CreateAccountCommand(cif, "VND", new BigDecimal("500000.00")));

        // Giả lập lỗi ở bước append destination
        doAnswer(invocation -> {
            AccountAggregate agg = invocation.getArgument(0);
            if (agg.accountId().equals(dst.getAccountId())) {
                throw new RuntimeException("Simulated Destination Failure");
            }
            return invocation.callRealMethod();
        }).when(eventStore).append(any());

        InitiateTransferCommand cmd = InitiateTransferCommand.builder()
                .sourceAccountId(src.getAccountId())
                .destinationAccountId(dst.getAccountId())
                .amount(new BigDecimal("200000.00"))
                .currency("VND")
                .idempotencyKey("fail-tx-" + UUID.randomUUID())
                .build();

        assertThatThrownBy(() -> transferHandler.handle(cmd))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Simulated Destination Failure");

        // Verify source balance KHÔNG bị trừ, vẫn nguyên 1,000,000 VND
        AccountView srcView = accountViewRepository.findById(src.getAccountId()).orElseThrow();
        assertThat(srcView.getAvailableBalance()).isEqualByComparingTo(new BigDecimal("1000000.00"));
        assertThat(srcView.getActualBalance()).isEqualByComparingTo(new BigDecimal("1000000.00"));
    }

    @Test
    @DisplayName("Fix B1: Tạo account V2 -> Projector tự động chạy (BEFORE_COMMIT) -> account_view có dữ liệu ngay")
    void testB1_ProjectorAutomaticallyTriggered() {
        String cif = ("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase();
        createCustomerHandler.handle(new CreateCustomerCommand(cif));

        AccountCreateRequestDTO request = new AccountCreateRequestDTO(cif, "VND", new BigDecimal("1000000.00"));
        AccountResponseDTO created = createAccountHandler.handle(request.toCommand());

        assertThat(created).isNotNull();
        assertThat(created.getAccountId()).isNotBlank();

        // Kiểm tra account_view đã được cập nhật bởi AccountProjector
        AccountView view = accountViewRepository.findById(created.getAccountId()).orElse(null);
        assertThat(view).isNotNull();
        assertThat(view.getAccountNumber()).isEqualTo(created.getAccountNumber());
        assertThat(view.getCustomerId()).isEqualTo(cif);
        assertThat(view.getAvailableBalance()).isEqualByComparingTo(new BigDecimal("1000000.00"));
        assertThat(view.getActualBalance()).isEqualByComparingTo(new BigDecimal("1000000.00"));

        // Kiểm tra qua GetAccountBalanceQueryHandler
        AccountBalanceResponseDTO balance = getAccountBalanceHandler.handle(new GetAccountBalanceQuery(created.getAccountId()));
        assertThat(balance).isNotNull();
        assertThat(balance.getAvailableBalance()).isEqualByComparingTo(new BigDecimal("1000000.00"));
    }

    @Test
    @DisplayName("Fix B2: Query handlers đọc account_view thành công sau khi tạo account V2; Issue card hoạt động")
    void testB2_QueryHandlersAndCardIssueReadAccountView() {
        String cif = ("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase();
        createCustomerHandler.handle(new CreateCustomerCommand(cif));

        AccountCreateRequestDTO accReq = new AccountCreateRequestDTO(cif, "VND", new BigDecimal("500000.00"));
        AccountResponseDTO account = createAccountHandler.handle(accReq.toCommand());

        // GetAccountByIdQueryHandler đọc từ account_view
        AccountResponseDTO fetched = getAccountByIdHandler.handle(new GetAccountByIdQuery(account.getAccountId()));
        assertThat(fetched).isNotNull();
        assertThat(fetched.getAccountId()).isEqualTo(account.getAccountId());
        assertThat(fetched.getBalance()).isEqualByComparingTo(new BigDecimal("500000.00"));

        // GetAccountsByCifQueryHandler đọc từ account_view
        List<AccountResponseDTO> accounts = getAccountsByCifHandler.handle(new GetAccountsByCifQuery(cif));
        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).getAccountId()).isEqualTo(account.getAccountId());

        // IssueCardCommandHandler đọc account_view và phát hành thẻ
        IssueCardRequestDTO cardReq = new IssueCardRequestDTO(account.getAccountId());
        IssueCardResponseDTO issuedCard = issueCardHandler.handle(cardReq.toCommand());
        assertThat(issuedCard).isNotNull();
        assertThat(issuedCard.getCardId()).isNotBlank();
        assertThat(issuedCard.getAccountId()).isEqualTo(account.getAccountId());
        assertThat(issuedCard.getCifNumber()).isEqualTo(cif);
        assertThat(issuedCard.getMaskedCardNumber()).contains("XX-XXXX");

        // Tra cứu thẻ theo ID và theo CIF
        IssueCardResponseDTO cardById = getCardByIdHandler.handle(new GetCardByIdQuery(issuedCard.getCardId()));
        assertThat(cardById.getCardId()).isEqualTo(issuedCard.getCardId());

        List<IssueCardResponseDTO> cardsByCif = getCardsByCifHandler.handle(new GetCardsByCifQuery(cif));
        assertThat(cardsByCif).hasSize(1);
        assertThat(cardsByCif.get(0).getCardId()).isEqualTo(issuedCard.getCardId());
    }

    @Test
    @DisplayName("Fix B4: Idempotency độc lập tại Corebank - Replay kết quả khi trùng key & payload, 409 khi sai lệch payload")
    void testB4_IdempotencyReplayAndConflict() {
        String cif = ("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase();
        createCustomerHandler.handle(new CreateCustomerCommand(cif));

        AccountResponseDTO src = createAccountHandler.handle(new CreateAccountCommand(cif, "VND", new BigDecimal("2000000.00")));
        AccountResponseDTO dst = createAccountHandler.handle(new CreateAccountCommand(cif, "VND", new BigDecimal("500000.00")));

        String idempotencyKey = "idem-" + UUID.randomUUID();
        TransferRequestDTO req1 = TransferRequestDTO.builder()
                .sourceAccountId(src.getAccountId())
                .destinationAccountId(dst.getAccountId())
                .amount(new BigDecimal("300000.00"))
                .currency("VND")
                .idempotencyKey(idempotencyKey)
                .description("Chuyen tien lan 1")
                .build();

        // Gửi lần 1: thành công
        TransferResponseDTO res1 = transferHandler.handle(req1.toCommand());
        assertThat(res1.getStatus()).isEqualTo(TransactionStatus.SUCCESS);
        String expectedTransferId = UUID.nameUUIDFromBytes(
                (src.getAccountId() + ":" + idempotencyKey).getBytes(StandardCharsets.UTF_8)
        ).toString();
        assertThat(res1.getTransactionId()).isEqualTo(expectedTransferId);

        // Kiểm tra số dư sau lần 1
        AccountView srcView1 = accountViewRepository.findById(src.getAccountId()).orElseThrow();
        AccountView dstView1 = accountViewRepository.findById(dst.getAccountId()).orElseThrow();
        assertThat(srcView1.getAvailableBalance()).isEqualByComparingTo(new BigDecimal("1700000.00"));
        assertThat(dstView1.getAvailableBalance()).isEqualByComparingTo(new BigDecimal("800000.00"));

        // Gửi lần 2: cùng idempotencyKey và cùng payload -> Replay kết quả cũ, số dư KHÔNG đổi
        TransferResponseDTO res2 = transferHandler.handle(req1.toCommand());
        assertThat(res2.getTransactionId()).isEqualTo(res1.getTransactionId());
        assertThat(res2.getStatus()).isEqualTo(TransactionStatus.SUCCESS);

        AccountView srcView2 = accountViewRepository.findById(src.getAccountId()).orElseThrow();
        AccountView dstView2 = accountViewRepository.findById(dst.getAccountId()).orElseThrow();
        assertThat(srcView2.getAvailableBalance()).isEqualByComparingTo(new BigDecimal("1700000.00"));
        assertThat(dstView2.getAvailableBalance()).isEqualByComparingTo(new BigDecimal("800000.00"));

        // Gửi lần 3: cùng idempotencyKey nhưng thay đổi số tiền -> Phải bắn ConflictException (HTTP 409)
        TransferRequestDTO reqMismatch = TransferRequestDTO.builder()
                .sourceAccountId(src.getAccountId())
                .destinationAccountId(dst.getAccountId())
                .amount(new BigDecimal("400000.00")) // khác 300000.00
                .currency("VND")
                .idempotencyKey(idempotencyKey)
                .description("Chuyen tien lan 1 sua so tien")
                .build();

        assertThatThrownBy(() -> transferHandler.handle(reqMismatch.toCommand()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Idempotency key conflict");
    }

    @Test
    @DisplayName("Phase 1d: Bảo mật thông tin (D13) - RequestLoggingConfig tắt log payload; Thẻ không có CVV; Khách hàng có thông tin định danh và chống trùng CCCD")
    void testPhase1d_SecurityAndDataCompliance() throws Exception {
        // 1. RequestLoggingConfig: includePayload phải là false
        CommonsRequestLoggingFilter filter = requestLoggingConfig.logFilter();
        Field payloadField = org.springframework.web.filter.AbstractRequestLoggingFilter.class.getDeclaredField("includePayload");
        payloadField.setAccessible(true);
        boolean includePayload = (boolean) payloadField.get(filter);
        assertThat(includePayload).isFalse();

        // 2. Thẻ ngân hàng không lưu CVV trong DTO response
        String cif = ("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase();
        createCustomerHandler.handle(new CreateCustomerCommand(cif));
        AccountResponseDTO acc = createAccountHandler.handle(new CreateAccountCommand(cif, "VND", new BigDecimal("100000.00")));
        IssueCardResponseDTO card = issueCardHandler.handle(new IssueCardCommand(acc.getAccountId()));
        assertThat(card.getMaskedCardNumber()).matches("^[0-9]{4}-[0-9]{2}XX-XXXX-[0-9]{4}$");

        // 3. Khách hàng: quản lý CCCD/Họ tên/SĐT/Email và chặn trùng CCCD
        String nationalId = "001200009999";
        CustomerCreateRequestDTO cust1 = CustomerCreateRequestDTO.builder()
                .cifNumber(("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase())
                .nationalId(nationalId)
                .fullName("Nguyen Van Test")
                .phoneNumber("0912345678")
                .email("test@example.com")
                .build();
        CustomerResponseDTO createdCust1 = createCustomerHandler.handle(cust1.toCommand());
        assertThat(createdCust1.getNationalId()).isEqualTo("001200****99");

        // Bắn trùng CCCD -> Phải ném ngoại lệ
        CustomerCreateRequestDTO cust2 = CustomerCreateRequestDTO.builder()
                .cifNumber(("CIF" + UUID.randomUUID().toString().substring(0, 8)).toUpperCase())
                .nationalId(nationalId)
                .fullName("Nguyen Van Test Duplicate")
                .phoneNumber("0912345679")
                .email("test2@example.com")
                .build();
        assertThatThrownBy(() -> createCustomerHandler.handle(cust2.toCommand()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("National ID already exists");
    }
}
