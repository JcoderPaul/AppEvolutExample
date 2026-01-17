package me.oldboy.market.repository;

import lombok.AllArgsConstructor;
import me.oldboy.market.cache_bd.BrandDB;
import me.oldboy.market.entity.prod_species.Brand;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с объектами Brand (брэнд).
 * Реализует только просмотровые-поисковые методы.
 */
@AllArgsConstructor
public class BrandRepository implements CrudRepository<Integer, Brand> {

    private BrandDB brandDB;

    /**
     * Находит все доступные бренды Brand
     *
     * @return список найденных брэндов Brand
     */
    @Override
    public List<Brand> findAll() {
        return brandDB.getBrandList();
    }

    /**
     * Находит брэнд Brand по уникальному идентификатору ID
     *
     * @param id идентификатор брэнда в системе
     * @return Optional, содержащий Brand, если брэнд найден, иначе пустой Optional.
     */
    @Override
    public Optional<Brand> findById(Integer id) {
        return brandDB.getById(id);
    }

    /*
    Поскольку в задании нет упоминания об управлении другими сущностями кроме товара,
    методы приведенные ниже не реализованы, но имеют перспективу в дальнейшем, т.к.
    брэнды приходят и уходят, и ими придется управлять.
    */
    @Override
    public boolean delete(Integer id) {
        return false;
    }

    @Override
    public void update(Brand entity) {
    }

    @Override
    public Brand save(Brand entity) {
        return null;
    }
}