package me.oldboy.market.controlers.view;

import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.repository.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViewBrandControllerTest {
    @Mock
    private BrandRepository brandRepository;
    @InjectMocks
    private ViewBrandController viewBrandController;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private List<Brand> brandList;

    @BeforeEach
    void setUp() {
        brandList = Arrays.asList(
                createTestBrand(1, "Walther"),
                createTestBrand(2, "Beretta"),
                createTestBrand(3, "Heckler & Koch")
        );
    }

    @Test
    void printAllBrands_shouldPrintExpectedDataToScreen_Test() {
        when(brandRepository.findAll()).thenReturn(brandList);

        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        List<Brand> result = viewBrandController.printAllBrands();

        assertThat(result).isNotNull();
        assertThat(3).isEqualTo(result.size());

        verify(brandRepository, times(1)).findAll();

        String output = outputStream.toString();
        assertThat(output.contains("Walther")).isTrue();
        assertThat(output.contains("Beretta")).isTrue();
        assertThat(output.contains("Heckler & Koch")).isTrue();

        System.setOut(originalOut);
    }

    private Brand createTestBrand(Integer id, String name) {
        return Brand.builder()
                .id(id)
                .name(name)
                .build();
    }
}