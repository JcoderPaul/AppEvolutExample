package me.oldboy.market.repository;

import lombok.AllArgsConstructor;
import me.oldboy.market.cache_bd.CategoryDB;
import me.oldboy.market.entity.prod_species.Category;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с объектами Category (категория).
 * Реализует только просмотровые-поисковые методы.
 */
@AllArgsConstructor
public class CategoryRepository implements CrudRepository<Integer, Category> {

    private CategoryDB categoryDB;

    /**
     * Находит все доступные категории Category
     *
     * @return список найденных категорий Category
     */
    @Override
    public List<Category> findAll() {
        return categoryDB.getCategoryList();
    }

    /**
     * Находит категорию Category по уникальному идентификатору ID
     *
     * @param id идентификатор категории в системе
     * @return Optional, содержащий Category, если категория найдена, иначе пустой Optional.
     */
    @Override
    public Optional<Category> findById(Integer id) {
        return categoryDB.findById(id);
    }

    /*
    Поскольку в задании нет упоминания об управлении другими сущностями кроме товара,
    методы приведенные ниже не реализованы, но имеют перспективу в дальнейшем, т.к.
    категории товаров часто меняются, и ими придется управлять.
    */
    @Override
    public boolean delete(Integer id) {
        return false;
    }

    @Override
    public void update(Category entity) {

    }

    @Override
    public Category save(Category entity) {
        return null;
    }
}