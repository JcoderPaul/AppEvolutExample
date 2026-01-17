package me.oldboy.market.cache_bd;

import lombok.Getter;
import me.oldboy.market.entity.Audit;
import me.oldboy.market.exceptions.BrandDBException;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс имитирующий "кэш" таблицы БД содержащей записи аудита
 */
public class AuditDB {

    @Getter
    private List<Audit> auditLogList = new ArrayList<>();

    private static AuditDB INSTANCE;

    public static AuditDB getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new AuditDB();
        }
        return INSTANCE;
    }

    /**
     * Метод добавляющий новые записи о действиях пользователей в аудит "кэш"
     *
     * @param auditLog запись о действии пользователя
     * @return ID сохраненной аудит записи
     */
    public Long add(Audit auditLog) {
        long index = 1;

        if (auditLogList.size() != 0) {
            index = index + auditLogList
                    .stream()
                    .map(a -> a.getId())
                    .max((a, b) -> a > b ? 1 : -1)
                    .orElseThrow(() -> new BrandDBException("Element not found"));
        }

        auditLog.setId(index);
        auditLogList.add(auditLog);

        return index;
    }
}