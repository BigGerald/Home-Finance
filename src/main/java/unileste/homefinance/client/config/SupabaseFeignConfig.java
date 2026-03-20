package unileste.homefinance.client.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class SupabaseFeignConfig {

    @Value("${supabase.api.key}")
    private String apiKey;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("apikey", apiKey);
            requestTemplate.header("Content-Type", "application/json");
        };
    }
    @Bean
    public RequestInterceptor authForwardInterceptor() {
        return requestTemplate -> {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                String token = jwtAuth.getToken().getTokenValue();

                requestTemplate.header("Authorization", "Bearer " + token);
            }
        };
    }
}