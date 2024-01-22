package com.ebarbe.service.mapper;

import com.ebarbe.domain.Status;
import com.ebarbe.service.dto.StatusDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Status} and its DTO {@link StatusDTO}.
 */
@Mapper(componentModel = "spring")
public interface StatusMapper extends EntityMapper<StatusDTO, Status> {}
