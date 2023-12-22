package com.ebarbe.service.mapper;

import com.ebarbe.domain.Hierarchy;
import com.ebarbe.domain.Person;
import com.ebarbe.domain.User;
import com.ebarbe.service.dto.HierarchyDTO;
import com.ebarbe.service.dto.PersonDTO;
import com.ebarbe.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Person} and its DTO {@link PersonDTO}.
 */
@Mapper(componentModel = "spring")
public interface PersonMapper extends EntityMapper<PersonDTO, Person> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    @Mapping(target = "hierarchy", source = "hierarchy", qualifiedByName = "hierarchyId")
    PersonDTO toDto(Person s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("hierarchyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    HierarchyDTO toDtoHierarchyId(Hierarchy hierarchy);
}
