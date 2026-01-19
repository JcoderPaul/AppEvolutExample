package me.oldboy.market.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.oldboy.market.entity.User;
import me.oldboy.market.entity.enums.Role;
import me.oldboy.market.services.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private FilterConfig filterConfig;

    @Mock
    private ServletContext servletContext;

    @Mock
    private JwtTokenGenerator jwtTokenGenerator;

    @Mock
    private UserService userService;

    private JwtTokenFilter jwtTokenFilter;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtTokenFilter = new JwtTokenFilter();

        /* Настройка тестового пользователя */
        testUser = User.builder()
                .userId(1L)
                .email("admin@market.ru")
                .password("encodedPassword")
                .role(Role.ADMIN)
                .build();

        /* Настройка конфигурации фильтра */
        when(filterConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute("jwtTokenGenerator")).thenReturn(jwtTokenGenerator);
        when(servletContext.getAttribute("userService")).thenReturn(userService);
    }

    @Test
    void doFilter_withValidToken_shouldAuthenticateUser_Test() throws Exception {
        String validToken = "valid_jwt_token";
        String bearerToken = "Bearer " + validToken;

        when(userService.getUserByEmail(any())).thenReturn(testUser);

        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtTokenGenerator.extractUserName(validToken)).thenReturn("test@example.com");
        when(jwtTokenGenerator.isValid(validToken, testUser)).thenReturn(true);

        JwtAuthUser expectedAuth = new JwtAuthUser(testUser.getEmail(), testUser.getRole());
        when(jwtTokenGenerator.authentication(validToken, testUser)).thenReturn(expectedAuth);

        jwtTokenFilter.init(filterConfig);

        jwtTokenFilter.doFilter(request, response, filterChain);

        verify(jwtTokenGenerator).extractUserName(validToken);
        verify(jwtTokenGenerator).isValid(validToken, testUser);
        verify(jwtTokenGenerator).authentication(validToken, testUser);
        verify(servletContext).setAttribute("authentication", expectedAuth);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_withoutToken_shouldThrowException_Test() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);
        jwtTokenFilter.init(filterConfig);

        assertThatThrownBy(() -> jwtTokenFilter.doFilter(request, response, filterChain))
                .isInstanceOf(TokenFilterException.class)
                .hasMessageContaining("Have no JWT token! Access denied.");

        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilter_withInvalidToken_shouldSetEmptyAuthentication_Test() throws Exception {
        String invalidToken = "no_valid_jwt_token";
        String bearerToken = "Bearer " + invalidToken;

        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtTokenGenerator.extractUserName(invalidToken)).thenReturn("admin@market.ru");

        jwtTokenFilter.init(filterConfig);

        jwtTokenFilter.doFilter(request, response, filterChain);

        verify(servletContext).setAttribute(eq("authentication"), any(JwtAuthUser.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_whenUserNotFound_shouldSetEmptyAuthentication_Test() throws Exception {
        String validToken = "valid-jwt-token";
        String bearerToken = "Bearer " + validToken;

        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtTokenGenerator.extractUserName(validToken)).thenReturn("nonexistent@example.com");
        when(userService.getUserByEmail("nonexistent@example.com")).thenReturn(null);

        jwtTokenFilter.init(filterConfig);

        jwtTokenFilter.doFilter(request, response, filterChain);

        verify(servletContext).setAttribute(eq("authentication"), any(JwtAuthUser.class));
        verify(filterChain).doFilter(request, response);
    }
}