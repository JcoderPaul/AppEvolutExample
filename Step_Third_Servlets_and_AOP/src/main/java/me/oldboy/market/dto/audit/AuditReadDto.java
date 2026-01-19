package me.oldboy.market.dto.audit;

import lombok.Builder;
import me.oldboy.market.entity.enums.Action;
import me.oldboy.market.entity.enums.Status;

import java.time.LocalDateTime;

/**
 * DTO для представления аудит-записи.
 */
@Builder
public record AuditReadDto(Long id,
                           LocalDateTime createAt,
                           String createBy,
                           Action action,
                           Status isSuccess,
                           String auditableRecord) {

}