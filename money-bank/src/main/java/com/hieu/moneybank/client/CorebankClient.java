package com.hieu.moneybank.client;

import com.hieu.moneybank.dto.request.TransferRequestDTO;
import com.hieu.moneybank.dto.response.TransferResponseDTO;
import com.hieu.moneybank.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Client gọi Corebank qua OAuth2 Client Credentials.
 *
 * WebClient được inject từ {@link com.hieu.moneybank.config.WebClientConfig}
 * với filter tự động lấy token từ Keycloak (registration: moneybank-internal)
 * trước mỗi request — không forward token của user cuối.
 *
 * Corebank sẽ nhận token có scope "corebank:read" / "corebank:write"
 * và authorize theo scope đó tại SecurityConfig của mình.
 */
@Component
@Slf4j
public class CorebankClient {

    private final WebClient corebankWebClient;

    @Value("${corebank.url:http://localhost:8180}")
    private String corebankBaseUrl;

    public CorebankClient(@Qualifier("corebankWebClient") WebClient corebankWebClient) {
        this.corebankWebClient = corebankWebClient;
    }

    public TransferResponseDTO executeTransfer(TransferRequestDTO request) {
        log.info("Calling corebank to execute transfer to account: {}", request.getDestinationAccountId());

        return corebankWebClient
                .post()
                .uri(corebankBaseUrl + "/api/v1/transfers")
                .bodyValue(request)
                .retrieve()
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
                )
                .bodyToMono(TransferResponseDTO.class)
                .doOnError(e -> log.error("Failed to call corebank for transfer", e))
                .block(); // blocking vì corebank handler hiện tại là synchronous
    }
}
