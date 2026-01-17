package me.oldboy.market.cache_bd;

import me.oldboy.market.entity.Audit;
import me.oldboy.market.entity.log_enum.Action;
import me.oldboy.market.entity.log_enum.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class AuditDBTest {
    private AuditDB auditDB;
    private Audit audRec_1, audRec_2, audRec_3;

    @BeforeEach
    void setUp(){
        auditDB = AuditDB.getINSTANCE();

        audRec_1 = Audit.builder()
                .timestamp(new Date().getTime())
                .userEmail("u1@marcet.ru")
                .action(Action.ADD_PRODUCT)
                .isSuccess(Status.SUCCESS)
                .build();
        audRec_2 = Audit.builder()
                .timestamp(new Date().getTime())
                .userEmail("u2@marcet.ru")
                .action(Action.LOGIN)
                .isSuccess(Status.SUCCESS)
                .build();
        audRec_3 = Audit.builder()
                .timestamp(new Date().getTime())
                .userEmail("u1@marcet.ru")
                .action(Action.LOGOUT)
                .isSuccess(Status.SUCCESS)
                .build();
    }

    @AfterEach
    void cleanBase(){
        auditDB.getAuditLogList().clear();
    }


    @Test
    void add_shouldReturnGeneratedId_Test() {
        assertThat(auditDB.add(audRec_1)).isEqualTo(1L);
        assertThat(auditDB.add(audRec_2)).isEqualTo(2L);
        assertThat(auditDB.add(audRec_3)).isEqualTo(3L);
    }
}