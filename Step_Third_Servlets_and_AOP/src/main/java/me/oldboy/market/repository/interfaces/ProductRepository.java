package me.oldboy.market.repository.interfaces;

import me.oldboy.market.entity.Product;
import me.oldboy.market.repository.interfaces.crud.CrudDao;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object интерфейс для работы с сущностью {@link Product}.
 * Расширяет базовый CRUD функционал для операций с товарами (продуктами).
 *
 * @see CrudDao
 * @see Product
 */
public interface ProductRepository extends CrudDao<Long, Product> {
    Optional<List<Product>> findByCategoryId(Integer categoryId);
    Optional<List<Product>> findByBrandId(Integer brandId);
}
