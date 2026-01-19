package me.oldboy.market.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.aop.annotations.Auditable;
import me.oldboy.market.aop.annotations.Loggable;
import me.oldboy.market.entity.enums.Action;
import me.oldboy.market.exceptions.ControllerLayerException;
import me.oldboy.market.security.JwtAuthResponse;
import me.oldboy.market.services.SecurityService;
import me.oldboy.market.services.interfaces.UserService;

/**
 * Класс - контроллер для управления пользователями (в текущей реализации только идентификация "в системе")
 */
@Slf4j
@AllArgsConstructor
public class UserController {

    private UserService userService;
    private SecurityService securityService;

    /**
     * Метод подтверждает аутентичность полученных через аргументы данных
     *
     * @param email    email зарегистрированного в системе пользователя
     * @param password пароль зарегистрированного в системе пользователя
     * @return dto содержащий JWT токен для передачи в запросах
     */
    @Loggable
    @Auditable(operationType = Action.LOGIN)
    public JwtAuthResponse loginUser(String email, String password) {
        if (userService.isEmailUnique(email)) {
            throw new ControllerLayerException("Login '" + email + "' not found!");
        }
        return securityService.loginUser(email, password);
    }
}