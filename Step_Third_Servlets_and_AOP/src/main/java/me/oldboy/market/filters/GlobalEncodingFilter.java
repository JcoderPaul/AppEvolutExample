package me.oldboy.market.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Фильтр для установки глобальной кодировки и типа содержимого для HTTP запросов и ответов.
 * Обеспечивает корректную обработку символов и единообразие формата ответов во всем приложении.
 *
 * @see Filter
 * @see FilterConfig
 */
@WebFilter(
        filterName = "CharacterEncodingFilter",
        urlPatterns = "/*",
        initParams = {
                @WebInitParam(name = "encoding", value = "UTF-8"),
                @WebInitParam(name = "contentType", value = "application/json;charset=UTF-8")
        }
)
public class GlobalEncodingFilter implements Filter {
    /**
     * Кодировка символов для запросов и ответов
     */
    private String encoding;
    /**
     * Тип содержимого для HTTP ответов
     */
    private String contentType;

    /**
     * Инициализирует фильтр с параметрами из конфигурации.
     * Устанавливает значения кодировки и типа содержимого, используя параметры инициализации
     * или значения по умолчанию, если параметры не указаны.
     *
     * @param filterConfig конфигурация фильтра, содержащая параметры инициализации
     * @throws ServletException если произошла ошибка при инициализации фильтра
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.encoding = filterConfig.getInitParameter("encoding");
        this.contentType = filterConfig.getInitParameter("contentType");

        if (this.encoding == null) {
            this.encoding = "UTF-8";
        }
        if (this.contentType == null) {
            this.contentType = "application/json;charset=UTF-8";
        }
    }

    /**
     * Выполняет фильтрацию HTTP запросов и ответов.
     * Устанавливает заданную кодировку и тип содержимого для всех проходящих через фильтр запросов.
     *
     * @param req   объект запроса для фильтрации
     * @param resp  объект ответа для фильтрации
     * @param chain цепочка фильтров для передачи управления
     * @throws IOException      если произошла ошибка ввода-вывода при обработке запроса/ответа
     * @throws ServletException если произошла ошибка сервлета при фильтрации
     */
    @Override
    public void doFilter(ServletRequest req,
                         ServletResponse resp,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) req;
        HttpServletResponse httpResponse = (HttpServletResponse) resp;

        /* Устанавливаем кодировку */
        httpRequest.setCharacterEncoding(encoding);
        httpResponse.setCharacterEncoding(encoding);
        httpResponse.setContentType(contentType);

        chain.doFilter(req, resp);
    }

    /**
     * Вызывается при уничтожении фильтра сервлет-контейнером.
     * Освобождает ресурсы, занятые фильтром (в данной реализации не требуется).
     * В текущей реализации метод не выполняет никаких действий, так как фильтр
     * не использует ресурсы, требующие явного освобождения.
     */
    @Override
    public void destroy() {

    }
}