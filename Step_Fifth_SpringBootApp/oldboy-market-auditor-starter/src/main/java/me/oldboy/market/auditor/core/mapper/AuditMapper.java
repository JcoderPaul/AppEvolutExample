package me.oldboy.market.auditor.core.mapper;

import me.oldboy.market.auditor.core.dto.audit.AuditReadDto;
import me.oldboy.market.auditor.core.entity.Audit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * Mapper для преобразования между Audit и AuditDto.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuditMapper {

    AuditMapper INSTANCE = Mappers.getMapper(AuditMapper.class);

    @Mapping(target = "createBy", source = "audit.createBy.email")
    AuditReadDto mapToReadDto(Audit audit);
}
