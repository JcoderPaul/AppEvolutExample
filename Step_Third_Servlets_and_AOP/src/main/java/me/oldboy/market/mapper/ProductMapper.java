package me.oldboy.market.mapper;

import me.oldboy.market.dto.product.ProductReadDto;
import me.oldboy.market.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Mapper для преобразования между Product и ProductDto.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductReadDto mapToReadDto(Product product);

    Product mapReadDtoToEntity(ProductReadDto productReadDto);
}
