package me.oldboy.market.repository;

import lombok.AllArgsConstructor;
import me.oldboy.market.cache_bd.AuditDB;
import me.oldboy.market.entity.Audit;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с журналом аудита.
 * Реализует принцип "append-only" - записи можно только добавлять, но не изменять или удалять.
 */
@AllArgsConstructor
public class AuditRepository implements CrudRepository<Long, Audit> {

    private AuditDB auditDB;

    /**
     * Сохраняет новую запись аудита в системе.
     *
     * @param entity запись аудита для сохранения
     * @return сохраненная запись с присвоенным ID
     */
    @Override
    public Audit save(Audit entity) {
        Long genId = auditDB.add(entity);
        return findById(genId).get();
    }

    /**
     * Возвращает все записи из журнала аудита.
     *
     * @return список всех записей аудита в порядке добавления
     */
    @Override
    public List<Audit> findAll() {
        return auditDB.getAuditLogList();
    }

    /**
     * Находит запись аудита по идентификатору ID.
     *
     * @param id идентификатор записи аудита
     * @return Optional с найденной записью Audit или empty если не найдено
     */
    @Override
    public Optional<Audit> findById(Long id) {
        return auditDB.getAuditLogList()
                .stream()
                .filter(audit -> audit.getId().equals(id))
                .findFirst();
    }

    @Override
    public void update(Audit updateAudit) {
        /* Аудит для того и нужен, чтобы существующие записи нельзя было Изменить, хотя бы из приложения */
    }

    @Override
    public boolean delete(Long id) {
        /* Аудит для того и нужен, чтобы существующие записи нельзя было Удалить, хотя бы из приложения */
        return false;
    }
}