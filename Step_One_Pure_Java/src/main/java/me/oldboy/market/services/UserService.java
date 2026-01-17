package me.oldboy.market.services;

import lombok.AllArgsConstructor;
import me.oldboy.market.entity.User;
import me.oldboy.market.exceptions.UserServiceException;
import me.oldboy.market.repository.UserRepository;

/**
 * Сервисный класс для управления пользователями. Предоставляет
 * бизнес-логику для операций с пользователями и обработку ошибок
 * на уровне приложения (хотя в текущей реализации нам достаточно
 * просмотрово-поискового функционала).
 *
 * @see User
 * @see UserRepository
 * @see UserServiceException
 */
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;

    /**
     * Находит пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return найденный пользователь
     * @throws UserServiceException если пользователь с указанным ID не найден
     */
    public User getUserById(Long userId) throws UserServiceException {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new UserServiceException("User with ID - " + userId + " not found"));
    }

    /**
     * Находит пользователя по email адресу.
     * Email используется как уникальный идентификатор для входа в систему.
     *
     * @param email email адрес пользователя
     * @return найденный пользователь
     * @throws UserServiceException если пользователь с указанным email не найден
     */
    public User getUserByEmail(String email) throws UserServiceException {
        return userRepository
                .findUserByEmail(email)
                .orElseThrow(() -> new UserServiceException("User with email - " + email + " not found"));
    }
}