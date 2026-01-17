package me.oldboy.market.cache_bd;

import lombok.Getter;
import me.oldboy.market.entity.User;
import me.oldboy.market.exceptions.BrandDBException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Класс имитирует "кэш" таблицы БД содержащей данные о пользователях
 */
public class UserDB {
    private static UserDB INSTANCE;

    public static UserDB getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new UserDB();
        }
        return INSTANCE;
    }

    @Getter
    private Map<Long, User> userDb = new HashMap<>();

    /**
     * Метод добавляет нового пользователя User в "кэш" таблицу хранящую сведения о пользователях
     *
     * @param user новый добавляемый пользователь
     * @return уникальный идентификатор ID добавленного пользователя
     */
    public Long add(User user) {
        long index = 1;

        if (userDb.size() != 0) {
            index = index + userDb.keySet().stream()
                    .max((a, b) -> a > b ? 1 : -1)
                    .orElseThrow(() -> new BrandDBException("Element not found"));
        }

        user.setUserId(index);
        userDb.put(index, user);

        return index;
    }

    /**
     * Метод ищет пользователя User по уникальному идентификатору ID
     *
     * @param id значение идентификатора искомого пользователя
     * @return Optional, содержащий пользователя User, если он найден, иначе пустой Optional.
     */
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(userDb.get(id));
    }

    /**
     * Метод ищет пользователя User по его email-у
     *
     * @param email email искомого пользователя
     * @return Optional, содержащий пользователя User, если он найден, иначе пустой Optional.
     */
    public Optional<User> findUserByEmail(String email) {
        return userDb.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }
}