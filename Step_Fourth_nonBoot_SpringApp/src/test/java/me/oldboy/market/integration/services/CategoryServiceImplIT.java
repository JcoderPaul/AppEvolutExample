package me.oldboy.market.integration.services;

import me.oldboy.market.config.test_data_source.TestContainerInit;
import me.oldboy.market.dto.category.CategoryReadDto;
import me.oldboy.market.services.interfaces.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryServiceImplIT extends TestContainerInit {
    @Autowired
    private CategoryService categoryService;
    private Integer existId, nonExistId;
    private String existCategoryName, nonExistCategoryName;

    @BeforeEach
    public void setUp() {
        /* Предварительные тестовые данные */
        existId = 1;
        nonExistId = 100;

        existCategoryName = "Обувь";
        nonExistCategoryName = "Кочерыжки";
    }

    /* Основные тесты для реализаций методов */

    @Test
    @DisplayName("Должен вернуть найденную по ID категорию - ID есть в БД")
    void findById_shouldReturnFoundCategory_Test() {
        Optional<CategoryReadDto> foundCategory = categoryService.findById(existId);

        assertThat(foundCategory.isPresent()).isTrue();
        assertThat(foundCategory.get().name()).isEqualTo(existCategoryName);
    }

    @Test
    @DisplayName("Должен вернуть пустую запись для ненайденной по ID категории - ID в БД отсутствует")
    void findById_shouldReturnOptionalEmpty_notFoundCategory_Test() {
        assertThat(categoryService.findById(nonExistId).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Должен вернуть список всех категорий доступных")
    void findAll_shouldReturnAllCategoryList_Test() {
        assertThat(categoryService.findAll().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Должен вернуть категорию по её названию - название категории в БД есть")
    void findByName_shouldReturnFoundCategory_existName_Test() {
        Optional<CategoryReadDto> foundCategory = categoryService.findByName(existCategoryName);
        assertThat(foundCategory.isPresent()).isTrue();
        assertThat(foundCategory.get().id()).isEqualTo(existId);
    }

    @Test
    @DisplayName("Должен вернуть пустую запись - категория по названию не найдена - выбранного названия в БД нет")
    void findByName_shouldReturnOptionalEmpty_notExistName_Test() {
        assertThat(categoryService.findByName(nonExistCategoryName).isEmpty()).isTrue();
    }
}