package me.oldboy.market.usermanager.core.repository;

import me.oldboy.market.usermanager.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с пользователями в БД.
 * Предоставляет методы для выполнения CRUD операций и поиска пользователей.
 * Наследует стандартные операции из {@link JpaRepository} и {@link CrudRepository}.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, CrudRepository<User, Long> {

    /**
     * Находит пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return {@link Optional} с найденным пользователем или пустой {@link Optional}
     */
    Optional<User> findById(@Param("userId") Long userId);

    /**
     * Находит пользователя по email адресу.
     *
     * @param email email адрес пользователя
     * @return {@link Optional} с найденным пользователем или пустой {@link Optional}
     */
    Optional<User> findByEmail(@Param("email") String email);
}