package me.oldboy.market.productmanager.core.mapper;

import me.oldboy.market.productmanager.core.dto.category.CategoryReadDto;
import me.oldboy.market.productmanager.core.entity.prod_species.Category;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Mapper для преобразования между Category и CategoryDto.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryReadDto mapToReadDto(Category category);
}
