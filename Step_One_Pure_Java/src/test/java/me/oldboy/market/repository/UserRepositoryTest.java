package me.oldboy.market.repository;

import me.oldboy.market.cache_bd.UserDB;
import me.oldboy.market.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest {

    private UserDB userDB;
    private UserRepository userRepository;
    private User u1, u2;
    private String email_1, email_2;

    @BeforeEach
    void setUp(){
        userDB = UserDB.getINSTANCE();
        userRepository = new UserRepository(userDB);

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


    @Test
    void save_shouldReturnSavedUser_Test() {
        assertThat(userDB.getUserDb().size()).isEqualTo(2);
        User u3 = User.builder()
                .email("u3@market.ru")
                .password("0000")
                .build();

        userRepository.save(u3);
        assertThat(userDB.getUserDb().size()).isEqualTo(3);
    }

    @Test
    void findAll_shouldReturnUsersList_Test() {
        assertThat(userRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    void findById_shouldReturnFoundUser_Test() {
        Optional<User> mayBeFoundUser_1= userRepository.findById(1L);
        Optional<User> mayBeFoundUser_2= userRepository.findById(2L);

        assertThat(mayBeFoundUser_1).isNotEmpty();
        assertThat(mayBeFoundUser_2).isNotEmpty();

        assertThat(mayBeFoundUser_1).contains(u1);
        assertThat(mayBeFoundUser_2).contains(u2);
    }

    @Test
    void findById_shouldReturnOptionalEmpty_ifUserNotExist_Test() {
        Optional<User> notFoundUser= userRepository.findById(100L);

        assertThat(notFoundUser).isEmpty();
    }

    @Test
    void update_shouldSilentUpdateUser_Test() {
        /* Получаем текущее значение, как и ранее сеттеров бы хватило (но на горизонте реальная БД) */
        Optional<User> mayBeFoundUser = userRepository.findById(1L);
        String newEmail = "T1000@market.ru";
        String newPassword = "1000v800";
        Long forUpdateUserId = mayBeFoundUser.get().getUserId();

        /* В базе все еще 2-а user-a */
        assertThat(userRepository.findAll().size()).isEqualTo(2);

        /* Id не меняется, обновляем только email и password */
        User updateUser = User.builder()
                .userId(forUpdateUserId)
                .email(newEmail)
                .password(newPassword)
                .build();
        userRepository.update(updateUser);

        /* В базе все еще 2-а user-a */
        assertThat(userRepository.findAll().size()).isEqualTo(2);

        assertThat(userRepository.findById(1L).get().getEmail()).isEqualTo(newEmail);
        assertThat(userRepository.findById(1L).get().getPassword()).isEqualTo(newPassword);
    }

    @Test
    void delete_shouldReturnTrueAfterDeleteUser_Test() {
        /* В БД 2-а user-a */
        assertThat(userRepository.findAll().size()).isEqualTo(2);
        /* Удаляем существующего user-a */
        assertThat(userRepository.delete(1L)).isTrue();
        /* В БД 1-н user */
        assertThat(userRepository.findAll().size()).isEqualTo(1);
        /* И это user с ID = 2L */
        assertThat(userRepository.findAll().contains(u2)).isTrue();
    }

    @Test
    void delete_shouldReturnFalse_afterTryToRemoveNonExistUser_Test() {
        /* В БД 2-а user-a */
        assertThat(userRepository.findAll().size()).isEqualTo(2);
        /* Удаляем не существующего user-a */
        assertThat(userRepository.delete(100L)).isFalse();
        /* В БД все еще 2-а user-a */
        assertThat(userRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    void findUserByEmail_shouldReturnFoundUser_Test() {
        assertThat(userRepository.findUserByEmail(email_1)).isNotEmpty();
        assertThat(userRepository.findUserByEmail(email_1)).contains(u1);

        assertThat(userRepository.findUserByEmail(email_2)).isNotEmpty();
        assertThat(userRepository.findUserByEmail(email_2)).contains(u2);
    }

    @Test
    void findUserByEmail_shouldReturnOptionalEmpty_Test() {
        assertThat(userRepository.findUserByEmail("tuta_netu@mail.com")).isEmpty();
    }
}