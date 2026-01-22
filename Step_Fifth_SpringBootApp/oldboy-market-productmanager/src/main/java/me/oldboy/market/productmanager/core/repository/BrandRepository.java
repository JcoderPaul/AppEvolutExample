package me.oldboy.market.productmanager.core.repository;

import me.oldboy.market.productmanager.core.entity.prod_species.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с брендами в БД.
 * Предоставляет методы для выполнения CRUD операций и специализированных запросов к таблице брендов.
 * Наследует стандартные операции из {@link JpaRepository} и {@link CrudRepository}.
 */
@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer>, CrudRepository<Brand, Integer> {

    /**
     * Находит бренд по идентификатору.
     *
     * @param entityId идентификатор бренда
     * @return {@link Optional} с найденным брендом или пустой {@link Optional}
     */
    Optional<Brand> findById(Integer entityId);

    /**
     * Получает все бренды из БД.
     *
     * @return список всех брендов
     */
    List<Brand> findAll();

    /**
     * Находит бренд по названию.
     * Выполняет нативный SQL запрос к таблице брендов для поиска по названию.
     *
     * @param brandName название бренда для поиска
     * @return {@link Optional} с найденным брендом или пустой {@link Optional}
     */
    @Query(value = "SELECT br.* " +
            "FROM my_market.brands AS br " +
            "WHERE br.brand_name = :categoryName",
            nativeQuery = true)
    Optional<Brand> findByName(@Param("categoryName") String brandName);
}