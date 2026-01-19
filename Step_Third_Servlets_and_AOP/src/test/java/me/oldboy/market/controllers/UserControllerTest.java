package me.oldboy.market.controllers;

import me.oldboy.market.exceptions.ControllerLayerException;
import me.oldboy.market.security.JwtAuthResponse;
import me.oldboy.market.services.SecurityService;
import me.oldboy.market.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private SecurityService securityService;
    @Mock
    private UserServiceImpl userService;
    @InjectMocks
    private UserController userController;
    private JwtAuthResponse jwtAuthResponse;
    private String testEmail, testPassword;

    @BeforeEach
    void setUp() {
        jwtAuthResponse = new JwtAuthResponse();
        testEmail = "test@mail.me";
        testPassword = "1234";
    }

    @Test
    void loginUser_successLogin_Test() {
        when(userService.isEmailUnique(testEmail)).thenReturn(false);
        when(securityService.loginUser(testEmail, testPassword)).thenReturn(jwtAuthResponse);

        userController.loginUser(testEmail, testPassword);

        assertThat(jwtAuthResponse).isNotNull();

        verify(userService, times(1)).isEmailUnique(anyString());
        verify(securityService, times(1)).loginUser(anyString(), anyString());
    }

    @Test
    void loginUser_shouldReturnException_notFoundEmail_Test() {
        when(userService.isEmailUnique(testEmail)).thenReturn(true);

        assertThatThrownBy(() -> userController.loginUser(testEmail, testPassword))
                .isInstanceOf(ControllerLayerException.class)
                .hasMessageContaining("Login '" + testEmail + "' not found!");

        verify(userService, times(1)).isEmailUnique(anyString());
        verifyNoInteractions(securityService);
    }
}