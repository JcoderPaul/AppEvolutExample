package me.oldboy.market.cache_bd.loaders;

import me.oldboy.market.cache_bd.UserDB;
import me.oldboy.market.entity.User;

/**
 * Класс загружающий данные в кэш "таблицу" хранящую данные о пользователях
 */
public class UserDBLoader {
    /**
     * Метод инициализирующий процесс загрузки данных в "кэш"
     *
     * @param userDB кэш БД для загрузки данных по пользователям
     */
    public static void initInMemoryBase(UserDB userDB) {
        User user_1 = User.builder()
                .email("admin@market.ru")
                .password("1234")
                .build();

        User user_2 = User.builder()
                .email("another_admin@market.ru")
                .password("4321")
                .build();

        userDB.add(user_1);
        userDB.add(user_2);
    }
}
