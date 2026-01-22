package me.oldboy.market.exceptions.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для валидации DTO в Spring приложении.
 * Класс перехватывает исключения валидации, возникающие при обработке входящих HTTP запросов,
 * и преобразует их в стандартизированный формат ответа. Используется для обработки ошибок
 * валидации данных, аннотированных javax.validation constraints.
 */
@Slf4j
@ControllerAdvice
public class DtoValidationHandler {

    /**
     * Обрабатывает исключения валидации аргументов методов контроллера.
     *
     * @param exception исключение валидации, перехваченное Spring Framework
     * @return {@link ResponseEntity} с HTTP 400 Bad Request и JSON объектом ошибок валидации
     * @throws JsonProcessingException если возникает ошибка при сериализации в JSON
     * @apiNote Метод автоматически активируется когда любой контроллер выбрасывает
     * MethodArgumentNotValidException при неудачной валидации @Validated DTO
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException exception) throws JsonProcessingException {

        Map<String, String> mapValidationErrors = new HashMap<>();
        exception.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    mapValidationErrors.put(fieldName, errorMessage);
                });

        return ResponseEntity.badRequest().body(new ObjectMapper().writer()
                .withDefaultPrettyPrinter()
                .writeValueAsString(mapValidationErrors));
    }
}
