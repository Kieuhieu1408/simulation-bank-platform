package com.hieu.apigateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final WebClient webClient;

    @Value("${KEYCLOAK_ISSUER_URI:http://localhost:8090/realms/simulation-bank}")
    private String keycloakIssuerUri;

    public AuthController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public static class LoginRequest {
        public String username;
        public String password;
        public String clientId; // Optional, can default
    }

    @PostMapping("/login")
    public Mono<String> login(@RequestBody LoginRequest loginRequest) {
        String tokenUrl = keycloakIssuerUri + "/protocol/openid-connect/token";
        String clientId = (loginRequest.clientId != null) ? loginRequest.clientId : "admin-cli";

        return webClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=password&username=" + loginRequest.username + "&password=" + loginRequest.password + "&client_id=" + clientId)
                .retrieve()
                .bodyToMono(String.class);
    }
}
