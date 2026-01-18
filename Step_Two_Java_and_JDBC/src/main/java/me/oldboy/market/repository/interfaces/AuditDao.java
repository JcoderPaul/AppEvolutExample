package me.oldboy.market.repository.interfaces;

import me.oldboy.market.entity.Audit;
import me.oldboy.market.repository.interfaces.crud.CrudDao;

/**
 * Data Access Object интерфейс для работы с сущностью {@link Audit}.
 * Расширяет базовый CRUD функционал для операций с аудит-записями (фиксация действий пользователей).
 *
 * @see CrudDao
 * @see Audit
 */
public interface AuditDao extends CrudDao<Long, Audit> {
}
