package me.oldboy.market.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.dto.category.CategoryReadDto;
import me.oldboy.market.services.interfaces.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для управления категориями товаров.
 * Предоставляет API для работы с категориями товаров.
 * Все методы возвращают данные в формате JSON.
 *
 * @see CategoryService
 * @see CategoryReadDto
 */
@Slf4j
@RestController
@RequestMapping("/market/categories")
@Tag(name = "CategoryController", description = "Реализует просмотровые методы для существующих категорий товара (продукта)")
public class CategoryController {

    private CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Получает категорию по её идентификатору.
     * Если категория с указанным идентификатором существует, возвращает её данные.
     * В противном случае возвращает HTTP 404 Not Found.
     *
     * @param categoryId идентификатор категории
     * @return {@link ResponseEntity} с найденной категорией или статусом 404
     * @apiExample Пример запроса: GET /market/categories/1
     */
    @GetMapping("/{categoryId}")
    @Operation(summary = "Поиск категории по ее ID",
            description = "Возвращает информацию о категории или статус контент не найден")
    public ResponseEntity<?> getCategoryById(@PathVariable("categoryId")
                                             Integer categoryId) {
        return categoryService.findById(categoryId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Получает все категории товаров.
     * Возвращает полный список всех доступных категорий товаров в формате JSON.
     *
     * @return {@link ResponseEntity} со списком всех категорий
     * @apiExample Пример запроса: GET /market/categories
     */
    @GetMapping()
    @Operation(summary = "Поиск всех доступных категорий",
            description = "Возвращает информацию о всех найденных категориях")
    public ResponseEntity<?> getAllCategories() {
        List<CategoryReadDto> categoryList = categoryService.findAll();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(categoryList);
    }

    /**
     * Находит категорию по названию.
     * Если категория с указанным названием существует, возвращает её данные.
     * В противном случае возвращает HTTP 404 Not Found.
     *
     * @param categoryName название категории для поиска
     * @return {@link ResponseEntity} с найденной категорией или статусом 404
     * @apiExample Пример запроса: GET /market/categories/?categoryName=MagicWands
     */
    @GetMapping("/")
    @Operation(summary = "Поиск категории по ее названию",
            description = "Возвращает информацию о категории по названию или статус контент не найден")
    public ResponseEntity<?> getCategoryByName(@RequestParam(name = "categoryName", required = false)
                                               String categoryName) {
        return categoryService.findByName(categoryName)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}