package me.oldboy.market.controllers;

import me.oldboy.market.dto.category.CategoryReadDto;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.services.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {
    @Mock
    private CategoryServiceImpl categoryService;
    @InjectMocks
    private CategoryController categoryController;

    private Integer existId, nonExistId;
    private Category existCategory;

    @BeforeEach
    void setUp() {
        existId = 1;
        nonExistId = 100;

        existCategory = Category.builder()
                .id(existId)
                .name("Куюранги")
                .build();
    }

    @Test
    void findCategoryById_shouldReturnFoundDto_Test() {
        when(categoryService.findById(existId)).thenReturn(existCategory);
        CategoryReadDto res = categoryController.findCategoryById(existId);

        assertThat(res).isNotNull();
        assertThat(res.name()).isEqualTo(existCategory.getName());
    }

    @Test
    void findCategoryById_shouldReturnNull_Test() {
        when(categoryService.findById(nonExistId)).thenReturn(null);

        CategoryReadDto res = categoryController.findCategoryById(nonExistId);

        assertThat(res).isNull();
    }

    @Test
    void findAllCategories_shouldReturnAllCategoriesList_Test() {
        List<Category> allCategories = Collections.singletonList(existCategory);
        when(categoryService.findAll()).thenReturn(allCategories);

        List<CategoryReadDto> res = categoryController.findAllCategories();

        assertThat(res).isNotNull();
        assertThat(res.size()).isEqualTo(1);
    }
}