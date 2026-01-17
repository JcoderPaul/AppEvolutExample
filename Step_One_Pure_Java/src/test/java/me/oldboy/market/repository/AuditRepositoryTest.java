package me.oldboy.market.repository;

import me.oldboy.market.cache_bd.AuditDB;
import me.oldboy.market.entity.Audit;
import me.oldboy.market.entity.log_enum.Action;
import me.oldboy.market.entity.log_enum.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AuditRepositoryTest {
    private AuditDB auditDB;
    private AuditRepository auditRepository;
    private Audit rec_1, rec_2;

    @BeforeEach
    void setUp(){
        auditDB = AuditDB.getINSTANCE();
        auditRepository = new AuditRepository(auditDB);

        rec_1 = Audit.builder()
                .timestamp(new Date().getTime())
                .userEmail("sanara@market.ru")
                .action(Action.ADD_PRODUCT)
                .isSuccess(Status.SUCCESS)
                .build();

        rec_2 = Audit.builder()
                .timestamp(new Date().getTime())
                .userEmail("duglas@market.ru")
                .action(Action.UPDATE_PRODUCT)
                .isSuccess(Status.FAIL)
                .build();

        auditDB.add(rec_1);
        auditDB.add(rec_2);
    }

    @AfterEach
    void cleanBase(){
        auditDB.getAuditLogList().clear();
    }

    @Test
    void save_shouldReturnSavedAuditRecord_Test() {
        assertThat(auditDB.getAuditLogList().size()).isEqualTo(2);
        Audit rec_3 = Audit.builder()
                .timestamp(new Date().getTime())
                .userEmail("malcolm@market.ru")
                .action(Action.UPDATE_PRODUCT)
                .isSuccess(Status.SUCCESS)
                .build();

        Audit newRec = auditRepository.save(rec_3);

        assertThat(newRec.getId()).isEqualTo(3L);
        assertThat(auditDB.getAuditLogList().size()).isEqualTo(3);
    }

    @Test
    void findAll_shouldReturnAllAuditList_Test() {
        assertThat(auditRepository.findAll()).isNotEmpty();
        assertThat(auditRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    void findById_shouldReturnFoundAuditRecord_Test() {
        Optional<Audit> mayBeFoundAud_1 = auditRepository.findById(1L);
        Optional<Audit> mayBeFoundAud_2 = auditRepository.findById(2L);

        assertThat(mayBeFoundAud_1).isNotEmpty();
        assertThat(mayBeFoundAud_2).isNotEmpty();

        assertThat(mayBeFoundAud_1).contains(rec_1);
        assertThat(mayBeFoundAud_2).contains(rec_2);
    }

    @Test
    void findById_shouldReturnOptionalEmpty_ifAuditRecordNotExist_Test() {
        Optional<Audit> notFoundAudit= auditRepository.findById(100L);
        assertThat(notFoundAudit).isEmpty();
    }
}