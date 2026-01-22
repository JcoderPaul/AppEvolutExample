package me.oldboy.market.exceptions.handlers;

import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.exceptions.*;
import me.oldboy.market.exceptions.exception_entity.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Глобальный обработчик исключений для бизнес-логики приложения.
 * <p>
 * Предоставляет централизованную обработку кастомных исключений, возникающих
 * на различных уровнях приложения (controller, service, data access).
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает кастомные исключения бизнес-логики приложения.
     * <p>
     * Метод перехватывает указанные типы исключений и преобразует их в
     * стандартизированный ответ с HTTP статусом 400 Bad Request.
     *
     * @param exception перехваченное исключение бизнес-логики
     * @return {@link ResponseEntity} с HTTP 400 Bad Request и объектом {@link ExceptionResponse}
     * @apiNote Все обрабатываемые исключения должны быть подтипами {@link RuntimeException}
     */
    @ExceptionHandler({
            ControllerLayerException.class,
            LoginNotFoundException.class,
            LiquibaseMigrationException.class
    })
    public ResponseEntity<ExceptionResponse> handleExceptions(RuntimeException exception) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(exception.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
