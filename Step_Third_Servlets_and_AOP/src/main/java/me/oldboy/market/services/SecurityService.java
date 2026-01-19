package me.oldboy.market.services;

import me.oldboy.market.dto.user.UserReadDto;
import me.oldboy.market.entity.User;
import me.oldboy.market.entity.enums.Role;
import me.oldboy.market.exceptions.SecurityServiceException;
import me.oldboy.market.mapper.UserMapper;
import me.oldboy.market.repository.interfaces.UserRepository;
import me.oldboy.market.security.JwtAuthResponse;
import me.oldboy.market.security.JwtTokenGenerator;

/**
 * Сервис безопасности, отвечающий за аутентификацию пользователей и генерацию JWT токенов.
 * Обеспечивает процесс входа пользователя в систему и выдачу токенов доступа.
 *
 * @see JwtTokenGenerator
 * @see UserRepository
 * @see JwtAuthResponse
 * @see SecurityServiceException
 */
public class SecurityService {
    /**
     * Генератор JWT токенов для создания токенов доступа
     */
    private final JwtTokenGenerator jwtTokenGenerator;
    /**
     * Репозиторий для работы с данными пользователей
     */
    private final UserRepository userRepository;

    /**
     * Создает новый экземпляр SecurityService с указанными зависимостями.
     *
     * @param userRepository    репозиторий для доступа к данным пользователей
     * @param jwtTokenGenerator генератор JWT токенов для создания токенов аутентификации
     */
    public SecurityService(UserRepository userRepository, JwtTokenGenerator jwtTokenGenerator) {
        this.userRepository = userRepository;
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    /**
     * Выполняет аутентификацию пользователя и генерирует JWT токен при успешном входе.
     *
     * @param email    email пользователя для аутентификации
     * @param password пароль пользователя для проверки
     * @return объект {@link JwtAuthResponse} содержащий данные пользователя и JWT токен
     * @throws SecurityServiceException если:
     *                                  - Пользователь с указанным email не найден
     *                                  - Предоставленный пароль не совпадает с паролем в базе данных
     */
    public JwtAuthResponse loginUser(String email, String password) {
        User mayBeUserInBase = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new SecurityServiceException("User with email - " + email + " not found"));

        if (!mayBeUserInBase.getPassword().equals(password)) {
            throw new SecurityServiceException("Wrong password!");
        }

        UserReadDto foundUser = UserMapper.INSTANCE.mapToUserReadDto(mayBeUserInBase);

        String token = jwtTokenGenerator.getToken(foundUser.userId(),
                foundUser.email(),
                Role.valueOf(foundUser.role()));

        JwtAuthResponse jwtAuthResponse =
                new JwtAuthResponse(foundUser.userId(), foundUser.email(), Role.valueOf(foundUser.role()), token);

        return jwtAuthResponse;
    }
}