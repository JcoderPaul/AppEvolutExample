package me.oldboy.market.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.auditor.core.dto.audit.AuditReadDto;
import me.oldboy.market.auditor.core.service.interfaces.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для управления аудит-записями системы.
 * Предоставляет API для получения информации о действиях пользователей в системе.
 * Все методы возвращают данные в формате JSON.
 */
@Slf4j
@RestController
@RequestMapping("/market/audits")
@Tag(name = "AuditController", description = "Реализуем ряд просмотровых методов для аудит-записей")
public class AuditController {

    private final AuditService auditService;

    @Autowired
    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Получает аудит-запись по её идентификатору.
     * Если запись с указанным идентификатором существует, возвращает её.
     * В противном случае возвращает HTTP 404 Not Found.
     *
     * @param auditId идентификатор аудит-записи
     * @return {@link ResponseEntity} с найденной записью или статусом 404
     * @apiExample Пример запроса: GET /market/audits/123
     */
    @GetMapping("/{auditId}")
    @Operation(summary = "Поиск аудит-записи по ее ID - уникальному идентификатору",
            description = "Возвращает информацию о найденной аудит записи или статус контент не найден")
    public ResponseEntity<?> getAuditRecordById(@PathVariable("auditId")
                                                Long auditId) {
        return auditService.findById(auditId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Получает все аудит-записи системы.
     * Возвращает полный список всех доступных аудит-записей в формате JSON.
     *
     * @return {@link ResponseEntity} со списком всех аудит-записей
     * @apiExample Пример запроса: GET /market/audits
     */
    @GetMapping()
    @Operation(summary = "Поиск всех доступных аудит-записей",
            description = "Возвращает список найденных аудит-записей")
    public ResponseEntity<?> getAllAudits() {
        List<AuditReadDto> auditList = auditService.findAll();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(auditList);
    }

    /**
     * Находит аудит-записи по email пользователя.
     *
     * @param userEmail email пользователя для фильтрации
     * @return {@link ResponseEntity} с найденными записями или статусом 404
     * @apiExample Пример запроса: GET /market/audits/?userEmail=user@example.com
     */
    @GetMapping("/")
    @Operation(summary = "Поиск всех аудит-записей связанных с конкретных пользователем по его email",
            description = "Возвращает информацию о найденных аудит-записях или статус контент не найден, если для заданного email записи отсутствуют")
    public ResponseEntity<?> getAuditByUserEmail(@RequestParam(name = "userEmail", required = false)
                                                 String userEmail) {
        List<AuditReadDto> mayBeFoundList = auditService.findAllAuditsByUserEmail(userEmail);
        if (mayBeFoundList.size() != 0) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(mayBeFoundList);
        }
        return ResponseEntity.notFound().build();
    }
}