package me.oldboy.market.services.interfaces;

import me.oldboy.market.entity.Audit;
import me.oldboy.market.services.interfaces.crud.CreateOnlyService;
import me.oldboy.market.services.interfaces.crud.ReadOnlyService;

/**
 * Специализированный сервис для работы с записями аудита.
 * Предоставляет операции чтения и создания записей аудита.
 *
 * @see ReadOnlyService
 * @see CreateOnlyService
 * @see Audit
 */
public interface AuditService extends ReadOnlyService<Long, Audit>, CreateOnlyService<Long, Audit> {
}
