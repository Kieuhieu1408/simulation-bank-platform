package com.hieu.moneybank.client;

import com.hieu.moneybank.config.CorebankProperties;
import com.hieu.moneybank.dto.request.AccountCreateRequestDTO;
import com.hieu.moneybank.dto.request.CustomerCreateRequestDTO;
import com.hieu.moneybank.dto.request.TransferRequestDTO;
import com.hieu.moneybank.dto.response.*;
import com.hieu.moneybank.exception.BusinessException;
import com.hieu.moneybank.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

/**
 * Client gọi Corebank qua OAuth2 Client Credentials (ROLE_SERVICE_MONEYBANK).
 *
 * WebClient được inject từ {@link com.hieu.moneybank.config.WebClientConfig}
 * với filter tự động lấy token từ Keycloak (registration: moneybank-internal).
 */
@Component
@Slf4j
public class CorebankClient {

    private final WebClient corebankWebClient;
    private final CorebankProperties properties;

    public CorebankClient(@Qualifier("corebankWebClient") WebClient corebankWebClient,
                          CorebankProperties properties) {
        this.corebankWebClient = corebankWebClient;
        this.properties = properties;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Transfers
    // ──────────────────────────────────────────────────────────────────────────

    public TransferResponseDTO executeTransfer(TransferRequestDTO request) {
        log.info("Calling Corebank to execute transfer: source={}, destination={}, amount={}, key={}",
                request.getSourceAccountId(), request.getDestinationAccountId(), request.getAmount(), request.getIdempotencyKey());

        return applyErrorHandling(
                corebankWebClient.post()
                        .uri("/api/v1/transfers")
                        .bodyValue(request)
                        .retrieve()
        )
        .bodyToMono(TransferResponseDTO.class)
        .timeout(properties.getTotalTimeout())
        .doOnError(e -> log.error("Failed to call Corebank for executeTransfer", e))
        .block();
    }

    public TransferResponseDTO getTransfer(String id) {
        log.info("Calling Corebank to get transfer: id={}", id);

        return applyErrorHandling(
                corebankWebClient.get()
                        .uri("/api/v1/transfers/{id}", id)
                        .retrieve()
        )
        .bodyToMono(TransferResponseDTO.class)
        .timeout(properties.getTotalTimeout())
        .doOnError(e -> log.error("Failed to call Corebank for getTransfer id={}", id, e))
        .block();
    }

    public List<TransferResponseDTO> getTransfersByAccountId(String accountId) {
        log.info("Calling Corebank to get transfers by accountId: {}", accountId);

        return applyErrorHandling(
                corebankWebClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/v1/transfers")
                                .queryParam("accountId", accountId)
                                .build())
                        .retrieve()
        )
        .bodyToMono(new ParameterizedTypeReference<List<TransferResponseDTO>>() {})
        .timeout(properties.getTotalTimeout())
        .doOnError(e -> log.error("Failed to call Corebank for getTransfersByAccountId accountId={}", accountId, e))
        .block();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Accounts
    // ──────────────────────────────────────────────────────────────────────────

    public AccountResponseDTO getAccount(String id) {
        log.info("Calling Corebank to get account: id={}", id);

        return applyErrorHandling(
                corebankWebClient.get()
                        .uri("/api/v1/accounts/{id}", id)
                        .retrieve()
        )
        .bodyToMono(AccountResponseDTO.class)
        .timeout(properties.getTotalTimeout())
        .doOnError(e -> log.error("Failed to call Corebank for getAccount id={}", id, e))
        .block();
    }

    public AccountBalanceResponseDTO getAccountBalance(String id) {
        log.info("Calling Corebank to get account balance: id={}", id);

        return applyErrorHandling(
                corebankWebClient.get()
                        .uri("/api/v1/accounts/{id}/balance", id)
                        .retrieve()
        )
        .bodyToMono(AccountBalanceResponseDTO.class)
        .timeout(properties.getTotalTimeout())
        .doOnError(e -> log.error("Failed to call Corebank for getAccountBalance id={}", id, e))
        .block();
    }

    public AccountResponseDTO createAccount(AccountCreateRequestDTO request) {
        log.info("Calling Corebank to create account for cif={}", request.getCifNumber());

        return applyErrorHandling(
                corebankWebClient.post()
                        .uri("/api/v1/accounts")
                        .bodyValue(request)
                        .retrieve()
        )
        .bodyToMono(AccountResponseDTO.class)
        .timeout(properties.getTotalTimeout())
        .doOnError(e -> log.error("Failed to call Corebank for createAccount cif={}", request.getCifNumber(), e))
        .block();
    }

    public Page<TransactionHistoryItemDTO> getTransactionHistory(String id, Instant from, Instant to, int page, int size) {
        log.info("Calling Corebank to get transaction history for account id={}, page={}, size={}", id, page, size);

        return applyErrorHandling(
                corebankWebClient.get()
                        .uri(uriBuilder -> {
                            var builder = uriBuilder.path("/api/v1/accounts/{id}/transactions")
                                    .queryParam("page", page)
                                    .queryParam("size", size);
                            if (from != null) {
                                builder.queryParam("from", from.toString());
                            }
                            if (to != null) {
                                builder.queryParam("to", to.toString());
                            }
                            return builder.build(id);
                        })
                        .retrieve()
        )
        .bodyToMono(new ParameterizedTypeReference<RestResponsePage<TransactionHistoryItemDTO>>() {})
        .timeout(properties.getTotalTimeout())
        .doOnError(e -> log.error("Failed to call Corebank for getTransactionHistory account id={}", id, e))
        .block();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Cards
    // ──────────────────────────────────────────────────────────────────────────

    public IssueCardResponseDTO issueCard(String accountId) {
        log.info("Calling Corebank to issue card for accountId={}", accountId);

        return applyErrorHandling(
                corebankWebClient.post()
                        .uri("/api/v1/accounts/{id}/cards", accountId)
                        .retrieve()
        )
        .bodyToMono(IssueCardResponseDTO.class)
        .timeout(properties.getTotalTimeout())
        .doOnError(e -> log.error("Failed to call Corebank for issueCard accountId={}", accountId, e))
        .block();
    }

    public IssueCardResponseDTO getCard(String id) {
        log.info("Calling Corebank to get card id={}", id);

        return applyErrorHandling(
                corebankWebClient.get()
                        .uri("/api/v1/cards/{id}", id)
                        .retrieve()
        )
        .bodyToMono(IssueCardResponseDTO.class)
        .timeout(properties.getTotalTimeout())
        .doOnError(e -> log.error("Failed to call Corebank for getCard id={}", id, e))
        .block();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Customers
    // ──────────────────────────────────────────────────────────────────────────

    public CustomerResponseDTO getCustomer(String cifNumber) {
        log.info("Calling Corebank to get customer cif={}", cifNumber);

        return applyErrorHandling(
                corebankWebClient.get()
                        .uri("/api/v1/customers/{cifNumber}", cifNumber)
                        .retrieve()
        )
        .bodyToMono(CustomerResponseDTO.class)
        .timeout(properties.getTotalTimeout())
        .doOnError(e -> log.error("Failed to call Corebank for getCustomer cif={}", cifNumber, e))
        .block();
    }

    public CustomerResponseDTO createCustomer(CustomerCreateRequestDTO request) {
        log.info("Calling Corebank to create customer cif={}", request.getCifNumber());

        return applyErrorHandling(
                corebankWebClient.post()
                        .uri("/api/v1/customers")
                        .bodyValue(request)
                        .retrieve()
        )
        .bodyToMono(CustomerResponseDTO.class)
        .timeout(properties.getTotalTimeout())
        .doOnError(e -> log.error("Failed to call Corebank for createCustomer cif={}", request.getCifNumber(), e))
        .block();
    }

    public List<AccountResponseDTO> getAccountsByCif(String cifNumber) {
        log.info("Calling Corebank to get accounts for customer cif={}", cifNumber);

        return applyErrorHandling(
                corebankWebClient.get()
                        .uri("/api/v1/customers/{cifNumber}/accounts", cifNumber)
                        .retrieve()
        )
        .bodyToMono(new ParameterizedTypeReference<List<AccountResponseDTO>>() {})
        .timeout(properties.getTotalTimeout())
        .doOnError(e -> log.error("Failed to call Corebank for getAccountsByCif cif={}", cifNumber, e))
        .block();
    }

    public List<IssueCardResponseDTO> getCardsByCif(String cifNumber) {
        log.info("Calling Corebank to get cards for customer cif={}", cifNumber);

        return applyErrorHandling(
                corebankWebClient.get()
                        .uri("/api/v1/customers/{cifNumber}/cards", cifNumber)
                        .retrieve()
        )
        .bodyToMono(new ParameterizedTypeReference<List<IssueCardResponseDTO>>() {})
        .timeout(properties.getTotalTimeout())
        .doOnError(e -> log.error("Failed to call Corebank for getCardsByCif cif={}", cifNumber, e))
        .block();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Helper: Error Handling
    // ──────────────────────────────────────────────────────────────────────────

    private ResponseSpec applyErrorHandling(ResponseSpec responseSpec) {
        return responseSpec
                .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new NotFoundException("Không tìm thấy dữ liệu tại Corebank: " + body)
                                ))
                )
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new BusinessException("Corebank từ chối yêu cầu [" + response.statusCode() + "]: " + body)
                                ))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new BusinessException("Corebank lỗi nội bộ [" + response.statusCode() + "]: " + body)
                                ))
                );
    }
}
