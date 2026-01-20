package me.oldboy.market.services.interfaces;

import me.oldboy.market.dto.audit.AuditCreateDto;
import me.oldboy.market.dto.audit.AuditReadDto;
import me.oldboy.market.dto.brand.BrandReadDto;
import me.oldboy.market.entity.Audit;
import me.oldboy.market.services.interfaces.crud.CreateOnlyService;
import me.oldboy.market.services.interfaces.crud.ReadOnlyService;

import java.util.List;
import java.util.Optional;

/**
 * Специализированный сервис для работы с записями аудита.
 * Предоставляет операции чтения и создания записей аудита.
 *
 * @see ReadOnlyService
 * @see CreateOnlyService
 * @see Audit
 */
public interface AuditService extends ReadOnlyService<Long, AuditReadDto>, CreateOnlyService<AuditCreateDto, AuditReadDto> {
    List<AuditReadDto> findAllAuditsByUserEmail(String userEmail);
}
