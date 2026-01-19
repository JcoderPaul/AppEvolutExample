package me.oldboy.market.servlets.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.oldboy.market.controllers.UserController;
import me.oldboy.market.dto.JsonFormResponse;
import me.oldboy.market.entity.enums.Role;
import me.oldboy.market.exceptions.ControllerLayerException;
import me.oldboy.market.exceptions.SecurityServiceException;
import me.oldboy.market.security.JwtAuthRequest;
import me.oldboy.market.security.JwtAuthResponse;
import me.oldboy.market.servlets.MockServletInputStream;
import me.oldboy.market.servlets.MockServletOutputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserLoginServletTest {
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;
    @Mock
    private UserController userController;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private UserLoginServlet userLoginServlet;
    private MockServletInputStream mockInputStream;
    private MockServletOutputStream mockOutputStream;

    private JwtAuthRequest jwtAuthRequest;
    private JwtAuthResponse jwtAuthResponse;
    private String strRequestBody;
    private ObjectWriter objectWriter;

    @BeforeEach
    void setUp() throws Exception {
        /* Оформляем JSON для входящего потока в виде строки, она при помощи ObjectMapper будет преобразована в JwtAuthRequest */
        strRequestBody = "{\"email\":\"admin@market.ru\",\"password\":\"1234\"}";
        /* Готовим имитацию запроса и ответа */
        jwtAuthRequest = new JwtAuthRequest("admin@market.ru", "1234");
        jwtAuthResponse = new JwtAuthResponse(1L, "admin@market.ru", Role.ADMIN, "generatedAccessToken");
        /* Нам нужно качественно переписать JSON в String наш возвращаемый "response" объект - используем функционал ObjectWriter */
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

        /* Формируем подмену входящего потока на request */
        ByteArrayInputStream inputStream = new ByteArrayInputStream(strRequestBody.getBytes(StandardCharsets.UTF_8));
        mockInputStream = new MockServletInputStream(inputStream);
        /* Формируем подмену исходящего потока с response */
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mockOutputStream = new MockServletOutputStream(outputStream);

        /* Формируем стандартные mock-и/stub-ы для тестируемого метода */
        when(req.getInputStream()).thenReturn(mockInputStream);
        when(resp.getOutputStream()).thenReturn(mockOutputStream);

        when(objectMapper.readValue(mockInputStream, JwtAuthRequest.class)).thenReturn(jwtAuthRequest);
        /* А вот тут интересная работа с void методом, сделаем его чуть универсальным ... */
        doAnswer(invocation -> {
            /* Аргументов в методе *.writeValue() два: 0 - исходящий поток, 1 - то, что в него пытаются затолкать */
            OutputStream outputStreamFromDoPostMethod = invocation.getArgument(0);
            /* У нас может быть несколько ситуаций: нормальная работа метода и бросок исключения при различных входящих данных из response */
            if (invocation.getArgument(1) instanceof JwtAuthResponse || resp.getStatus() == 202) {
                /* Обрабатываем штатную ситуацию с нормальным входом в приложение */
                JwtAuthResponse jwtAuthResponseToWriter = invocation.getArgument(1);
                String jwtResponseAsJson = objectWriter.writeValueAsString(jwtAuthResponseToWriter);
                /* Записываем результат в исходящий поток */
                outputStreamFromDoPostMethod.write(jwtResponseAsJson.getBytes(StandardCharsets.UTF_8));
            } else if (invocation.getArgument(1) instanceof JsonFormResponse || resp.getStatus() == 409 || resp.getStatus() == 400) {
                /* Обрабатываем имитацию броска исключения в тестах */
                JsonFormResponse response = invocation.getArgument(1);
                String jsonResponse = "{\"message\":\"" + response.message() + "\"}";
                outputStreamFromDoPostMethod.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
            }
            return null;
        }).when(objectMapper).writeValue(any(OutputStream.class), any());
    }

    @AfterEach
    public void closeAllStream() throws IOException {
        req.getInputStream().close();
        resp.getOutputStream().close();
    }

    @Test
    void doPost_successLogin_Test() throws Exception {
        /* Мокаем возврат из метода контролера */
        when(userController.loginUser(jwtAuthRequest.getEmail(), jwtAuthRequest.getPassword())).thenReturn(jwtAuthResponse);

        /* Вызываем тестируемый метод */
        userLoginServlet.doPost(req, resp);

        /* Проверяем утверждение */
        assertThat(resp.getOutputStream().toString()).isEqualTo(objectWriter.writeValueAsString(jwtAuthResponse));

        /* Проверяем поведение сервлета под тестом, сколько чего было использовано */
        verify(resp, times(1)).setContentType("application/json");
        verify(resp, times(1)).setStatus(HttpServletResponse.SC_ACCEPTED);
        verify(resp, times(2)).getOutputStream();
        verify(req, times(1)).getInputStream();
    }

    @Test
    void doPost_failedLogin_wrongEmail_Test() throws Exception {
        /* Имитируем бросок исключения - ситуацию с неверным логином */
        when(userController.loginUser(jwtAuthRequest.getEmail(), jwtAuthRequest.getPassword()))
                .thenThrow(new ControllerLayerException("Login '" + jwtAuthRequest.getEmail() + "' not found!"));

        /* Вызываем тестируемый метод */
        userLoginServlet.doPost(req, resp);

        /* Проверяем утверждение */
        assertThat(resp.getOutputStream().toString())
                .isEqualTo("{\"message\":\"Login '" + jwtAuthRequest.getEmail() + "' not found!\"}");

        /* Проверяем поведение сервлета под тестом, сколько чего было использовано */
        verify(resp, times(1)).setContentType("application/json");
        verify(resp, times(1)).setStatus(HttpServletResponse.SC_CONFLICT);
        verify(resp, times(2)).getOutputStream();
        verify(req, times(1)).getInputStream();
    }

    @Test
    void doPost_failedLogin_wrongPassword_Test() throws Exception {
        /* Имитируем бросок исключения - ситуацию с неверным паролем */
        when(userController.loginUser(jwtAuthRequest.getEmail(), jwtAuthRequest.getPassword()))
                .thenThrow(new SecurityServiceException("Wrong password!"));

        /* Вызываем тестируемый метод */
        userLoginServlet.doPost(req, resp);

        /* Проверяем утверждение */
        assertThat(resp.getOutputStream().toString())
                .isEqualTo("{\"message\":\"Wrong password!\"}");

        /* Проверяем поведение сервлета под тестом, сколько чего было использовано */
        verify(resp, times(1)).setContentType("application/json");
        verify(resp, times(1)).setStatus(HttpServletResponse.SC_CONFLICT);
        verify(resp, times(2)).getOutputStream();
        verify(req, times(1)).getInputStream();
    }
}