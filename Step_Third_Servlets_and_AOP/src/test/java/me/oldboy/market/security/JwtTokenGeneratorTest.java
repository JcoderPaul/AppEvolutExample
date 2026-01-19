package me.oldboy.market.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.SneakyThrows;
import me.oldboy.market.entity.User;
import me.oldboy.market.entity.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtTokenGeneratorTest {
    private static final String SECRET = "moySecretnyiKluchKotoriyTakoyDlininyChtoPodoydetDlyaHS512256BitnogoShifrovaniya";
    private JwtTokenGenerator jwtTokenGenerator;
    private User testUser, testAnotherUser;
    private Long userId, userAnotherId;
    private String email, emailAnother;
    private Role role;

    @BeforeEach
    void setUp() {
        jwtTokenGenerator = new JwtTokenGenerator(SECRET);

        userId = 1L;
        userAnotherId = 10L;

        email = "admin@market.ru";
        emailAnother = "user@market.ru";

        role = Role.ADMIN;

        testUser = User.builder()
                .userId(userId)
                .email(email)
                .role(role)
                .build();

        testAnotherUser = User.builder()
                .userId(userAnotherId)
                .email(emailAnother)
                .role(role)
                .build();
    }

    @DisplayName("Тест генерации токена")
    @Test
    void getToken_withValidParameters_shouldGenerateValidToken_Test() {
        /* Просто вызываем метод генерирующий токен */
        String token = jwtTokenGenerator.getToken(userId, email, role);

        /* Проверяем не 'null' ли он */
        assertThat(token).isNotNull();
        assertThat(token.isEmpty()).isFalse();

        /* Парсим содержимое токена и ... */
        Claims claims = Jwts.parser()
                .verifyWith(jwtTokenGenerator.signKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        /* Сопоставляем его с ожиданием */
        assertThat(email).isEqualTo(claims.getSubject());
        assertThat(userId).isEqualTo(claims.get("id", Long.class));
        assertThat(role.toString()).isEqualTo(claims.get("role", String.class));
        assertThat(claims.getExpiration()).isNotNull();
    }

    @Nested
    @DisplayName("Набор тестов для *.extractUserName() метода - 'получение имени из токена' - в классе JwtTokenGenerator")
    class ExtractUserNameMethodTests {

        @DisplayName("Тест извлечения имени пользователя")
        @Test
        void extractUserName_withValidToken_shouldReturnEmail_Test() {
            /* Генерируем токен */
            String token = jwtTokenGenerator.getToken(userId, email, role);

            /* Запускаем тестируемый метод */
            String actualEmail = jwtTokenGenerator.extractUserName(token);

            assertEquals(email, actualEmail);
        }

        @DisplayName("Тест получение невалидного токена")
        @Test
        void extractUserName_withInvalidToken_shouldThrowException_Test() {
            String invalidToken = "wah.wah.invalid.token.here.da";

            assertThatThrownBy(() -> jwtTokenGenerator.extractUserName(invalidToken))
                    .isInstanceOf(Exception.class);
        }
    }

    @Nested
    @DisplayName("Набор тестов для *.isValid() метода - 'валидация токена' - в классе JwtTokenGenerator")
    class IsValidMethodTests {

        @Test
        void isValid_withValidTokenAndMatchingUser_shouldReturnTrue_Test() {
            /* Генерируем токен на основе данных пользователя */
            String token =
                    jwtTokenGenerator.getToken(testUser.getUserId(), testUser.getEmail(), testUser.getRole());

            /* Проверяем работу тестируемого метода на основе данных того же пользователя */
            boolean isValid = jwtTokenGenerator.isValid(token, testUser);

            /* Все верно - валиден */
            assertThat(isValid).isTrue();
        }

        @Test
        void isValid_withNonMatchingEmail_shouldReturnFalse_Test() {
            /* Генерируем токен на основе данных пользователя */
            String token =
                    jwtTokenGenerator.getToken(testAnotherUser.getUserId(), testAnotherUser.getEmail(), testAnotherUser.getRole());

            /* Но, проверяем работу тестируемого метода на основе данных другого пользователя */
            boolean isValid = jwtTokenGenerator.isValid(token, testUser);

            /* Все верно - не валиден */
            assertThat(isValid).isFalse();
        }

        @Test
        void isValid_withMalformedToken_shouldThrowException_Test() {
            String malformedToken = "wah.malformed.token";

            assertThatThrownBy(() -> jwtTokenGenerator.isValid(malformedToken, testUser))
                    .isInstanceOf(Exception.class);
        }
    }

    @Nested
    @DisplayName("Набор тестов для *.authentication() метода - 'аутентификация' - в классе JwtTokenGenerator")
    class AuthenticationMethodTests {

        @SneakyThrows
        @Test
        void authentication_withValidTokenAndUser_shouldReturnJwtAuthUser_Test() {
            /* Генерим токен с известными данными */
            String token = jwtTokenGenerator.getToken(userId, email, role);

            /* "Аутентифицируем" пользователя с известными данными */
            JwtAuthUser authUser = jwtTokenGenerator.authentication(token, testUser);

            assertThat(authUser).isNotNull();
            assertThat(email).isEqualTo(authUser.getEmail());
            assertThat(role).isEqualTo(authUser.getRole());
        }

        @Test
        void authentication_withInvalidToken_shouldThrowAccessDeniedException_Test() {
            String invalidToken = "wah.invalid.token";

            assertThatThrownBy(() -> jwtTokenGenerator.authentication(invalidToken, testUser))
                    .isInstanceOf(Exception.class);
        }
    }
}