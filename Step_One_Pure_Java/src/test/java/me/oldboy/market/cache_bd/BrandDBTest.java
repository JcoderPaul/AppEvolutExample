package me.oldboy.market.cache_bd;

import me.oldboy.market.entity.prod_species.Brand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BrandDBTest {

    private BrandDB brandDB;
    private Brand b1, b2, b3;

    @BeforeEach
    void setUp(){
        brandDB = BrandDB.getINSTANCE();
        b1 = Brand.builder()
                .name("Brand_1")
                .build();
        b2 = Brand.builder()
                .name("Brand_2")
                .build();
        b3 = Brand.builder()
                .name("Brand_3")
                .build();
    }

    @AfterEach
    void cleanBase(){
        brandDB.getBrandList().clear();
        brandDB.getIndexBrand().clear();
    }

    @Test
    void add_shouldReturnGeneratedBrandId_Test() {
        Integer generatedId_1 = brandDB.add(b1);
        assertThat(generatedId_1).isEqualTo(1);

        Integer generatedId_2 = brandDB.add(b2);
        assertThat(generatedId_2).isEqualTo(2);

        Integer generatedId_3 = brandDB.add(b3);
        assertThat(generatedId_3).isEqualTo(3);
    }

    @Test
    void getById_shouldReturnBrandById_Test() {
        Integer generatedId_1 = brandDB.add(b1);
        Integer generatedId_2 = brandDB.add(b2);
        Integer generatedId_3 = brandDB.add(b3);

        assertThat(brandDB.getById(generatedId_1)).contains(b1);
        assertThat(brandDB.getById(generatedId_2)).contains(b2);
        assertThat(brandDB.getById(generatedId_3)).contains(b3);
    }

    @Test
    void getById_shouldReturnOptionalEmpty_Test() {
        assertThat(brandDB.getById(45)).isEmpty();
    }
}