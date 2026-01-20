package me.oldboy.market.config.security_config;

import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.config.jwt_config.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * Класс определяет конфигурацию безопасности приложения
 */
@Slf4j
@Configuration
@EnableWebSecurity(debug = true)
@ComponentScan({
        "me.oldboy.market.config.security_config"
        , "me.oldboy.market.config.security_details"
        , "me.oldboy.market.config.jwt_config"
        , "me.oldboy.market.config.swagger"
        , "me.oldboy.market.controllers"
        , "me.oldboy.market.exceptions"
})
public class AppSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Autowired
    public AppSecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Определяет цепочку фильтров, которую можно сопоставить с HttpServletRequest,
     * чтобы определить, применяется ли она к текущему запросу.
     *
     * @param httpSecurity позволяет настроить веб-безопасность для определенных HTTP-запросов
     * @param jwtAuthFilter внешний настраиваемый фильтр для обработки JWT-токена
     * @return основная цепочка фильтров безопасности
     */
    @Bean
    public SecurityFilterChain getSecurityFilterChain(HttpSecurity httpSecurity, JwtAuthFilter jwtAuthFilter) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(urlConfig -> urlConfig
                        .requestMatchers(
                                "/market/users/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**").permitAll()
                        .requestMatchers(
                                "/market/products/**",
                                "/market/categories/**",
                                "/market/brands/**",
                                "/market/audits/**").authenticated()
                        .anyRequest().authenticated());

        return httpSecurity.build();
    }

    /**
     * Определяет кодировщик паролей
     *
     * @return выбранный кодировщик паролей
     */
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Исключает endpoint-ы Swagger/OpenAPI из проверки безопасности.
     *
     * @return {@link WebSecurityCustomizer} для настройки игнорируемых путей
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
        );
    }
}