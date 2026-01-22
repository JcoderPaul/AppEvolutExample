package me.oldboy.market.auditor.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Класс свойств конфигурации для модуля аудита действий пользователей.
 * Предоставляет централизованный доступ к настройкам аудита через Spring Boot Configuration Properties.
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "market.auditor")
public class AuditProperties {
    /**
     * Флаг, определяющий включение/выключение функционала аудита.
     */
    private boolean runit;
}
