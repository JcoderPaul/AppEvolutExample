package me.oldboy.market.repository;

import lombok.AllArgsConstructor;
import lombok.Setter;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.repository.interfaces.CategoryDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с категориями товара (продукта) в реляционной базе данных.
 * Реализует операции CRUD через JDBC.
 *
 * @see CategoryDao
 * @see Category
 */
@AllArgsConstructor
public class CategoryRepository implements CategoryDao {
    /**
     * JDBC соединение с базой данных
     */
    @Setter
    private Connection connection;

    /**
     * (SQL command) запрос на вставку (сохранение) Category entity в таблицу categories БД
     */
    private static final String CREATE_CATEGORY_SQL = """
            INSERT INTO my_market.categories (category_name)
            VALUES (?);
            """;

    /**
     * (SQL command) запрос на удаление одной записи из таблицы categories БД по ее ID
     */
    private static final String DELETE_CATEGORY_BY_ID_SQL = """
            DELETE FROM my_market.categories
            WHERE category_id = ?
            """;

    /**
     * (SQL command) запрос на изменение одной записи в таблице categories БД
     */
    private static final String UPDATE_CATEGORY_SQL = """
            UPDATE my_market.categories
            SET category_name = ?
            WHERE category_id = ?
            """;

    /**
     * (SQL command) запрос на получение всех записей из таблицы categories БД
     */
    private static final String FIND_ALL_CATEGORIES_SQL = """
            SELECT category_id,
                   category_name
            FROM my_market.categories
            """;

    /**
     * (SQL command) запрос на получение одной записи из таблицы categories БД по ее ID
     */
    private static final String FIND_CATEGORY_BY_ID_SQL =
            FIND_ALL_CATEGORIES_SQL + """
                    WHERE category_id = ?
                    """;

    /**
     * (SQL command) запрос на получение одной записи из таблицы categories БД по полю category_name (название категории)
     */
    private static final String FIND_CATEGORY_BY_NAME_SQL =
            FIND_ALL_CATEGORIES_SQL + """
                    WHERE category_name = ?
                    """;

    /**
     * Создает новую запись о категории товара в базе данных.
     *
     * @param entityWithNoId новая сущность категория без идентификатора
     * @return Optional с созданной категорией и присвоенным ей ID
     */
    @Override
    public Optional<Category> create(Category entityWithNoId) {
        Category createdCategory = null;
        try (PreparedStatement prepareStatement =
                     connection.prepareStatement(CREATE_CATEGORY_SQL, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatement.setString(1, entityWithNoId.getName());

            prepareStatement.executeUpdate();

            try (ResultSet resultSet = prepareStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    createdCategory = categoryBuild(resultSet);
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
        return Optional.ofNullable(createdCategory);
    }

    /**
     * Находит категорию по идентификатору.
     *
     * @param entityId идентификатор категории в таблице БД
     * @return Optional с найденной категорией, empty - если не найдена
     */
    @Override
    public Optional<Category> findById(Integer entityId) {
        Category foundCategory = null;
        try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_CATEGORY_BY_ID_SQL)) {
            prepareStatement.setLong(1, entityId);
            try (ResultSet queryResult = prepareStatement.executeQuery()) {
                if (queryResult.next()) {
                    foundCategory = categoryBuild(queryResult);
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
        return Optional.ofNullable(foundCategory);
    }

    /**
     * Обновляет данные по категории в базе данных.
     *
     * @param updateData категория с обновленными данными
     * @return true - категория обновлена, false - в противном случае
     */
    @Override
    public boolean update(Category updateData) {
        Boolean isUpdated = false;
        try (PreparedStatement prepareStatement = connection.prepareStatement(UPDATE_CATEGORY_SQL)) {
            prepareStatement.setString(1, updateData.getName());
            prepareStatement.setLong(2, updateData.getId());

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
     * Удаляет категорию по идентификатору из БД.
     *
     * @param entityId идентификатор категории для удаления
     * @return true - категория удалена, false - в противном случае
     */
    @Override
    public boolean delete(Integer entityId) {
        boolean isDeleted = false;
        try (PreparedStatement prepareStatement = connection.prepareStatement(DELETE_CATEGORY_BY_ID_SQL)) {
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
     * Возвращает все категории из базы данных.
     *
     * @return список всех доступных категорий из БД
     */
    @Override
    public List<Category> findAll() {
        List<Category> findAll = new ArrayList<>();
        try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_ALL_CATEGORIES_SQL)) {
            try (ResultSet queryResult = prepareStatement.executeQuery()) {
                while (queryResult.next()) {
                    findAll.add(categoryBuild(queryResult));
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
     * Находит категорию по его названию.
     *
     * @param categoryName название категории
     * @return Optional с данными по категории, empty - если категория не найдена
     */
    public Optional<Category> findByName(String categoryName) {
        Category foundCategory = null;
        try (PreparedStatement prepareStatement = connection.prepareStatement(FIND_CATEGORY_BY_NAME_SQL)) {
            prepareStatement.setString(1, categoryName);
            try (ResultSet queryResult = prepareStatement.executeQuery()) {
                if (queryResult.next()) {
                    foundCategory = categoryBuild(queryResult);
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
        return Optional.ofNullable(foundCategory);
    }

    /**
     * Создает объект Category из ResultSet.
     *
     * @param resultSet ResultSet с данными по категории
     * @return объект Category
     * @throws SQLException при ошибках чтения из ResultSet
     */
    private Category categoryBuild(ResultSet resultSet) throws SQLException {
        return Category.builder()
                .id(resultSet.getInt("category_id"))
                .name(resultSet.getString("category_name"))
                .build();
    }
}