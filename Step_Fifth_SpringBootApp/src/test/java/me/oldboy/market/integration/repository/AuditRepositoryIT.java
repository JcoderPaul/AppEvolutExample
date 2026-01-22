package me.oldboy.market.integration.repository;

import me.oldboy.market.auditor.core.entity.Audit;
import me.oldboy.market.auditor.core.repository.AuditRepository;
import me.oldboy.market.integration.TestContainerInit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AuditRepositoryIT extends TestContainerInit {

    @Autowired
    private AuditRepository auditRepository;
    private Long existId, notExistingId;
    private String existName, notExistingName;

    @BeforeEach
    void setUp() {
        existId = 1L;
        notExistingId = 200L;

        existName = "admin@admin.ru";
        notExistingName = "sanara@swordwing.de";
    }

    @Test
    @DisplayName("Должен вернуть найденную по ID аудит-запись и подтвердить это - запись найдена")
    void findById_shouldReturnTrue_forExistingAuditRecord_Test() {
        Optional<Audit> mayBeExistRecord = auditRepository.findById(existId);
        mayBeExistRecord.ifPresent(audit -> assertThat(audit).isNotNull());
    }

    @Test
    @DisplayName("Должен вернуть false для несуществующего ID аудит-записи - запись не найдена")
    void findById_shouldReturnFalse_forNonExistingAuditRecord_Test() {
        assertThat(auditRepository.findById(notExistingId).isPresent()).isFalse();
    }

    @Test
    @DisplayName("Должен вернуть список всех аудит-записей")
    void findAll_shouldReturnAuditRecordsList_Test() {
        assertThat(auditRepository.findAll()).isNotNull();
        assertThat(auditRepository.findAll().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Должен вернуть список аудит-записей для выбранного email")
    void findByCreationUserEmail_shouldReturnOptionsRecordsList_forExistingRecordsWithEmail_Test() {
        assertThat(auditRepository.findByCreationUserEmail(existName).isPresent()).isTrue();
        assertThat(auditRepository.findByCreationUserEmail(existName).get().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Должен вернуть пустой список аудит-записей для не существующего email (или записей просто нет)")
    void findByCreationUserEmail_shouldReturnOptionalEmptyList_forNonExistingRecordWithEmail_Test() {
        assertThat(auditRepository.findByCreationUserEmail(notExistingName).isPresent()).isTrue();
        assertThat(auditRepository.findByCreationUserEmail(notExistingName).get().size()).isEqualTo(0);
    }
}