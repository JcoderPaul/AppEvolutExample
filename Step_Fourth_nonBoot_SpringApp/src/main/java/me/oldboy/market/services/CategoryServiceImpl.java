package me.oldboy.market.services;

import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.aop.annotations.Loggable;
import me.oldboy.market.dto.category.CategoryReadDto;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.mapper.CategoryMapper;
import me.oldboy.market.repository.CategoryRepository;
import me.oldboy.market.services.interfaces.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для работы с категориями товаров (продуктов).
 *
 * @see CategoryService
 * @see CategoryRepository
 * @see Category
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    /**
     * Репозиторий для работы с записями о категориях товаров
     */
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Находит категорию в БД по ее идентификатору.
     *
     * @param entityId идентификатор категории в таблице БД
     * @return найденная категория или null если запись о ней не найдена
     */
    @Override
    @Loggable
    public Optional<CategoryReadDto> findById(Integer entityId) {
        return categoryRepository
                .findById(entityId)
                .map(CategoryMapper.INSTANCE::mapToReadDto);
    }

    /**
     * Возвращает все категории товаров (продуктов) из системы.
     *
     * @return список всех категорий товаров
     */
    @Override
    @Loggable
    public List<CategoryReadDto> findAll() {
        return categoryRepository
                .findAll()
                .stream()
                .map(CategoryMapper.INSTANCE::mapToReadDto)
                .toList();
    }

    /**
     * Находит категорию в БД по ее названию (имени).
     *
     * @param categoryName уникальное название категории (имя) в таблице БД
     * @return найденная категория или null если запись о ней не найдена
     */
    @Override
    @Loggable
    public Optional<CategoryReadDto> findByName(String categoryName) {
        return categoryRepository
                .findByName(categoryName)
                .map(CategoryMapper.INSTANCE::mapToReadDto);
    }
}