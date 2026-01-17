package me.oldboy.market.services;

import lombok.AllArgsConstructor;
import me.oldboy.market.entity.Audit;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.log_enum.Action;
import me.oldboy.market.entity.log_enum.Status;
import me.oldboy.market.repository.AuditRepository;

import java.util.Date;
import java.util.List;

/**
 * Сервисный класс для управления записями аудит-догов.
 * Предоставляет высокоуровневые методы для логирования действий пользователей и получения истории аудита.
 *
 * @see Audit
 * @see AuditRepository
 * @see Action
 * @see Status
 */
@AllArgsConstructor
public class AuditService {
    private AuditRepository auditRepository;

    /**
     * Создает и сохраняет запись аудита действий пользователя.
     * Автоматически устанавливает текущую временную метку.
     *
     * @param action тип выполненного действия
     * @param isSuccess статус выполнения операции
     * @param email email пользователя, выполнившего действие
     * @param product продукт, над которым выполнено действие (может быть null, для ситуации LogIn/LogOut)
     * @return true - запись успешно сохранена, false - в противном случае
     */
    public boolean saveAuditRecord(Action action, Status isSuccess, String email, Product product){
        Audit audRecord = Audit.builder()
                .timestamp(new Date().getTime())
                .userEmail(email)
                .action(action)
                .isSuccess(isSuccess)
                .product(product)
                .build();

        Audit savedAud = auditRepository.save(audRecord);

        if(savedAud != null){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Возвращает полную историю записей аудита.
     *
     * @return список всех записей аудита в порядке добавления
     */
    public List<Audit> findAllAuditRecord(){
        return auditRepository.findAll();
    }
}