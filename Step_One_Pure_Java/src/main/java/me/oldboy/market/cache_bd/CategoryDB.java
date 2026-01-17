package me.oldboy.market.cache_bd;

import lombok.Getter;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.exceptions.CategoryDBException;

import java.util.*;

/**
 * Класс имитирующий "кэш" таблицы БД содержащей данные о доступных товарных категориях
 */
public class CategoryDB {
    @Getter
    private List<Category> categoryList = new ArrayList<>();
    @Getter
    private Map<Integer, Category> indexCategory = new HashMap<>();

    private static CategoryDB INSTANCE;

    public static CategoryDB getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new CategoryDB();
        }
        return INSTANCE;
    }

    /**
     * Метод добавляющий новую категорию в "кэш" таблицы хранящей сведения о доступных категориях
     *
     * @param category новая категория добавляемая в БД
     * @return уникальный идентификатор ID категории добавленной в "кэш" БД
     */
    public Integer add(Category category) {
        Integer index = 1;

        if (categoryList.size() != 0) {
            index = index + categoryList.stream()
                    .map(c -> c.getId())
                    .max((c, d) -> c > d ? 1 : -1)
                    .orElseThrow(() -> new CategoryDBException("Element not found"));
        }

        category.setId(index);
        categoryList.add(category);
        indexCategory.put(index, category);

        return index;
    }

    /**
     * Метод извлекает искомую по ID категорию из "кэша" БД
     *
     * @param id уникальный идентификатор категории
     * @return Optional, содержащий Category объект, если категория найдена, иначе пустой Optional.
     */
    public Optional<Category> findById(Integer id) {
        return Optional.ofNullable(indexCategory.get(id));
    }
}