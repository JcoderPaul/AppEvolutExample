package me.oldboy.market.services;

import me.oldboy.market.cache_bd.AuditDB;
import me.oldboy.market.entity.Audit;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.log_enum.Action;
import me.oldboy.market.entity.log_enum.Status;
import me.oldboy.market.repository.AuditRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class AuditServiceTest {

    private AuditDB auditDB;
    private AuditRepository auditRepository;
    private AuditService auditService;
    private Audit rec_1, rec_2;

    @BeforeEach
    void setUp(){
        auditDB = AuditDB.getINSTANCE();
        auditRepository = new AuditRepository(auditDB);
        auditService = new AuditService(auditRepository);

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
    void saveAuditRecord_shouldReturnTrueAfterSave_Test() {
        assertThat(auditDB.getAuditLogList().size()).isEqualTo(2);

        String email = "admin@market.ru";
        Action newAction = Action.ADD_PRODUCT;
        Status status = Status.FAIL;
        Product updatedProduct = Product.builder()
                .name("Wow prod")
                .build();

        assertThat(auditService.saveAuditRecord(newAction, status, email, updatedProduct)).isTrue();
        assertThat(auditDB.getAuditLogList().size()).isEqualTo(3);
    }
}