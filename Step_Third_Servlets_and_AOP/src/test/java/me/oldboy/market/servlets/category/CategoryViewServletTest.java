package me.oldboy.market.servlets.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import me.oldboy.market.controllers.CategoryController;
import me.oldboy.market.dto.JsonFormResponse;
import me.oldboy.market.dto.category.CategoryReadDto;
import me.oldboy.market.servlets.MockServletOutputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryViewServletTest {
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;
    @Mock
    private CategoryController categoryController;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private CategoryViewServlet categoryViewServlet;
    private MockServletOutputStream mockOutputStream;
    private CategoryReadDto categoryReadDto, categoryReadDtoTwo, categoryReadDtoThree;
    private ObjectWriter objectWriter;

    private String jsonDtoResponse, jsonListResponse, jsonMessageResponse;
    private List<CategoryReadDto> categoryReadDtoList;

    @BeforeEach
    void setUp() throws Exception {
        categoryReadDto = new CategoryReadDto(1, "Наживка");
        categoryReadDtoTwo = new CategoryReadDto(2, "Блесна");
        categoryReadDtoThree = new CategoryReadDto(3, "Мангалы");

        categoryReadDtoList = List.of(categoryReadDto, categoryReadDtoTwo, categoryReadDtoThree);

        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mockOutputStream = new MockServletOutputStream(outputStream);

        when(resp.getOutputStream()).thenReturn(mockOutputStream);
        doAnswer(invocation -> {
            OutputStream outputStreamFromDoPostMethod = invocation.getArgument(0);
            if (invocation.getArgument(1) instanceof CategoryReadDto) {
                CategoryReadDto categoryReadDtoToOutput = invocation.getArgument(1);
                jsonDtoResponse = objectWriter.writeValueAsString(categoryReadDtoToOutput);
                outputStreamFromDoPostMethod.write(jsonDtoResponse.getBytes(StandardCharsets.UTF_8));
            } else if (invocation.getArgument(1) instanceof List) {
                List<CategoryReadDto> listToPrint = invocation.getArgument(1);
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
    void doGet_shouldViewAllCategory_Test() {
        when(req.getPathInfo()).thenReturn("/");
        when(req.getParameterMap()).thenReturn(Map.of());
        when(categoryController.findAllCategories()).thenReturn(categoryReadDtoList);

        categoryViewServlet.doGet(req, resp);

        assertThat(jsonListResponse).contains(objectWriter.writeValueAsString(categoryReadDtoList));

        verify(resp, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(resp, times(1)).getOutputStream();
    }

    @SneakyThrows
    @Test
    void doGet_shouldReturnMessage_wrongPath_Test() {
        when(req.getPathInfo()).thenReturn("/");
        when(req.getParameterMap()).thenReturn(Map.of("sub", new String[1]));

        categoryViewServlet.doGet(req, resp);

        assertThat(jsonMessageResponse).contains("Неизвестный путь запроса");

        verify(resp, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(resp, times(1)).getOutputStream();
    }

    @SneakyThrows
    @Test
    void doGet_shouldViewCategoryById_Test() {
        Integer categoryId = 1;

        when(req.getPathInfo()).thenReturn("/" + categoryId);
        when(req.getParameterMap()).thenReturn(Map.of());

        when(categoryController.findCategoryById(categoryId)).thenReturn(categoryReadDto);

        categoryViewServlet.doGet(req, resp);

        assertThat(jsonDtoResponse).contains(objectWriter.writeValueAsString(categoryReadDto));

        verify(resp, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(resp, times(1)).getOutputStream();
    }

    @SneakyThrows
    @Test
    void doGet_shouldReturnMessage_categoryNotFoundById_Test() {
        Integer categoryId = 1;

        when(req.getPathInfo()).thenReturn("/" + categoryId);
        when(req.getParameterMap()).thenReturn(Map.of());

        when(categoryController.findCategoryById(categoryId)).thenReturn(null);

        categoryViewServlet.doGet(req, resp);

        assertThat(jsonMessageResponse).contains("Категория с ID " + categoryId + " не найдена");

        verify(resp, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(resp, times(1)).getOutputStream();
    }

    @SneakyThrows
    @Test
    void doGet_shouldReturnMessage_wrongCategoryIdFormat_Test() {
        String notCorrectCategoryId = "one";

        when(req.getPathInfo()).thenReturn("/" + notCorrectCategoryId);
        when(req.getParameterMap()).thenReturn(Map.of());

        categoryViewServlet.doGet(req, resp);

        assertThat(jsonMessageResponse).contains("Неверный формат ID категории: " + notCorrectCategoryId);

        verify(resp, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(resp, times(1)).getOutputStream();
    }

    @SneakyThrows
    @Test
    void doGet_shouldReturnMessage_unexpectedPath_Test() {
        String notCorrectPath = "/subcategory/cheeses/4";

        when(req.getPathInfo()).thenReturn(notCorrectPath);
        when(req.getParameterMap()).thenReturn(Map.of());

        categoryViewServlet.doGet(req, resp);

        assertThat(jsonMessageResponse).contains("Неподдерживаемая структура пути: " + notCorrectPath);

        verify(resp, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(resp, times(1)).getOutputStream();
    }
}