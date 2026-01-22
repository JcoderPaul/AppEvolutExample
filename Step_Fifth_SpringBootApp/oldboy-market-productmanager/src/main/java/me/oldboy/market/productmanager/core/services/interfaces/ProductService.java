package me.oldboy.market.productmanager.core.services.interfaces;

import me.oldboy.market.productmanager.core.dto.product.ProductCreateDto;
import me.oldboy.market.productmanager.core.dto.product.ProductReadDto;
import me.oldboy.market.productmanager.core.dto.product.ProductUpdateDto;
import me.oldboy.market.productmanager.core.services.interfaces.crud.CreateOnlyService;
import me.oldboy.market.productmanager.core.services.interfaces.crud.ReadOnlyService;
import me.oldboy.market.productmanager.core.services.interfaces.crud.UpdateDeleteService;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления товарами в системе маркетплейса.
 * Расширяет базовый CRUD функционал специализированными методами для работы с товарами.
 */
public interface ProductService extends
        UpdateDeleteService<Long, ProductUpdateDto, ProductReadDto>,
        CreateOnlyService<ProductCreateDto, ProductReadDto>,
        ReadOnlyService<Long, ProductReadDto> {

    /**
     * Находит товар по идентификатору категории и идентификатору товара.
     *
     * @param id        идентификатор категории
     * @param productId идентификатор товара
     * @return найденный товар
     */
    Optional<ProductReadDto> findProductByCategoryAndId(Integer id, Long productId);

    /**
     * Находит товар по идентификатору бренда и идентификатору товара.
     *
     * @param id        идентификатор бренда
     * @param productId идентификатор товара
     * @return найденный товар
     */
    Optional<ProductReadDto> findProductByBrandAndId(Integer id, Long productId);

    /**
     * Находит товар по идентификатору бренда и названию товара.
     *
     * @param id   идентификатор бренда
     * @param name название товара
     * @return найденный товар
     */
    Optional<ProductReadDto> findProductByBrandAndName(Integer id, String name);

    /**
     * Находит все товары указанной категории.
     *
     * @param id идентификатор категории
     * @return список товаров категории, может быть пустым
     */
    List<ProductReadDto> findProductByCategory(Integer id);

    /**
     * Находит все товары указанного бренда.
     *
     * @param id идентификатор бренда
     * @return список товаров бренда, может быть пустым
     */
    List<ProductReadDto> findProductByBrand(Integer id);

    /**
     * Находит все товары указанного бренда.
     *
     * @param brandId    идентификатор бренда
     * @param categoryId идентификатор категории
     * @return список товаров бренда, может быть пустым
     */
    List<ProductReadDto> findProductByBrandAndCategory(Integer brandId, Integer categoryId);

    /**
     * Проверяет уникальность названия товара в системе.
     *
     * @param productName название товара для проверки
     * @return true - название уникально, false - товар с таким названием уже существует
     */
    boolean isProductNameUnique(String productName);
}
