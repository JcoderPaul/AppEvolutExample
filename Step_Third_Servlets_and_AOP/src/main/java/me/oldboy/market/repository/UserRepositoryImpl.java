package me.oldboy.market.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.config.connection.DbConnectionPool;
import me.oldboy.market.entity.User;
import me.oldboy.market.entity.enums.Role;
import me.oldboy.market.repository.handlers.ConnectionCloseHandler;
import me.oldboy.market.repository.handlers.ConnectionRollbackHandler;
import me.oldboy.market.repository.interfaces.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с пользователями в реляционной базе данных.
 * Реализует операции CRUD через JDBC.
 *
 * @see UserRepository
 * @see User
 * @see Role
 */
@Slf4j
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    /**
     * Пул соединение с базой данных
     */
    private DbConnectionPool connectionPool;

    /**
     * SQL запрос для создания нового пользователя
     */
    private static final String CREATE_USER_SQL = """
            INSERT INTO my_market.users (email, user_pass, role)
            VALUES (?, ?, ?);
            """;

    /**
     * SQL запрос для удаления пользователя по идентификатору
     */
    private static final String DELETE_USER_BY_ID_SQL = """
            DELETE FROM my_market.users
            WHERE user_id = ?
            """;

    /**
     * SQL запрос для обновления данных пользователя
     */
    private static final String UPDATE_USER_SQL = """
            UPDATE my_market.users
            SET email = ?,
                user_pass = ?,
                role = ?
            WHERE user_id = ?
            """;

    /**
     * SQL запрос для получения всех пользователей
     */
    private static final String FIND_ALL_USERS_SQL = """
            SELECT user_id,
                   email,
                   user_pass, 
                   role
            FROM my_market.users
            """;

    /* SQL запрос на чтение с фильтрацией по ID / SQL read query filtered by ID */

    /**
     * SQL запрос для получения пользователя по его идентификатору
     */
    private static final String FIND_USER_BY_ID_SQL = FIND_ALL_USERS_SQL + """
            WHERE user_id = ?
            """;

    /* SQL запрос на чтение с фильтрацией по логину / SQL read query filtered by login */

    /**
     * SQL запрос для получения пользователя по его email-у
     */
    private static final String FIND_USER_BY_EMAIL_SQL =
            FIND_ALL_USERS_SQL + """
                    WHERE email = ?
                    """;

    /**
     * Создает нового пользователя в базе данных.
     *
     * @param user пользователь для создания без ID
     * @return Optional с созданным пользователем с присвоенным ID
     */
    @Override
    public Optional<User> create(User user) {
        User createdUser = null;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement prepareStatement = connection.prepareStatement(CREATE_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {

                prepareStatement.setString(1, user.getEmail());
                prepareStatement.setString(2, user.getPassword());
                prepareStatement.setString(3, user.getRole().name());

                prepareStatement.executeUpdate();

                try (ResultSet resultSet = prepareStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        createdUser = userBuild(resultSet);
                    }
                }
                connection.commit();
            }
        } catch (SQLException sqlException) {
            ConnectionRollbackHandler.handle(sqlException, "creation user", connection);
        } finally {
            ConnectionCloseHandler.handle(connection);
        }
        return Optional.ofNullable(createdUser);
    }

    /**
     * Находит пользователя по его идентификатору ID.
     *
     * @param entityId идентификатор пользователя ID
     * @return Optional с найденным пользователем, empty - если не найден
     */
    @Override
    public Optional<User> findById(Long entityId) {
        User foundUser = null;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_USER_BY_ID_SQL)) {
                prepareStatement.setLong(1, entityId);
                try (ResultSet queryResult = prepareStatement.executeQuery()) {
                    if (queryResult.next()) {
                        foundUser = userBuild(queryResult);
                    }
                }
                connection.commit();
            }
        } catch (SQLException sqlException) {
            ConnectionRollbackHandler.handle(sqlException, "find user by id", connection);
        } finally {
            ConnectionCloseHandler.handle(connection);
        }
        return Optional.ofNullable(foundUser);
    }

    /**
     * Обновляет данные пользователя в базе данных.
     *
     * @param updateData пользователь с обновленными данными
     * @return true - если пользователь обновлен, false - в противном случае
     */
    @Override
    public boolean update(User updateData) {
        Boolean isUserUpdated = false;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement prepareStatement = connection.prepareStatement(UPDATE_USER_SQL)) {

                prepareStatement.setString(1, updateData.getEmail());
                prepareStatement.setString(2, updateData.getPassword());
                prepareStatement.setString(3, updateData.getRole().name());
                prepareStatement.setLong(4, updateData.getUserId());

                isUserUpdated = prepareStatement.executeUpdate() > 0;
                connection.commit();
            }
        } catch (SQLException sqlException) {
            ConnectionRollbackHandler.handle(sqlException, "updating user", connection);
        } finally {
            ConnectionCloseHandler.handle(connection);
        }
        return isUserUpdated;
    }

    /**
     * Удаляет пользователя по его идентификатору ID.
     *
     * @param entityId идентификатор пользователя для удаления
     * @return true - если пользователь удален, false - в противном случае
     */
    @Override
    public boolean delete(Long entityId) {
        boolean isUserDeleted = false;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement prepareStatement = connection.prepareStatement(DELETE_USER_BY_ID_SQL)) {
                prepareStatement.setLong(1, entityId);
                isUserDeleted = prepareStatement.executeUpdate() > 0;
                connection.commit();
            }
        } catch (SQLException sqlException) {
            ConnectionRollbackHandler.handle(sqlException, "deleting user", connection);
        } finally {
            ConnectionCloseHandler.handle(connection);
        }
        return isUserDeleted;
    }

    /**
     * Возвращает всех пользователей из базы данных.
     *
     * @return список всех пользователей
     */
    @Override
    public List<User> findAll() {
        List<User> findAll = new ArrayList<>();
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_ALL_USERS_SQL)) {
                try (ResultSet queryResult = prepareStatement.executeQuery()) {
                    while (queryResult.next()) {
                        findAll.add(userBuild(queryResult));
                    }
                }
                connection.commit();
            }
        } catch (SQLException sqlException) {
            ConnectionRollbackHandler.handle(sqlException, "finding all users", connection);
        } finally {
            ConnectionCloseHandler.handle(connection);
        }
        return findAll;
    }

    /**
     * Находит пользователя по его email адресу.
     *
     * @param email email пользователя
     * @return Optional с найденным пользователем, empty - если не найден
     */
    @Override
    public Optional<User> findByEmail(String email) {
        User foundUser = null;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_USER_BY_EMAIL_SQL)) {
                prepareStatement.setString(1, email);
                try (ResultSet queryResult = prepareStatement.executeQuery()) {
                    if (queryResult.next()) {
                        foundUser = userBuild(queryResult);
                    }
                }
                connection.commit();
            }
        } catch (SQLException sqlException) {
            ConnectionRollbackHandler.handle(sqlException, "finding user by email", connection);
        } finally {
            ConnectionCloseHandler.handle(connection);
        }
        return Optional.ofNullable(foundUser);
    }

    /**
     * Проверяет уникальность email адреса.
     *
     * @param email email для проверки
     * @return true - если email уникален (не используется, нет в БД), false - если email уже занят (есть в БД)
     */
    @Override
    public boolean isEmailUnique(String email) {
        return findByEmail(email).isEmpty();
    }

    /**
     * Создает объект User из ResultSet.
     *
     * @param resultSet ResultSet с данными пользователя
     * @return объект User
     * @throws SQLException при ошибках чтения из ResultSet
     */
    private User userBuild(ResultSet resultSet) throws SQLException {
        return User.builder()
                .userId(resultSet.getLong("user_id"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("user_pass"))
                .role(Role.valueOf(resultSet.getString("role")))
                .build();
    }
}