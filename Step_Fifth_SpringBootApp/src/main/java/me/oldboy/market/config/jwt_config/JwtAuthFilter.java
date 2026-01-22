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
             return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui") ||
                path.equals("/swagger-ui.html") ||
                path.startsWith("/webjars/") ||
                path.equals("/market/users/login");
    }
}