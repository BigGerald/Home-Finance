package unileste.homefinance.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;

@Configuration
@Slf4j
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Home Finance API")
                        .description("API para gerenciamento financeiro residencial")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("Authorization")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ));
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
        );
    }
    @EventListener(ApplicationReadyEvent.class)
    public void exportOpenApi() {
        try {
            String url = "http://localhost:80/v3/api-docs";

            InputStream inputStream = new URL(url).openStream();
            String json = new String(inputStream.readAllBytes());

            FileWriter writer = new FileWriter("HomeFinanceOpenApi.json");
            writer.write(json);
            writer.close();

            log.info("✅ OpenAPI gerado com sucesso!");
        } catch (Exception e) {
            log.info("❌ Erro ao gerar OpenAPI: " + e.getMessage());
        }
    }
}