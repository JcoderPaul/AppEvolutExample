package me.oldboy.market.controlers;

import me.oldboy.market.entity.Audit;
import me.oldboy.market.entity.User;
import me.oldboy.market.entity.enums.Action;
import me.oldboy.market.entity.enums.Role;
import me.oldboy.market.entity.enums.Status;
import me.oldboy.market.exceptions.ServiceLayerException;
import me.oldboy.market.repository.AuditRepository;
import me.oldboy.market.repository.UserRepository;
import me.oldboy.market.services.AuditServiceImpl;
import me.oldboy.market.services.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginLogoutControllerTest {
    @Mock
    private UserServiceImpl userService;
    @Mock
    private AuditServiceImpl auditService;
    @InjectMocks
    private LoginLogoutController loginLogoutController;
    private User u1, u2;
    private String email_1, email_2, pass_1, pass_2;

    @BeforeEach
    void setUp(){
        email_1 = "admin@market.ru";
        email_2 = "another_admin@market.ru";
        pass_1 = "1234";
        pass_2 = "4321";

        u1 = User.builder()
                .email(email_1)
                .password(pass_1)
                .role(Role.ADMIN)
                .build();

        u2 = User.builder()
                .email(email_2)
                .password(pass_2)
                .role(Role.USER)
                .build();
    }

    @Test
    void logIn_shouldReturnAuthUser_Test() {
        when(userService.getUserByEmail(email_1)).thenReturn(u1);
        User createdUser = loginLogoutController.logIn(u1.getEmail(), u1.getPassword());

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo(u1.getEmail());
        assertThat(createdUser.getPassword()).isEqualTo(u1.getPassword());
        assertThat(createdUser.getRole()).isEqualTo(u1.getRole());

        verify(userService, times(1)).getUserByEmail(any(String.class));
        verify(auditService, times(1)).create(any(Audit.class));
    }

    @Test
    void logIn_shouldReturnExceptionMessage_emailNotFound_andCreateFailAudRecord_Test() {
        when(userService.getUserByEmail(email_1))
                .thenThrow(new ServiceLayerException("User with email - " + email_1 + " not found"));

        User createdUser = loginLogoutController.logIn(u1.getEmail(), u1.getPassword());

        assertThat(createdUser).isNull();

        verify(userService, times(1)).getUserByEmail(any(String.class));
        verify(auditService, times(1)).create(any(Audit.class));
    }

    @Test
    void logIn_shouldReturnExceptionMessage_wrongPassword_Test() {
        when(userService.getUserByEmail(email_1)).thenReturn(u1);

        User createdUser = loginLogoutController.logIn(u1.getEmail(), pass_2);

        assertThat(createdUser).isNull();

        verify(userService, times(1)).getUserByEmail(any(String.class));
        verify(auditService, times(1)).create(any(Audit.class));
    }

    @Test
    void logOut_shouldLogOutRecording_Test() {
        loginLogoutController.logOut(email_1);

        verify(auditService, times(1)).create(argThat(audit ->
                audit.getCreateBy().equals(email_1) &&
                        audit.getAction() == Action.LOGOUT &&
                        audit.getIsSuccess() == Status.SUCCESS
        ));
    }
}