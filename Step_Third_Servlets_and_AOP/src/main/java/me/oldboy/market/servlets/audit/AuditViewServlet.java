package me.oldboy.market.servlets.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.oldboy.market.controllers.AuditController;
import me.oldboy.market.dto.JsonFormResponse;
import me.oldboy.market.dto.audit.AuditReadDto;
import me.oldboy.market.exceptions.ControllerLayerException;
import me.oldboy.market.exceptions.ServiceLayerException;
import me.oldboy.market.security.TokenFilterException;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервлет для просмотра аудит-записей системы.
 * Обрабатывает GET запросы для получения информации о действиях пользователей в системе.
 *
 * @see AuditController
 * @see AuditReadDto
 */
@WebServlet(name = "AuditViewServlet", urlPatterns = {"/market/audits/*"})
public class AuditViewServlet extends HttpServlet {
    /**
     * Контроллер для работы с аудит-записями
     */
    private AuditController auditController;
    /**
     * Объект для сериализации/десериализации JSON
     */
    private ObjectMapper objectMapper;

    /**
     * Инициализирует сервлет, получая зависимости из контекста приложения.
     *
     * @param config конфигурация сервлета
     * @throws ServletException если произошла ошибка при инициализации
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        auditController = (AuditController) getServletContext().getAttribute("auditController");
        objectMapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    /**
     * Обрабатывает GET запросы для получения аудит-записей.
     *
     * @param req  HTTP запрос
     * @param resp HTTP ответ
     * @throws IOException если произошла ошибка ввода-вывода
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {

            /*
                У нас будет 3-и типа GET запроса:
                1. /market/audits/ - вывести все аудит записи (обрабатывается handleAllAudits(resp))
                2. /market/audits/1 - вывести одну запись по ID (обрабатывается handleSingleAudit(auditId, resp))
                3. /market/audits?userEmail=.... - найти все записи действий пользователя (обрабатывается handleAuditByUserEmail(userEmail, resp))
            */
            String pathInfo = req.getPathInfo();
            String userEmail = req.getParameter("userEmail");
            Map<String, String[]> parameterMap = req.getParameterMap();

            if (pathInfo == null || pathInfo.equals("/")) {
                if (userEmail != null && !userEmail.trim().isEmpty()) {
                    /* Обрабатываем /market/audits?userEmail=... - получить аудит записи по идентификатору пользователя */
                    handleAuditByUserEmail(userEmail.trim(), resp);
                    return;
                }
                if (parameterMap.size() == 0) {
                    /* Обрабатываем /market/audits/ - получить список всех записей */
                    handleAllAudits(resp);
                    return;
                } else {
                    throw new ConstraintViolationException("Неизвестный путь запроса", null);
                }
            }

            List<String> components = Arrays.stream(pathInfo.split("/"))
                    .filter(str -> !str.isEmpty())
                    .collect(Collectors.toList());

            int count = components.size();

            if (count == 1) {
                /* Обрабатываем /market/audits/1 - получить данные об одной аудит записи по ее ID */
                String parsAuditRecordIdFromPath = components.get(0);
                if (isNumeric(parsAuditRecordIdFromPath)) {
                    handleSingleAudit(Long.parseLong(parsAuditRecordIdFromPath), resp);
                } else {
                    throw new ConstraintViolationException("Неверный формат ID аудит записи: " + parsAuditRecordIdFromPath, null);
                }
            } else {
                throw new ConstraintViolationException("Неподдерживаемая структура пути: " + pathInfo, null);
            }

        } catch (AccessDeniedException | TokenFilterException e) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            objectMapper.writeValue(resp.getOutputStream(), new JsonFormResponse(e.getMessage()));
        } catch (ControllerLayerException | ServiceLayerException | ConstraintViolationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getOutputStream(), new JsonFormResponse(e.getMessage()));
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getOutputStream(), new JsonFormResponse(e.getMessage()));
        }
    }

    /* --- Вспомогательные методы-обработчики find... запросов --- */

    /**
     * Обрабатывает запрос на получение всех аудит-записей.
     *
     * @param resp HTTP ответ для записи результата
     * @throws IOException если произошла ошибка при записи ответа
     */
    private void handleAllAudits(HttpServletResponse resp) throws IOException {
        List<AuditReadDto> toScreen = auditController.findAllAuditRecords();
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getOutputStream(), toScreen);
    }

    /**
     * Обрабатывает запрос на получение конкретной аудит-записи по ID.
     *
     * @param auditId ID аудит-записи
     * @param resp    HTTP ответ для записи результата
     * @throws IOException если произошла ошибка при записи ответа
     */
    private void handleSingleAudit(Long auditId, HttpServletResponse resp) throws IOException {
        AuditReadDto toScreen = auditController.findAuditRecordById(auditId);
        if (toScreen != null) {
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), toScreen);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getOutputStream(), new JsonFormResponse("Аудит запись с ID " + auditId + " не найдена"));
        }
    }

    /**
     * Обрабатывает запрос на получение аудит-записей по email пользователя.
     *
     * @param userEmail email пользователя для поиска записей
     * @param resp      HTTP ответ для записи результата
     * @throws IOException если произошла ошибка при записи ответа
     */
    private void handleAuditByUserEmail(String userEmail, HttpServletResponse resp) throws IOException {
        List<AuditReadDto> toScreen = auditController.findAllAuditRecordsByUserEmail(userEmail);
        if (toScreen != null && toScreen.size() != 0) {
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), toScreen);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getOutputStream(), new JsonFormResponse("Записей для email " + userEmail + " не найдено"));
        }
    }

    /* --- Вспомогательный метод для валидации вводимого числа --- */

    /**
     * Проверяет, является ли строка числовым значением.
     *
     * @param str строка для проверки
     * @return true - если строка может быть преобразована в Long, иначе - false
     */
    private boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}