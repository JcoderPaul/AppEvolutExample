package me.oldboy.market.integration.services;

import me.oldboy.market.integration.TestContainerInit;
import me.oldboy.market.productmanager.core.dto.brand.BrandReadDto;
import me.oldboy.market.productmanager.core.services.interfaces.BrandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class BrandServiceImplIT extends TestContainerInit {
    @Autowired
    private BrandService brandService;
    private Integer existId, nonExistId;
    private String existBrandName, nonExistBrandName;

    @BeforeEach
    public void setUp() {
        /* Предварительные тестовые данные */
        existId = 1;
        nonExistId = 100;

        existBrandName = "Puma";
        nonExistBrandName = "Kocherizky";
    }

    /* Основные тесты для реализаций методов */

    @Test
    @DisplayName("Должен вернуть true - найденный по ID брэнд - брэнд есть в БД (сверка названия подтверждена)")
    void findById_shouldReturnFoundBrand_Test() {
        Optional<BrandReadDto> foundBrand = brandService.findById(existId);
        assertThat(foundBrand.isPresent()).isTrue();
        assertThat(foundBrand.get().name()).isEqualTo(existBrandName);
    }

    @Test
    @DisplayName("Должен вернуть true и пустую запись - брэнд по ID не найден - ID в БД отсутствует")
    void findById_shouldReturnOptionalEmpty_notFoundBrand_Test() {
        assertThat(brandService.findById(nonExistId).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Должен вернуть список всех брэндов доступных в БД")
    void findAll_shouldReturnAllBrandList_Test() {
        assertThat(brandService.findAll().size()).isEqualTo(4);
    }

    @Test
    @DisplayName("Должен вернуть true - найден брэнд по его названию, если в БД есть такое название")
    void findByName_shouldReturnFoundBrand_existName_Test() {
        Optional<BrandReadDto> foundBrand = brandService.findByName(existBrandName);
        assertThat(foundBrand.isPresent()).isTrue();
        assertThat(foundBrand.get().id()).isEqualTo(existId);
    }

    @Test
    @DisplayName("Должен вернуть пустую запись - брэнд по названию не найден, если в БД такого названия нет")
    void findByName_shouldReturnOptionalEmpty_notExistName_Test() {
        assertThat(brandService.findByName(nonExistBrandName).isEmpty()).isTrue();
    }
}