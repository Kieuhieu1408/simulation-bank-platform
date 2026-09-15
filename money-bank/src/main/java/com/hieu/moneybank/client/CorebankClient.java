package com.hieu.moneybank.client;

import com.hieu.moneybank.dto.request.TransferRequestDTO;
import com.hieu.moneybank.dto.response.TransferResponseDTO;
import com.hieu.moneybank.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
@Slf4j
public class CorebankClient {

    private final RestTemplate restTemplate;

    @Value("${corebank.url:http://localhost:8180}")
    private String corebankBaseUrl;

    public TransferResponseDTO executeTransfer(TransferRequestDTO request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Forward token từ request hiện tại
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String token = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
            if (token != null) {
                headers.set(HttpHeaders.AUTHORIZATION, token);
            }
        }

        HttpEntity<TransferRequestDTO> entity = new HttpEntity<>(request, headers);
        String url = corebankBaseUrl + "/api/v1/transfers";
        
        try {
            log.info("Calling corebank to execute transfer: {}", url);
            ResponseEntity<TransferResponseDTO> response = restTemplate.postForEntity(url, entity, TransferResponseDTO.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to call corebank for transfer", e);
            throw new BusinessException("Chuyển tiền thất bại tại Corebank: " + e.getMessage());
        }
    }
}
