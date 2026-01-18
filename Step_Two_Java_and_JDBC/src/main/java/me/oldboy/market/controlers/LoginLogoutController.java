package me.oldboy.market.controlers;

import lombok.AllArgsConstructor;
import me.oldboy.market.entity.Audit;
import me.oldboy.market.entity.User;
import me.oldboy.market.entity.enums.Action;
import me.oldboy.market.entity.enums.Status;
import me.oldboy.market.exceptions.ControllerLayerException;
import me.oldboy.market.exceptions.ServiceLayerException;
import me.oldboy.market.services.AuditServiceImpl;
import me.oldboy.market.services.UserServiceImpl;

import java.time.LocalDateTime;

/**
 * Класс для идентификации пользователя в системе
 */
@AllArgsConstructor
public class LoginLogoutController {
    private UserServiceImpl userService;
    private AuditServiceImpl auditService;

    /**
     * Проверяет, может ли пользователь работать в системе
     *
     * @param email    электронная почта пользователя зарегистрированная в системе
     * @param password пароль пользователя зарегистрированный в системе
     * @return полные данные зарегистрированного в системе пользователя, если указанные были верны
     */
    public User logIn(String email, String password) {
        User user = null;

        Audit loginAuditRecord = Audit.builder()
                .createAt(LocalDateTime.now())
                .createBy(email)
                .action(Action.LOGIN)
                .auditableRecord(null)
                .build();

        try {
            user = userService.getUserByEmail(email);
            if (user.getPassword().equals(password)) {
                loginAuditRecord.setIsSuccess(Status.SUCCESS);

                auditService.create(loginAuditRecord);
            } else {
                throw new ControllerLayerException("Wrong password");
            }
        } catch (ServiceLayerException | ControllerLayerException e) {
            loginAuditRecord.setIsSuccess(Status.FAIL);
            try {
                auditService.create(loginAuditRecord);
            } catch (ServiceLayerException exception) {
                System.out.println(exception.getMessage());
            }
            System.out.println(e.getMessage());
            user = null;
        }
        return user;
    }

    /**
     * Передает в аудит-логер электронную почту пользователя при выходе из системы
     *
     * @param email электронный адрес пользователя завершившего работу с системой
     */
    public void logOut(String email) {
        Audit successLogoutAuditRecord = Audit.builder()
                .createAt(LocalDateTime.now())
                .createBy(email)
                .action(Action.LOGOUT)
                .isSuccess(Status.SUCCESS)
                .auditableRecord(null)
                .build();

        auditService.create(successLogoutAuditRecord);
    }
}