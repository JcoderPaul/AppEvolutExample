package me.oldboy.market.integration.repository;

import me.oldboy.market.config.test_data_source.TestContainerInit;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.repository.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class BrandRepositoryIT extends TestContainerInit {

    @Autowired
    private BrandRepository brandRepository;
    private Integer existId, notExistingId;
    private String existName, notExistingName;

    @BeforeEach
    void setUp() {
        existId = 1;
        notExistingId = 200;

        existName = "Puma";
        notExistingName = "Burner";
    }

    @Test
    @DisplayName("Должен вернуть найденный по ID брэнд - брэнд есть в БД")
    void findById_shouldReturnTrue_forExistingBrand_Test() {
        Optional<Brand> mayBeExistBrand = brandRepository.findById(existId);
        if (mayBeExistBrand.isPresent()) {
            assertThat(mayBeExistBrand.get()).isNotNull();
        }
    }

    @Test
    @DisplayName("Должен вернуть false для ненайденного по ID брэнда - брэнд в БД отсутствует")
    void findById_shouldReturnFalse_forNonExistingBrand_Test() {
        assertThat(brandRepository.findById(notExistingId).isPresent()).isFalse();
    }

    @Test
    @DisplayName("Должен вернуть список всех брэндов доступных в БД")
    void findAll_shouldReturnBrandList_Test() {
        assertThat(brandRepository.findAll()).isNotNull();
        assertThat(brandRepository.findAll().size()).isEqualTo(4);
    }

    @Test
    @DisplayName("Должен вернуть брэнд по его названию")
    void findByName_shouldReturnTrue_forExistingBrandName_Test() {
        assertThat(brandRepository.findByName(existName).isPresent()).isTrue();
    }

    @Test
    @DisplayName("Должен вернуть false брэнд по названию не найден")
    void findByName_shouldReturnFalse_forNonExistingBrandName_Test() {
        assertThat(brandRepository.findByName(notExistingName).isPresent()).isFalse();
    }
}