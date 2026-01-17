package me.oldboy.market.repository;

import me.oldboy.market.cache_bd.BrandDB;
import me.oldboy.market.cache_bd.CategoryDB;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CategoryRepositoryTest {

    private CategoryDB categoryDB;
    private CategoryRepository categoryRepository;
    private Category cat_1, cat_2, cat_3;

    @BeforeEach
    void setUp(){
        categoryDB = CategoryDB.getINSTANCE();
        categoryRepository = new CategoryRepository(categoryDB);

        cat_1 = Category.builder()
                .name("Shoes")
                .build();

        cat_2 = Category.builder()
                .name("Cleaning")
                .build();

        cat_3 = Category.builder()
                .name("Electronics")
                .build();

        categoryDB.add(cat_1);
        categoryDB.add(cat_2);
        categoryDB.add(cat_3);
    }

    @AfterEach
    void cleanBase(){
        categoryDB.getCategoryList().clear();
        categoryDB.getIndexCategory().clear();
    }

    @Test
    void findAll_shouldReturnAllCategoryList_Test() {
        assertThat(categoryRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    void findById_shouldReturnBrandById_Test() {
        assertThat(categoryRepository.findById(cat_1.getId())).contains(cat_1);
        assertThat(categoryRepository.findById(cat_2.getId())).contains(cat_2);
        assertThat(categoryRepository.findById(cat_3.getId())).contains(cat_3);
    }
}