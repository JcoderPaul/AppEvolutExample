package me.oldboy.market.integration.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import me.oldboy.market.config.jwt_config.JwtTokenGenerator;
import me.oldboy.market.config.security_details.ClientDetailsService;
import me.oldboy.market.integration.TestContainerInit;
import me.oldboy.market.productmanager.core.dto.brand.BrandReadDto;
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

class BrandControllerIT extends TestContainerInit {
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
    private String existBrandName, nonExistBrandName;
    private String BEARER_PREFIX = "Bearer ";
    private String HEADER_NAME = "Authorization";

    @BeforeEach
    @SneakyThrows
    void setUp() {
        existId = TestConstant.existId;
        nonExistId = TestConstant.nonExistId;
        existBrandName = "Puma";
        nonExistBrandName = "Amup";

        mockMvc = MockMvcBuilders.webAppContextSetup(appContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        validJwtToken = TestTokenGenerator.generate(clientDetailsService, jwtTokenGenerator, BEARER_PREFIX);
        notValidToken = TestConstant.notValidToken;
    }

    @Nested
    @DisplayName("Блок тестов на *.getBrandById() метод BrandController")
    class GetBrandByIdMethodTests {
        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть найденный брэнд по его ID")
        void getBrandById_shouldReturnFoundBrand_Test() {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/market/brands/{id}", existId)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isOk())
                    .andReturn();

            String strReturn = result.getResponse().getContentAsString();
            BrandReadDto readDto = objectMapper.readValue(strReturn, BrandReadDto.class);

            assertThat(Math.toIntExact(existId)).isEqualTo(readDto.id());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 404 Not Found для несуществующего ID")
        void getBrandById_shouldReturnNotFoundStatus_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/brands/{id}", nonExistId)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 401 Unauthorized для невалидного JWT Token-а")
        void getBrandById_shouldReturn4xxStatus_notValidToken_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/brands/{id}", existId)
                            .header(HEADER_NAME, notValidToken))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Test
    @SneakyThrows
    @DisplayName("Должен вернуть список всех брэндов")
    void getAllBrands_shouldReturnBrandList_Test() {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/market/brands")
                        .header(HEADER_NAME, validJwtToken))
                .andExpect(status().isOk())
                .andReturn();

        String strReturn = result.getResponse().getContentAsString();
        List<BrandReadDto> listFromResponse =
                objectMapper.readValue(strReturn, new TypeReference<List<BrandReadDto>>() {
                });

        assertThat(listFromResponse.size()).isGreaterThan(3);
    }

    @Nested
    @DisplayName("Блок тестов на *.getBrandByName() метод BrandController")
    class GetBrandByNameMethodTests {
        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть брэнд найденный по названию")
        void getBrandByName_shouldReturnFoundBrand_Test() {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/market/brands/")
                            .header(HEADER_NAME, validJwtToken)
                            .param("brandName", existBrandName))
                    .andExpect(status().isOk())
                    .andReturn();

            String strReturn = result.getResponse().getContentAsString();
            BrandReadDto readDto = objectMapper.readValue(strReturn, BrandReadDto.class);

            assertThat(existBrandName).isEqualTo(readDto.name());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 404 Not Found для несуществующего названия")
        void getBrandByName_shouldReturnNotFoundStatus_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/brands/")
                            .header(HEADER_NAME, validJwtToken)
                            .param("brandName", nonExistBrandName))
                    .andExpect(status().isNotFound());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 401 Unauthorized для невалидного JWT Token-а")
        void getBrandByName_shouldReturn4xxStatus_notValidToken_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/brands/")
                            .header(HEADER_NAME, notValidToken)
                            .param("brandName", existBrandName))
                    .andExpect(status().is4xxClientError());
        }
    }
}