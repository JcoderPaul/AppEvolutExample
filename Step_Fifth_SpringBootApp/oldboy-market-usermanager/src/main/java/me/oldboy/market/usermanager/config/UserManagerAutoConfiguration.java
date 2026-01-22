package me.oldboy.market.usermanager.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.usermanager.core.repository.UserRepository;
import me.oldboy.market.usermanager.core.services.UserServiceImpl;
import me.oldboy.market.usermanager.core.services.interfaces.UserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация автоматического подключения модуля управления пользователями (User).
 * Создаёт и настраивает компоненты для взаимодействия с пользователями приложения,
 * если в classpath присутствуют необходимые классы.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(UserManagerProperties.class)
@ConditionalOnClass(UserManagerProperties.class)
public class UserManagerAutoConfiguration {

    /**
     * Инициализационный метод для фиксации факта загрузки конфигурации.
     * Выводит информационное сообщение в лог при инициализации конфигурации.
     */
    @PostConstruct
    void init() {
        log.info("UserManagerConfiguration init");
    }

    /**
     * Создаёт бин сервиса для управления (взаимодействия) пользователями.
     * Сервис отвечает за просмотр данных пользователя и их проверку в БД.
     */
    @Bean
    @ConditionalOnMissingBean(UserService.class)
    public UserService userService(UserRepository userRepository) {
        return new UserServiceImpl(userRepository);
    }
}