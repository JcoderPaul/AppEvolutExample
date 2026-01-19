package me.oldboy.market.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.aop.annotations.Loggable;
import me.oldboy.market.dto.audit.AuditReadDto;
import me.oldboy.market.mapper.AuditMapper;
import me.oldboy.market.services.interfaces.AuditService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс - контроллер для управления аудит-записями (в текущей реализации только просмотр)
 */
@Slf4j
@AllArgsConstructor
public class AuditController {

    private AuditService auditService;

    /**
     * Ищет аудит-запись по уникальному идентификационному номеру ID в БД
     *
     * @param auditId идентификатор аудит записи в БД
     * @return найденную аудит-запись в случае успеха и null - если запись не найдена
     */
    @Loggable
    public AuditReadDto findAuditRecordById(Long auditId) {
        return AuditMapper.INSTANCE.mapToReadDto(auditService.findById(auditId));
    }

    /**
     * Возвращает список всех доступных аудит-записей.
     *
     * @return все доступные аудит-записи
     */
    @Loggable
    public List<AuditReadDto> findAllAuditRecords() {
        return auditService
                .findAll()
                .stream()
                .map(audit -> AuditMapper.INSTANCE.mapToReadDto(audit))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список всех доступных аудит-записей для выбранного пользователя по email.
     *
     * @return все доступные аудит записи, фиксирующие действия конкретного пользователя (фикс. по email)
     */
    @Loggable
    public List<AuditReadDto> findAllAuditRecordsByUserEmail(String email) {
        return auditService
                .findAll()
                .stream()
                .filter(audit -> audit.getCreateBy().equals(email))
                .map(audit -> AuditMapper.INSTANCE.mapToReadDto(audit))
                .collect(Collectors.toList());
    }
}