package me.oldboy.market.controllers;

import me.oldboy.market.dto.audit.AuditReadDto;
import me.oldboy.market.entity.Audit;
import me.oldboy.market.entity.enums.Action;
import me.oldboy.market.entity.enums.Status;
import me.oldboy.market.services.AuditServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditControllerTest {
    @Mock
    private AuditServiceImpl auditService;
    @InjectMocks
    private AuditController auditController;

    private Audit testAudit, anotherTestAudit;
    private AuditReadDto testAuditReadDto;
    private List<Audit> testAuditList;
    private Long existId, anotherId, nonExistId;
    private String creatorEmail, creatorEmailTwo, nonExistEmail, productStringView, productStringViewTwo;

    @BeforeEach
    void setUp() {
        existId = 1L;
        anotherId = 2L;
        nonExistId = 100L;

        creatorEmail = "malcolm@swordwing.de";
        creatorEmailTwo = "sanara@swordwing.de";
        nonExistEmail = "zloy@nasorog.com";

        productStringView = "Product{id=1, name='SuperDuper'}";
        productStringViewTwo = "Product{id=2, name='SimpleDuper'}";

        testAudit = Audit.builder()
                .id(existId)
                .createAt(LocalDateTime.of(2023, 10, 1, 12, 0))
                .createBy(creatorEmail)
                .action(Action.ADD_PRODUCT)
                .isSuccess(Status.SUCCESS)
                .auditableRecord(productStringView)
                .build();

        testAuditReadDto = new AuditReadDto(
                existId,
                LocalDateTime.of(2023, 10, 1, 12, 0),
                creatorEmail,
                Action.ADD_PRODUCT,
                Status.SUCCESS,
                productStringView
        );

        anotherTestAudit = Audit.builder()
                .id(anotherId)
                .createAt(LocalDateTime.of(2023, 10, 2, 14, 30))
                .createBy(creatorEmailTwo)
                .action(Action.UPDATE_PRODUCT)
                .isSuccess(Status.SUCCESS)
                .auditableRecord(productStringViewTwo)
                .build();

        testAuditList = Arrays.asList(testAudit, anotherTestAudit);
    }

    @Nested
    @DisplayName("Блок тестов на *.findAuditRecordById()")
    class FindAuditRecordMethodTests {

        @Test
        void findAuditRecordById_ShouldReturnAuditRecord_withExistingId_Test() {
            when(auditService.findById(existId)).thenReturn(testAudit);

            AuditReadDto result = auditController.findAuditRecordById(existId);

            assertThat(result).isNotNull();

            assertThat(existId).isEqualTo(result.id());
            assertThat(creatorEmail).isEqualTo(result.createBy());
            assertThat(Action.ADD_PRODUCT).isEqualTo(result.action());
            assertThat(Status.SUCCESS).isEqualTo(result.isSuccess());

            verify(auditService, times(1)).findById(existId);
        }

        @Test
        void findAuditRecordById_shouldReturnNull_withNonExistingId() {
            when(auditService.findById(nonExistId)).thenReturn(null);

            AuditReadDto result = auditController.findAuditRecordById(nonExistId);

            assertThat(result).isNull();
            verify(auditService, times(1)).findById(nonExistId);
        }
    }

    @Test
    void findAllAuditRecords_shouldReturnAllRecords_whenRecordsExist_Test() {
        when(auditService.findAll()).thenReturn(testAuditList);

        List<AuditReadDto> result = auditController.findAllAuditRecords();

        assertThat(result).isNotNull();
        assertThat(2).isEqualTo(result.size());

        assertThat(existId).isEqualTo(result.get(0).id());
        assertThat(creatorEmail).isEqualTo(result.get(0).createBy());
        assertThat(Action.ADD_PRODUCT).isEqualTo(result.get(0).action());

        assertThat(anotherId).isEqualTo(result.get(1).id());
        assertThat(creatorEmailTwo).isEqualTo(result.get(1).createBy());
        assertThat(Action.UPDATE_PRODUCT).isEqualTo(result.get(1).action());

        verify(auditService).findAll();
    }

    @Nested
    @DisplayName("Блок тестов на *.findAllAuditRecordsByUserEmail()")
    class FindAllAuditRecordsByUserEmailTests {

        @Test
        void findAllAuditRecordsByUserEmail_shouldReturnFilteredRecords_withExistingEmail_Test() {
            when(auditService.findAll()).thenReturn(testAuditList);

            List<AuditReadDto> result = auditController.findAllAuditRecordsByUserEmail(creatorEmail);

            assertThat(result).isNotNull();
            assertThat(1).isEqualTo(result.size());
            assertThat(creatorEmail).isEqualTo(result.get(0).createBy());
            assertThat(existId).isEqualTo(result.get(0).id());

            verify(auditService).findAll();
        }

        @Test
        void findAllAuditRecordsByUserEmail_shouldReturnEmptyList_withNonExistingEmail_Test() {
            when(auditService.findAll()).thenReturn(testAuditList);

            List<AuditReadDto> result = auditController.findAllAuditRecordsByUserEmail(nonExistEmail);

            assertThat(result).isNotNull();
            assertThat(result.size()).isEqualTo(0);

            verify(auditService).findAll();
        }

        @Test
        void findAllAuditRecordsByUserEmail_shouldReturnEmptyList_whenNoAuditRecords_Test() {
            when(auditService.findAll()).thenReturn(Collections.emptyList());

            List<AuditReadDto> result = auditController.findAllAuditRecordsByUserEmail(creatorEmail);

            assertThat(result).isNotNull();
            assertThat(result.size()).isEqualTo(0);

            verify(auditService).findAll();
        }
    }
}