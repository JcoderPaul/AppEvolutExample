package me.oldboy.market.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.config.connection.DbConnectionPool;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.repository.handlers.ConnectionCloseHandler;
import me.oldboy.market.repository.handlers.ConnectionRollbackHandler;
import me.oldboy.market.repository.interfaces.ProductRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с товарами (продуктами) в реляционной базе данных.
 * Реализует операции CRUD через JDBC.
 *
 * @see ProductRepository
 * @see Product
 * @see Category
 * @see Brand
 */
@Slf4j
@AllArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    /**
     * Пул соединение с базой данных
     */
    private DbConnectionPool connectionPool;

    /**
     * (SQL command) запрос на вставку (сохранение) Product entity в таблицу products БД
     */
    private static final String CREATE_PRODUCTS_SQL = """
            INSERT INTO my_market.products (product_name, price, category_id, brand_id, description, stock_quantity, creation_at, modified_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?);
            """;

    /**
     * (SQL command) запрос на удаление одной записи из таблицы products БД
     */
    private static final String DELETE_PRODUCT_BY_ID_SQL = """
            DELETE FROM my_market.products
            WHERE id = ?
            """;

    /**
     * (SQL command) запрос на изменение одной записи в таблице products БД (ключевым элементом является ID продукта)
     */
    private static final String UPDATE_PRODUCT_SQL = """
            UPDATE my_market.products
            SET product_name = ?,
                price = ?,
                category_id = ?,
                brand_id = ?,
                description = ?,
                stock_quantity = ?,
                creation_at = ?,
                modified_at = ?
            WHERE id = ?
            """;

    /**
     * (SQL command) запрос на получение всех записей из таблицы products БД
     */
    private static final String FIND_ALL_PRODUCTS_SQL = """
            SELECT id,
                   product_name,
                   price,
                   category_id,
                   brand_id,
                   description,
                   stock_quantity,
                   creation_at,
                   modified_at
            FROM my_market.products
            """;

    /**
     * (SQL command) запрос на получение одной записи из таблицы products БД по ее ID
     */
    private static final String FIND_PRODUCT_BY_ID_SQL = FIND_ALL_PRODUCTS_SQL + """
            WHERE id = ?
            """;

    /**
     * (SQL command) запрос на получение всех записей из таблицы products БД по полю category_id (получить все записи по категории)
     */
    private static final String FIND_PRODUCTS_BY_CATEGORY_ID_SQL =
            FIND_ALL_PRODUCTS_SQL + """
                    WHERE category_id = ?
                    """;

    /**
     * (SQL command) запрос на получение всех записей из таблицы products БД по полю category_id (получить все записи по категории)
     */
    private static final String FIND_PRODUCTS_BY_BRAND_ID_SQL =
            FIND_ALL_PRODUCTS_SQL + """
                    WHERE brand_id = ?
                    """;

    /**
     * Создает новую запись о товаре в базе данных.
     *
     * @param entityWithNoId новая сущность товара Product без идентификатора
     * @return Optional с созданным товаром и присвоенным ему ID в БД
     */
    @Override
    public Optional<Product> create(Product entityWithNoId) {
        Product createdEntityWithId = null;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement prepareStatement = connection.prepareStatement(CREATE_PRODUCTS_SQL, Statement.RETURN_GENERATED_KEYS)) {

                prepareStatement.setString(1, entityWithNoId.getName());
                prepareStatement.setDouble(2, entityWithNoId.getPrice());
                prepareStatement.setInt(3, entityWithNoId.getCategoryId());
                prepareStatement.setInt(4, entityWithNoId.getBrandId());
                prepareStatement.setString(5, entityWithNoId.getDescription());
                prepareStatement.setInt(6, entityWithNoId.getStockQuantity());
                prepareStatement.setObject(7, entityWithNoId.getCreationAt());
                prepareStatement.setObject(8, entityWithNoId.getModifiedAt());

                prepareStatement.executeUpdate();

                try (ResultSet resultSet = prepareStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        createdEntityWithId = productBuild(resultSet);
                    }
                }
                connection.commit();
            }
        } catch (SQLException sqlException) {
            ConnectionRollbackHandler.handle(sqlException, "creating product", connection);
        } finally {
            ConnectionCloseHandler.handle(connection);
        }
        return Optional.ofNullable(createdEntityWithId);
    }

    /**
     * Находит товар по идентификатору.
     *
     * @param entityId идентификатор товара в таблице БД
     * @return Optional с найденным товаром, empty - если товар не найден
     */
    @Override
    public Optional<Product> findById(Long entityId) {
        Product foundEntity = null;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_PRODUCT_BY_ID_SQL)) {
                prepareStatement.setLong(1, entityId);
                try (ResultSet queryResult = prepareStatement.executeQuery()) {
                    if (queryResult.next()) {
                        foundEntity = productBuild(queryResult);
                    }
                }
                connection.commit();
            }
        } catch (SQLException sqlException) {
            ConnectionRollbackHandler.handle(sqlException, "finding product by id", connection);
        } finally {
            ConnectionCloseHandler.handle(connection);
        }
        return Optional.ofNullable(foundEntity);
    }

    /**
     * Обновляет данные по товару в базе данных.
     *
     * @param updateData товар с обновленными данными
     * @return true - товар обновлен, false - в противном случае
     */
    @Override
    public boolean update(Product updateData) {
        Boolean isUpdated = false;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement prepareStatement = connection.prepareStatement(UPDATE_PRODUCT_SQL)) {

                prepareStatement.setString(1, updateData.getName());
                prepareStatement.setDouble(2, updateData.getPrice());
                prepareStatement.setInt(3, updateData.getCategoryId());
                prepareStatement.setInt(4, updateData.getBrandId());
                prepareStatement.setString(5, updateData.getDescription());
                prepareStatement.setInt(6, updateData.getStockQuantity());
                prepareStatement.setObject(7, updateData.getCreationAt());
                prepareStatement.setObject(8, updateData.getModifiedAt());
                prepareStatement.setLong(9, updateData.getId());

                isUpdated = prepareStatement.executeUpdate() > 0;
                connection.commit();
            }
        } catch (SQLException sqlException) {
            ConnectionRollbackHandler.handle(sqlException, "updating product", connection);
        } finally {
            ConnectionCloseHandler.handle(connection);
        }
        return isUpdated;
    }

    /**
     * Удаляет товар по идентификатору из БД.
     *
     * @param entityId идентификатор товара для удаления
     * @return true - товар удален, false - в противном случае
     */
    @Override
    public boolean delete(Long entityId) {
        boolean isDeleted = false;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement prepareStatement = connection.prepareStatement(DELETE_PRODUCT_BY_ID_SQL)) {
                prepareStatement.setLong(1, entityId);
                isDeleted = prepareStatement.executeUpdate() > 0;
                connection.commit();
            }
        } catch (SQLException sqlException) {
            ConnectionRollbackHandler.handle(sqlException, "deleting product", connection);
        } finally {
            ConnectionCloseHandler.handle(connection);
        }
        return isDeleted;
    }

    /**
     * Возвращает все товары из базы данных.
     *
     * @return список всех доступных товаров из БД
     */
    @Override
    public List<Product> findAll() {
        List<Product> findAll = new ArrayList<>();
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_ALL_PRODUCTS_SQL)) {
                try (ResultSet queryResult = prepareStatement.executeQuery()) {
                    while (queryResult.next()) {
                        findAll.add(productBuild(queryResult));
                    }
                }
                connection.commit();
            }
        } catch (SQLException sqlException) {
            ConnectionRollbackHandler.handle(sqlException, "finding all products", connection);
        } finally {
            ConnectionCloseHandler.handle(connection);
        }
        return findAll;
    }

    /**
     * Находит все товары по идентификатору категории товара.
     *
     * @param categoryId идентификатор категории товара
     * @return Optional - с данными по всем найденным товарам относящимся к искомой категории,
     * Optional empty list - если по заданной категории не найдено ни одного товара
     */
    @Override
    public Optional<List<Product>> findByCategoryId(Integer categoryId) {
        List<Product> foundRecords = new ArrayList<>();
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_PRODUCTS_BY_CATEGORY_ID_SQL)) {
                prepareStatement.setInt(1, categoryId);
                try (ResultSet queryResult = prepareStatement.executeQuery()) {
                    while (queryResult.next()) {
                        foundRecords.add(productBuild(queryResult));
                    }
                }
                connection.commit();
            }
        } catch (SQLException sqlException) {
            ConnectionRollbackHandler.handle(sqlException, "finding all product by category", connection);
        } finally {
            ConnectionCloseHandler.handle(connection);
        }
        return Optional.ofNullable(foundRecords);
    }

    /**
     * Находит все товары по идентификатору брэнда товара.
     *
     * @param brandId идентификатор брэнда товара
     * @return Optional - с данными по всем найденным товарам относящимся к искомому брэнду,
     * Optional empty list - если по заданному брэнду не найдено ни одного товара
     */
    @Override
    public Optional<List<Product>> findByBrandId(Integer brandId) {
        List<Product> foundRecords = new ArrayList<>();
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_PRODUCTS_BY_BRAND_ID_SQL)) {
                prepareStatement.setLong(1, brandId);
                try (ResultSet queryResult = prepareStatement.executeQuery()) {
                    while (queryResult.next()) {
                        foundRecords.add(productBuild(queryResult));
                    }
                }
                connection.commit();
            }
        } catch (SQLException sqlException) {
            ConnectionRollbackHandler.handle(sqlException, "finding all product by brand", connection);
        } finally {
            ConnectionCloseHandler.handle(connection);
        }
        return Optional.ofNullable(foundRecords);
    }

    /**
     * Создает объект Product из ResultSet.
     *
     * @param resultSet ResultSet с данными по товару (продукту, Product)
     * @return объект Product
     * @throws SQLException при ошибках чтения из ResultSet
     */
    private Product productBuild(ResultSet resultSet) throws SQLException {
        return Product.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("product_name"))
                .price(resultSet.getDouble("price"))
                .categoryId(resultSet.getInt("category_id"))
                .brandId(resultSet.getInt("brand_id"))
                .description(resultSet.getString("description"))
                .stockQuantity(resultSet.getInt("stock_quantity"))
                .creationAt(resultSet.getObject("creation_at", Timestamp.class).toLocalDateTime())
                .modifiedAt(resultSet.getObject("modified_at", Timestamp.class).toLocalDateTime())
                .build();
    }
}