package me.oldboy.market.productmanager.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Класс свойств конфигурации для модуля управления продуктами (Product).
 * Предоставляет централизованный доступ к настройкам модуля через
 * Spring Boot Configuration Properties.
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "market.productmanager")
public class ProductManagerProperties {
}
