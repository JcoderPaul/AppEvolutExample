package me.oldboy.market.servlets.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.oldboy.market.controllers.ProductController;
import me.oldboy.market.dto.JsonFormResponse;
import me.oldboy.market.dto.product.ProductCreateDto;
import me.oldboy.market.dto.product.ProductReadDto;
import me.oldboy.market.dto.product.ProductUpdateDto;
import me.oldboy.market.exceptions.ControllerLayerException;
import me.oldboy.market.exceptions.ServiceLayerException;
import me.oldboy.market.security.JwtAuthUser;
import me.oldboy.market.security.TokenFilterException;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервлет для управления товарами в системе.
 * <p>
 * Обрабатывает CRUD операции (создание, чтение, обновление, удаление) для товаров,
 * а также предоставляет различные методы поиска и фильтрации товаров.
 *
 * @see ProductController
 * @see ProductReadDto
 * @see ProductCreateDto
 * @see ProductUpdateDto
 */
@WebServlet(name = "ProductManageServlet", urlPatterns = {"/market/products/*"})
public class ProductManageServlet extends HttpServlet {

    /**
     * Контроллер для работы с товарами
     */
    private ProductController productController;

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
        productController = (ProductController) getServletContext().getAttribute("productController");
        objectMapper = (ObjectMapper) getServletContext().getAttribute("objectMapper");
    }

    /**
     * Создает новый товар в системе.
     *
     * @param req  HTTP запрос с телом в формате JSON
     * @param resp HTTP ответ
     * @throws IOException если произошла ошибка ввода-вывода
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            /* Фиксируем, кто создаем товар для аудита */
            String authUserEmail = getAuthUserEmail();

            ProductCreateDto createDto = objectMapper.readValue(req.getInputStream(), ProductCreateDto.class);

            if (productController.createProduct(createDto, authUserEmail) != null) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                objectMapper.writeValue(resp.getOutputStream(), new JsonFormResponse("Product creation was successful"));
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

    /**
     * Обновляет существующий товар в системе.
     *
     * @param req  HTTP запрос с телом в формате JSON
     * @param resp HTTP ответ
     * @throws IOException если произошла ошибка ввода-вывода
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            /* Фиксируем, кто пытается обновить товар */
            String authUserEmail = getAuthUserEmail();

            ProductUpdateDto updateDto = objectMapper.readValue(req.getInputStream(), ProductUpdateDto.class);

            if (productController.updateProduct(updateDto, authUserEmail)) {
                resp.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(resp.getOutputStream(), new JsonFormResponse("Update success!"));
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

    /**
     * Удаляет товар из системы по указанному ID.
     *
     * @param req  HTTP запрос с ID товара в пути
     * @param resp HTTP ответ
     * @throws IOException если произошла ошибка ввода-вывода
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            String authUserEmail = getAuthUserEmail();

            String path = req.getPathInfo();
            if (path == null || path.equals("/")) {
                throw new ConstraintViolationException("Product ID is required", null);
            }

            Long productId = Long.valueOf(path.substring(1));

            if (productController.deleteProduct(productId, authUserEmail)) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                objectMapper.writeValue(resp.getOutputStream(), new JsonFormResponse("Remove success!"));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getOutputStream(), new JsonFormResponse("Remove failed!"));
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

    /**
     * Обрабатывает GET запросы для получения информации о товарах.
     * Поддерживает 5 различных типов запросов для поиска и фильтрации товаров.
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
                У нас будет 5-ть типов GET запроса:
                1. /market/products/ - вывести все товары (обрабатывается handleAllProducts(resp))
                2. /market/products/1 - вывести один товар по ID (обрабатывается handleSingleProduct(productId, resp))
                3. /market/products/categories/1 - вывести все товары по ID категории товара (обрабатывается handleProductsByCategory(categoryId, resp))
                4. /market/products/brands/1 - вывести все товары по ID брэнда товара (обрабатывается handleProductsByBrand(brandId, resp))
                5. /market/products?prodName=valenki - вывести товар по названию (обрабатывается handleProductsByName(productName, resp))

            */
            String pathInfo = req.getPathInfo();
            String prodName = req.getParameter("prodName");
            Map<String, String[]> parameterMap = req.getParameterMap();

            if (pathInfo == null || pathInfo.equals("/")) {
                if (prodName != null && !prodName.trim().isEmpty()) {
                    /* Обрабатываем /market/products?prodName=... - получить товар по названию */
                    handleProductsByName(prodName.trim(), resp);
                    return;
                }
                if (parameterMap.size() == 0) {
                    /* Обрабатываем /market/products/ - получить список всех продуктов */
                    handleAllProducts(resp);
                    return;
                } else {
                    throw new ConstraintViolationException("Unknown request path", null);
                }
            }

            /*
            Парсим полученный pathInfo - если "полет нормальный", то мы можем получить:
            - Список из одного элемента - запрос на получение данных о товаре по ID;
            - Список из двух элементов - "есть высокая вероятность", что пользователь хочет
            посмотреть все товары по выбранной категории или брэнду, например:
            "/categories/1" -> ["categories", "1"]
            Все остальное от Лукавого.
            */
            List<String> components = Arrays.stream(pathInfo.split("/"))
                    .filter(str -> !str.isEmpty())
                    .collect(Collectors.toList());

            int count = components.size();

            if (count == 1) {
                /* Обрабатываем /market/products/1 - получить данные о продукте по его ID */
                String parsProductIdFromPath = components.get(0);
                if (isNumeric(parsProductIdFromPath)) {
                    /* Пока все нормально - productId у нас Long (помним, что categoryId и brandId у нас Int) */
                    handleSingleProduct(Long.parseLong(parsProductIdFromPath), resp);
                } else {
                    throw new ConstraintViolationException("Invalid product ID format: " + parsProductIdFromPath, null);
                }
            } else if (count == 2) {
                /*
                Обрабатываем оставшиеся варианты:
                - /market/products/categories/1
                - /market/products/brands/1
                */
                String categoryOrBrand = components.get(0); // "categories" или "brands"
                String parsIdFromPath = components.get(1); // ID категории или бренда (не забываем, они уже Integer)

                if (!isNumeric(parsIdFromPath)) {
                    throw new ConstraintViolationException("Invalid ID format for category/brand: " + parsIdFromPath, null);
                }
                /* Что бы не делать метод подобный isNumeric для Integer парсим Long -> преобразуем в Integer */
                Integer id = Math.toIntExact(Long.parseLong(parsIdFromPath));

                if (categoryOrBrand.equals("categories")) {
                    handleProductsByCategory(id, resp);
                } else if (categoryOrBrand.equals("brands")) {
                    handleProductsByBrand(id, resp);
                } else {
                    throw new ConstraintViolationException("Unknown path: /" + categoryOrBrand + "/" + parsIdFromPath, null);
                }
            } else {
                throw new ConstraintViolationException("Unsupported path structure: " + pathInfo, null);
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

    /**
     * Получает email аутентифицированного пользователя из контекста для целей аудита.
     *
     * @return email аутентифицированного пользователя
     * @throws AccessDeniedException если пользователь не аутентифицирован
     */
    private String getAuthUserEmail() {
        JwtAuthUser jwtAuthUser = (JwtAuthUser) getServletContext().getAttribute("authentication");
        return jwtAuthUser.getEmail();
    }

    /* --- Вспомогательные методы-обработчики find... запросов --- */

    /**
     * Обрабатывает запрос на получение всех товаров.
     *
     * @param resp HTTP ответ для записи результата
     * @throws IOException если произошла ошибка при записи ответа
     */
    private void handleAllProducts(HttpServletResponse resp) throws IOException {
        List<ProductReadDto> toScreen = productController.findAllProduct();
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getOutputStream(), toScreen);
    }

    /**
     * Обрабатывает запрос на получение конкретного товара по ID.
     *
     * @param productId ID товара
     * @param resp      HTTP ответ для записи результата
     * @throws IOException если произошла ошибка при записи ответа
     */
    private void handleSingleProduct(long productId, HttpServletResponse resp) throws IOException {
        ProductReadDto toScreen = productController.findProductById(productId);
        if (toScreen != null) {
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), toScreen);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getOutputStream(), new JsonFormResponse("Product with ID " + productId + " not found"));
        }
    }

    /**
     * Обрабатывает запрос на получение товаров по категории.
     *
     * @param categoryId ID категории
     * @param resp       HTTP ответ для записи результата
     * @throws IOException если произошла ошибка при записи ответа
     */
    private void handleProductsByCategory(Integer categoryId, HttpServletResponse resp) throws IOException {
        List<ProductReadDto> toScreen = productController.findProductsByCategory(categoryId);
        if (toScreen.size() != 0) {
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), toScreen);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getOutputStream(), new JsonFormResponse("Category with ID " + categoryId + " not found, or there are no products in this category in the database"));
        }
    }

    /**
     * Обрабатывает запрос на получение товаров по бренду.
     *
     * @param brandId ID бренда
     * @param resp    HTTP ответ для записи результата
     * @throws IOException если произошла ошибка при записи ответа
     */
    private void handleProductsByBrand(Integer brandId, HttpServletResponse resp) throws IOException {
        List<ProductReadDto> toScreen = productController.findProductsByBrand(brandId);
        if (toScreen.size() != 0) {
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), toScreen);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getOutputStream(), new JsonFormResponse("Brand with ID " + brandId + " not found, or there are no products of this brand in the database"));
        }
    }

    /**
     * Обрабатывает запрос на получение товара по названию.
     *
     * @param productName название товара
     * @param resp        HTTP ответ для записи результата
     * @throws IOException если произошла ошибка при записи ответа
     */
    private void handleProductsByName(String productName, HttpServletResponse resp) throws IOException {
        ProductReadDto toScreen = productController.findProductByName(productName);
        if (toScreen != null) {
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getOutputStream(), toScreen);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getOutputStream(), new JsonFormResponse("Product with the name " + productName + " not found"));
        }
    }

    /* --- Вспомогательный метод для валидации вводимого числа, тут проверяем на Long, по надобности будет "конверт" в Int --- */

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