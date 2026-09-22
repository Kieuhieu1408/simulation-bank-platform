package com.hieu.moneybank.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Cấu hình WebClient dùng cho service-to-service calls.
 *
 * corebankWebClient tự động:
 *   1. Lấy access token từ Keycloak qua Client Credentials (grant_type=client_credentials)
 *   2. Cache token đến khi hết hạn, tự refresh khi cần
 *   3. Gắn "Authorization: Bearer <token>" vào mỗi request đến Corebank
 *
 * Token này có scope "corebank:read corebank:write" — không phải token của user cuối.
 * Corebank phân quyền theo scope tại SecurityConfig của nó.
 */
@Configuration
public class WebClientConfig {

    @Value("${corebank.url:http://localhost:8180}")
    private String corebankUrl;

    /**
     * OAuth2AuthorizedClientManager quản lý vòng đời token:
     * lấy mới, cache, và refresh tự động.
     */
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()   // chỉ dùng Client Credentials cho internal service
                .build();

        DefaultOAuth2AuthorizedClientManager manager = new DefaultOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientRepository);
        manager.setAuthorizedClientProvider(provider);
        return manager;
    }

    /**
     * WebClient cho Corebank — tự động gắn Bearer token qua Client Credentials.
     * Registration name "moneybank-internal" khớp với application.yaml.
     */
    @Bean("corebankWebClient")
    public WebClient corebankWebClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Filter =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

        // Chỉ định registration nào sẽ được dùng mặc định cho client này
        oauth2Filter.setDefaultClientRegistrationId("moneybank-internal");

        return WebClient.builder()
                .baseUrl(corebankUrl)
                .apply(oauth2Filter.oauth2Configuration())
                .build();
    }
}
