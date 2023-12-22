package com.ebarbe.service.mapper;

import com.ebarbe.domain.Hierarchy;
import com.ebarbe.service.dto.HierarchyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Hierarchy} and its DTO {@link HierarchyDTO}.
 */
@Mapper(componentModel = "spring")
public interface HierarchyMapper extends EntityMapper<HierarchyDTO, Hierarchy> {}
