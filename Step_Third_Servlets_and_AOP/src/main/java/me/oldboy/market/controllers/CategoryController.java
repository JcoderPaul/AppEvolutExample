package me.oldboy.market.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.aop.annotations.Loggable;
import me.oldboy.market.dto.category.CategoryReadDto;
import me.oldboy.market.mapper.CategoryMapper;
import me.oldboy.market.services.interfaces.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс - контроллер для управления категориями товара (в текущей реализации только просмотр)
 */
@Slf4j
@AllArgsConstructor
public class CategoryController {
    private CategoryService categoryService;

    /**
     * Ищет категорию товара по уникальному идентификационному номеру ID в БД
     *
     * @param categoryId идентификатор категории товара в БД
     * @return найденная категория в случае успеха и null - если не найдена
     */
    @Loggable
    public CategoryReadDto findCategoryById(Integer categoryId) {
        return CategoryMapper.INSTANCE.mapToReadDto(categoryService.findById(categoryId));
    }

    /**
     * Возвращает список всех доступных категорий.
     *
     * @return все доступные категории
     */
    @Loggable
    public List<CategoryReadDto> findAllCategories() {
        return categoryService
                .findAll()
                .stream()
                .map(category -> CategoryMapper.INSTANCE.mapToReadDto(category))
                .collect(Collectors.toList());
    }
}