package com.ebarbe.service.mapper;

import com.ebarbe.domain.EventType;
import com.ebarbe.service.dto.EventTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EventType} and its DTO {@link EventTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventTypeMapper extends EntityMapper<EventTypeDTO, EventType> {}
