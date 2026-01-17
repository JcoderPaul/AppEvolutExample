package me.oldboy.market.cache_bd;

import me.oldboy.market.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserDBTest {

    private UserDB userDB;
    private User u1, u2, u3;
    private Long gen_1, gen_2, gen_3;

    @BeforeEach
    void setUp(){
        userDB = UserDB.getINSTANCE();
        u1 = User.builder()
                .email("u1@market.ru")
                .password("1234")
                .build();
        u2 = User.builder()
                .email("u2@market.ru")
                .password("4321")
                .build();
        u3 = User.builder()
                .email("u3@market.ru")
                .password("2143")
                .build();

        gen_1 = userDB.add(u1);
        gen_2 = userDB.add(u2);
        gen_3 = userDB.add(u3);
    }

    @AfterEach
    void cleanBase(){
        userDB.getUserDb().clear();
    }

    @Test
    void add_shouldReturnGeneratedId_Test() {
        assertThat(gen_1).isEqualTo(u1.getUserId());
        assertThat(gen_2).isEqualTo(u2.getUserId());
        assertThat(gen_3).isEqualTo(u3.getUserId());
    }

    @Test
    void findUserById_shouldReturnFoundUserById_Test() {
        assertThat(userDB.findUserById(gen_2)).isNotEmpty();
        assertThat(userDB.findUserById(gen_2).get().getUserId()).isEqualTo(gen_2);
    }

    @Test
    void findUserById_shouldReturnOptionalEmpty_Test() {
        assertThat(userDB.findUserById(10L)).isEmpty();
    }

    @Test
    void findUserByEmail_shouldReturnFoundUserByEmail_Test() {
        assertThat(userDB.findUserByEmail(u3.getEmail())).isNotEmpty();
        assertThat(userDB.findUserByEmail(u3.getEmail()).get().getEmail()).isEqualTo(u3.getEmail());
    }

    @Test
    void findUserByEmail_shouldReturnOptionalEmpty_Test() {
        assertThat(userDB.findUserByEmail("notExist@market.ru")).isEmpty();
    }
}