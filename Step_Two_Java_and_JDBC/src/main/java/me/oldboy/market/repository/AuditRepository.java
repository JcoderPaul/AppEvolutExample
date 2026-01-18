package me.oldboy.market.repository;

import lombok.AllArgsConstructor;
import lombok.Setter;
import me.oldboy.market.entity.Audit;
import me.oldboy.market.entity.enums.Action;
import me.oldboy.market.entity.enums.Status;
import me.oldboy.market.repository.interfaces.AuditDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
Хотя для целей аудита достаточно метода добавления записей в БД
и их просмотра соответственно, но тут присутствует полный CRUD
функционал, на возможную перспективу.
*/

/**
 * Репозиторий для работы с записями аудита в реляционной базе данных.
 * Реализует операции CRUD через JDBC.
 *
 * @see AuditDao
 * @see Audit
 * @see Action
 * @see Status
 */
@AllArgsConstructor
public class AuditRepository implements AuditDao {
    /**
     * JDBC соединение с базой данных
     */
    @Setter
    private Connection connection;

    /**
     * (SQL command) запрос на вставку (сохранение) Audit entity в таблицу audits БД
     */
    private static final String CREATE_AUDITS_SQL = """
            INSERT INTO my_market.audits (created_at, created_by, action, is_success, auditable_record)
            VALUES (?, ?, ?, ?, ?);
            """;

    /**
     * (SQL command) запрос на удаление одной записи из таблицы audits БД по идентификатору
     */
    private static final String DELETE_AUDIT_BY_ID_SQL = """
            DELETE FROM my_market.audits
            WHERE id = ?
            """;

    /**
     * (SQL command) запрос на изменение одной записи в таблице audits БД
     */
    private static final String UPDATE_AUDIT_SQL = """
            UPDATE my_market.audits
            SET created_at = ?,
                created_by = ?,
                action = ?,
                is_success = ?,
                auditable_record = ?                
            WHERE id = ?
            """;

    /**
     * (SQL command) запрос на получение всех записей из таблицы audits БД
     */
    private static final String FIND_ALL_AUDITS_SQL = """
            SELECT id,
                   created_at,
                   created_by,
                   action,
                   is_success,
                   auditable_record
            FROM my_market.audits
            """;

    /**
     * (SQL command) запрос на получение одной записи из таблицы audits БД по ее ID
     */
    private static final String FIND_AUDIT_BY_ID_SQL = FIND_ALL_AUDITS_SQL + """
            WHERE id = ?
            """;

    /**
     * (SQL command) запрос на получение всех записей из таблицы audits БД по полю created_by (кто внес изменения - email)
     */
    private static final String FIND_AUDITS_BY_USER_EMAIL_SQL =
            FIND_ALL_AUDITS_SQL + """
                    WHERE created_by = ?
                    """;

    /**
     * Создает новую запись аудита в базе данных.
     *
     * @param entityWithNoId запись аудита без идентификатора
     * @return Optional с созданной записью с присвоенным ID
     */
    @Override
    public Optional<Audit> create(Audit entityWithNoId) {
        Audit createdEntityWithId = null;
        try (PreparedStatement prepareStatement =
                     connection.prepareStatement(CREATE_AUDITS_SQL, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatement.setObject(1, entityWithNoId.getCreateAt());
            prepareStatement.setString(2, entityWithNoId.getCreateBy());
            prepareStatement.setString(3, entityWithNoId.getAction().name());
            prepareStatement.setString(4, entityWithNoId.getIsSuccess().name());
            prepareStatement.setString(5, entityWithNoId.getAuditableRecord());

            prepareStatement.executeUpdate();

            try (ResultSet resultSet = prepareStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    createdEntityWithId = auditBuild(resultSet);
                }
            }
            connection.commit();
        } catch (SQLException sqlException) {
            try {
                connection.rollback();
                System.out.println("Произошел rollback транзакции");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            sqlException.printStackTrace();
        }
        return Optional.ofNullable(createdEntityWithId);
    }

    /**
     * Находит запись аудита по идентификатору.
     *
     * @param entityId идентификатор записи аудита
     * @return Optional с найденной записью или empty если не найдена
     */
    @Override
    public Optional<Audit> findById(Long entityId) {
        Audit foundEntity = null;
        try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_AUDIT_BY_ID_SQL)) {
            prepareStatement.setLong(1, entityId);
            try (ResultSet queryResult = prepareStatement.executeQuery()) {
                if (queryResult.next()) {
                    foundEntity = auditBuild(queryResult);
                }
            }
            connection.commit();
        } catch (SQLException sqlException) {
            try {
                connection.rollback();
                System.out.println("Произошел rollback транзакции");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            sqlException.printStackTrace();
        }
        return Optional.ofNullable(foundEntity);
    }

    /**
     * Обновляет запись аудита в базе данных.
     *
     * @param updateData запись аудита с обновленными данными
     * @return true - запись обновлена, false - в противном случае
     */
    @Override
    public boolean update(Audit updateData) {
        Boolean isUpdated = false;
        try (PreparedStatement prepareStatement = connection.prepareStatement(UPDATE_AUDIT_SQL)) {
            prepareStatement.setObject(1, updateData.getCreateAt());
            prepareStatement.setString(2, updateData.getCreateBy());
            prepareStatement.setString(3, updateData.getAction().name());
            prepareStatement.setString(4, updateData.getIsSuccess().name());
            prepareStatement.setString(5, updateData.getAuditableRecord());
            prepareStatement.setLong(6, updateData.getId());

            isUpdated = prepareStatement.executeUpdate() > 0;
            connection.commit();
        } catch (SQLException sqlException) {
            try {
                connection.rollback();
                System.out.println("Произошел rollback транзакции");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            sqlException.printStackTrace();
        }
        return isUpdated;
    }

    /**
     * Удаляет запись аудита по идентификатору.
     *
     * @param entityId идентификатор записи для удаления
     * @return true - запись удалена, false - в противном случае
     */
    @Override
    public boolean delete(Long entityId) {
        boolean isDeleted = false;
        try (PreparedStatement prepareStatement = connection.prepareStatement(DELETE_AUDIT_BY_ID_SQL)) {
            prepareStatement.setLong(1, entityId);
            isDeleted = prepareStatement.executeUpdate() > 0;
            connection.commit();
        } catch (SQLException sqlException) {
            try {
                connection.rollback();
                System.out.println("Произошел rollback транзакции");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            sqlException.printStackTrace();
        }
        return isDeleted;
    }

    /**
     * Возвращает все записи аудита из базы данных.
     *
     * @return список всех записей аудита
     */
    @Override
    public List<Audit> findAll() {
        List<Audit> findAll = new ArrayList<>();
        try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_ALL_AUDITS_SQL)) {
            try (ResultSet queryResult = prepareStatement.executeQuery()) {
                while (queryResult.next()) {
                    findAll.add(auditBuild(queryResult));
                }
            }
            connection.commit();
        } catch (SQLException sqlException) {
            try {
                connection.rollback();
                System.out.println("Произошел rollback транзакции");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            sqlException.printStackTrace();
        }
        return findAll;
    }

    /**
     * Находит все записи аудита по email пользователя.
     *
     * @param email email пользователя
     * @return Optional со списком записей, empty - если записи не найдены
     */
    public Optional<List<Audit>> findByCreationUserEmail(String email) {
        List<Audit> foundRecords = new ArrayList<>();
        try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_AUDITS_BY_USER_EMAIL_SQL)) {
            prepareStatement.setString(1, email);
            try (ResultSet queryResult = prepareStatement.executeQuery()) {
                while (queryResult.next()) {
                    foundRecords.add(auditBuild(queryResult));
                }
            }
            connection.commit();
        } catch (SQLException sqlException) {
            try {
                connection.rollback();
                System.out.println("Произошел rollback транзакции");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            sqlException.printStackTrace();
        }
        return Optional.ofNullable(foundRecords);
    }

    /**
     * Создает объект Audit из ResultSet.
     *
     * @param resultSet ResultSet с данными записи аудита
     * @return объект Audit
     * @throws SQLException при ошибках чтения из ResultSet
     */
    private Audit auditBuild(ResultSet resultSet) throws SQLException {
        return Audit.builder()
                .id(resultSet.getLong("id"))
                .createAt(resultSet.getObject("created_at", Timestamp.class).toLocalDateTime())
                .createBy(resultSet.getString("created_by"))
                .action(Action.valueOf(resultSet.getString("action")))
                .isSuccess(Status.valueOf(resultSet.getString("is_success")))
                .auditableRecord(resultSet.getString("auditable_record"))
                .build();
    }
}