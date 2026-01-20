package me.oldboy.market.repository;

import me.oldboy.market.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с товарами в БД.
 * Предоставляет методы для выполнения CRUD операций и специализированных запросов к таблице товаров.
 * Наследует стандартные операции из {@link JpaRepository} и {@link CrudRepository}.
 *
 * @see Product
 * @see JpaRepository
 * @see CrudRepository
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, CrudRepository<Product, Long> {

    /**
     * Находит товар по идентификатору.
     *
     * @param entityId идентификатор товара
     * @return {@link Optional} с найденным товаром или пустой {@link Optional}
     */
    Optional<Product> findById(Long entityId);

    /**
     * Получает все товары из БД.
     *
     * @return список всех товаров
     */
    List<Product> findAll();

    /**
     * Находит товары по идентификатору категории.
     *
     * @param categoryId идентификатор категории
     * @return {@link Optional} со списком товаров категории или пустой {@link Optional}
     */
    @Query(value = "SELECT prod.* " +
            "FROM my_market.products AS prod " +
            "WHERE prod.category_id = :categoryId",
            nativeQuery = true)
    Optional<List<Product>> findByCategory(@Param("categoryId") Integer categoryId);

    /**
     * Находит товары по идентификатору бренда.
     *
     * @param brandId идентификатор бренда
     * @return {@link Optional} со списком товаров бренда или пустой {@link Optional}
     */
    @Query(value = "SELECT prod.* " +
            "FROM my_market.products AS prod " +
            "WHERE prod.brand_id = :brandId",
            nativeQuery = true)
    Optional<List<Product>> findByBrand(@Param("brandId") Integer brandId);

    /**
     * Находит товары по комбинации бренда и категории.
     *
     * @param brandId    идентификатор бренда
     * @param categoryId идентификатор категории
     * @return {@link Optional} со списком товаров или пустой {@link Optional}
     */
    @Query(value = "SELECT prod.* " +
            "FROM my_market.products AS prod " +
            "WHERE prod.brand_id = :brandId " +
            "AND prod.category_id = :categoryId",
            nativeQuery = true)
    Optional<List<Product>> findByBrandAndCategory(@Param("brandId") Integer brandId,
                                                   @Param("categoryId") Integer categoryId);
}