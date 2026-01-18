package me.oldboy.market.services;

import lombok.AllArgsConstructor;
import me.oldboy.market.entity.User;
import me.oldboy.market.exceptions.ServiceLayerException;
import me.oldboy.market.exceptions.UnexpectedIdServiceLayerException;
import me.oldboy.market.repository.UserRepository;
import me.oldboy.market.services.interfaces.UserService;

import java.util.List;

/**
 * Реализация сервиса для управления пользователями.
 * Содержит бизнес-логику поиска пользователей и проверки уникальности email.
 *
 * @see UserService
 * @see UserRepository
 * @see User
 */
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    /**
     * Репозиторий для работы с пользователями
     */
    private UserRepository userRepository;

    /**
     * Находит пользователя по идентификатору.
     *
     * @param entityId идентификатор пользователя
     * @return найденный пользователь
     * @throws UnexpectedIdServiceLayerException если пользователь с указанным ID не найден
     */
    @Override
    public User findById(Long entityId) throws UnexpectedIdServiceLayerException {
        return userRepository
                .findById(entityId)
                .orElseThrow(() -> new UnexpectedIdServiceLayerException("User with ID - " + entityId + " not found"));
    }

    /**
     * Возвращает всех пользователей системы.
     *
     * @return список всех пользователей
     */
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Находит пользователя по email адресу.
     * Email используется как уникальный идентификатор для входа в систему.
     *
     * @param email email адрес пользователя
     * @return найденный пользователь
     * @throws ServiceLayerException если пользователь с указанным email не найден
     */
    public User getUserByEmail(String email) throws ServiceLayerException {
        return findAll()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny()
                .orElseThrow(() -> new ServiceLayerException("User with email - " + email + " not found"));
    }

    /**
     * Проверяет уникальность email адреса в системе.
     *
     * @param email email для проверки
     * @return true - email уникален, false - email уже используется в системе
     */
    public boolean isEmailUnique(String email) {
        return userRepository.isEmailUnique(email);
    }
}