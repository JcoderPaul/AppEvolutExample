package me.oldboy.market.mapper;

import me.oldboy.market.dto.audit.AuditReadDto;
import me.oldboy.market.entity.Audit;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Mapper для преобразования между Audit и AuditDto.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuditMapper {
    AuditMapper INSTANCE = Mappers.getMapper(AuditMapper.class);

    AuditReadDto mapToReadDto(Audit audit);
}
