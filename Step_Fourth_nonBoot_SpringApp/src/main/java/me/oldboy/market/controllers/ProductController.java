package me.oldboy.market.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.aop.annotations.Auditable;
import me.oldboy.market.dto.product.ProductCreateDto;
import me.oldboy.market.dto.product.ProductReadDto;
import me.oldboy.market.dto.product.ProductUpdateDto;
import me.oldboy.market.entity.enums.Action;
import me.oldboy.market.services.interfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для управления товарами.
 * Предоставляет полный CRUD API для работы с товарами, включая расширенные возможности
 * фильтрации и поиска по различным критериям. Все методы возвращают данные в формате JSON.
 * <p>
 * Все операции модификации данных (создание, обновление, удаление) фиксируются системой аудита
 * через аннотацию {@link Auditable}.
 *
 * @see ProductService
 * @see ProductCreateDto
 * @see ProductUpdateDto
 * @see ProductReadDto
 * @see Auditable
 */
@Slf4j
@RestController
@RequestMapping("/market/products")
@Tag(name = "ProductController", description = "Управление Product (продуктами)")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Создает новый товар в системе.
     * Операция включает валидацию входных данных и аудит-запись о добавлении товара.
     * Возвращает созданный товар с присвоенным идентификатором.
     *
     * @param productCreateDto DTO с данными для создания товара
     * @return {@link ResponseEntity} с созданным товаром в формате JSON
     * @apiExample Пример запроса: POST /market/products/
     * {
     * "name": "Булки пышные",
     * "price": 210.0,
     * "categoryId": 1,
     * "brandId": 4,
     * "description": "Прикосновение - трепет, укус - наслаждение",
     * "stockQuantity": 5
     * }
     */
    @PostMapping("/")
    @Auditable(operationType = Action.ADD_PRODUCT)
    @Operation(summary = "Создание товара (продукта)",
            description = "Возвращает информацию о созданном продукте")
    public ResponseEntity<?> createProduct(@Validated
                                           @RequestBody
                                           ProductCreateDto productCreateDto) {
        ProductReadDto createdDto = productService.create(productCreateDto);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createdDto);
    }

    /**
     * Обновляет существующий товар в системе.
     * Операция включает валидацию входных данных и аудит-запись об обновлении товара.
     * Возвращает обновленные данные товара.
     *
     * @param productUpdateDto DTO с данными для обновления товара
     * @return {@link ResponseEntity} с обновленным товаром или статусом 404 если товар не найден
     * @apiExample Пример запроса: PUT /market/products/
     * {
     * "id": "10",
     * "name": "Скатерть самобранка (опытный образец)",
     * "price": 540.0,
     * "description": "Не заплатишь - не накроет (прим. НИИ "ЧАВО")",
     * "stockQuantity": 2
     * }
     */
    @PutMapping("/")
    @Auditable(operationType = Action.UPDATE_PRODUCT)
    @Operation(summary = "Обновление товара (продукта)",
            description = "Возвращает информацию об обновленном продукте")
    public ResponseEntity<?> updateProduct(@Validated
                                           @RequestBody
                                           ProductUpdateDto productUpdateDto) {
        ProductReadDto productReadDto = productService.update(productUpdateDto);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productReadDto);
    }

    /**
     * Удаляет товар по идентификатору.
     * Операция включает аудит-запись об удалении товара.
     * При успешном удалении возвращает статус 204 No Content.
     *
     * @param productId идентификатор удаляемого товара
     * @return {@link ResponseEntity} со статусом 204 No Content
     * @apiExample Пример запроса: DELETE /market/products/123
     */
    @DeleteMapping("/{productId}")
    @Auditable(operationType = Action.DELETE_PRODUCT)
    @Operation(summary = "Удаление товара (продукта)",
            description = "Возвращает статус подтверждающий отсутствие контента")
    public ResponseEntity<?> deleteProduct(@PathVariable("productId")
                                           Long productId) {
        boolean isDeleted = productService.delete(productId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Получает товар по его идентификатору.
     *
     * @param productId идентификатор товара
     * @return {@link ResponseEntity} с найденным товаром или статусом 404
     * @apiExample Пример запроса: GET /market/products/123
     */
    @GetMapping("/{productId}")
    @Operation(summary = "Получение товара (продукта) по его ID",
            description = "Возвращает найденный товар (продукт) или отсутствие запрошенного контента")
    public ResponseEntity<?> getProductById(@PathVariable("productId")
                                            Long productId) {
        return productService.findById(productId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Получает все товары.
     *
     * @return {@link ResponseEntity} со списком всех товаров в формате JSON
     * @apiExample Пример запроса: GET /market/products
     */
    @GetMapping()
    @Operation(summary = "Получение списка всех доступных товаров",
            description = "Возвращает список всех найденных товаров (продуктов)")
    public ResponseEntity<?> getAllProducts() {
        List<ProductReadDto> productList = productService.findAll();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(productList);
    }

    /**
     * Получает все товары определенной категории по ее ID.
     *
     * @param categoryId идентификатор категории
     * @return {@link ResponseEntity} со списком товаров категории или статусом 404
     * @apiExample Пример запроса: GET /market/products/categories/1
     */
    @GetMapping("/categories/{categoryId}")
    @Operation(summary = "Получение списка всех доступных товаров по категории",
            description = "Возвращает список всех найденных товаров (продуктов) по выбранной категории")
    public ResponseEntity<?> getAllProductsByCategoryId(@PathVariable("categoryId")
                                                        Integer categoryId) {
        List<ProductReadDto> productList = productService.findProductByCategory(categoryId);

        return listAnswerHandler(productList);
    }

    /**
     * Получает все товары определенного бренда по его ID.
     *
     * @param brandId идентификатор бренда
     * @return {@link ResponseEntity} со списком товаров бренда или статусом 404
     * @apiExample Пример запроса: GET /market/products/brands/1
     */
    @GetMapping("/brands/{brandId}")
    @Operation(summary = "Получение списка всех доступных товаров по брэнду",
            description = "Возвращает список всех найденных товаров (продуктов) по выбранному брэнду (ID брэнда)")
    public ResponseEntity<?> getAllProductsByBrandId(@PathVariable("brandId")
                                                     Integer brandId) {
        List<ProductReadDto> productList = productService.findProductByBrand(brandId);

        return listAnswerHandler(productList);
    }

    /**
     * Находит товар по бренду и его названию.
     *
     * @param brandId     идентификатор бренда
     * @param productName название товара
     * @return {@link ResponseEntity} с найденным товаром или статусом 404
     * @apiExample Пример запроса: GET /market/products/brands/1/?productName=МечНеСкладец
     */
    @GetMapping("/brands/{brandId}/")
    @Operation(summary = "Получение (продукта) товара по брэнду и названию",
            description = "Возвращает найденный товар (продукт) или статус отсутствия искомого контента")
    public ResponseEntity<?> getProductByBrandAndName(@PathVariable("brandId")
                                                      Integer brandId,
                                                      @RequestParam(name = "productName", required = false)
                                                      String productName) {
        return productService.findProductByBrandAndName(brandId, productName)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Получает все товары по комбинации ID бренда и ID категории.
     *
     * @param brandId    идентификатор бренда
     * @param categoryId идентификатор категории
     * @return {@link ResponseEntity} со списком товаров или статусом 404
     * @apiExample Пример запроса: GET /market/products/brands/1/categories/2
     */
    @GetMapping("/brands/{brandId}/categories/{categoryId}")
    @Operation(summary = "Получение списка (продуктов) товаров по брэнду и категории",
            description = "Возвращает список найденный товаров (продуктов) по категории и брэнду или статус контент не найден")
    public ResponseEntity<?> getAllProductsByBrandAndCategories(@PathVariable("brandId")
                                                                Integer brandId,
                                                                @PathVariable("categoryId")
                                                                Integer categoryId) {
        List<ProductReadDto> productList = productService.findProductByBrandAndCategory(brandId, categoryId);

        return listAnswerHandler(productList);
    }

    /**
     * Находит товар по идентификатору и категории.
     *
     * @param productId  идентификатор товара
     * @param categoryId идентификатор категории
     * @return {@link ResponseEntity} с найденным товаром или статусом 404
     * @apiExample Пример запроса: GET /market/products/123/categories/1
     */
    @GetMapping("/{productId}/categories/{categoryId}")
    @Operation(summary = "Получение (продукта) товара по уникальному идентификатору и категории",
            description = "Возвращает найденный товар (продукт) по его ID и категории или статус контент не найден")
    public ResponseEntity<?> getProductByIdAndCategory(@PathVariable("productId")
                                                       Long productId,
                                                       @PathVariable("categoryId")
                                                       Integer categoryId) {
        return productService.findProductByCategoryAndId(categoryId, productId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Находит товар по идентификатору и бренду.
     *
     * @param productId идентификатор товара
     * @param brandId   идентификатор бренда
     * @return {@link ResponseEntity} с найденным товаром или статусом 404
     * @apiExample Пример запроса: GET /market/products/123/brands/1
     */
    @GetMapping("/{productId}/brands/{brandId}")
    @Operation(summary = "Получение (продукта) товара по уникальному идентификатору и брэнду",
            description = "Возвращает найденный товар (продукт) по его ID и брэнду или статус контент не найден")
    public ResponseEntity<?> getProductByIdAndBrand(@PathVariable("productId")
                                                    Long productId,
                                                    @PathVariable("brandId")
                                                    Integer brandId) {
        return productService.findProductByBrandAndId(brandId, productId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Обрабатывает ответ со списком товаров.
     * Вспомогательный метод для унифицированной обработки ответов, содержащих списки товаров.
     * Возвращает 200 OK с данными если список не пуст, либо 404 Not Found если список пуст.
     *
     * @param mayBeFoundList список товаров для обработки
     * @return {@link ResponseEntity} с соответствующим статусом и данными
     */
    private ResponseEntity<?> listAnswerHandler(List<ProductReadDto> mayBeFoundList) {
        if (mayBeFoundList.size() != 0) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(mayBeFoundList);
        }
        return ResponseEntity.notFound().build();
    }
}