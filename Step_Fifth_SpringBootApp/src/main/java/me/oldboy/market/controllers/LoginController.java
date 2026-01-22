package me.oldboy.market.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.auditor.core.dto.jwt.JwtAuthRequest;
import me.oldboy.market.config.jwt_config.JwtTokenGenerator;
import me.oldboy.market.config.security_details.ClientDetailsService;
import me.oldboy.market.config.security_details.SecurityUserDetails;
import me.oldboy.market.dto.jwt.JwtAuthResponse;
import me.oldboy.market.exceptions.ControllerLayerException;
import me.oldboy.market.usermanager.core.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST контроллер для аутентификации и авторизации пользователей в системе.
 * Предоставляет endpoint для входа пользователей в систему с использованием JWT токенов.
 * Контроллер реализует процесс аутентификации с проверкой учетных данных и генерацией
 * JWT токена для доступа к защищенным ресурсам.
 */
@Slf4j
@RestController
@RequestMapping("/market/users")
@Tag(name = "LoginController", description = "Реализует только процесс аутентификации")
public class LoginController {

    private final UserService userService;
    private final ClientDetailsService clientDetailsService;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final PasswordEncoder passwordEncoder;

    /**
     * Конструктор с внедрением зависимостей для сервисов аутентификации.
     *
     * @param userService          сервис для работы с пользователями
     * @param clientDetailsService сервис для загрузки данных пользователя
     * @param jwtTokenGenerator    генератор JWT токенов
     * @param passwordEncoder      кодировщик паролей для проверки хешей
     */
    @Autowired
    public LoginController(UserService userService,
                           ClientDetailsService clientDetailsService,
                           JwtTokenGenerator jwtTokenGenerator,
                           PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.clientDetailsService = clientDetailsService;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Аутентифицирует пользователя в системе и возвращает JWT токен.
     *
     * @param jwtAuthRequest объект запроса с учетными данными пользователя
     * @return {@link ResponseEntity} с JWT токеном и информацией о пользователе
     * @throws ControllerLayerException если введен неверный пароль
     * @apiExample Пример запроса: POST /market/users/login
     * {
     * "email": "admin@admin.ru",
     * "password": "1234"
     * }
     */
    @PostMapping("/login")
    @Operation(summary = "Аутентификация пользователя в приложении",
            description = "Возвращает ответную 'сущность' с подтвержденным email пользователя и сгенерированный JWT Token")
    public ResponseEntity<JwtAuthResponse> loginUser(@Validated
                                                     @RequestBody
                                                     JwtAuthRequest jwtAuthRequest) {
        SecurityUserDetails userDetails =
                (SecurityUserDetails) clientDetailsService.loadUserByUsername(jwtAuthRequest.getEmail());

        if (!passwordEncoder.matches(jwtAuthRequest.getPassword(), userDetails.getUser().getPassword())) {
            throw new ControllerLayerException("Entered wrong password!");
        }

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();

        long userId = userDetails.getUser().getUserId();
        String userEmail = userDetails.getUser().getEmail();
        String jwtToken = jwtTokenGenerator.getToken(userId, userEmail);

        jwtAuthResponse.setId(userId);
        jwtAuthResponse.setEmail(userEmail);
        jwtAuthResponse.setAccessToken(jwtToken);

        return ResponseEntity.ok().body(jwtAuthResponse);
    }
}