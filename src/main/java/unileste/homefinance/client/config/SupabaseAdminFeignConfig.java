package unileste.homefinance.client.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class SupabaseAdminFeignConfig {
    @Value("${supabase.api.secret-key}")
    private String adminApiKey;

    @Bean
    public RequestInterceptor supabaseAuthRequestInterceptor() {
        return template -> {
            template.header("apikey", adminApiKey);
        };
    }
}
