package me.oldboy.market.integration.repository;

import me.oldboy.market.config.test_data_source.TestContainerInit;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryRepositoryIT extends TestContainerInit {

    @Autowired
    private CategoryRepository categoryRepository;
    private Integer existId, notExistingId;
    private String existName, notExistingName;

    @BeforeEach
    void setUp() {
        existId = 1;
        notExistingId = 200;

        existName = "Обувь";
        notExistingName = "Медикаменты";
    }

    @Test
    @DisplayName("Должен вернуть найденную по ID категорию - категория есть в БД")
    void findById_shouldReturnTrue_forExistingCategory_Test() {
        Optional<Category> mayBeExistCategory = categoryRepository.findById(existId);
        if (mayBeExistCategory.isPresent()) {
            assertThat(mayBeExistCategory.get()).isNotNull();
        }
    }

    @Test
    @DisplayName("Должен вернуть false для ненайденной по ID категории - категория в БД отсутствует")
    void findById_shouldReturnFalse_forNonExistingCategory_Test() {
        assertThat(categoryRepository.findById(notExistingId).isPresent()).isFalse();
    }

    @Test
    @DisplayName("Должен вернуть список всех категорий доступных в БД")
    void findAll_shouldReturnCategoryList_Test() {
        assertThat(categoryRepository.findAll()).isNotNull();
        assertThat(categoryRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Должен вернуть категорию по её названию")
    void findByName_shouldReturnTrue_forExistingCategoryName_Test() {
        assertThat(categoryRepository.findByName(existName).isPresent()).isTrue();
    }

    @Test
    @DisplayName("Должен вернуть false категория по названию не найдена")
    void findByName_shouldReturnFalse_forNonExistingCategoryName_Test() {
        assertThat(categoryRepository.findByName(notExistingName).isPresent()).isFalse();
    }
}