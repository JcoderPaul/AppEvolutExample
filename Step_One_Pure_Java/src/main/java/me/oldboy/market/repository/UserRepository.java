package me.oldboy.market.repository;

import lombok.AllArgsConstructor;
import me.oldboy.market.cache_bd.UserDB;
import me.oldboy.market.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с пользователями системы.
 * Предоставляет методы для управления учетными записями пользователей,
 * включая поиск по email и обновление учетных данных (хотя в приложении
 * реально не используются - на перспективу).
 *
 * @see User
 * @see UserDB
 */
@AllArgsConstructor
public class UserRepository implements CrudRepository<Long, User>{

    private UserDB userDB;

    /**
     * Сохраняет нового пользователя User в "кэше БД".
     *
     * @param entity пользователь User для сохранения без ID
     * @return сохраненный пользователь User с присвоенным идентификатором ID
     */
    @Override
    public User save(User entity) {
        Long genId = userDB.add(entity);
        return userDB.findUserById(genId).get();
    }

    /**
     * Возвращает всех пользователей Users из "кэша БД".
     *
     * @return список всех пользователей Users
     */
    @Override
    public List<User> findAll() {
        return userDB.getUserDb()
                .values()
                .stream()
                .toList();
    }

    /**
     * Находит пользователя User по идентификатору ID.
     *
     * @param id идентификатор пользователя
     * @return Optional с найденным пользователем, empty если не найден
     */
    @Override
    public Optional<User> findById(Long id) {
        return userDB.findUserById(id);
    }

    /**
     * Обновляет данные пользователя User-a - email и пароль существующего пользователя.
     *
     * @param updateUser пользователь с обновленными данными (id неизменен)
     */
    @Override
    public void update(User updateUser) {
        if(findById(updateUser.getUserId()).isPresent()){
            User foundUserForUpdate = userDB.getUserDb().get(updateUser.getUserId());
            foundUserForUpdate.setEmail(updateUser.getEmail());
            foundUserForUpdate.setPassword(updateUser.getPassword());
        }
    }

    /**
     * Удаляет пользователя User по идентификатору ID.
     *
     * @param id идентификатор пользователя для удаления
     * @return true - если пользователь был найден и удален, false - в противном случае
     */
    @Override
    public boolean delete(Long id) {
        boolean isDelete = false;
        if(findById(id).isPresent()){
            userDB.getUserDb().remove(id);
            isDelete = true;
        }
        return isDelete;
    }

    /**
     * Находит пользователя по email адресу.
     * Email используется как уникальный идентификатор для входа в систему.
     *
     * @param email email адрес пользователя
     * @return Optional с найденным пользователем User, empty если не найден
     */
    public Optional<User> findUserByEmail(String email){
        return userDB.getUserDb()
                .values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }
}