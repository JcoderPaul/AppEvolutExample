package me.oldboy.market.cache_bd;

import me.oldboy.market.entity.prod_species.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryDBTest {

    private CategoryDB categoryDB;
    private Category cat1, cat2, cat3;

    @BeforeEach
    void setUp(){
        categoryDB = CategoryDB.getINSTANCE();
        cat1 = Category.builder()
                .name("Category_1")
                .build();
        cat2 = Category.builder()
                .name("Category_2")
                .build();
        cat3 = Category.builder()
                .name("Category_3")
                .build();
    }

    @AfterEach
    void cleanBase(){
        categoryDB.getCategoryList().clear();
        categoryDB.getIndexCategory().clear();
    }

    @Test
    void add_shouldReturnGeneratedCategoryId_Test() {
        Integer generatedId_1 = categoryDB.add(cat1);
        assertThat(generatedId_1).isEqualTo(1);

        Integer generatedId_2 = categoryDB.add(cat2);
        assertThat(generatedId_2).isEqualTo(2);

        Integer generatedId_3 = categoryDB.add(cat3);
        assertThat(generatedId_3).isEqualTo(3);
    }

    @Test
    void get_shouldReturnCategoryById_Test() {
        Integer generatedId_1 = categoryDB.add(cat1);
        Integer generatedId_2 = categoryDB.add(cat2);
        Integer generatedId_3 = categoryDB.add(cat3);

        assertThat(categoryDB.findById(generatedId_1)).contains(cat1);
        assertThat(categoryDB.findById(generatedId_2)).contains(cat2);
        assertThat(categoryDB.findById(generatedId_3)).contains(cat3);
    }
}