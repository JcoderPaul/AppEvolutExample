package me.oldboy.market.validate;

import javax.validation.*;
import java.util.Set;

/**
 * Утилитный класс для валидации Data Transfer Objects (DTO) с использованием Bean Validation API.
 * Обеспечивает централизованную проверку объектов на соответствие заданным ограничениям (аннотациям).
 * Реализует паттерн Singleton для обеспечения единственного экземпляра валидатора в приложении.
 */
public class ValidatorDto {

    /**
     * Единственный экземпляр класса (Singleton pattern)
     */
    private static ValidatorDto instance;

    /**
     * Приватный конструктор для реализации паттерна Singleton.
     * Предотвращает создание экземпляров класса извне.
     */
    private ValidatorDto() {
    }

    /**
     * Возвращает единственный экземпляр ValidatorDto.
     * Использует ленивую инициализацию - экземпляр создается при первом вызове.
     *
     * @return единственный экземпляр ValidatorDto
     */
    public static ValidatorDto getInstance() {
        if (instance == null) {
            instance = new ValidatorDto();
        }
        return instance;
    }

    /**
     * Выполняет валидацию объекта DTO на основе Bean Validation аннотаций.
     *
     * @param <T> тип валидируемого объекта
     * @param t   объект для валидации
     * @throws ConstraintViolationException если объект не прошел валидацию
     * @throws IllegalArgumentException     если переданный объект равен null
     */
    public <T> void isValidData(T t) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<T>> validationResult = validator.validate(t);
        if (!validationResult.isEmpty()) {
            throw new ConstraintViolationException(validationResult);
        }
    }
}
