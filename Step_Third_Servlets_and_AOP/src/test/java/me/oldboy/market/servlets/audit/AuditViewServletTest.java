package me.oldboy.market.servlets.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import me.oldboy.market.controllers.AuditController;
import me.oldboy.market.dto.JsonFormResponse;
import me.oldboy.market.dto.audit.AuditReadDto;
import me.oldboy.market.dto.brand.BrandReadDto;
import me.oldboy.market.entity.enums.Action;
import me.oldboy.market.entity.enums.Status;
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditViewServletTest {
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;
    @Mock
    private AuditController auditController;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private AuditViewServlet auditViewServlet;
    private MockServletOutputStream mockOutputStream;
    private AuditReadDto auditReadDto, auditReadDtoTwo, auditReadDtoThree;
    private ObjectWriter objectWriter;
    private String jsonDtoResponse, jsonListResponse, jsonMessageResponse, userMail;
    private List<AuditReadDto> auditReadDtoList;

    @BeforeEach
    void setUp() throws Exception {
        userMail = "admin@market.ru";
        auditReadDto = AuditReadDto.builder()
                .id(1L)
                .action(Action.ADD_PRODUCT)
                .auditableRecord("Prod_1")
                .isSuccess(Status.SUCCESS)
                .createAt(LocalDateTime.now())
                .createBy(userMail)
                .build();
        auditReadDtoTwo = AuditReadDto.builder()
                .id(2L)
                .action(Action.ADD_PRODUCT)
                .auditableRecord("Prod_2")
                .isSuccess(Status.FAIL)
                .createAt(LocalDateTime.now().plusMonths(1))
                .createBy(userMail)
                .build();
        auditReadDtoThree = AuditReadDto.builder()
                .id(3L)
                .action(Action.UPDATE_PRODUCT)
                .auditableRecord("Prod_3")
                .isSuccess(Status.SUCCESS)
                .createAt(LocalDateTime.now().minusMonths(1))
                .createBy(userMail)
                .build();

        auditReadDtoList = new ArrayList<>();
        auditReadDtoList.add(auditReadDto);
        auditReadDtoList.add(auditReadDtoTwo);
        auditReadDtoList.add(auditReadDtoThree);

        ObjectMapper objectMapperForOutput = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        objectMapperForOutput.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        objectMapperForOutput.registerModule(module);

        objectWriter = objectMapperForOutput.writer().withDefaultPrettyPrinter();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mockOutputStream = new MockServletOutputStream(outputStream);

        when(resp.getOutputStream()).thenReturn(mockOutputStream);
        doAnswer(invocation -> {
            OutputStream outputStreamFromDoPostMethod = invocation.getArgument(0);
            if (invocation.getArgument(1) instanceof AuditReadDto) {
                AuditReadDto auditReadDtoToOutput = invocation.getArgument(1);
                jsonDtoResponse = objectWriter.writeValueAsString(auditReadDtoToOutput);
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
    void doGet_shouldViewAllAuditRecord_Test() {
        when(req.getPathInfo()).thenReturn("/");
        when(req.getParameterMap()).thenReturn(Map.of());
        when(auditController.findAllAuditRecords()).thenReturn(auditReadDtoList);

        auditViewServlet.doGet(req, resp);

        assertThat(jsonListResponse).contains(objectWriter.writeValueAsString(auditReadDtoList));

        verify(resp, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(resp, times(1)).getOutputStream();
    }

    @SneakyThrows
    @Test
    void doGet_shouldReturnMessage_wrongPath_Test() {
        when(req.getPathInfo()).thenReturn("/");
        when(req.getParameterMap()).thenReturn(Map.of("sub", new String[1]));

        auditViewServlet.doGet(req, resp);

        assertThat(jsonMessageResponse).contains("Неизвестный путь запроса");

        verify(resp, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(resp, times(1)).getOutputStream();
    }

    @SneakyThrows
    @Test
    void doGet_shouldViewAuditRecordById_Test() {
        Long auditRecordId = 1L;

        when(req.getPathInfo()).thenReturn("/" + auditRecordId);
        when(req.getParameterMap()).thenReturn(Map.of());

        when(auditController.findAuditRecordById(auditRecordId)).thenReturn(auditReadDto);

        auditViewServlet.doGet(req, resp);

        assertThat(jsonDtoResponse).contains(objectWriter.writeValueAsString(auditReadDto));

        verify(resp, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(resp, times(1)).getOutputStream();
    }

    @SneakyThrows
    @Test
    void doGet_shouldReturnMessage_auditRecordNotFoundById_Test() {
        Long auditRecordId = 1L;

        when(req.getPathInfo()).thenReturn("/" + auditRecordId);
        when(req.getParameterMap()).thenReturn(Map.of());

        when(auditController.findAuditRecordById(auditRecordId)).thenReturn(null);

        auditViewServlet.doGet(req, resp);

        assertThat(jsonMessageResponse).contains("Аудит запись с ID " + auditRecordId + " не найдена");

        verify(resp, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(resp, times(1)).getOutputStream();
    }

    @SneakyThrows
    @Test
    void doGet_shouldReturnMessage_wrongAuditRecordIdFormat_Test() {
        String notCorrectBrandId = "one";

        when(req.getPathInfo()).thenReturn("/" + notCorrectBrandId);
        when(req.getParameterMap()).thenReturn(Map.of());

        auditViewServlet.doGet(req, resp);

        assertThat(jsonMessageResponse).contains("Неверный формат ID аудит записи: " + notCorrectBrandId);

        verify(resp, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(resp, times(1)).getOutputStream();
    }

    @SneakyThrows
    @Test
    void doGet_shouldReturnMessage_unexpectedPath_Test() {
        String notCorrectPath = "/audit-sub-record/failed/category";

        when(req.getPathInfo()).thenReturn(notCorrectPath);
        when(req.getParameterMap()).thenReturn(Map.of());

        auditViewServlet.doGet(req, resp);

        assertThat(jsonMessageResponse).contains("Неподдерживаемая структура пути: " + notCorrectPath);

        verify(resp, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(resp, times(1)).getOutputStream();
    }

    @SneakyThrows
    @Test
    void doGet_viewAuditByEmail_Test() {
        when(req.getPathInfo()).thenReturn("/");
        when(req.getParameter("userEmail")).thenReturn(userMail);
        when(req.getParameterMap()).thenReturn(Map.of("userEmail", new String[]{userMail}));
        when(auditController.findAllAuditRecordsByUserEmail(userMail)).thenReturn(auditReadDtoList);

        auditViewServlet.doGet(req, resp);

        assertThat(jsonListResponse).contains(objectWriter.writeValueAsString(auditReadDtoList));

        verify(resp, times(1)).setContentType("application/json");
        verify(resp, times(1)).setStatus(HttpServletResponse.SC_OK);
        verify(resp, times(1)).getOutputStream();
    }

    @SneakyThrows
    @Test
    void doGet_viewAuditByEmail_shouldReturnMessage_emailNotFound_Test() {
        when(req.getPathInfo()).thenReturn("/");
        when(req.getParameter("userEmail")).thenReturn(userMail);
        when(req.getParameterMap()).thenReturn(Map.of("userEmail", new String[]{userMail}));
        when(auditController.findAllAuditRecordsByUserEmail(userMail)).thenReturn(null);

        auditViewServlet.doGet(req, resp);

        assertThat(jsonMessageResponse).contains("Записей для email " + userMail + " не найдено");

        verify(resp, times(1)).setContentType("application/json");
        verify(resp, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(resp, times(1)).getOutputStream();
    }
}