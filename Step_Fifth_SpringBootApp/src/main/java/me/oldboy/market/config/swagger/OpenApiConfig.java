package me.oldboy.market.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурация OpenAPI 3.0 (Swagger UI) для текущего проекта.
 */
@Slf4j
@Configuration
@EnableWebMvc
@EnableAutoConfiguration
@ComponentScan(basePackages = "me.oldboy.market")
public class OpenApiConfig implements WebMvcConfigurer {

    /**
     * Настраивает спецификацию OpenAPI с JWT аутентификацией.
     *
     * @return сконфигурированная спецификация OpenAPI
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .info(new Info()
                        .title("Market product manager v.4")
                        .contact(new Contact()
                                .name(": Ермолаев Павел Николаевич")
                                .email("Jcoder.Paul@gmail.com")));
    }
}