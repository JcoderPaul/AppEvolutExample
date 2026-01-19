package me.oldboy.market.servlets.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.oldboy.market.controllers.CategoryController;
import me.oldboy.market.dto.JsonFormResponse;
import me.oldboy.market.dto.category.CategoryReadDto;
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
 * Сервлет для работы с категориями товаров.
 * Обрабатывает GET запросы для получения информации о категориях в системе.
 *
 * @see CategoryController
 * @see CategoryReadDto
 */
@WebServlet(name = "CategoryViewServlet", urlPatterns = {"/market/categories/*"})
public class CategoryViewServlet extends HttpServlet {

    /**
     * Контроллер для работы с категориями товаров
     */
    private CategoryController categoryController;

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
        categoryController = (CategoryController) getServletContext().getAttribute("categoryController");
        objectMapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    /**
     * Обрабатывает GET запросы для получения информации о категориях товаров.
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
                У нас будет 2-а типа GET запроса:
                1. /market/categories/ - вывести все категории товаров (обрабатывается handleAllCategories(resp))
                2. /market/categories/1 - вывести категорию по ID (обрабатывается handleSingleCategory(categoryId, resp))
            */
            String pathInfo = req.getPathInfo();
            Map<String, String[]> parameterMap = req.getParameterMap();

            if (pathInfo == null || pathInfo.equals("/")) {
                if (parameterMap.size() == 0) {
                    /* Обрабатываем /market/brands/ - получить список всех брэндов */
                    handleAllCategories(resp);
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
                /* Обрабатываем /market/category/1 - получить данные об одной категории по ID */
                String parsIdFromPath = components.get(0);
                if (isNumeric(parsIdFromPath)) {
                    handleSingleCategory(Integer.parseInt(parsIdFromPath), resp);
                } else {
                    throw new ConstraintViolationException("Неверный формат ID категории: " + parsIdFromPath, null);
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
     * Обрабатывает запрос на получение всех категорий товаров.
     *
     * @param resp HTTP ответ для записи результата
     * @throws IOException если произошла ошибка при записи ответа
     */
    private void handleAllCategories(HttpServletResponse resp) throws IOException {
        List<CategoryReadDto> toScreen = categoryController.findAllCategories();
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getOutputStream(), toScreen);
    }

    /**
     * Обрабатывает запрос на получение конкретной категории по ID.
     *
     * @param categoryId ID категории
     * @param resp       HTTP ответ для записи результата
     * @throws IOException если произошла ошибка при записи ответа
     */
    private void handleSingleCategory(Integer categoryId, HttpServletResponse resp) throws IOException {
        CategoryReadDto toScreen = categoryController.findCategoryById(categoryId);
        if (toScreen != null) {
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), toScreen);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getOutputStream(), new JsonFormResponse("Категория с ID " + categoryId + " не найдена"));
        }
    }

    /* --- Вспомогательный метод для валидации вводимого числа --- */

    /**
     * Проверяет, является ли строка числовым значением (целым числом).
     *
     * @param str строка для проверки
     * @return true - если строка может быть преобразована в Integer, иначе - false
     */
    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}