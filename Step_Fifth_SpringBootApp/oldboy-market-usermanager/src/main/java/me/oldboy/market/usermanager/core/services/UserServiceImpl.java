package me.oldboy.market.usermanager.core.services;

import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.logger.annotation.EnableLog;
import me.oldboy.market.usermanager.core.entity.User;
import me.oldboy.market.usermanager.core.exceptions.UserManagerModuleException;
import me.oldboy.market.usermanager.core.repository.UserRepository;
import me.oldboy.market.usermanager.core.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для управления пользователями.
 * Содержит бизнес-логику поиска пользователей и проверки уникальности email.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    /**
     * Репозиторий для работы с пользователями
     */
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Находит пользователя по идентификатору.
     *
     * @param entityId идентификатор пользователя
     * @return найденный пользователь или Optional.empty в противном случае
     */
    @Override
    @EnableLog
    public Optional<User> findById(Long entityId) {
        return userRepository
                .findById(entityId);
    }

    /**
     * Возвращает всех пользователей системы.
     *
     * @return список всех пользователей
     */
    @Override
    @EnableLog
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Находит пользователя по email адресу.
     * Email используется как уникальный идентификатор для входа в систему.
     *
     * @param email email адрес пользователя
     * @return найденный пользователь
     * @throws UserManagerModuleException если пользователь с указанным email не найден
     */
    @Override
    @EnableLog
    public User getUserByEmail(String email) throws UserManagerModuleException {
        return findAll()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny()
                .orElseThrow(() -> new UserManagerModuleException("User with email - " + email + " not found"));
    }

    /**
     * Проверяет уникальность email адреса в системе.
     *
     * @param email email для проверки
     * @return true - email уникален, false - email уже используется в системе
     */
    @Override
    @EnableLog
    public boolean isEmailUnique(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }
}