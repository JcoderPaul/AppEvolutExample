package me.oldboy.market.controlers.view;

import me.oldboy.market.entity.Audit;
import me.oldboy.market.entity.enums.Action;
import me.oldboy.market.entity.enums.Status;
import me.oldboy.market.repository.AuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViewAuditRecordControllerTest {
    @Mock
    private AuditRepository auditRepository;
    @InjectMocks
    private ViewAuditRecordController viewAuditRecordController;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private List<Audit> recordsList;

    @BeforeEach
    void setUp() {
        viewAuditRecordController = new ViewAuditRecordController(auditRepository);

        recordsList = Arrays.asList(
                createTestAudit(1L, "admin@market.ru", Action.LOGIN, Status.SUCCESS),
                createTestAudit(2L, "user@market.ru", Action.ADD_PRODUCT, Status.SUCCESS),
                createTestAudit(3L, "manager@market.ru", Action.LOGOUT, Status.SUCCESS)
        );
    }

    @Test
    void printAllAuditRecord_shouldPrintAuditListToScreen_Test() {
        when(auditRepository.findAll()).thenReturn(recordsList);

        /* Нам нужно поймать и перенаправить исходящий поток, предварительно запомнив откуда мы его "сдернули" */
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        /* Вызываем тестируемый метод */
        List<Audit> result = viewAuditRecordController.printAllAuditRecord();

        /* Классический набор проверки утверждений */
        assertThat(result).isNotNull();
        assertThat(3).isEqualTo(result.size());

        verify(auditRepository, times(1)).findAll();

        /* Проверяем, что прилетело из перенаправленного потока - там должен быть ожидаемый минимум данных */
        String output = outputStream.toString();
        assertTrue(output.contains("---------------------------------------------------------------------"));
        assertTrue(output.contains("Test record 1"));
        assertTrue(output.contains("Test record 2"));
        assertTrue(output.contains("Test record 3"));

        /* Самое важно не забываем восстановить системные настройки потока */
        System.setOut(originalOut);
    }

    private Audit createTestAudit(Long id, String userEmail, Action action, Status status) {
        return Audit.builder()
                .id(id)
                .createAt(LocalDateTime.now())
                .createBy(userEmail)
                .action(action)
                .isSuccess(status)
                .auditableRecord("Test record " + id)
                .build();
    }
}