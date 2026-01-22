package me.oldboy.market.auditor.core.repository;

import me.oldboy.market.auditor.core.entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с аудит-записями в БД.
 * Предоставляет методы для выполнения CRUD операций и специализированных запросов
 * к таблице аудита. Наследует стандартные операции из {@link JpaRepository} и {@link CrudRepository}.
 */
@Repository
public interface AuditRepository extends JpaRepository<Audit, Long>, CrudRepository<Audit, Long> {

    /**
     * Находит аудит-запись по идентификатору.
     *
     * @param entityId идентификатор аудит-записи
     * @return {@link Optional} с найденной записью или пустой {@link Optional}
     */
    Optional<Audit> findById(Long entityId);

    /**
     * Получает все аудит-записи из БД.
     *
     * @return список всех аудит-записей
     */
    List<Audit> findAll();

    /**
     * Находит аудит-записи по email пользователя, создавшего запись.
     * <p>
     * Выполняет нативный SQL запрос к таблице аудита для поиска записей по полю created_by.
     *
     * @param email email пользователя для поиска
     * @return {@link Optional} со списком найденных записей или пустой {@link Optional}
     */
    @Query(value = "SELECT aud.* " +
            "FROM my_market.audits AS aud " +
            "WHERE aud.created_by = :email",
            nativeQuery = true)
    Optional<List<Audit>> findByCreationUserEmail(@Param("email") String email);
}