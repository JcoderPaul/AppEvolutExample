package me.oldboy.market.auditor.core.dto.audit;

import lombok.*;
import me.oldboy.market.auditor.core.entity.enums.Action;
import me.oldboy.market.auditor.core.entity.enums.Status;

import java.time.LocalDateTime;

/**
 * DTO для создания аудит-записи (будущий Audit).
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AuditCreateDto {
    LocalDateTime createAt;
    String userEmail;
    Action action;
    Status isSuccess;
    String auditableRecord;
}
