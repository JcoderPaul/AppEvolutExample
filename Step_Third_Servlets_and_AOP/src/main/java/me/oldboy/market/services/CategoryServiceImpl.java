package me.oldboy.market.services;

import lombok.AllArgsConstructor;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.repository.CategoryRepositoryImpl;
import me.oldboy.market.repository.interfaces.CategoryRepository;
import me.oldboy.market.services.interfaces.CategoryService;

import java.util.List;

/**
 * Реализация сервиса для работы с категориями товаров (продуктов).
 *
 * @see CategoryService
 * @see CategoryRepositoryImpl
 * @see Category
 */
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    /**
     * Репозиторий для работы с записями о категориях товаров
     */
    private CategoryRepository categoryRepository;

    /**
     * Находит категорию в БД по ее идентификатору.
     *
     * @param entityId идентификатор категории в таблице БД
     * @return найденная категория или null если запись о ней не найдена
     */
    @Override
    public Category findById(Integer entityId) {
        return categoryRepository.findById(entityId).orElse(null);
    }

    /**
     * Возвращает все категории товаров (продуктов) из системы.
     *
     * @return список всех категорий товаров
     */
    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    /**
     * Находит категорию в БД по ее названию (имени).
     *
     * @param categoryName уникальное название категории (имя) в таблице БД
     * @return найденная категория или null если запись о ней не найдена
     */
    @Override
    public Category findByName(String categoryName) {
        return categoryRepository.findByName(categoryName).orElse(null);
    }
}
