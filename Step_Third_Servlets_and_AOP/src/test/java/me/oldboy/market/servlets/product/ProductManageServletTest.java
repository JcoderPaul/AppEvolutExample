package me.oldboy.market.servlets.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import me.oldboy.market.controllers.ProductController;
import me.oldboy.market.dto.JsonFormResponse;
import me.oldboy.market.dto.product.ProductCreateDto;
import me.oldboy.market.dto.product.ProductReadDto;
import me.oldboy.market.dto.product.ProductUpdateDto;
import me.oldboy.market.entity.enums.Role;
import me.oldboy.market.exceptions.ControllerLayerException;
import me.oldboy.market.mapper.ProductMapper;
import me.oldboy.market.security.JwtAuthUser;
import me.oldboy.market.servlets.MockServletInputStream;
import me.oldboy.market.servlets.MockServletOutputStream;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductManageServletTest {
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;
    @Mock
    private ProductController productController;
    @Mock
    private static ServletContext servletContext;
    @Mock
    private static ServletConfig servletConfig;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private ProductManageServlet productManageServlet;
    private MockServletInputStream mockInputStream;
    private MockServletOutputStream mockOutputStream;
    private JwtAuthUser jwtAuthUser;
    private ProductReadDto productReadDto;
    private ProductCreateDto productCreateDto;
    private ProductUpdateDto productUpdateDto;
    private ObjectWriter objectWriter;
    private String jsonDtoResponse, jsonListResponse, jsonMessageResponse;
    private List<ProductReadDto> productReadDtoList, categoryProdList, brandProdList;

    @BeforeEach
    void setUp() throws Exception {
        jwtAuthUser = new JwtAuthUser("admin@market.ru", Role.ADMIN);

        /* Настроим имитацию записей для CRUD операций */
        productCreateDto = ProductCreateDto.builder()
                .name("Завтрак туриста с перловкой и свининой")
                .price(145.6)
                .categoryId(1)
                .brandId(2)
                .description("Пальчики оближешь")
                .stockQuantity(100)
                .build();
        productReadDto = ProductReadDto.builder()
                .id(13L)
                .name("Завтрак туриста с перловкой и свининой")
                .price(145.6)
                .categoryId(1)
                .brandId(2)
                .description("Пальчики оближешь")
                .stockQuantity(100)
                .creationAt(LocalDateTime.now().minusMonths(1))
                .modifiedAt(LocalDateTime.now().plusMonths(1))
                .build();
        productUpdateDto = ProductUpdateDto.builder()
                .id(13L)
                .name("Корм собачий 'Радость гурмана'")
                .price(245.6)
                .description("Не лезь лапой, ешь мордой")
                .stockQuantity(300)
                .build();

        categoryProdList = List.of(
                ProductReadDto.builder().id(1L).name("Лук").categoryId(1).brandId(1).build(),
                ProductReadDto.builder().id(2L).name("Стрелы").categoryId(1).brandId(1).build()
        );
        brandProdList = List.of(
                ProductReadDto.builder().id(3L).name("Мясо").categoryId(2).brandId(2).build(),
                ProductReadDto.builder().id(4L).name("Пирожки").categoryId(2).brandId(2).build()
        );
        productReadDtoList = new ArrayList<>();
        productReadDtoList.addAll(categoryProdList);
        productReadDtoList.addAll(brandProdList);

        /*
        Нам нужно качественно переписать JSON в String наш возвращаемый "response"
        объект - используем функционал ObjectWriter, так же "Объясним" ObjectWriter-у,
        как работать с датами
        */
        ObjectMapper objectMapperForOutput = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        objectMapperForOutput.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        objectMapperForOutput.registerModule(module);

        objectWriter = objectMapperForOutput.writer().withDefaultPrettyPrinter();

        /* Формируем подмену исходящего потока с response */
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mockOutputStream = new MockServletOutputStream(outputStream);

        /* Формируем стандартные mock-и/stub-ы для тестируемого метода */
        when(resp.getOutputStream()).thenReturn(mockOutputStream);

        doAnswer(invocation -> {
            /* Аргументов в методе *.writeValue() два: 0 - исходящий поток, 1 - то, что в него пытаются затолкать */
            OutputStream outputStreamFromDoPostMethod = invocation.getArgument(0);
            /* Метод обрабатывает 3-и варианта входящих данных см. тестируемый класс */
            if (invocation.getArgument(1) instanceof ProductReadDto) {
                ProductReadDto productReadDtoToOutput = invocation.getArgument(1);
                jsonDtoResponse = objectWriter.writeValueAsString(productReadDtoToOutput);
                outputStreamFromDoPostMethod.write(jsonDtoResponse.getBytes(StandardCharsets.UTF_8));
            } else if (invocation.getArgument(1) instanceof List) {
                List<ProductReadDto> listToPrint = invocation.getArgument(1);
                jsonListResponse = objectWriter.writeValueAsString(listToPrint);
                outputStreamFromDoPostMethod.write(jsonListResponse.getBytes(StandardCharsets.UTF_8));
            } else if (invocation.getArgument(1) instanceof JsonFormResponse) {
                JsonFormResponse response = invocation.getArgument(1);
                jsonMessageResponse = "{\"message\":\"" + response.message() + "\"}";
                outputStreamFromDoPostMethod.write(jsonMessageResponse.getBytes(StandardCharsets.UTF_8));
            }
            return null;
        }).when(this.objectMapper).writeValue(any(OutputStream.class), any());
    }

    @AfterEach
    public void closeAllStream() throws IOException {
        resp.getOutputStream().close();
    }

    @SneakyThrows
    @Test
    void doPost_createProductSuccess_Test() {
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute("authentication")).thenReturn(jwtAuthUser);

        /* Формируем подмену входящего потока */
        String strRequestBody = objectWriter.writeValueAsString(productCreateDto);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(strRequestBody.getBytes(StandardCharsets.UTF_8));
        mockInputStream = new MockServletInputStream(inputStream);

        when(req.getInputStream()).thenReturn(mockInputStream);
        when(objectMapper.readValue(mockInputStream, ProductCreateDto.class)).thenReturn(productCreateDto);
        when(productController.createProduct(productCreateDto, jwtAuthUser.getEmail()))
                .thenReturn(ProductMapper.INSTANCE.mapReadDtoToEntity(productReadDto));

        productManageServlet.doPost(req, resp);

        assertThat(jsonMessageResponse).contains("Product creation was successful");

        verify(resp, times(1)).setContentType("application/json");
        verify(resp, times(1)).setStatus(HttpServletResponse.SC_CREATED);
        verify(resp, times(1)).getOutputStream();
        verify(req, times(1)).getInputStream();

        req.getInputStream().close();
    }

    /* В данном случае еще два исключения со слоя контроллеров из метода *.createProduct() тестируются абсолютно также */
    @SneakyThrows
    @Test
    void doPost_createProductThrowException_Test() {
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute("authentication")).thenReturn(jwtAuthUser);

        /* Формируем подмену входящего потока */
        String strRequestBody = objectWriter.writeValueAsString(productCreateDto);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(strRequestBody.getBytes(StandardCharsets.UTF_8));
        mockInputStream = new MockServletInputStream(inputStream);

        when(req.getInputStream()).thenReturn(mockInputStream);
        when(objectMapper.readValue(mockInputStream, ProductCreateDto.class)).thenReturn(productCreateDto);
        when(productController.createProduct(productCreateDto, jwtAuthUser.getEmail()))
                .thenThrow(new ControllerLayerException("The specified category was not found, ID - " + productCreateDto.categoryId() + " not correct"));

        productManageServlet.doPost(req, resp);

        assertThat(resp.getOutputStream().toString())
                .isEqualTo("{\"message\":\"The specified category was not found, ID - " + productCreateDto.categoryId() + " not correct\"}");

        verify(resp, times(1)).setContentType("application/json");
        verify(resp, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(resp, times(2)).getOutputStream();
        verify(req, times(1)).getInputStream();

        req.getInputStream().close();
    }

    @SneakyThrows
    @Test
    void doPut_updateProductSuccess_Test() {
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute("authentication")).thenReturn(jwtAuthUser);

        /* Формируем подмену входящего потока */
        String strRequestBody = objectWriter.writeValueAsString(productUpdateDto);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(strRequestBody.getBytes(StandardCharsets.UTF_8));
        mockInputStream = new MockServletInputStream(inputStream);

        when(req.getInputStream()).thenReturn(mockInputStream);
        when(objectMapper.readValue(mockInputStream, ProductUpdateDto.class)).thenReturn(productUpdateDto);
        when(productController.updateProduct(productUpdateDto, jwtAuthUser.getEmail()))
                .thenReturn(true);

        productManageServlet.doPut(req, resp);

        assertThat(jsonMessageResponse).contains("Update success!");

        verify(resp, times(1)).setContentType("application/json");
        verify(resp, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(resp, times(1)).getOutputStream();
        verify(req, times(1)).getInputStream();

        req.getInputStream().close();
    }

    @SneakyThrows
    @Test
    void doPut_updateProductException_notFoundProduct_Test() {
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute("authentication")).thenReturn(jwtAuthUser);

        /* Формируем подмену входящего потока */
        String strRequestBody = objectWriter.writeValueAsString(productUpdateDto);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(strRequestBody.getBytes(StandardCharsets.UTF_8));
        mockInputStream = new MockServletInputStream(inputStream);

        when(req.getInputStream()).thenReturn(mockInputStream);
        when(objectMapper.readValue(mockInputStream, ProductUpdateDto.class)).thenReturn(productUpdateDto);
        when(productController.updateProduct(productUpdateDto, jwtAuthUser.getEmail()))
                .thenThrow(new ControllerLayerException("Updating product with ID - " + productUpdateDto.id() + " not found"));

        productManageServlet.doPut(req, resp);

        assertThat(resp.getOutputStream().toString())
                .isEqualTo("{\"message\":\"Updating product with ID - " + productUpdateDto.id() + " not found\"}");

        verify(resp, times(1)).setContentType("application/json");
        verify(resp, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(resp, times(2)).getOutputStream();
        verify(req, times(1)).getInputStream();

        req.getInputStream().close();
    }

    @SneakyThrows
    @Test
    void doDelete_deleteProductSuccess_Test() {
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute("authentication")).thenReturn(jwtAuthUser);

        /* Формируем подмену входящего потока */
        when(req.getPathInfo()).thenReturn("/1");
        when(productController.deleteProduct(1L, jwtAuthUser.getEmail())).thenReturn(true);

        productManageServlet.doDelete(req, resp);

        assertThat(jsonMessageResponse).contains("Remove success!");

        verify(resp, times(1)).setContentType("application/json");
        verify(resp, times(1)).setStatus(HttpServletResponse.SC_NO_CONTENT);
        verify(resp, times(1)).getOutputStream();
    }

    @SneakyThrows
    @Test
    void doDelete_deleteProductFailed_Test() {
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute("authentication")).thenReturn(jwtAuthUser);

        /* Формируем подмену входящего потока */
        when(req.getPathInfo()).thenReturn("/1");
        when(productController.deleteProduct(1L, jwtAuthUser.getEmail())).thenReturn(false);

        productManageServlet.doDelete(req, resp);

        assertThat(jsonMessageResponse).contains("Remove failed!");

        verify(resp, times(1)).setContentType("application/json");
        verify(resp, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(resp, times(1)).getOutputStream();
    }

    @DisplayName("Тест на *.handleAllProducts() метод ProductManageServlet")
    @SneakyThrows
    @Test
    void doGet_viewAllProduct_Test() {
        when(req.getPathInfo()).thenReturn("/");
        when(req.getParameter("prodName")).thenReturn(null);
        when(req.getParameterMap()).thenReturn(Map.of());
        when(productController.findAllProduct()).thenReturn(productReadDtoList);

        productManageServlet.doGet(req, resp);

        assertThat(jsonListResponse).contains(objectWriter.writeValueAsString(productReadDtoList));

        verify(resp, times(1)).setContentType("application/json");
        verify(resp, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(resp, times(1)).getOutputStream();
    }

    @Nested
    @DisplayName("Блок тестов на *.handleProductsByName() метод ProductManageServlet")
    class HandleProductsByNameMethodOnProductManageServletTests {
        @SneakyThrows
        @Test
        void doGet_viewProductByName_Test() {
            when(req.getPathInfo()).thenReturn("/");
            when(req.getParameter("prodName")).thenReturn("Лук");
            when(req.getParameterMap()).thenReturn(Map.of());
            when(productController.findProductByName("Лук")).thenReturn(productReadDto);

            productManageServlet.doGet(req, resp);

            assertThat(jsonDtoResponse).contains(objectWriter.writeValueAsString(productReadDto));

            verify(resp, times(1)).setContentType("application/json");
            verify(resp, times(1)).setStatus(HttpServletResponse.SC_OK);
            verify(resp, times(1)).getOutputStream();
        }

        @SneakyThrows
        @Test
        void doGet_viewProductByName_notFoundAnswer_Test() {
            String prodName = "Праща";

            when(req.getPathInfo()).thenReturn("/");
            when(req.getParameter("prodName")).thenReturn(prodName);
            when(req.getParameterMap()).thenReturn(Map.of());

            when(productController.findProductByName(prodName)).thenReturn(null);

            productManageServlet.doGet(req, resp);

            assertThat(jsonMessageResponse)
                    .contains("Product with the name " + prodName + " not found");

            verify(resp, times(1)).setContentType("application/json");
            verify(resp, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
            verify(resp, times(1)).getOutputStream();
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.handleSingleProduct() метод ProductManageServlet")
    class HandleSingleProductMethodOnProductManageServletTests {
        @SneakyThrows
        @Test
        void doGet_viewProductById_Test() {
            Long productId = 13L;

            when(req.getPathInfo()).thenReturn("/13");
            when(req.getParameter("prodName")).thenReturn(null);
            when(req.getParameterMap()).thenReturn(Map.of());

            when(productController.findProductById(productId)).thenReturn(productReadDto);

            productManageServlet.doGet(req, resp);

            assertThat(jsonDtoResponse).contains(objectWriter.writeValueAsString(productReadDto));

            verify(resp, times(1)).setContentType("application/json");
            verify(resp, times(1)).setStatus(HttpServletResponse.SC_OK);
            verify(resp, times(1)).getOutputStream();
        }

        @SneakyThrows
        @Test
        void doGet_viewProductById_shouldReturnNotFoundMessage_Test() {
            Long productId = 13L;

            when(req.getPathInfo()).thenReturn("/13");
            when(req.getParameter("prodName")).thenReturn(null);
            when(req.getParameterMap()).thenReturn(Map.of());

            when(productController.findProductById(productId)).thenReturn(null);

            productManageServlet.doGet(req, resp);

            assertThat(jsonMessageResponse)
                    .contains("Product with ID " + productId + " not found");

            verify(resp, times(1)).setContentType("application/json");
            verify(resp, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
            verify(resp, times(1)).getOutputStream();
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.handleProductsByCategory() метод ProductManageServlet")
    class HandleProductsByCategoryMethodOnProductManageServletTests {
        @SneakyThrows
        @Test
        void doGet_viewAllProductsByCategoryId_Test() {
            Integer categoryId = 1;

            when(req.getPathInfo()).thenReturn("/categories/1");
            when(req.getParameter("prodName")).thenReturn(null);
            when(req.getParameterMap()).thenReturn(Map.of());

            when(productController.findProductsByCategory(categoryId)).thenReturn(categoryProdList);

            productManageServlet.doGet(req, resp);

            assertThat(jsonListResponse)
                    .contains(objectWriter.writeValueAsString(categoryProdList));

            verify(resp, times(1)).setContentType("application/json");
            verify(resp, times(1)).setStatus(HttpServletResponse.SC_OK);
            verify(resp, times(1)).getOutputStream();
        }

        @SneakyThrows
        @Test
        void doGet_viewAllProductsByCategoryId_categoryNotFoundMessage_Test() {
            Integer categoryId = 1;

            when(req.getPathInfo()).thenReturn("/categories/1");
            when(req.getParameter("prodName")).thenReturn(null);
            when(req.getParameterMap()).thenReturn(Map.of());

            when(productController.findProductsByCategory(categoryId)).thenReturn(List.of());

            productManageServlet.doGet(req, resp);

            assertThat(jsonMessageResponse)
                    .contains("Category with ID " + categoryId + " not found, or there are no products in this category in the database");

            verify(resp, times(1)).setContentType("application/json");
            verify(resp, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
            verify(resp, times(1)).getOutputStream();
        }
    }

    @Nested
    @DisplayName("Блок тестов на *.handleProductsByBrand() метод ProductManageServlet")
    class HandleProductsByBrandMethodOnProductManageServletTests {
        @SneakyThrows
        @Test
        void doGet_viewAllProductsByBrandId_Test() {
            Integer brandId = 1;

            when(req.getPathInfo()).thenReturn("/brands/1");
            when(req.getParameter("prodName")).thenReturn(null);
            when(req.getParameterMap()).thenReturn(Map.of());

            when(productController.findProductsByBrand(brandId)).thenReturn(brandProdList);

            productManageServlet.doGet(req, resp);

            assertThat(jsonListResponse)
                    .contains(objectWriter.writeValueAsString(brandProdList));

            verify(resp, times(1)).setContentType("application/json");
            verify(resp, times(1)).setStatus(HttpServletResponse.SC_OK);
            verify(resp, times(1)).getOutputStream();
        }

        @SneakyThrows
        @Test
        void doGet_viewAllProductsByBrandId_brandNotFoundMessage_Test() {
            Integer brandId = 1;

            when(req.getPathInfo()).thenReturn("/brands/1");
            when(req.getParameter("prodName")).thenReturn(null);
            when(req.getParameterMap()).thenReturn(Map.of());

            when(productController.findProductsByBrand(brandId)).thenReturn(List.of());

            productManageServlet.doGet(req, resp);

            assertThat(jsonMessageResponse)
                    .contains("Brand with ID " + brandId + " not found, or there are no products of this brand in the database");

            verify(resp, times(1)).setContentType("application/json");
            verify(resp, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
            verify(resp, times(1)).getOutputStream();
        }
    }

    @Nested
    @DisplayName("Блок тестов на ошибки пути (естественно не полный, а то, что 'бросается в глаза' сразу)")
    class WrongPathDoGetMethodReactionOnProductManageServletTests {
        @SneakyThrows
        @Test
        void doGet_unknownRequestPathMessage_Test() {
            when(req.getPathInfo()).thenReturn(null);
            when(req.getParameter("prodName")).thenReturn(null);
            when(req.getParameterMap()).thenReturn(Map.of("b", new String[2]));

            productManageServlet.doGet(req, resp);

            assertThat(jsonMessageResponse).contains("Unknown request path");

            verify(resp, times(1)).setContentType("application/json");
            verify(resp, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
            verify(resp, times(1)).getOutputStream();
        }

        @SneakyThrows
        @Test
        void doGet_wrongProductRequestId_notNumericData_Test() {
            String shouldBeNumericButNot = "b";

            when(req.getPathInfo()).thenReturn("/" + shouldBeNumericButNot);
            when(req.getParameter("prodName")).thenReturn(null);
            when(req.getParameterMap()).thenReturn(Map.of());

            productManageServlet.doGet(req, resp);

            assertThat(jsonMessageResponse).contains("Invalid product ID format: " + shouldBeNumericButNot);

            verify(resp, times(1)).setContentType("application/json");
            verify(resp, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
            verify(resp, times(1)).getOutputStream();
        }

        @SneakyThrows
        @Test
        void doGet_wrongCategoryRequestIdFormat_notNumericData_Test() {
            String shouldBeNumericButNot = "b";

            when(req.getPathInfo()).thenReturn("/categories/" + shouldBeNumericButNot);
            when(req.getParameter("prodName")).thenReturn(null);
            when(req.getParameterMap()).thenReturn(Map.of());

            productManageServlet.doGet(req, resp);

            assertThat(jsonMessageResponse).contains("Invalid ID format for category/brand: " + shouldBeNumericButNot);

            verify(resp, times(1)).setContentType("application/json");
            verify(resp, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
            verify(resp, times(1)).getOutputStream();
        }

        @SneakyThrows
        @Test
        void doGet_wrongPathRequestFormat_Test() {
            String partOfPath = "/subcategory/";
            String shouldBeNumericButNot = "5";

            when(req.getPathInfo()).thenReturn(partOfPath + shouldBeNumericButNot);
            when(req.getParameter("prodName")).thenReturn(null);
            when(req.getParameterMap()).thenReturn(Map.of());

            productManageServlet.doGet(req, resp);

            assertThat(jsonMessageResponse).contains("Unknown path: " + partOfPath + shouldBeNumericButNot);

            verify(resp, times(1)).setContentType("application/json");
            verify(resp, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
            verify(resp, times(1)).getOutputStream();
        }

        @SneakyThrows
        @Test
        void doGet_unexpectedPathRequestFormat_Test() {
            String partOfPath = "/categories/subcategory/5";

            when(req.getPathInfo()).thenReturn(partOfPath);
            when(req.getParameter("prodName")).thenReturn(null);
            when(req.getParameterMap()).thenReturn(Map.of());

            productManageServlet.doGet(req, resp);

            assertThat(jsonMessageResponse).contains("Unsupported path structure: " + partOfPath);

            verify(resp, times(1)).setContentType("application/json");
            verify(resp, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
            verify(resp, times(1)).getOutputStream();
        }
    }
}