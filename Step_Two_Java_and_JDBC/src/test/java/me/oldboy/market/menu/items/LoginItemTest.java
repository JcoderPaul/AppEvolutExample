package me.oldboy.market.menu.items;

import me.oldboy.market.controlers.LoginLogoutController;
import me.oldboy.market.entity.User;
import me.oldboy.market.exceptions.WrongEnteredDataFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginItemTest {

    @Mock
    private LoginLogoutController loginLogoutController;

    @InjectMocks
    private LoginItem loginItem;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    private Scanner scanner;

    private String tEmail = "test@market.ru";
    private String tPassword = "1234";

    private final User testUser = User.builder()
            .userId(1L)
            .email(tEmail)
            .password(tPassword)
            .build();

    @Test
    void login_SuccessfulLogin_Test() {
        String input = "1\n" + tEmail+ "\n" + tPassword + "\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(loginLogoutController.logIn(tEmail, tPassword))
                .thenReturn(testUser);

        User result = loginItem.login(scanner);

        assertThat(result).isNotNull();
        assertThat(testUser).isEqualTo(result);

        verify(loginLogoutController, times(1)).logIn(tEmail, tPassword);
    }

    @Test
    void login_ExitImmediately_Test() {
        String input = "2\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        User result = loginItem.login(scanner);

        assertNull(result);
        verify(loginLogoutController, never()).logIn(anyString(), anyString());
    }

    @Test
    void login_InvalidChoiceThenLogin_Test() {
        String input = "3\n1\n" + tEmail+ "\n" + tPassword + "\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(loginLogoutController.logIn(tEmail, tPassword)).thenReturn(testUser);

        User result = loginItem.login(scanner);

        assertNotNull(result);
        verify(loginLogoutController, times(1)).logIn(tEmail, tPassword);
    }

    @Test
    void login_LoginReturnsNull_Test() {
        String input = "1\n" + tEmail+ "\nwrongpass\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(loginLogoutController.logIn(tEmail, "wrongpass")).thenReturn(null);

        User result = loginItem.login(scanner);

        assertNull(result);
        verify(loginLogoutController, times(1)).logIn(tEmail, "wrongpass");
    }

    /* --- Тест приватного метода с Reflection API --- */

    @Test
    void enterLoginAndPassMenu_ValidInput_Test() throws Exception {
        String input = tEmail + "\n" + tPassword + "\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        Method method = LoginItem.class.getDeclaredMethod("enterLoginAndPassMenu", Scanner.class);
        method.setAccessible(true);

        String[] result = (String[]) method.invoke(loginItem, scanner);

        assertThat(result);
        assertThat(tEmail).isEqualTo(result[0]);
        assertThat(tPassword).isEqualTo(result[1]);
    }

    @Test
    void enterLoginAndPassMenu_ValidEmailDomains_Test() throws Exception {
        String[] validEmails = {
                "test@mail.ru",
                "user@gmail.com",
                "admin@company.com",
                "name@domain.ru"
        };

        for (String email : validEmails) {
            String input = email + "\n" + tPassword + "\n";
            scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

            Method method = LoginItem.class.getDeclaredMethod("enterLoginAndPassMenu", Scanner.class);
            method.setAccessible(true);

            String[] result = (String[]) method.invoke(loginItem, scanner);

            assertThat(email).isEqualTo(result[0]);
        }
    }

    /* Не валидный формат почты */
    @Test
    void enterLoginAndPassMenu_InvalidEmailFormat_Test() throws Exception {
        /* Сначала вводим невалидную строку -> ловим предупреждение -> вводим валидный email и пароль */
        String input = "invalid-email\n" + tEmail + "\n" + tPassword + "\n";
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        Method method = LoginItem.class.getDeclaredMethod("enterLoginAndPassMenu", Scanner.class);
        method.setAccessible(true);

        method.invoke(loginItem, scanner);

        String output = outputStream.toString();

        assertTrue(output.contains("Ошибка ввода!"));
        assertTrue(output.contains("Введите email (пример формата ввода test@test.ru):"));

        System.setOut(originalOut);
    }

    @Test
    void enterLoginAndPassMenu_InvalidEmailDomains_Test() throws Exception {
        String[] invalidEmails = {
                "test@mail.org",    // не .ru или .com
                "user@gmail.net",   // не .ru или .com
                "invalid-email",    // нет @
                "@domain.com",      // нет имени пользователя
                "test@.com"         // нет домена
        };

        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        for (String email : invalidEmails) {
            /* Сначала вводим неправильный email -> ловим предупреждение -> вводим валидный email и пароль */
            String input = email + "\n" + tEmail + "\n" + tPassword + "\n" ;
            scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

            Method method = LoginItem.class.getDeclaredMethod("enterLoginAndPassMenu", Scanner.class);
            method.setAccessible(true);

            method.invoke(loginItem, scanner);

            String output = outputStream.toString();

            assertTrue(output.contains("Ошибка ввода!"));
            assertTrue(output.contains("Введите email (пример формата ввода test@test.ru):"));

            System.setOut(originalOut);
        }
    }
}