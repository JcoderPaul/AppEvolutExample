package me.oldboy.market.servlets.brand;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.oldboy.market.controllers.BrandController;
import me.oldboy.market.dto.JsonFormResponse;
import me.oldboy.market.dto.brand.BrandReadDto;
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
 * Сервлет для работы с брендами товаров.
 * Обрабатывает GET запросы для получения информации о брендах в системе.
 *
 * @see BrandController
 * @see BrandReadDto
 */
@WebServlet(name = "BrandViewServlet", urlPatterns = {"/market/brands/*"})
public class BrandViewServlet extends HttpServlet {
    /**
     * Контроллер для работы с брендами
     */
    private BrandController brandController;

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
        brandController = (BrandController) getServletContext().getAttribute("brandController");
        objectMapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    /**
     * Обрабатывает GET запросы для получения информации о брендах.
     *
     * @param req HTTP запрос
     * @param resp HTTP ответ
     * @throws IOException если произошла ошибка ввода-вывода
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {

            /*
                У нас будет 2-и типа GET запроса:
                1. /market/brands/ - вывести все брэнды (обрабатывается handleAllBrands(resp))
                2. /market/brands/1 - вывести брэнд по ID (обрабатывается handleSingleBrand(auditId, resp))
            */
            String pathInfo = req.getPathInfo();
            Map<String, String[]> parameterMap = req.getParameterMap();

            if (pathInfo == null || pathInfo.equals("/")) {
                if (parameterMap.size() == 0) {
                    /* Обрабатываем /market/brands/ - получить список всех брэндов */
                    handleAllBrands(resp);
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
                /* Обрабатываем /market/brands/1 - получить данные об одном брэнде по его ID */
                String parsIdFromPath = components.get(0);
                if (isNumeric(parsIdFromPath)) {
                    handleSingleBrand(Integer.parseInt(parsIdFromPath), resp);
                } else {
                    throw new ConstraintViolationException("Неверный формат ID брэнда: " + parsIdFromPath, null);
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
     * Обрабатывает запрос на получение всех брендов.
     *
     * @param resp HTTP ответ для записи результата
     * @throws IOException если произошла ошибка при записи ответа
     */
    private void handleAllBrands(HttpServletResponse resp) throws IOException {
        List<BrandReadDto> toScreen = brandController.findAllBrands();
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getOutputStream(), toScreen);
    }

    /**
     * Обрабатывает запрос на получение конкретного бренда по ID.
     *
     * @param brandId ID бренда
     * @param resp HTTP ответ для записи результата
     * @throws IOException если произошла ошибка при записи ответа
     */
    private void handleSingleBrand(Integer brandId, HttpServletResponse resp) throws IOException {
        BrandReadDto toScreen = brandController.findBrandById(brandId);
        if (toScreen != null) {
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), toScreen);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getOutputStream(), new JsonFormResponse("Брэнд с ID " + brandId + " не найден"));
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