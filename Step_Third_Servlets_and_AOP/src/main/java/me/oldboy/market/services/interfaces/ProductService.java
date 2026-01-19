package me.oldboy.market.services.interfaces;

import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.services.interfaces.crud.CrudService;

import java.util.List;

/**
 * Сервис для управления товарами в системе маркетплейса.
 * Расширяет базовый CRUD функционал специализированными методами для работы с товарами.
 *
 * @see CrudService
 * @see Product
 * @see Category
 * @see Brand
 */
public interface ProductService extends CrudService<Long, Product> {

    /**
     * Находит товар по идентификатору категории и идентификатору товара.
     *
     * @param id        идентификатор категории
     * @param productId идентификатор товара
     * @return найденный товар
     */
    Product findProductByCategoryAndId(Integer id, Long productId);

    /**
     * Находит товар по идентификатору бренда и идентификатору товара.
     *
     * @param id        идентификатор бренда
     * @param productId идентификатор товара
     * @return найденный товар
     */
    Product findProductByBrandAndId(Integer id, Long productId);

    /**
     * Находит товар по идентификатору бренда и названию товара.
     *
     * @param id   идентификатор бренда
     * @param name название товара
     * @return найденный товар
     */
    Product findProductByBrandAndName(Integer id, String name);

    /**
     * Находит все товары указанной категории.
     *
     * @param id идентификатор категории
     * @return список товаров категории, может быть пустым
     */
    List<Product> findProductByCategory(Integer id);

    /**
     * Находит все товары указанного бренда.
     *
     * @param id идентификатор бренда
     * @return список товаров бренда, может быть пустым
     */
    List<Product> findProductByBrand(Integer id);

    /**
     * Проверяет уникальность названия товара в системе.
     *
     * @param productName название товара для проверки
     * @return true - название уникально, false - товар с таким названием уже существует
     */
    boolean isProductNameUnique(String productName);
}
