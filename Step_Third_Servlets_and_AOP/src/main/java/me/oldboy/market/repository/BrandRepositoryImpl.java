package me.oldboy.market.repository;

import lombok.AllArgsConstructor;
import me.oldboy.market.config.connection.DbConnectionPool;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.repository.handlers.ConnectionCloseHandler;
import me.oldboy.market.repository.handlers.ConnectionRollbackHandler;
import me.oldboy.market.repository.interfaces.BrandRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с брэндами товара (продукта) в реляционной базе данных.
 * Реализует операции CRUD через JDBC.
 *
 * @see BrandRepository
 * @see Brand
 */
@AllArgsConstructor
public class BrandRepositoryImpl implements BrandRepository {
    /**
     * Пул соединение с базой данных
     */
    private DbConnectionPool connectionPool;

    /**
     * (SQL command) запрос на вставку (сохранение) Brand entity в таблицу brands БД
     */
    private static final String CREATE_BRANDS_SQL = """
            INSERT INTO my_market.brands (brand_name)
            VALUES (?);
            """;

    /**
     * (SQL command) запрос на удаление одной записи из таблицы brands БД по идентификатору
     */
    private static final String DELETE_BRAND_BY_ID_SQL = """
            DELETE FROM my_market.brands
            WHERE brand_id = ?
            """;

    /**
     * (SQL command) запрос на изменение одной записи в таблице brands БД
     */
    private static final String UPDATE_BRANDS_SQL = """
            UPDATE my_market.brands
            SET brand_name = ?
            WHERE brand_id = ?
            """;

    /**
     * (SQL command) запрос на получение всех записей из таблицы brands БД
     */
    private static final String FIND_ALL_BRANDS_SQL = """
            SELECT brand_id,
                   brand_name
            FROM my_market.brands
            """;

    /**
     * (SQL command) запрос на получение одной записи из таблицы brands БД по ее ID
     */
    private static final String FIND_BRAND_BY_ID_SQL = FIND_ALL_BRANDS_SQL + """
            WHERE brand_id = ?
            """;

    /**
     * (SQL command) запрос на получение одной записи из таблицы brands БД по полю brand_name (название брэнда)
     */
    private static final String FIND_BRAND_BY_NAME_SQL =
            FIND_ALL_BRANDS_SQL + """
                    WHERE brand_name = ?
                    """;

    /**
     * Создает новую запись о брэнде товара в базе данных.
     *
     * @param entityWithNoId новая сущность брэнда без идентификатора
     * @return Optional с созданным брэндом и присвоенным ему ID
     */
    @Override
    public Optional<Brand> create(Brand entityWithNoId) {
        Brand createdBrand = null;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement prepareStatement =
                         connection.prepareStatement(CREATE_BRANDS_SQL, Statement.RETURN_GENERATED_KEYS)) {
                prepareStatement.setString(1, entityWithNoId.getName());
                prepareStatement.executeUpdate();
                try (ResultSet resultSet = prepareStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        createdBrand = brandBuild(resultSet);
                    }
                }
                connection.commit();
            }
        } catch (SQLException sqlException) {
            ConnectionRollbackHandler.handle(sqlException, "creating brand", connection);
        } finally {
            ConnectionCloseHandler.handle(connection);
        }
        return Optional.ofNullable(createdBrand);
    }

    /**
     * Находит брэнд по идентификатору.
     *
     * @param entityId идентификатор брэнда в таблице БД
     * @return Optional с найденным брэндом, empty - если не найден
     */
    @Override
    public Optional<Brand> findById(Integer entityId) {
        Brand foundBrand = null;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_BRAND_BY_ID_SQL)) {
                prepareStatement.setLong(1, entityId);
                try (ResultSet queryResult = prepareStatement.executeQuery()) {
                    if (queryResult.next()) {
                        foundBrand = brandBuild(queryResult);
                    }
                }
                connection.commit();
            }
        } catch (SQLException sqlException) {
            ConnectionRollbackHandler.handle(sqlException, "finding brand by id", connection);
        } finally {
            ConnectionCloseHandler.handle(connection);
        }
        return Optional.ofNullable(foundBrand);
    }

    /**
     * Обновляет данные брэнда в базе данных.
     *
     * @param updateData брэнд с обновленными данными
     * @return true - брэнд обновлен, false - в противном случае
     */
    @Override
    public boolean update(Brand updateData) {
        Boolean isUpdated = false;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement prepareStatement = connection.prepareStatement(UPDATE_BRANDS_SQL)) {

                prepareStatement.setString(1, updateData.getName());
                prepareStatement.setLong(2, updateData.getId());

                isUpdated = prepareStatement.executeUpdate() > 0;
                connection.commit();
            }
        } catch (SQLException sqlException) {
            ConnectionRollbackHandler.handle(sqlException, "updating brand", connection);
        } finally {
            ConnectionCloseHandler.handle(connection);
        }
        return isUpdated;
    }

    /**
     * Удаляет брэнд по идентификатору из БД.
     *
     * @param entityId идентификатор брэнда для удаления
     * @return true - брэнд удален, false - в противном случае
     */
    @Override
    public boolean delete(Integer entityId) {
        boolean isDeleted = false;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement prepareStatement = connection.prepareStatement(DELETE_BRAND_BY_ID_SQL)) {
                prepareStatement.setLong(1, entityId);
                isDeleted = prepareStatement.executeUpdate() > 0;
                connection.commit();
            }
        } catch (SQLException sqlException) {
            ConnectionRollbackHandler.handle(sqlException, "deleting brand", connection);
        } finally {
            ConnectionCloseHandler.handle(connection);
        }
        return isDeleted;
    }

    /**
     * Возвращает все брэнды из базы данных.
     *
     * @return список всех доступных брэндов из БД
     */
    @Override
    public List<Brand> findAll() {
        List<Brand> findAll = new ArrayList<>();
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_ALL_BRANDS_SQL)) {
                try (ResultSet queryResult = prepareStatement.executeQuery()) {
                    while (queryResult.next()) {
                        findAll.add(brandBuild(queryResult));
                    }
                }
                connection.commit();
            }
        } catch (SQLException sqlException) {
            ConnectionRollbackHandler.handle(sqlException, "finding all brand", connection);
        } finally {
            ConnectionCloseHandler.handle(connection);
        }
        return findAll;
    }

    /**
     * Находит брэнд по его названию.
     *
     * @param brandName название брэнда
     * @return Optional с данными по брэнду, empty - если брэнд не найден
     */
    public Optional<Brand> findByName(String brandName) {
        Brand foundBrand = null;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_BRAND_BY_NAME_SQL)) {
                prepareStatement.setString(1, brandName);
                try (ResultSet queryResult = prepareStatement.executeQuery()) {
                    if (queryResult.next()) {
                        foundBrand = brandBuild(queryResult);
                    }
                }
                connection.commit();
            }
        } catch (SQLException sqlException) {
            ConnectionRollbackHandler.handle(sqlException, "finding brand by name", connection);
        } finally {
            ConnectionCloseHandler.handle(connection);
        }
        return Optional.ofNullable(foundBrand);
    }

    /**
     * Создает объект Brand из ResultSet.
     *
     * @param resultSet ResultSet с данными по брэнду
     * @return объект Brand
     * @throws SQLException при ошибках чтения из ResultSet
     */
    private Brand brandBuild(ResultSet resultSet) throws SQLException {
        return Brand.builder()
                .id(resultSet.getInt("brand_id"))
                .name(resultSet.getString("brand_name"))
                .build();
    }
}