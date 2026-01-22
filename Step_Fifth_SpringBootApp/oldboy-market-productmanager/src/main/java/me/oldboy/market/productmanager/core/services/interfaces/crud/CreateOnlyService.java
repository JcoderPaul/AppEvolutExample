package me.oldboy.market.productmanager.core.services.interfaces.crud;

/**
 * Специализированный сервисный интерфейс только для операций создания сущностей.
 * Используется в сценариях где требуется только создание без других CRUD операций.
 *
 * @param <CreateDto>> тип принимаемого значения
 * @param <ReadDto>> тип возвращаемого значения
 */
public interface CreateOnlyService<CreateDto, ReadDto> {
    /**
     * Создает новую сущность в системе.
     * Идентификатор сущности генерируется автоматически.
     *
     * @param entityWithoutId сущность без идентификатора для создания
     * @return созданная сущность с присвоенным идентификатором
     */
    ReadDto create(CreateDto entityWithoutId);
}
