package me.oldboy.market.controlers;

import lombok.AllArgsConstructor;
import me.oldboy.market.entity.User;
import me.oldboy.market.entity.log_enum.Action;
import me.oldboy.market.entity.log_enum.Status;
import me.oldboy.market.exceptions.UserServiceException;
import me.oldboy.market.services.AuditService;
import me.oldboy.market.services.UserService;

/**
 * Класс для идентификации пользователя в системе
 */
@AllArgsConstructor
public class LoginLogoutController {
    private UserService userService;
    private AuditService auditService;

    /**
     * Проверяет может ли пользователь работать в системе
     *
     * @param email    электронная почта пользователя зарегистрированная в системе
     * @param password пароль пользователя зарегистрированный в системе
     * @return полные данные зарегистрированного в системе пользователя, если указанные были верны
     */
    public User logIn(String email, String password) {
        User user = null;
        try {
            user = userService.getUserByEmail(email);
            if (user.getPassword().equals(password)) {
                auditService.saveAuditRecord(Action.LOGIN, Status.SUCCESS, email, null);
            } else {
                throw new UserServiceException("Wrong password");
            }
        } catch (UserServiceException e) {
            user = null;
            auditService.saveAuditRecord(Action.LOGIN, Status.FAIL, email, null);
            System.out.println(e.getMessage());
        }
        return user;
    }

    /**
     * Передает в аудит-логер электронную почту пользователя при выходе из системы
     *
     * @param email электронная почта пользователя завершившего работу с системой
     */
    public void logOut(String email) {
        auditService.saveAuditRecord(Action.LOGOUT, Status.SUCCESS, email, null);
    }
}