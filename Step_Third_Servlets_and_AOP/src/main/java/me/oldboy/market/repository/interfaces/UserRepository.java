package me.oldboy.market.repository.interfaces;

import me.oldboy.market.entity.User;
import me.oldboy.market.repository.interfaces.crud.CrudDao;

import java.util.Optional;

/**
 * Data Access Object интерфейс для работы с сущностью {@link User}.
 * Расширяет базовый CRUD функционал для операций с пользователями.
 *
 * @see CrudDao
 * @see User
 */
public interface UserRepository extends CrudDao<Long, User> {
    Optional<User> findByEmail(String email);
    boolean isEmailUnique(String email);
}
