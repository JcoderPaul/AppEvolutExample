package me.oldboy.market.repository;

import lombok.AllArgsConstructor;
import me.oldboy.market.cache_bd.ProductDB;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с продуктами (товарами) Product в системе маркетплейса.
 * Реализует методы для поиска, сохранения, обновления и удаления товаров,
 * а также расширенные методы поиска по различным критериям.
 *
 * @see Product
 * @see ProductDB
 * @see Category
 * @see Brand
 */
@AllArgsConstructor
public class ProductRepository implements CrudRepository<Long, Product> {

    private ProductDB productDB;

    /**
     * Сохраняет новый продукт в системе.
     *
     * @param product продукт для сохранения
     * @return сохраненный продукт с присвоенным идентификатором ID
     */
    @Override
    public Product save(Product product) {
        Long generateId = productDB.add(product);
        return productDB.findProductById(generateId).get();
    }

    /**
     * Возвращает все продукты из "кэша" БД.
     *
     * @return список всех продуктов
     */
    @Override
    public List<Product> findAll() {
        return productDB.getProductsList();
    }

    /**
     * Находит продукт по уникальному идентификатору ID.
     *
     * @param productId идентификатор продукта
     * @return Optional с найденным товаром или empty если таковой не найден
     */
    @Override
    public Optional<Product> findById(Long productId) {
        return productDB.findProductById(productId);
    }

    /**
     * Обновляет информацию о продукте. Для каждого продукта
     * идентификатор является ключевым элементом и не меняется.
     *
     * @param updateProduct продукт с обновленными данными
     */
    @Override
    public void update(Product updateProduct) {
        productDB.update(updateProduct);
    }

    /**
     * Удаляет продукт Product по идентификатору.
     *
     * @param id идентификатор продукта Product для удаления
     * @return true - если товар был найден и удален, false - если товар не найден
     */
    @Override
    public boolean delete(Long id) {
        boolean isDelete = false;
        Optional<Product> mayBeFoundProduct = productDB.findProductById(id);
        if (mayBeFoundProduct.isPresent()) {
            isDelete = productDB.delete(mayBeFoundProduct.get());
        }
        return isDelete;
    }

    /**
     * Находит товар по категории Category и идентификатору продукта ID Product.
     *
     * @param category  категория для поиска
     * @param productId идентификатор продукта
     * @return Optional с найденным продуктом Product, empty в противном случае
     */
    public Optional<Product> findByCategoryAndId(Category category, Long productId) {
        return findByCategory(category)
                .stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst();
    }

    /**
     * Находит товар по бренду и идентификатору продукта ID Product.
     *
     * @param brand     бренд для поиска
     * @param productId идентификатор продукта
     * @return Optional с найденным продуктом Product, empty в противном случае
     */
    public Optional<Product> findByBrandAndId(Brand brand, Long productId) {
        return findByBrand(brand)
                .stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst();
    }

    /**
     * Находит продукт по бренду Brand и названию продукта.
     *
     * @param brand бренд для поиска
     * @param name  название продукта
     * @return Optional с найденным продуктом Product, empty в противном случае
     */
    public Optional<Product> findByBrandAndName(Brand brand, String name) {
        return findByBrand(brand)
                .stream()
                .filter(p -> p.getName().equals(name))
                .findFirst();
    }

    /**
     * Находит все продукты Product в указанной категории Category.
     *
     * @param category категория для фильтрации
     * @return список продуктов в указанной категории, может бросить исключение если категория не найдена
     */
    public List<Product> findByCategory(Category category) {
        return productDB.findProductByCategory(category);
    }

    /**
     * Находит все продукты Product указанного бренда Brand.
     *
     * @param brand бренд для фильтрации
     * @return список продуктов заданного бренда, может бросить исключение если категория не найдена
     */
    public List<Product> findByBrand(Brand brand) {
        return productDB.findProductByBrand(brand);
    }
}