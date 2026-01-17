package me.oldboy.market.controlers;

import me.oldboy.market.cache_bd.AuditDB;
import me.oldboy.market.cache_bd.UserDB;
import me.oldboy.market.entity.Audit;
import me.oldboy.market.entity.User;
import me.oldboy.market.entity.log_enum.Action;
import me.oldboy.market.entity.log_enum.Status;
import me.oldboy.market.repository.AuditRepository;
import me.oldboy.market.repository.UserRepository;
import me.oldboy.market.services.AuditService;
import me.oldboy.market.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class LoginLogoutControllerTest {
    private UserDB userDB;
    private UserRepository userRepository;
    private UserService userService;

    private AuditDB auditDB;
    private AuditRepository auditRepository;
    private AuditService auditService;
    private LoginLogoutController loginLogoutController;
    private User u1, u2;
    private String email_1, email_2, pass_1, pass_2;

    @BeforeEach
    void setUp(){
        userDB = UserDB.getINSTANCE();
        userRepository = new UserRepository(userDB);
        userService = new UserService(userRepository);

        auditDB = AuditDB.getINSTANCE();
        auditRepository = new AuditRepository(auditDB);
        auditService = new AuditService(auditRepository);

        loginLogoutController = new LoginLogoutController(userService, auditService);

        email_1 = "admin@market.ru";
        email_2 = "another_admin@market.ru";
        pass_1 = "1234";
        pass_2 = "4321";

        Audit rec_1 = Audit.builder()
                .timestamp(new Date().getTime())
                .userEmail(email_1)
                .action(Action.ADD_PRODUCT)
                .isSuccess(Status.SUCCESS)
                .build();

        Audit rec_2 = Audit.builder()
                .timestamp(new Date().getTime())
                .userEmail(email_2)
                .action(Action.UPDATE_PRODUCT)
                .isSuccess(Status.FAIL)
                .build();

        auditDB.add(rec_1);
        auditDB.add(rec_2);

        u1 = User.builder()
                .email(email_1)
                .password(pass_1)
                .build();

        u2 = User.builder()
                .email(email_2)
                .password(pass_2)
                .build();

        userDB.add(u1);
        userDB.add(u2);
    }

    @AfterEach
    void cleanBase(){
        userDB.getUserDb().clear();
        auditDB.getAuditLogList().clear();
    }

    @Test
    void logIn_shouldReturnAuthUser_Test() {
        assertThat(auditDB.getAuditLogList().size()).isEqualTo(2);

        User user = loginLogoutController.logIn(email_1, pass_1);
        assertThat(user).isEqualTo(u1);

        assertThat(auditDB.getAuditLogList().size()).isEqualTo(3);
    }

    @Test
    void logIn_shouldReturnExceptionMessage_emailNotFound_andCreateFailAudRecord_Test() {
        assertThat(auditDB.getAuditLogList().size()).isEqualTo(2);

        loginLogoutController.logIn("strange@mail.ru", pass_1);

        assertThat(auditDB.getAuditLogList().size()).isEqualTo(3);
        /* Не забываем у нас List */
        assertThat(auditDB.getAuditLogList().get(2).getIsSuccess()).isEqualTo(Status.FAIL);
    }

    @Test
    void logIn_shouldReturnExceptionMessage_wrongPassword_Test() {
        assertThat(auditDB.getAuditLogList().size()).isEqualTo(2);

        loginLogoutController.logIn(email_1, "0000");

        assertThat(auditDB.getAuditLogList().size()).isEqualTo(3);
        /* Не забываем у нас List */
        assertThat(auditDB.getAuditLogList().get(2).getIsSuccess()).isEqualTo(Status.FAIL);
    }

    @Test
    void logOut_shouldLogOutRecording_Test() {
        assertThat(auditDB.getAuditLogList().size()).isEqualTo(2);

        loginLogoutController.logOut(email_1);

        assertThat(auditDB.getAuditLogList().size()).isEqualTo(3);
    }
}