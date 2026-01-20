package me.oldboy.market.integration.services;

import me.oldboy.market.config.test_data_source.TestContainerInit;
import me.oldboy.market.entity.User;
import me.oldboy.market.exceptions.ServiceLayerException;
import me.oldboy.market.services.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceImplIT extends TestContainerInit {
    @Autowired
    private UserService userService;
    private Long existId, nonExistId;
    private String existEmail, nonExistEmail;

    @BeforeEach
    public void setUp() {
        /* Предварительные тестовые данные */
        existId = 1L;
        nonExistId = 100L;

        existEmail = "admin@admin.ru";
        nonExistEmail = "non_exist@email.com";
    }

    /* Основные тесты для реализаций методов */

    @Test
    @DisplayName("Должен вернуть найденного по заданному ID пользователя")
    void findById_shouldReturnFoundUser_Test() {
        assertThat(userService.findById(existId)).isNotNull();
    }

    @Test
    @DisplayName("Должен вернуть false - пустую запись - заданный ID не найден")
    void findById_shouldReturnOptionalEmpty_notFoundUserId_Test() {
        assertThat(userService.findById(nonExistId).isPresent()).isFalse();
    }

    @Test
    @DisplayName("Должен вернуть весь список доступных пользователей")
    void findAll_shouldReturnAllUserList_Test() {
        assertThat(userService.findAll().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Должен вернуть найденного по заданному email пользователя")
    void getUserByEmail_shouldReturnFoundUser_Test() {
        User foundUser = userService.getUserByEmail(existEmail);
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo(existEmail);
    }

    @Test
    @DisplayName("Должен бросить ServiceLayerException исключение - по заданному email пользователя не найдено")
    void getUserByEmail_shouldReturnException_Test() {
        assertThatThrownBy(() -> userService.getUserByEmail(nonExistEmail))
                .isInstanceOf(ServiceLayerException.class)
                .hasMessageContaining("User with email - " + nonExistEmail + " not found");
    }

    @Test
    @DisplayName("Должен вернуть false - заданный email не уникален")
    void isEmailUnique_shouldReturnFalse_Test() {
        assertThat(userService.isEmailUnique(existEmail)).isFalse();
    }

    @Test
    @DisplayName("Должен вернуть true - заданный email уникален")
    void isEmailUnique_shouldReturnTrue_Test() {
        assertThat(userService.isEmailUnique(nonExistEmail)).isTrue();
    }
}