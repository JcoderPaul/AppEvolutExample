package me.oldboy.market.services;

import lombok.SneakyThrows;
import me.oldboy.market.cache_bd.UserDB;
import me.oldboy.market.entity.User;
import me.oldboy.market.exceptions.UserServiceException;
import me.oldboy.market.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest {
    private UserDB userDB;
    private UserRepository userRepository;
    private UserService userService;
    private User u1, u2;
    private String email_1, email_2;

    @BeforeEach
    void setUp(){
        userDB = UserDB.getINSTANCE();
        userRepository = new UserRepository(userDB);
        userService = new UserService(userRepository);

        email_1 = "u1@market.ru";
        email_2 = "u2@market.ru";

        u1 = User.builder()
                .email(email_1)
                .password("1234")
                .build();
        u2 = User.builder()
                .email(email_2)
                .password("4321")
                .build();

        userDB.add(u1);
        userDB.add(u2);
    }

    @AfterEach
    void cleanBase(){
        userDB.getUserDb().clear();
    }


    @SneakyThrows
    @Test
    void getUserById_shouldReturnFoundUser_Test() {
        User foundUser_1= userService.getUserById(1L);
        User foundUser_2= userService.getUserById(2L);

        assertThat(foundUser_1).isEqualTo(u1);
        assertThat(foundUser_2).isEqualTo(u2);
    }

    @SneakyThrows
    @Test
    void getUserById_shouldReturnException_Test() {
        Long notExistId = 100L;
        assertThatThrownBy(() -> userService.getUserById(notExistId))
                .isInstanceOf(UserServiceException.class)
                .hasMessageContaining("User with ID - " + notExistId + " not found");
    }

    @SneakyThrows
    @Test
    void getUserByEmail_shouldReturnFoundUser_Test() {
        User foundUser_1= userService.getUserByEmail(email_1);
        User foundUser_2= userService.getUserByEmail(email_2);

        assertThat(foundUser_1).isEqualTo(u1);
        assertThat(foundUser_2).isEqualTo(u2);
    }

    @SneakyThrows
    @Test
    void getUserByEmail_shouldReturnException_Test() {
        String notExistEmail = "notexist@mail.ru";
        assertThatThrownBy(() -> userService.getUserByEmail(notExistEmail))
                .isInstanceOf(UserServiceException.class)
                .hasMessageContaining("User with email - " + notExistEmail + " not found");
    }
}