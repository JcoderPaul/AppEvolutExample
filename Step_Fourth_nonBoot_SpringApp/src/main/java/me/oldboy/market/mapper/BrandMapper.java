package me.oldboy.market.mapper;

import me.oldboy.market.dto.brand.BrandReadDto;
import me.oldboy.market.entity.prod_species.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Mapper для преобразования между Brand и BrandDto.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BrandMapper {
    BrandMapper INSTANCE = Mappers.getMapper(BrandMapper.class);

    BrandReadDto mapToReadDto(Brand brand);
}
