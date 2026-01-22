package me.oldboy.market.integration.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import me.oldboy.market.config.jwt_config.JwtTokenGenerator;
import me.oldboy.market.config.security_details.ClientDetailsService;
import me.oldboy.market.integration.TestContainerInit;
import me.oldboy.market.productmanager.core.dto.product.ProductCreateDto;
import me.oldboy.market.productmanager.core.dto.product.ProductReadDto;
import me.oldboy.market.productmanager.core.dto.product.ProductUpdateDto;
import me.oldboy.market.test_utils.TestConstant;
import me.oldboy.market.test_utils.TestTokenGenerator;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerIT extends TestContainerInit {
    @Autowired
    private WebApplicationContext appContext;
    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private String validJwtToken, notValidToken;
    private Long existId, nonExistId, anotherExistId;
    private String existEmail;
    private ProductCreateDto newProductCreateDto, notValidCreateDto,
            duplicateProductDto, notFoundCategoryIdDto,
            notFoundBrandIdDto;
    private ProductUpdateDto updateProductDto, notValidUpdateDto,
            updateInvalidProductIdDto, updateNotUniqueNameDto;
    private String BEARER_PREFIX = "Bearer ";
    private String HEADER_NAME = "Authorization";

    @BeforeEach
    @SneakyThrows
    void setUp() {
        existId = TestConstant.existId;
        nonExistId = TestConstant.nonExistId;
        existEmail = TestConstant.existUserEmail;
        anotherExistId = 2L;

        mockMvc = MockMvcBuilders.webAppContextSetup(appContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        validJwtToken = TestTokenGenerator.generate(clientDetailsService, jwtTokenGenerator, BEARER_PREFIX);
        notValidToken = TestConstant.notValidToken;
    }

    @Nested
    @DisplayName("Блок тестов на *.createProduct() метод ProductController")
    class CreateProductMethodTests {
        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть данные созданного товара (продукта)")
        void createProduct_shouldReturnCreatedProductDto_Test() {
            newProductCreateDto = ProductCreateDto.builder()
                    .name("Снегоступы")
                    .price(225.4)
                    .categoryId(2)
                    .brandId(3)
                    .description("Сугробы по колено.")
                    .stockQuantity(4)
                    .build();

            String forCreateProduct = objectMapper.writeValueAsString(newProductCreateDto);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/market/products/")
                            .header(HEADER_NAME, validJwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(forCreateProduct))
                    .andExpect(status().isOk())
                    .andReturn();

            String strReturn = result.getResponse().getContentAsString();
            ProductReadDto productReadDto = objectMapper.readValue(strReturn, ProductReadDto.class);

            assertThat(productReadDto.id()).isNotNull();
            assertThat(newProductCreateDto.name()).isEqualTo(productReadDto.name());
            assertThat(newProductCreateDto.price()).isEqualTo(productReadDto.price());
            assertThat(newProductCreateDto.description()).isEqualTo(productReadDto.description());
            assertThat(newProductCreateDto.stockQuantity()).isEqualTo(productReadDto.stockQuantity());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 401 Unauthorized для невалидного JWT Token-а")
        void createProduct_shouldReturnException_noValidToken_Test() {
            newProductCreateDto = ProductCreateDto.builder()
                    .name("Снегоступы")
                    .price(225.4)
                    .categoryId(2)
                    .brandId(3)
                    .description("По сугробам аки посуху.")
                    .stockQuantity(4)
                    .build();

            String forCreateProduct = objectMapper.writeValueAsString(newProductCreateDto);

            mockMvc.perform(MockMvcRequestBuilders.post("/market/products/")
                            .header(HEADER_NAME, notValidToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(forCreateProduct))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 400 Bad Request для невалидных входных данных JSON CreateDto")
        void createProduct_shouldReturnValidationError_Test() {
            notValidCreateDto = ProductCreateDto.builder()
                    .name("Сн")
                    .price(225.45643)
                    .categoryId(-2)
                    .brandId(-3)
                    .description("Су")
                    .stockQuantity(2)
                    .build();

            String forCreateProduct = objectMapper.writeValueAsString(notValidCreateDto);

            mockMvc.perform(MockMvcRequestBuilders.post("/market/products/")
                            .header(HEADER_NAME, validJwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(forCreateProduct))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("\"price\" : \"numeric value out of bounds (<22 digits>.<2 digits> expected)\"")))
                    .andExpect(content().string(containsString("\"brandId\" : \"must be greater than 0\"")))
                    .andExpect(content().string(containsString("\"name\" : \"Wrong format (to short/to long)\"")))
                    .andExpect(content().string(containsString("\"description\" : \"Wrong format (to short/to long)\"")))
                    .andExpect(content().string(containsString("\"categoryId\" : \"must be greater than 0\"")));
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 400 Bad Request для продукта с названием уже существующем в БД")
        void createReservation_shouldReturnException_duplicateProductName_Test() {
            duplicateProductDto = ProductCreateDto.builder()
                    .name("Парка")
                    .price(225.4)
                    .categoryId(2)
                    .brandId(3)
                    .description("Это вам не подерьгайка одичалых")
                    .stockQuantity(4)
                    .build();

            String forCreateProduct = objectMapper.writeValueAsString(duplicateProductDto);

            mockMvc.perform(MockMvcRequestBuilders.post("/market/products/")
                            .header(HEADER_NAME, validJwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(forCreateProduct))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Имя продукта" + duplicateProductDto.name() + " не уникально, сохранение товара не возможно")));
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 400 Bad Request для продукта с несуществующей категорией")
        void createReservation_shouldReturnException_notExistCategoryId_Test() {
            notFoundCategoryIdDto = ProductCreateDto.builder()
                    .name("Снегоступы")
                    .price(225.4)
                    .categoryId(10)
                    .brandId(3)
                    .description("Сугробы по колено.")
                    .stockQuantity(4)
                    .build();

            String forCreateProduct = objectMapper.writeValueAsString(notFoundCategoryIdDto);

            mockMvc.perform(MockMvcRequestBuilders.post("/market/products/")
                            .header(HEADER_NAME, validJwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(forCreateProduct))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Категория с ID - " + notFoundCategoryIdDto.categoryId() + " не найдена, сохранение товара не возможно")));
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 400 Bad Request для продукта с несуществующим брэндом")
        void createReservation_shouldReturnException_notExistBrandId_Test() {
            notFoundBrandIdDto = ProductCreateDto.builder()
                    .name("Снегоступы")
                    .price(225.4)
                    .categoryId(2)
                    .brandId(312)
                    .description("Сугробы по колено.")
                    .stockQuantity(4)
                    .build();

            String forCreateProduct = objectMapper.writeValueAsString(notFoundBrandIdDto);

            mockMvc.perform(MockMvcRequestBuilders.post("/market/products/")
                            .header(HEADER_NAME, validJwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(forCreateProduct))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Брэнд с ID - " + notFoundBrandIdDto.brandId() + " не найден, сохранение товара не возможно")));
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.updateProduct() метод ProductController")
    class UpdateProductMethodTests {
        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть обновленные данные для существующего продукта")
        void updateProduct_shouldReturnTrue_afterUpdateProduct_Test() {
            updateProductDto = ProductUpdateDto.builder()
                    .id(existId)
                    .name("Пончо")
                    .price(1123.2)
                    .description("B осла накрыть и самому укрыться")
                    .stockQuantity(5)
                    .build();

            String forUpdateProduct = objectMapper.writeValueAsString(updateProductDto);

            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/market/products/")
                            .header(HEADER_NAME, validJwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(forUpdateProduct))
                    .andExpect(status().isOk())
                    .andReturn();

            String strReturn = result.getResponse().getContentAsString();
            ProductReadDto productReadDto = objectMapper.readValue(strReturn, ProductReadDto.class);

            SoftAssertions softly = new SoftAssertions();

            softly.assertThat(updateProductDto.id()).isEqualTo(productReadDto.id());
            softly.assertThat(updateProductDto.name()).isEqualTo(productReadDto.name());
            softly.assertThat(updateProductDto.price()).isEqualTo(productReadDto.price());
            softly.assertThat(updateProductDto.description()).isEqualTo(productReadDto.description());
            softly.assertThat(updateProductDto.stockQuantity()).isEqualTo(productReadDto.stockQuantity());

            softly.assertAll();
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 401 Unauthorized для невалидного JWT Token-а")
        void updateProduct_shouldReturnException_onUpdateProductNoValidToken_Test() {
            updateProductDto = ProductUpdateDto.builder()
                    .id(existId)
                    .name("Пончо")
                    .price(1123.2)
                    .description("B осла накрыть и самому укрыться")
                    .stockQuantity(5)
                    .build();

            String forUpdateProduct = objectMapper.writeValueAsString(updateProductDto);

            mockMvc.perform(MockMvcRequestBuilders.put("/market/products/")
                            .header(HEADER_NAME, notValidToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(forUpdateProduct))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 400 Bad Request при неверном ID обновляемого продукта")
        void updateProduct_shouldReturnException_notFoundProductId_Test() {
            updateInvalidProductIdDto = ProductUpdateDto.builder()
                    .id(nonExistId)
                    .name("Пончо")
                    .price(1123.2)
                    .description("B осла накрыть и самому укрыться")
                    .stockQuantity(5)
                    .build();

            String forUpdateProduct = objectMapper.writeValueAsString(updateInvalidProductIdDto);

            mockMvc.perform(MockMvcRequestBuilders.put("/market/products/")
                            .header(HEADER_NAME, validJwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(forUpdateProduct))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Не найден ID - " + updateInvalidProductIdDto.id() + " продукта, обновление невозможно")));
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 400 Bad Request при не уникальном обновленном названии продукта")
        void updateProduct_shouldReturnException_notUniqueProductName_Test() {
            updateNotUniqueNameDto = ProductUpdateDto.builder()
                    .id(existId)
                    .name("Унты")
                    .price(1123.2)
                    .description("Пригодятся даже если у вас волосатые ноги")
                    .stockQuantity(5)
                    .build();

            String forUpdateProduct = objectMapper.writeValueAsString(updateNotUniqueNameDto);

            mockMvc.perform(MockMvcRequestBuilders.put("/market/products/")
                            .header(HEADER_NAME, validJwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(forUpdateProduct))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Имя продукта " + updateNotUniqueNameDto.name() + " не уникально, обновление невозможно")));
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 400 Bad Request и описание проблемы при введении не валидных данных")
        void updateProduct_shouldReturnValidationError_Test() {
            notValidUpdateDto = ProductUpdateDto.builder()
                    .id(-existId)
                    .name("Ун")
                    .price(1123.2323)
                    .description("Пр")
                    .stockQuantity(5)
                    .build();

            String forUpdateProduct = objectMapper.writeValueAsString(notValidUpdateDto);

            mockMvc.perform(MockMvcRequestBuilders.put("/market/products/")
                            .header(HEADER_NAME, validJwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(forUpdateProduct))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("\"price\" : \"numeric value out of bounds (<22 digits>.<2 digits> expected)\"")))
                    .andExpect(content().string(containsString("\"name\" : \"Wrong format (to short/to long)\"")))
                    .andExpect(content().string(containsString("\"description\" : \"Wrong format (to short/to long)\"")))
                    .andExpect(content().string(containsString("\"id\" : \"must be greater than 0\"")));
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.deleteProduct() метод ProductController")
    class DeleteProductMethodTests {
        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 204 No Content при удачном удалении продукта")
        void deleteProduct_shouldReturnTrueAndNoContent_afterDeleteProduct_Test() {
            mockMvc.perform(MockMvcRequestBuilders.delete("/market/products/{deleteId}", existId)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isNoContent());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 401 Unauthorized для невалидного JWT Token-а")
        void deleteProduct_shouldReturnUnauthorizedException_onDeleteProductNoValidToken_Test() {
            mockMvc.perform(MockMvcRequestBuilders.delete("/market/products/{deleteId}", existId)
                            .header(HEADER_NAME, notValidToken))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 400 Bad Request при неудачном удалении продукта - несуществующий ID")
        void deleteProduct_shouldReturnException_notExistentProductId_Test() {
            mockMvc.perform(MockMvcRequestBuilders.delete("/market/products/{deleteId}", nonExistId)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("Not found product with ID - " + nonExistId + " for delete!")));
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.getProductById() метод ProductController")
    class GetProductByIdMethodTests {
        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть найденный товар по существующему ID")
        void getProductById_shouldReturnFoundProduct_Test() {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/market/products/{productId}", existId)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isOk())
                    .andReturn();

            String strReturn = result.getResponse().getContentAsString();
            ProductReadDto productReadDto = objectMapper.readValue(strReturn, ProductReadDto.class);

            assertThat(existId).isEqualTo(productReadDto.id());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 404 Not Found для несуществующего товара")
        void getProductById_shouldReturnNotFoundProduct_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/products/{productId}", nonExistId)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 401 Unauthorized для невалидного JWT Token-а")
        void getProductById_shouldReturn4xxException_notValidToken_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/products/{productId}", existId)
                            .header(HEADER_NAME, notValidToken))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Test
    @SneakyThrows
    @DisplayName("Должен вернуть список всех существующих продуктов")
    void getAllProducts_shouldReturnProductList_Test() {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/market/products")
                        .header(HEADER_NAME, validJwtToken))
                .andExpect(status().isOk())
                .andReturn();

        String strReturn = result.getResponse().getContentAsString();
        List<ProductReadDto> listFromResponse =
                objectMapper.readValue(strReturn, new TypeReference<List<ProductReadDto>>() {
                });

        assertThat(listFromResponse.size()).isGreaterThan(8);
    }

    @Nested
    @DisplayName("Блок тестов на *.getAllProductsByCategoryId() метод ProductController")
    class GetAllProductsByCategoryIdMethodTests {
        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть список всех существующих продуктов для выбранной категории")
        void getAllProductsByCategoryId_shouldReturnNotEmptyList_Test() {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/market/products/categories/{id}", existId)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isOk())
                    .andReturn();

            String strReturn = result.getResponse().getContentAsString();
            List<ProductReadDto> listFromResponse =
                    objectMapper.readValue(strReturn, new TypeReference<List<ProductReadDto>>() {
                    });

            assertThat(listFromResponse.size()).isGreaterThan(2);
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 404 Not Found, пустой список если в категории нет продуктов или категория не существует")
        void getAllProductsByCategoryId_shouldReturnEmptyList_notExistentCategoryOrEmpty_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/products/categories/{id}", nonExistId)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 401 Unauthorized для невалидного JWT Token-а")
        void getAllProductsByCategoryId_shouldReturn4xxStatus_notValidToken_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/products/categories/{id}", existId)
                            .header(HEADER_NAME, notValidToken))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.getAllProductsByBrandId() метод ProductController")
    class GetAllProductsByBrandIdMethodTests {
        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть список всех существующих продуктов для выбранного брэнда")
        void getAllProductsByBrandId_shouldReturnProductList_Test() {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/market/products/brands/{id}", existId)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isOk())
                    .andReturn();

            String strReturn = result.getResponse().getContentAsString();
            List<ProductReadDto> listFromResponse =
                    objectMapper.readValue(strReturn, new TypeReference<List<ProductReadDto>>() {
                    });

            assertThat(listFromResponse.size()).isGreaterThan(2);
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 404 Not Found, пустой список если под брэндом нет продуктов или брэнда не существует")
        void getAllProductsByBrandId_shouldReturnEmptyList_notExistentBrandOrEmpty_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/products/brands/{id}", nonExistId)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 401 Unauthorized для невалидного JWT Token-а")
        void getAllProductsByBrandId_shouldReturn4xxStatus_notValidToken_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/products/brands/{id}", existId)
                            .header(HEADER_NAME, notValidToken))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.getProductByBrandAndName() метод ProductController")
    class GetProductByBrandAndNameMethodTests {
        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть продукт конкретного брэнда с искомым названием")
        void getProductByBrandAndName_shouldReturnFoundProduct_Test() {
            String productName = "Парка";
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/market/products/brands/{Id}/", existId)
                            .param("productName", productName)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isOk())
                    .andReturn();

            String strReturn = result.getResponse().getContentAsString();
            ProductReadDto productReadDto = objectMapper.readValue(strReturn, ProductReadDto.class);

            assertThat(existId).isEqualTo(productReadDto.id());
            assertThat(productName).isEqualTo(productReadDto.name());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 404 Not Found для несуществующего названия товара при существующем брэнде")
        void getProductByBrandAndName_shouldReturnNotFound_notExistName_Test() {
            String productName = "Палатка";
            mockMvc.perform(MockMvcRequestBuilders.get("/market/products/brands/{Id}/", existId)
                            .param("productName", productName)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 404 Not Found при несуществующего брэнде, но существующем названии")
        void getProductByBrandAndName_shouldReturnNotFound_notExistBrand_Test() {
            String productName = "Парка";
            mockMvc.perform(MockMvcRequestBuilders.get("/market/products/brands/{Id}/", nonExistId)
                            .param("productName", productName)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 401 Unauthorized для невалидного JWT Token-а")
        void getProductByBrandAndName_shouldReturn4xxStatus_notValidToken_Test() {
            String productName = "Парка";
            mockMvc.perform(MockMvcRequestBuilders.get("/market/products/brands/{Id}/", existId)
                            .param("productName", productName)
                            .header(HEADER_NAME, notValidToken))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.getAllProductsByBrandAndCategories() метод ProductController")
    class GetAllProductsByBrandAndCategoriesMethodTests {
        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть список товаров из выбранной категории с выбранным брэндом")
        void getAllProductsByBrandAndCategories_shouldReturnProductList_Test() {
            MvcResult result =
                    mockMvc.perform(MockMvcRequestBuilders.get("/market/products/brands/{id}/categories/{categoryId}", existId, anotherExistId)
                                    .header(HEADER_NAME, validJwtToken))
                            .andExpect(status().isOk())
                            .andReturn();

            String strReturn = result.getResponse().getContentAsString();
            List<ProductReadDto> listFromResponse =
                    objectMapper.readValue(strReturn, new TypeReference<List<ProductReadDto>>() {
                    });

            assertThat(listFromResponse.size()).isGreaterThan(1);
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 404 Not Found в случае отсутствия товаров в заданной категории с заданным брэндом")
        void getAllProductsByBrandAndCategories_shouldReturnEmptyListOrNotFound_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/products/brands/{id}/categories/{categoryId}", existId, nonExistId)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 401 Unauthorized для невалидного JWT Token-а")
        void getAllProductsByBrandAndCategories_shouldReturn4xxStatus_notValidToken_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/products/brands/{id}/categories/{categoryId}", existId, nonExistId)
                            .header(HEADER_NAME, notValidToken))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.getProductByIdAndCategory() метод ProductController")
    class GetProductByIdAndCategoryMethodTests {
        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть найденный товар по его ID и ID категории")
        void getProductByIdAndCategory_shouldReturnFoundProduct_Test() {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/market/products/{Id}/categories/{categoryId}", existId, anotherExistId)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isOk())
                    .andReturn();

            String strReturn = result.getResponse().getContentAsString();
            ProductReadDto productReadDto = objectMapper.readValue(strReturn, ProductReadDto.class);

            assertThat(existId).isEqualTo(productReadDto.id());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 404 Not Found для несуществующей комбинации ID товара и категории")
        void getProductByIdAndCategory_shouldReturnNotFoundProduct_productOrCategoryIdNotFoundOrInvalidCombination_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/products/{Id}/categories/{categoryId}", existId, existId)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 401 Unauthorized для невалидного JWT Token-а")
        void getProductByIdAndCategory_shouldReturn4xxStatus_notValidToken_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/products/{Id}/categories/{categoryId}", existId, existId)
                            .header(HEADER_NAME, notValidToken))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.getProductByIdAndBrand() метод ProductController")
    class GetProductByIdAndBrandMethodTests {
        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть найденный товар по его ID и ID выбранного брэнда")
        void getProductByIdAndBrand_shouldReturnFoundProduct_Test() {
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/market/products/{Id}/brands/{brandId}", existId, existId)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isOk())
                    .andReturn();

            String strReturn = result.getResponse().getContentAsString();
            ProductReadDto productReadDto = objectMapper.readValue(strReturn, ProductReadDto.class);

            assertThat(existId).isEqualTo(productReadDto.id());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 404 Not Found для несуществующей комбинации ID товара и брэнда")
        void getProductByIdAndBrand_shouldReturnNotFoundProduct_productOrBrandIdNotFoundOrInvalidCombination_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/products/{Id}/brands/{brandId}", existId, anotherExistId)
                            .header(HEADER_NAME, validJwtToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @SneakyThrows
        @DisplayName("Должен вернуть 404 Not Found для несуществующего названия категории")
        void getProductByIdAndBrand_shouldReturn4xxStatus_notValidToken_Test() {
            mockMvc.perform(MockMvcRequestBuilders.get("/market/products/{Id}/brands/{brandId}", existId, anotherExistId)
                            .header(HEADER_NAME, notValidToken))
                    .andExpect(status().is4xxClientError());
        }
    }
}