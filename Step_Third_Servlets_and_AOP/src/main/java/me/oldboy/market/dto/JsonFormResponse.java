package me.oldboy.market.dto;

/**
 * Представляет объект ответа для исключений, содержащий сообщение об ошибке (применяется в сервлетах).
 */
public record JsonFormResponse(String message) {
}
