package me.oldboy.market.dto.user;

/**
 * DTO для представления пользователя (временно не используется).
 */
public record UserReadDto(Long userId,
                          String email,
                          String role) {
}
