package me.oldboy.market.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.aop.annotations.Auditable;
import me.oldboy.market.aop.annotations.Loggable;
import me.oldboy.market.dto.product.ProductCreateDto;
import me.oldboy.market.dto.product.ProductReadDto;
import me.oldboy.market.dto.product.ProductUpdateDto;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.enums.Action;
import me.oldboy.market.exceptions.ControllerLayerException;
import me.oldboy.market.exceptions.ServiceLayerException;
import me.oldboy.market.mapper.ProductMapper;
import me.oldboy.market.services.interfaces.BrandService;
import me.oldboy.market.services.interfaces.CategoryService;
import me.oldboy.market.services.interfaces.ProductService;
import me.oldboy.market.validate.ValidatorDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс контролирует основные операции с товаром Product
 */
@Slf4j
@AllArgsConstructor
public class ProductController {
    private ProductService productService;
    private CategoryService categoryService;
    private BrandService brandService;

    /* --- CRUD блок --- */

    /**
     * Метод готовит запись о новом товаре Product в БД (без ID).
     *
     * @param productCreateDto создаваемый товар (продукт)
     * @param email            электронный адрес пользователя создавшего запись
     * @return созданный товар содержащий уникальный ID товара в БД
     */
    @Loggable
    @Auditable(operationType = Action.ADD_PRODUCT)
    public Product createProduct(@Valid ProductCreateDto productCreateDto, String email) {
        /* Проверяем входящие данные */
        ValidatorDto.getInstance().isValidData(productCreateDto);

        if (categoryService.findById(productCreateDto.categoryId()) == null) {
            throw new ControllerLayerException("The specified category was not found, ID - " + productCreateDto.categoryId() + " not correct");
        }

        if (brandService.findById(productCreateDto.brandId()) == null) {
            throw new ControllerLayerException("The selected brand was not found, ID - " + productCreateDto.brandId() + " not correct");
        }

        if (productService.findProductByBrandAndName(productCreateDto.brandId(), productCreateDto.name()) != null) {
            throw new ControllerLayerException("Duplicate product name");
        }

        Product forCreateProduct = Product.builder()
                .name(productCreateDto.name())
                .price(productCreateDto.price())
                .categoryId(productCreateDto.categoryId())
                .brandId(productCreateDto.brandId())
                .description(productCreateDto.description())
                .stockQuantity(productCreateDto.stockQuantity())
                .creationAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now().plusSeconds(1))
                .build();

        Product createdProductWithId = productService.create(forCreateProduct);

        return createdProductWithId;
    }

    /**
     * Метод готовит обновление существующей в БД записи о продукте (товаре, Product) и фиксирует пользователя сделавшего изменения
     *
     * @param productUpdateDto товар который нужно обновить (содержит уникальный ID, по-которому его идентифицируют)
     * @param email            электронный адрес пользователя изменившего сведения о товаре
     */
    @Auditable(operationType = Action.UPDATE_PRODUCT)
    @Loggable
    public boolean updateProduct(@Valid ProductUpdateDto productUpdateDto, String email) {
        /* Проверяем входящие данные */
        ValidatorDto.getInstance().isValidData(productUpdateDto);
        boolean isUpdated = false;

        try {
            Product foundProduct = productService.findById(productUpdateDto.id());

            if (foundProduct != null) {

                Product forUpdateProduct = Product.builder()
                        .id(productUpdateDto.id())
                        .name(productUpdateDto.name())
                        .price(productUpdateDto.price())
                        .categoryId(foundProduct.getCategoryId())
                        .brandId(foundProduct.getBrandId())
                        .description(productUpdateDto.description())
                        .stockQuantity(productUpdateDto.stockQuantity())
                        .creationAt(foundProduct.getCreationAt())
                        .modifiedAt(LocalDateTime.now())
                        .build();

                isUpdated = productService.update(forUpdateProduct);
            } else {
                throw new ControllerLayerException("Updating product with ID - " + productUpdateDto.id() + " not found");
            }

        } catch (ServiceLayerException | ControllerLayerException e) {
            System.err.println(e.getMessage());
            throw e;
        }
        return isUpdated;
    }

    /**
     * Метод готовит удаление товара Product из БД и фиксирующий пользователя проведшего операцию.
     *
     * @param productId уникальный идентификационный номер ID удаляемого товара
     * @param email     электронный адрес пользователя удаляющего товар Product
     * @return true - если товар удален, в противном случае - false
     */
    @Auditable(operationType = Action.DELETE_PRODUCT)
    @Loggable
    public boolean deleteProduct(Long productId, String email) {
        if (productService.findById(productId) == null) {
            throw new ControllerLayerException("Produced with ID - " + productId + " not found");
        }
        return productService.delete(productId);
    }

    /**
     * Ищет товар по уникальному идентификационному номеру ID в БД
     *
     * @param productId идентификатор товара в БД
     * @return найденный товар в случае успеха и null - если товар не найден
     */
    @Loggable
    public ProductReadDto findProductById(Long productId) {
        return ProductMapper.INSTANCE.mapToReadDto(productService.findById(productId));
    }

    /* --- Find... разными способами блок --- */

    /**
     * Возвращает список всех доступных продуктов.
     */
    @Loggable
    public List<ProductReadDto> findAllProduct() {
        return productService
                .findAll()
                .stream()
                .map(product -> ProductMapper.INSTANCE.mapToReadDto(product))
                .collect(Collectors.toList());
    }

    /**
     * Отображает продукт Product найденный по его брэнду Brand и уникальному названию
     *
     * @param name название искомого товара
     */
    @Loggable
    public ProductReadDto findProductByName(String name) {
        return productService
                .findAll()
                .stream()
                .filter(product -> product.getName().equals(name))
                .findAny()
                .map(product -> ProductMapper.INSTANCE.mapToReadDto(product))
                .orElse(null);
    }

    /**
     * Возвращает все продукты Product принадлежащих к категории Category
     *
     * @param categoryId уникальный идентификатор категории для поиска
     * @return список всех продуктов Product указанной категории Category
     */
    @Loggable
    public List<ProductReadDto> findProductsByCategory(Integer categoryId) {
        return productService
                .findProductByCategory(categoryId)
                .stream()
                .map(product -> ProductMapper.INSTANCE.mapToReadDto(product))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает все продукты Product принадлежащих брэнду Brand
     *
     * @param brandId уникальный идентификатор брэнда для поиска
     * @return список всех продуктов Product указанного брэнда Brand
     */
    @Loggable
    public List<ProductReadDto> findProductsByBrand(Integer brandId) {
        return productService
                .findProductByBrand(brandId)
                .stream()
                .map(product -> ProductMapper.INSTANCE.mapToReadDto(product))
                .collect(Collectors.toList());
    }
}