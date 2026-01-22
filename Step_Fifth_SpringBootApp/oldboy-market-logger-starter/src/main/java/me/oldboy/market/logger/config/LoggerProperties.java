package me.oldboy.market.logger.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Класс свойств конфигурации для модуля логирование скорости выполнения методов.
 * Предоставляет централизованный доступ к настройкам аудита через
 * Spring Boot Configuration Properties.
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "market.time.logger")
public class LoggerProperties {
    /**
     * Флаг, определяющий включение/выключение функционала логгера.
     */
    private boolean enabled;
}
