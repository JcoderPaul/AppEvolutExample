package me.oldboy.market.mapper;

import me.oldboy.market.dto.user.UserReadDto;
import me.oldboy.market.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Интерфейс для преобразования сущности User в DTO
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Преобразование сущности User в UserReadDto.
     *
     * @param user the User entity to convert
     * @return the converted UserReadDto
     */
    UserReadDto mapToUserReadDto(User user);
}