package me.oldboy.market.services;

import lombok.AllArgsConstructor;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.exceptions.ProductServiceException;
import me.oldboy.market.repository.ProductRepository;

import java.util.List;

/**
 * Сервисный класс для управления товарами в системе маркетплейса.
 * Предоставляет бизнес-логику для операций с товарами и делегирует
 * работу с данными в репозиторий.
 *
 * @see Product
 * @see ProductRepository
 * @see Category
 * @see Brand
 */
@AllArgsConstructor
public class ProductService {

    private ProductRepository productRepository;

    /**
     * Создает новый продукт Product в "кэше БД".
     *
     * @param product продукт для создания (без ID)
     * @return созданный продукт с присвоенным идентификатором ID
     */
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Возвращает все продукты из "кэша БД".
     *
     * @return список всех продуктов Product
     */
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    /**
     * Находит продукт по идентификатору - ID Product.
     *
     * @param id идентификатор продукта
     * @return найденный продукт - Product объект
     * @throws ProductServiceException если продукт с указанным ID не найден
     */
    public Product findProductById(Long id) throws ProductServiceException {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new ProductServiceException("Product by ID - " + id + " not found"));
    }
    /*---------------------------------------------------------------------------------------*/

    /**
     * Обновляет информацию о продукте.
     *
     * @param updateProduct продукт с обновленными данными
     */
    public void updateProduct(Product updateProduct) {
        productRepository.update(updateProduct);
    }

    /**
     * Удаляет продукт по идентификатору - ID Product.
     *
     * @param id идентификатор продукта для удаления
     * @return true - если продукт был удален, false - в противном случае
     */
    public boolean deleteProduct(Long id) {
        return productRepository.delete(id);
    }

    /**
     * Находит продукт по категории Category и идентификатору ID.
     *
     * @param category  категория для поиска
     * @param productId идентификатор продукта
     * @return найденный продукт, null - если продукт не найден
     */
    public Product findProductByCategoryAndId(Category category, Long productId) {
        return productRepository.findByCategoryAndId(category, productId).orElse(null);
    }

    /**
     * Находит продукт по бренду Brand и идентификатору продукта ID.
     *
     * @param brand     бренд для поиска
     * @param productId идентификатор продукта
     * @return найденный продукт, null - если продукт не найден
     */
    public Product findProductByBrandAndId(Brand brand, Long productId) {
        return productRepository.findByBrandAndId(brand, productId).orElse(null);
    }

    /**
     * Находит продукт по бренду и его названию.
     *
     * @param brand бренд для поиска продукта
     * @param name  название продукта
     * @return найденный продукт, null - в противном случае
     */
    public Product findProductByBrandAndName(Brand brand, String name) {
        return productRepository.findByBrandAndName(brand, name).orElse(null);
    }

    /**
     * Находит все продукты указанной категории.
     *
     * @param category категория для фильтрации
     * @return список продуктов категории, может кинуть исключение если категория не найдена
     */
    public List<Product> findProductByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    /**
     * Находит все продукты указанного бренда.
     *
     * @param brand бренд для фильтрации
     * @return список продуктов бренда, может кинуть исключение если брэнд не найден
     */
    public List<Product> findProductByBrand(Brand brand) {
        return productRepository.findByBrand(brand);
    }
}