package me.oldboy.market.cache_bd.loaders;

import me.oldboy.market.cache_bd.BrandDB;
import me.oldboy.market.entity.prod_species.Brand;

/**
 * Класс загружающий данные в кэш "таблицу" хранящую данные о брендах
 */
public class BrandDBLoader {
    /**
     * Метод инициализирующий процесс загрузки данных в "кэш"
     *
     * @param brandDB кэш БД для загрузки данных по доступным брэндам
     */
    public static void initInMemoryBase(BrandDB brandDB) {
        Brand br_1 = Brand.builder()
                .name("Puma")
                .build();

        Brand br_2 = Brand.builder()
                .name("PolarBear")
                .build();

        Brand br_3 = Brand.builder()
                .name("Marten")
                .build();

        brandDB.add(br_1);
        brandDB.add(br_2);
        brandDB.add(br_3);
    }
}
