package me.oldboy.market.repository;

import me.oldboy.market.cache_bd.BrandDB;
import me.oldboy.market.cache_bd.UserDB;
import me.oldboy.market.entity.User;
import me.oldboy.market.entity.prod_species.Brand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BrandRepositoryTest {
    private BrandDB brandDB;
    private BrandRepository brandRepository;
    private Brand br_1, br_2, br_3;

    @BeforeEach
    void setUp(){
        brandDB = BrandDB.getINSTANCE();
        brandRepository = new BrandRepository(brandDB);

        br_1 = Brand.builder()
                .name("Puma")
                .build();
        br_2 = Brand.builder()
                .name("PolarBear")
                .build();
        br_3 = Brand.builder()
                .name("Marten")
                .build();

        brandDB.add(br_1);
        brandDB.add(br_2);
        brandDB.add(br_3);
    }

    @AfterEach
    void cleanBase(){
        brandDB.getBrandList().clear();
        brandDB.getIndexBrand().clear();
    }

    @Test
    void findAll_shouldReturnAllBrandList_Test() {
        assertThat(brandRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    void findById_shouldReturnBrandById_Test() {
        assertThat(brandRepository.findById(br_1.getId())).contains(br_1);
        assertThat(brandRepository.findById(br_2.getId())).contains(br_2);
        assertThat(brandRepository.findById(br_3.getId())).contains(br_3);
    }
}