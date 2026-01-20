package me.oldboy.market.config.jwt_config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.config.security_details.ClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Класс - фильтр, который выполняется один раз для каждого запроса. Он проверяет,
 * есть ли у запроса действительный токен JWT, и устанавливает "аутентификацию"
 * пользователя в контексте безопасности.
 */
@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final ClientDetailsService clientDetailsService;
    private final JwtTokenGenerator jwtTokenGenerator;
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";

    @Autowired
    public JwtAuthFilter(ClientDetailsService clientDetailsService,
                         JwtTokenGenerator jwtTokenGenerator) {
        this.clientDetailsService = clientDetailsService;
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    /**
     * Вызывается для каждого запроса — проверяет, есть ли у запроса действительный токен JWT.
     * Если токен действителен — устанавливает аутентификацию в контексте безопасности.
     *
     * @param request     HTTP-запрос
     * @param response    HTTP-ответ
     * @param filterChain цепочка фильтров
     */
    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws ServletException, IOException {

        String authRequestHeader = request.getHeader(HEADER_NAME);
        String tokenFromRequest = null;

        if (authRequestHeader != null && authRequestHeader.startsWith(BEARER_PREFIX)) {
            tokenFromRequest = authRequestHeader.substring(BEARER_PREFIX.length());
        }

        try {
            String accountEmail = jwtTokenGenerator.extractUserName(tokenFromRequest);
            UserDetails userDetails = clientDetailsService.loadUserByUsername(accountEmail);

            if (jwtTokenGenerator.isValid(tokenFromRequest, userDetails)) {
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, userDetails.getUsername(), userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            /* Обрабатываем "руками" */
            String message;
            if (e instanceof ExpiredJwtException) {
                message = "JWT token expired";
            } else if (e instanceof MalformedJwtException) {
                message = "Invalid JWT token";
            } else {
                message = "JWT authentication failed";
            }

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("""
                    {"error":"Unauthorized","message":"%s"}
                    """.formatted(message));
            /* Стопаем цепь */
            return;
        }

        /*
        Любопытная ситуация, как бы я ни конфигурировал код, ошибка фильтра
        упорно пролезает в контейнер и прилетает не 4xx, а 500 статус. Хотя
        в тестах все красиво и ошибки токена 4xx отрабатываются MockMvc, но
        в "бою" упорный 500. Поэтому для полного однообразия код ниже:

        catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException("JWT token expired", e);
        } catch (MalformedJwtException e) {
            throw new JwtAuthenticationException("Invalid JWT token", e);
        } catch (Exception e) {
            throw new JwtAuthenticationException("JWT authentication failed", e);
        }

        пришлось заменить на код выше.
        */

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = requestUri.substring(contextPath.length());

        if (path.equals("/market/users/login") ||
                path.equals("/v3/api-docs/") ||
                path.equals("/swagger-ui/index.html") ||
                path.equals("/swagger-ui.html")) {
            return true;
        } else {
            return false;
        }
    }
}
