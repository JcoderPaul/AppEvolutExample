package me.oldboy.market.usermanager.core.exceptions.exception_entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс-обертка для унифицированного представления информации об исключениях в REST API.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExceptionResponse {
    /**
     * Сообщение об ошибке или исключении.
     */
    private String exceptionMsg;
}
