package me.oldboy.market.logger.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.logger.aspects.MethodLogger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация автоматического подключения логгера времени выполнения методов.
 * Создаёт и настраивает компоненты для логирования, если в classpath присутствуют
 * необходимые классы.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(LoggerProperties.class)
@ConditionalOnClass(LoggerProperties.class)
@ConditionalOnProperty(
        prefix = "market.time.logger",
        name = "enabled",
        havingValue = "true"
)
public class LoggerAutoConfiguration {

    /**
     * Инициализационный метод для фиксации факта загрузки конфигурации.
     * Выводит информационное сообщение в лог при инициализации бина.
     */
    @PostConstruct
    void init() {
        log.info("LoggerAutoConfiguration init");
    }

    /**
     * Создаёт бин логгера, если отсутствует пользовательская реализация.
     * Сервис отвечает за замер скорости работы методов.
     */
    @Bean
    @ConditionalOnMissingBean
    public MethodLogger methodLog() {
        return new MethodLogger();
    }
}
