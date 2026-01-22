package me.oldboy.market.auditor.core.dto.audit;

import lombok.Builder;
import me.oldboy.market.auditor.core.entity.enums.Action;
import me.oldboy.market.auditor.core.entity.enums.Status;

import java.time.LocalDateTime;

/**
 * DTO для представления извлеченной из БД аудит-записи.
 */
@Builder
public record AuditReadDto(Long id,
                           LocalDateTime createAt,
                           String createBy,
                           Action action,
                           Status isSuccess,
                           String auditableRecord) {
}