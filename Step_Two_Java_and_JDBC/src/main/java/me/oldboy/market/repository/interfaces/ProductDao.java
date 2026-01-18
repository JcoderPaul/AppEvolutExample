package me.oldboy.market.repository.interfaces;

import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.User;
import me.oldboy.market.repository.interfaces.crud.CrudDao;

/**
 * Data Access Object интерфейс для работы с сущностью {@link Product}.
 * Расширяет базовый CRUD функционал для операций с товарами (продуктами).
 *
 * @see CrudDao
 * @see Product
 */
public interface ProductDao extends CrudDao<Long, Product> {
}
