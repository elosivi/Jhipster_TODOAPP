package com.ebarbe.service.mapper;

import com.ebarbe.domain.Event;
import com.ebarbe.domain.Hierarchy;
import com.ebarbe.domain.Person;
import com.ebarbe.domain.RelEventPerson;
import com.ebarbe.service.dto.EventDTO;
import com.ebarbe.service.dto.HierarchyDTO;
import com.ebarbe.service.dto.PersonDTO;
import com.ebarbe.service.dto.RelEventPersonDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RelEventPerson} and its DTO {@link RelEventPersonDTO}.
 */
@Mapper(componentModel = "spring", uses = { EventMapper.class, PersonMapper.class, HierarchyMapper.class, UserMapper.class })
public interface RelEventPersonMapper extends EntityMapper<RelEventPersonDTO, RelEventPerson> {
    //@Mapping(target = "event", source = "event", qualifiedByName = "eventId")
    //@Mapping(target = "person", source = "person", qualifiedByName = "personId")
    //@Mapping(target = "hierarchy", source = "hierarchy", qualifiedByName = "hierarchyId")
    RelEventPersonDTO toDto(RelEventPerson s);

    @Named("eventId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EventDTO toDtoEventId(Event event);

    @Named("personId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PersonDTO toDtoPersonId(Person person);

    @Named("hierarchyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    HierarchyDTO toDtoHierarchyId(Hierarchy hierarchy);
}
