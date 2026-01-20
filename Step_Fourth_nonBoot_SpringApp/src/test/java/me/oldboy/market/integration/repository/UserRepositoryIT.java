package me.oldboy.market.integration.repository;

import me.oldboy.market.config.test_data_source.TestContainerInit;
import me.oldboy.market.entity.User;
import me.oldboy.market.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryIT extends TestContainerInit {

    @Autowired
    private UserRepository userRepository;
    private Long existUserId, notExistingId;
    private String existEmail, notExistingEmail;

    @BeforeEach
    void setUp() {
        existUserId = 1L;
        notExistingId = 200L;

        existEmail = "admin@market.ru";
        notExistingEmail = "malcolm@swordwing.de";
    }

    @Test
    @DisplayName("Должен вернуть найденного по ID пользователя - ID есть в БД")
    void findById_shouldReturnTrue_ForExistingRecord_Test() {
        Optional<User> mayBeUser = userRepository.findById(existUserId);
        if (mayBeUser.isPresent()) {
            assertThat(mayBeUser.get()).isNotNull();
        }
    }

    @Test
    @DisplayName("Должен вернуть false для ненайденного по ID пользователя - ID в БД отсутствует")
    void findById_shouldReturnFalse_ForEmptyRecord_Test() {
        Optional<User> mayBeUser = userRepository.findById(notExistingId);
        if (mayBeUser.isEmpty()) {
            assertThat(mayBeUser.isPresent()).isFalse();
        }
    }

    @Test
    @DisplayName("Должен вернуть найденного по email пользователя")
    void findByEmail_shouldReturnTrue_forExistingRecord_Test() {
        Optional<User> mayBeUser = userRepository.findByEmail(existEmail);
        if (mayBeUser.isPresent()) {
            assertThat(mayBeUser.get()).isNotNull();
        }
    }

    @Test
    @DisplayName("Должен вернуть false - пользователь не найден по email")
    void findByEmail_shouldReturnFalse_forNonExistingRecord_Test() {
        Optional<User> mayBeUser = userRepository.findByEmail(notExistingEmail);
        if (mayBeUser.isEmpty()) {
            assertThat(mayBeUser.isPresent()).isFalse();
        }
    }
}