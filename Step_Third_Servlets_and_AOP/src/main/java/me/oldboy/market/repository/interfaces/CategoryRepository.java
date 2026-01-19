package me.oldboy.market.repository.interfaces;

import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.repository.interfaces.crud.CrudDao;

import java.util.Optional;

/**
 * Data Access Object интерфейс для работы с сущностью {@link Category}.
 * Расширяет базовый CRUD функционал для операций с категориями товаров.
 *
 * @see CrudDao
 * @see Category
 */
public interface CategoryRepository extends CrudDao<Integer, Category> {
    Optional<Category> findByName(String categoryName);
}
