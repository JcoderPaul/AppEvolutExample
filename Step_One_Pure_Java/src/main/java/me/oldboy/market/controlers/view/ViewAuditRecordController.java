package me.oldboy.market.controlers.view;

import lombok.AllArgsConstructor;
import me.oldboy.market.entity.Audit;
import me.oldboy.market.repository.AuditRepository;

import java.util.List;

/**
 * Класс для отображения аудит записей
 */
@AllArgsConstructor
public class ViewAuditRecordController {
    private AuditRepository auditRepository;

    /**
     * Метод отображает все доступные аудит записи
     *
     * @return коллекцию аудит Audit объектов, записей о действиях пользователей в системе
     */
    public List<Audit> printAllAuditRecord() {
        List<Audit> allAud = auditRepository.findAll();
        System.out.println("---------------------------------------------------------------------");
        allAud.forEach(System.out::println);
        System.out.println("---------------------------------------------------------------------");
        return allAud;
    }
}