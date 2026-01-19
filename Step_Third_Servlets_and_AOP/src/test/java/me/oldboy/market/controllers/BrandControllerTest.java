package me.oldboy.market.controllers;

import me.oldboy.market.dto.brand.BrandReadDto;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.services.BrandServiceImpl;
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
class BrandControllerTest {
    @Mock
    private BrandServiceImpl brandService;
    @InjectMocks
    private BrandController brandController;
    private Integer existId, nonExistId;
    private Brand existBrand;

    @BeforeEach
    void setUp() {
        existId = 1;
        nonExistId = 100;

        existBrand = Brand.builder()
                .id(existId)
                .name("Tungus")
                .build();
    }

    @Test
    void findBrandById_shouldReturnFoundDto_Test() {
        when(brandService.findById(existId)).thenReturn(existBrand);
        BrandReadDto res = brandController.findBrandById(existId);

        assertThat(res).isNotNull();
        assertThat(res.name()).isEqualTo(existBrand.getName());
    }

    @Test
    void findBrandById_shouldReturnNull_Test() {
        when(brandService.findById(nonExistId)).thenReturn(null);

        BrandReadDto res = brandController.findBrandById(nonExistId);

        assertThat(res).isNull();
    }

    @Test
    void findAllBrands_shouldReturnAllBrandList_Test() {
        List<Brand> allBrands = Collections.singletonList(existBrand);
        when(brandService.findAll()).thenReturn(allBrands);

        List<BrandReadDto> res = brandController.findAllBrands();

        assertThat(res).isNotNull();
        assertThat(res.size()).isEqualTo(1);
    }
}