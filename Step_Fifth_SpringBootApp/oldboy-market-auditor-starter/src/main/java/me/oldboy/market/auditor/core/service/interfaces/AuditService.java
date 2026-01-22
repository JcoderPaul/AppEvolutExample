package me.oldboy.market.auditor.core.service.interfaces;

import me.oldboy.market.auditor.core.dto.audit.AuditCreateDto;
import me.oldboy.market.auditor.core.dto.audit.AuditReadDto;
import me.oldboy.market.auditor.core.service.interfaces.crud.CreateOnlyService;
import me.oldboy.market.auditor.core.service.interfaces.crud.ReadOnlyService;

import java.util.List;

/**
 * Специализированный сервис для работы с записями аудита.
 * Предоставляет операции чтения и создания записей аудита.
 */
public interface AuditService extends ReadOnlyService<Long, AuditReadDto>, CreateOnlyService<AuditCreateDto, AuditReadDto> {
    List<AuditReadDto> findAllAuditsByUserEmail(String userEmail);
}
