package me.oldboy.market.productmanager.core.repository;

import me.oldboy.market.productmanager.core.entity.prod_species.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с категориями товаров в базе данных.
 * Предоставляет методы для выполнения CRUD операций и специализированных запросов к таблице категорий.
 * Наследует стандартные операции из {@link JpaRepository} и {@link CrudRepository}.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>, CrudRepository<Category, Integer> {

    /**
     * Находит категорию по идентификатору.
     *
     * @param entityId идентификатор категории
     * @return {@link Optional} с найденной категорией или пустой {@link Optional}
     */
    Optional<Category> findById(Integer entityId);

    /**
     * Получает все категории из базы данных.
     *
     * @return список всех категорий
     */
    List<Category> findAll();

    /**
     * Находит категорию по названию.
     * Выполняет нативный SQL запрос к таблице категорий для поиска по названию.
     *
     * @param categoryName название категории для поиска
     * @return {@link Optional} с найденной категорией или пустой {@link Optional}
     */
    @Query(value = "SELECT cat.* " +
            "FROM my_market.categories AS cat " +
            "WHERE cat.category_name = :categoryName",
            nativeQuery = true)
    Optional<Category> findByName(@Param("categoryName") String categoryName);
}