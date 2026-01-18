package me.oldboy.market.services.interfaces;

import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.services.interfaces.crud.ReadOnlyService;

/**
 * Специализированный сервис для работы с категориями товаров.
 * Предоставляет только операции чтения (поиска).
 *
 * @see ReadOnlyService
 * @see Category
 */
public interface CategoryService extends ReadOnlyService<Integer, Category> {
    /**
     * Находит сущность по его уникальному имени (названию категории).
     *
     * @param categoryName имя сущности (название категории товара)
     * @return найденная сущность
     */
    Category findByName(String categoryName);
}
