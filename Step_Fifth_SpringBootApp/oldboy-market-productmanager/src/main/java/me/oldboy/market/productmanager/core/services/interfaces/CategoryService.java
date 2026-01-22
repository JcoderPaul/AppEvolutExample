package me.oldboy.market.productmanager.core.services.interfaces;


import me.oldboy.market.productmanager.core.dto.category.CategoryReadDto;
import me.oldboy.market.productmanager.core.services.interfaces.crud.ReadOnlyService;

import java.util.Optional;

/**
 * Специализированный сервис для работы с категориями товаров.
 * Предоставляет только операции чтения (поиска).
 */
public interface CategoryService extends ReadOnlyService<Integer, CategoryReadDto> {
    /**
     * Находит сущность по его уникальному имени (названию категории).
     *
     * @param categoryName имя сущности (название категории товара)
     * @return найденная сущность
     */
    Optional<CategoryReadDto> findByName(String categoryName);
}
