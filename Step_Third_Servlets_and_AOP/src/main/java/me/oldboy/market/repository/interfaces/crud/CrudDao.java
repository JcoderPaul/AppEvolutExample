package me.oldboy.market.repository.interfaces.crud;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object интерфейс для базовых CRUD операций
 *
 * @param <Key>    тип ключа сущности
 * @param <Entity> тип сущности
 */
public interface CrudDao<Key, Entity> {
    /**
     * Создает новую сущность
     */
    Optional<Entity> create(Entity entityWithOutId);

    /**
     * Находит сущность по идентификатору
     */
    Optional<Entity> findById(Key entityId);

    /**
     * Обновляет сущность
     */
    boolean update(Entity updateData);

    /**
     * Удаляет сущность по идентификатору
     */
    boolean delete(Key entityId);

    /**
     * Возвращает все сущности
     */
    List<Entity> findAll();
}
