package me.oldboy.market.integration.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import me.oldboy.market.config.jwt_config.JwtTokenGenerator;
import me.oldboy.market.config.security_details.ClientDetailsService;
import me.oldboy.market.integration.TestContainerInit;
import me.oldboy.market.productmanager.core.dto.category.CategoryReadDto;
import me.oldboy.market.test_utils.TestConstant;
import me.oldboy.market.test_utils.TestTokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerIT extends TestContainerInit {
    @Autowired
    private WebApplicationContext appContext;
    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private String validJwtToken, notValidToken;
    private Long existId, nonExistId;
    private String existCategoryName, nonExistCategoryName;
    private String BEARER_PREFIX = "Bearer ";
    private String HEADER_NAME = "Authorization";

    @BeforeEach
    @SneakyThrows
    void setUp() {
        existId = TestConstant.existId;
        nonExistId = TestConstant.nonExistId;
        existCategoryName = "Обувь";
        nonExistCategoryName = "Электроника";

        mockMvc = MockMvcBuilders.webAppContextSetup(appContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        validJwtToken = TestTokenGenerator.generate(clientDetailsService, jwtTokenGenerator, BEARER_PREFIX);
        notValidToken = TestConstant.notValidToken;
    }

    @Nested
    @DisplayName("Блок тестов на *.getCategoryById() метод CategoryController")
    class GetCategoryByIdMethodTests {
        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть найденную категорию по её ID")
        void getCategoryById_shouldReturnFoundCategory_Test() {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/market/categories/{id}", existId)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isOk())
                    .andReturn();

            String strReturn = result.getResponse().getContentAsString();
            CategoryReadDto readDto = objectMapper.readValue(strReturn, CategoryReadDto.class);

            assertThat(Math.toIntExact(existId)).isEqualTo(readDto.id());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 404 Not Found для несуществующего ID категории")
        void getCategoryById_shouldReturnNotFoundStatusCode_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/categories/{id}", nonExistId)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 401 Unauthorized для невалидного JWT Token-а")
        void getCategoryById_shouldReturn4xxStatus_notValidToken_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/categories/{id}", existId)
                            .header(HEADER_NAME, notValidToken))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Test
    @SneakyThrows
    @DisplayName("Должен вернуть вернуть список всех доступных категорий товара")
    void getAllCategories_shouldReturnCategoryList_Test() {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/market/categories")
                        .header(HEADER_NAME, validJwtToken))
                .andExpect(status().isOk())
                .andReturn();

        String strReturn = result.getResponse().getContentAsString();
        List<CategoryReadDto> listFromResponse =
                objectMapper.readValue(strReturn, new TypeReference<List<CategoryReadDto>>() {
                });

        assertThat(listFromResponse.size()).isGreaterThan(2);
    }

    @Nested
    @DisplayName("Блок тестов на *.getCategoryByName() метод CategoryController")
    class GetCategoryByNameMethodTests {
        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть найденную по названию категорию")
        void getCategoryByName_shouldReturnFoundCategory_Test() {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/market/categories/")
                            .header(HEADER_NAME, validJwtToken)
                            .param("categoryName", existCategoryName))
                    .andExpect(status().isOk())
                    .andReturn();

            String strReturn = result.getResponse().getContentAsString();
            CategoryReadDto readDto = objectMapper.readValue(strReturn, CategoryReadDto.class);

            assertThat(existCategoryName).isEqualTo(readDto.name());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 404 Not Found для несуществующего названия категории")
        void getCategoryByName_shouldReturnNotFoundStatusCode_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/categories/")
                            .header(HEADER_NAME, validJwtToken)
                            .param("categoryName", nonExistCategoryName))
                    .andExpect(status().isNotFound());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 401 Unauthorized для невалидного JWT Token-а")
        void getCategoryByName_shouldReturn4xxStatus_notValidToken_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/categories/")
                            .header(HEADER_NAME, notValidToken)
                            .param("categoryName", existCategoryName))
                    .andExpect(status().is4xxClientError());
        }
    }
}