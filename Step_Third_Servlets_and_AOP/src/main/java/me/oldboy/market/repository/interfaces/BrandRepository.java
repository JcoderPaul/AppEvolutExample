package me.oldboy.market.repository.interfaces;

import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.repository.interfaces.crud.CrudDao;

import java.util.Optional;

/**
 * Data Access Object интерфейс для работы с сущностью {@link Brand}.
 * Расширяет базовый CRUD функционал для операций с брэндами товаров.
 *
 * @see CrudDao
 * @see Brand
 */
public interface BrandRepository extends CrudDao<Integer, Brand> {
    Optional<Brand> findByName(String brandName);
}
