package me.oldboy.market.usermanager.core.services.interfaces.crud;

import java.util.List;
import java.util.Optional;

/**
 * Специализированный сервисный интерфейс только для операций чтения сущностей.
 * Используется в сценариях где требуется только чтение данных без возможности модификации.
 *
 * @param <Key>    тип уникального идентификатора сущности
 * @param <ReadDto>> тип возвращаемой сущности
 */
public interface ReadOnlyService<Key, ReadDto> {
    /**
     * Находит сущность по уникальному идентификатору.
     *
     * @param entityId идентификатор сущности
     * @return найденная сущность
     */
    Optional<ReadDto> findById(Key entityId);

    /**
     * Возвращает все сущности из системы.
     *
     * @return список всех сущностей, может быть пустым
     */
    List<ReadDto> findAll();
}
