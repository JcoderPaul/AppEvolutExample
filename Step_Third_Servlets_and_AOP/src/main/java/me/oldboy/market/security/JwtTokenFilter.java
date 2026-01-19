package me.oldboy.market.security;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpServletRequest;
import me.oldboy.market.entity.User;
import me.oldboy.market.services.interfaces.UserService;

import java.io.IOException;

/**
 * Фильтр проверяет все входящие HTTP запросы на наличие JWT токена в заголовке Authorization
 * Если найден действующий JWT, он аутентифицирует пользователя и сохраняет эти сведения в сервлет-контексте приложения.
 * Если JWT не найден / или он недействителен, то в сервлет-контексте сохраняет не (пустой) аутентифицированный объект.
 */
@WebFilter(
        servletNames = {
                "ProductManageServlet", "UserManageServlet"
                , "AuditViewServlet", "CategoryViewServlet"
                , "BrandViewServlet"
        },
        initParams = @WebInitParam(name = "order", value = "1")
)
public class JwtTokenFilter implements Filter {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private JwtTokenGenerator jwtTokenGenerator;
    private ServletContext servletContext;
    private UserService userService;

    /**
     * Инициализация фильтра
     *
     * @param config the filter configuration
     */
    @Override
    public void init(FilterConfig config) {
        this.servletContext = config.getServletContext();
        jwtTokenGenerator = (JwtTokenGenerator) servletContext.getAttribute("jwtTokenGenerator");
        userService = (UserService) servletContext.getAttribute("userService");
    }

    /**
     * Проверяет наличие JWT в заголовке Authorization входящего запроса, паттерн задан в параметрах @WebFilter
     * Если найден действующий/валидный JWT, аутентифицирует пользователя и сохраняет в текущем контексте приложения.
     * Если JWT не найден / недействителен, в контексте приложения сохраняется "пустой" объект JwtUserContext.
     *
     * @param servletRequest  входящий запрос
     * @param servletResponse ответ приложения
     * @param filterChain     цепочка применяемых к сервлетам фильтров
     * @throws Exception        if an I/O error occurs during this filter's processing of the request
     * @throws ServletException if the processing fails for any other reason
     */
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) {
        String bearerToken = ((HttpServletRequest) servletRequest).getHeader(HEADER_NAME);
        String token = null;
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            token = bearerToken.substring(BEARER_PREFIX.length());
        } else {
            throw new TokenFilterException("Have no JWT token! Access denied.");
        }
        try {
            String login = jwtTokenGenerator.extractUserName(token);
            User mayBeUserFound = userService.getUserByEmail(login);
            if (mayBeUserFound != null && jwtTokenGenerator.isValid(token, mayBeUserFound)) {
                JwtAuthUser authentication = jwtTokenGenerator.authentication(token, mayBeUserFound);
                servletContext.setAttribute("authentication", authentication);
            } else {
                servletContext.setAttribute("authentication", new JwtAuthUser(null, null));
            }
        } catch (Exception e) {
            throw new TokenFilterException(e.getMessage());
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (IOException | ServletException e) {
            throw new RuntimeException(e);
        }
    }
}