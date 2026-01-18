package me.oldboy.market.repository.interfaces;

import me.oldboy.market.entity.User;
import me.oldboy.market.repository.interfaces.crud.CrudDao;

/**
 * Data Access Object интерфейс для работы с сущностью {@link User}.
 * Расширяет базовый CRUD функционал для операций с пользователями.
 *
 * @see CrudDao
 * @see User
 */
public interface UserDao extends CrudDao<Long, User> {
}
