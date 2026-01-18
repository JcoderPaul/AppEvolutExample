package me.oldboy.market.services.interfaces;

import me.oldboy.market.entity.User;
import me.oldboy.market.services.interfaces.crud.ReadOnlyService;

/**
 * Специализированный сервис для работы с пользователями.
 * Предоставляет только операции чтения (поиска).
 *
 * @see ReadOnlyService
 * @see User
 */
public interface UserService extends ReadOnlyService<Long, User> {
}
