package me.oldboy.market.servlets.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.oldboy.market.controllers.UserController;
import me.oldboy.market.dto.JsonFormResponse;
import me.oldboy.market.exceptions.ControllerLayerException;
import me.oldboy.market.exceptions.SecurityServiceException;
import me.oldboy.market.security.JwtAuthRequest;
import me.oldboy.market.security.JwtAuthResponse;
import me.oldboy.market.validate.ValidatorDto;

import javax.validation.ConstraintViolationException;
import java.io.IOException;

/**
 * Сервлет для аутентификации пользователей в системе.
 * Обрабатывает запросы на вход пользователя и возвращает JWT токен для доступа к защищенным ресурсам.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/market/users/login"})
public class UserLoginServlet extends HttpServlet {
    /**
     * Контроллер для работы с пользователями
     */
    private UserController userController;
    /**
     * Объект для сериализации/десериализации JSON
     */
    private ObjectMapper objectMapper;

    /**
     * Инициализирует сервлет, получая зависимости из контекста приложения.
     *
     * @param config конфигурация сервлета
     * @throws ServletException если произошла ошибка при инициализации
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userController = (UserController) getServletContext().getAttribute("userController");
        objectMapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    /**
     * Обрабатывает POST запрос на аутентификацию пользователя.
     *
     * @param req  HTTP запрос с телом в формате JSON, содержащим email и пароль
     * @param resp HTTP ответ с данными аутентификации или сообщением об ошибке
     * @throws IOException если произошла ошибка ввода-вывода
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            JwtAuthRequest jwtAuthRequest = objectMapper.readValue(req.getInputStream(), JwtAuthRequest.class);

            ValidatorDto.getInstance().isValidData(jwtAuthRequest);

            JwtAuthResponse jwtAuthResponse =
                    userController.loginUser(jwtAuthRequest.getEmail(), jwtAuthRequest.getPassword());

            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            objectMapper.writeValue(resp.getOutputStream(), jwtAuthResponse);
        } catch (ControllerLayerException | SecurityServiceException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            objectMapper.writeValue(resp.getOutputStream(), new JsonFormResponse(e.getMessage()));
        } catch (ConstraintViolationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), new JsonFormResponse(e.getMessage()));
        }
    }
}