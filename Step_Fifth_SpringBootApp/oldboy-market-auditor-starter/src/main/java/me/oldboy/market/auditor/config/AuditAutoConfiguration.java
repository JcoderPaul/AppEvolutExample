package me.oldboy.market.auditor.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.auditor.core.aspects.AuditingAspect;
import me.oldboy.market.auditor.core.repository.AuditRepository;
import me.oldboy.market.auditor.core.service.AuditServiceImpl;
import me.oldboy.market.auditor.core.service.interfaces.AuditService;
import me.oldboy.market.usermanager.core.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация автоматического подключения аудита действий пользователей.
 * Создаёт и настраивает компоненты для аудита, если в classpath присутствуют необходимые классы.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(AuditProperties.class)
@ConditionalOnClass(AuditProperties.class)
public class AuditAutoConfiguration {

    /**
     * Инициализационный метод для фиксации факта загрузки конфигурации.
     * Выводит информационное сообщение в лог при инициализации бина.
     */
    @PostConstruct
    void init() {
        log.info("AuditorAutoConfiguration init");
    }

    /**
     * Создаёт бин аудит-сервиса, если отсутствует пользовательская реализация.
     * Сервис отвечает за сохранение записей аудита в базу данных.
     */
    @Bean
    @ConditionalOnMissingBean(AuditService.class)
    public AuditService audService(AuditRepository auditRepository, UserRepository userRepository) {
        return new AuditServiceImpl(auditRepository, userRepository);
    }

    /**
     * Создаёт бин аспекта аудита для перехвата методов.
     * Аспект активируется только при включённом свойстве {@code market.auditor.runit=true}.
     * По умолчанию аспект включён ({@code matchIfMissing = true}).
     */
    @Bean
    @ConditionalOnMissingBean(AuditingAspect.class)
    @ConditionalOnProperty(
            prefix = "market.auditor",
            name = "runit",
            havingValue = "true",
            matchIfMissing = true
    )
    public AuditingAspect audAspect(AuditService auditService) {
        return new AuditingAspect(auditService);
    }
}