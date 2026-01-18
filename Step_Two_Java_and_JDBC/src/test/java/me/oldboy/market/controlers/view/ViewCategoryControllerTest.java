package me.oldboy.market.controlers.view;

import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.repository.CategoryRepository;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViewCategoryControllerTest {
    @Mock
    private CategoryRepository categoryRepository;
    @InjectMocks
    private ViewCategoryController viewCategoryController;
    private List<Category> categoryList;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        categoryList = Arrays.asList(
                createTestCategory(1, "Оружие"),
                createTestCategory(2, "Снаряжение"),
                createTestCategory(3, "Боеприпасы")
        );
    }

    @Test
    void printAllCategory_shouldPrintExpectedDataToScreen_Test() {
        when(categoryRepository.findAll()).thenReturn(categoryList);

        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        List<Category> result = viewCategoryController.printAllCategory();

        assertThat(result).isNotNull();
        assertThat(3).isEqualTo(result.size());

        verify(categoryRepository, times(1)).findAll();

        String output = outputStream.toString();
        assertTrue(output.contains("Оружие"));
        assertTrue(output.contains("Снаряжение"));
        assertTrue(output.contains("Боеприпасы"));

        System.setOut(originalOut);
    }

    private Category createTestCategory(Integer id, String name) {
        return Category.builder()
                .id(id)
                .name(name)
                .build();
    }
}