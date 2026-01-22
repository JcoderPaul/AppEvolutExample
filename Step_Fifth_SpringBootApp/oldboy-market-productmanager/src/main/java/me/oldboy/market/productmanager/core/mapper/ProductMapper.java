package me.oldboy.market.productmanager.core.mapper;

import me.oldboy.market.productmanager.core.dto.product.ProductCreateDto;
import me.oldboy.market.productmanager.core.dto.product.ProductReadDto;
import me.oldboy.market.productmanager.core.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Mapper для преобразования между Product и ProductDto.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "categoryName", source = "product.category.name")
    @Mapping(target = "brandName", source = "product.brand.name")
    ProductReadDto mapToReadDto(Product product);

    Product mapCreateDtoToEntity(ProductCreateDto productCreateDto);
}
