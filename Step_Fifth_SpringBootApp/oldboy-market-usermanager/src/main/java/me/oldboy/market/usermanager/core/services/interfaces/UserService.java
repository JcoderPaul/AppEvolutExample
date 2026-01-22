package me.oldboy.market.usermanager.core.services.interfaces;

import me.oldboy.market.usermanager.core.entity.User;
import me.oldboy.market.usermanager.core.services.interfaces.crud.ReadOnlyService;

/**
 * Специализированный сервис для работы с пользователями.
 * Предоставляет только операции чтения (поиска).
 */
public interface UserService extends ReadOnlyService<Long, User> {
    User getUserByEmail(String email);
    boolean isEmailUnique(String email);
}
