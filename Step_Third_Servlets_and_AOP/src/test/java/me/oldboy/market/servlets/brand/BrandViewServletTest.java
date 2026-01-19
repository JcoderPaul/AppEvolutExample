package me.oldboy.market.servlets.brand;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import me.oldboy.market.controllers.BrandController;
import me.oldboy.market.dto.JsonFormResponse;
import me.oldboy.market.dto.brand.BrandReadDto;
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
class BrandViewServletTest {
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;
    @Mock
    private BrandController brandController;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private BrandViewServlet brandViewServlet;
    private MockServletOutputStream mockOutputStream;
    private BrandReadDto brandReadDto, brandReadDtoTwo, brandReadDtoThree;
    private ObjectWriter objectWriter;

    private String jsonDtoResponse, jsonListResponse, jsonMessageResponse;
    private List<BrandReadDto> brandReadDtoList;

    @BeforeEach
    void setUp() throws Exception {
        brandReadDto = new BrandReadDto(1, "Ракета");
        brandReadDtoTwo = new BrandReadDto(2, "ИЖ");
        brandReadDtoThree = new BrandReadDto(3, "Полюс");

        brandReadDtoList = List.of(brandReadDto, brandReadDtoTwo, brandReadDtoThree);

        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mockOutputStream = new MockServletOutputStream(outputStream);

        when(resp.getOutputStream()).thenReturn(mockOutputStream);
        doAnswer(invocation -> {
            OutputStream outputStreamFromDoPostMethod = invocation.getArgument(0);
            if (invocation.getArgument(1) instanceof BrandReadDto) {
                BrandReadDto brandReadDtoToOutput = invocation.getArgument(1);
                jsonDtoResponse = objectWriter.writeValueAsString(brandReadDtoToOutput);
                outputStreamFromDoPostMethod.write(jsonDtoResponse.getBytes(StandardCharsets.UTF_8));
            } else if (invocation.getArgument(1) instanceof List) {
                List<BrandReadDto> listToPrint = invocation.getArgument(1);
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
    void doGet_shouldViewAllBrand_Test() {
        when(req.getPathInfo()).thenReturn("/");
        when(req.getParameterMap()).thenReturn(Map.of());
        when(brandController.findAllBrands()).thenReturn(brandReadDtoList);

        brandViewServlet.doGet(req, resp);

        assertThat(jsonListResponse).contains(objectWriter.writeValueAsString(brandReadDtoList));

        verify(resp, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(resp, times(1)).getOutputStream();
    }

    @SneakyThrows
    @Test
    void doGet_shouldReturnMessage_wrongPath_Test() {
        when(req.getPathInfo()).thenReturn("/");
        when(req.getParameterMap()).thenReturn(Map.of("sub", new String[1]));

        brandViewServlet.doGet(req, resp);

        assertThat(jsonMessageResponse).contains("Неизвестный путь запроса");

        verify(resp, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(resp, times(1)).getOutputStream();
    }

    @SneakyThrows
    @Test
    void doGet_shouldViewCategoryById_Test() {
        Integer brandId = 1;

        when(req.getPathInfo()).thenReturn("/" + brandId);
        when(req.getParameterMap()).thenReturn(Map.of());

        when(brandController.findBrandById(brandId)).thenReturn(brandReadDto);

        brandViewServlet.doGet(req, resp);

        assertThat(jsonDtoResponse).contains(objectWriter.writeValueAsString(brandReadDto));

        verify(resp, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(resp, times(1)).getOutputStream();
    }

    @SneakyThrows
    @Test
    void doGet_shouldReturnMessage_categoryNotFoundById_Test() {
        Integer brandId = 1;

        when(req.getPathInfo()).thenReturn("/" + brandId);
        when(req.getParameterMap()).thenReturn(Map.of());

        when(brandController.findBrandById(brandId)).thenReturn(null);

        brandViewServlet.doGet(req, resp);

        assertThat(jsonMessageResponse).contains("Брэнд с ID " + brandId + " не найден");

        verify(resp, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(resp, times(1)).getOutputStream();
    }

    @SneakyThrows
    @Test
    void doGet_shouldReturnMessage_wrongCategoryIdFormat_Test() {
        String notCorrectBrandId = "one";

        when(req.getPathInfo()).thenReturn("/" + notCorrectBrandId);
        when(req.getParameterMap()).thenReturn(Map.of());

        brandViewServlet.doGet(req, resp);

        assertThat(jsonMessageResponse).contains("Неверный формат ID брэнда: " + notCorrectBrandId);

        verify(resp, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(resp, times(1)).getOutputStream();
    }

    @SneakyThrows
    @Test
    void doGet_shouldReturnMessage_unexpectedPath_Test() {
        String notCorrectPath = "/subcategory/cheeses/4";

        when(req.getPathInfo()).thenReturn(notCorrectPath);
        when(req.getParameterMap()).thenReturn(Map.of());

        brandViewServlet.doGet(req, resp);

        assertThat(jsonMessageResponse).contains("Неподдерживаемая структура пути: " + notCorrectPath);

        verify(resp, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(resp, times(1)).getOutputStream();
    }
}