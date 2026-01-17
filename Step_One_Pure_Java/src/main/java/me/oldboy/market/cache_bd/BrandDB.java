package me.oldboy.market.cache_bd;

import lombok.Getter;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.exceptions.BrandDBException;

import java.util.*;

/**
 * Класс имитирующий "кэш" таблицы БД содержащей данные о доступных брэндах
 */
public class BrandDB {
    @Getter
    private List<Brand> brandList = new ArrayList<>();
    @Getter
    private Map<Integer, Brand> indexBrand = new HashMap<>();

    private static BrandDB INSTANCE;

    public static BrandDB getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new BrandDB();
        }
        return INSTANCE;
    }

    /**
     * Метод добавляющий новый брэнд в "кэш" таблицы хранящей сведения о доступных брэндах
     *
     * @param brand новый брэнд добавляемый в БД
     * @return уникальный ID идентификатор добавленного в БД брэнда
     */
    public Integer add(Brand brand) {
        Integer index = 1;

        if (brandList.size() != 0) {
            index = index + brandList.stream()
                    .map(b -> b.getId())
                    .max((a, b) -> a > b ? 1 : -1)
                    .orElseThrow(() -> new BrandDBException("Element not found"));
        }

        brand.setId(index);
        brandList.add(brand);
        indexBrand.put(index, brand);

        return index;
    }

    /**
     * Метод извлекает искомый по ID брэнд из "кэша" БД
     *
     * @param id уникальный идентификатор брэнда
     * @return Optional, содержащий Brand, если искомый брэнд найден, иначе пустой Optional.
     */
    public Optional<Brand> getById(Integer id) {
        return Optional.ofNullable(indexBrand.get(id));
    }
}
