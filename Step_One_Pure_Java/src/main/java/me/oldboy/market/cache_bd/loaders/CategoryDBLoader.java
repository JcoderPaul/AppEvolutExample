package me.oldboy.market.cache_bd.loaders;

import me.oldboy.market.cache_bd.CategoryDB;
import me.oldboy.market.entity.prod_species.Category;

/**
 * Класс загружающий данные в кэш "таблицу" хранящую данные о категориях товаров
 */
public class CategoryDBLoader {
    /**
     * Метод инициализирующий процесс загрузки данных в "кэш"
     *
     * @param categoryDB кэш БД для загрузки данных по доступным категориям товаров
     */
    public static void initInMemoryBase(CategoryDB categoryDB) {
        Category cat_1 = Category.builder()
                .name("Обувь")
                .build();

        Category cat_2 = Category.builder()
                .name("Уборка")
                .build();

        Category cat_3 = Category.builder()
                .name("Электроника")
                .build();

        categoryDB.add(cat_1);
        categoryDB.add(cat_2);
        categoryDB.add(cat_3);
    }
}
