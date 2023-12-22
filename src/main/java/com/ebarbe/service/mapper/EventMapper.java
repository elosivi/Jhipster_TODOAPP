package com.ebarbe.service.mapper;

import com.ebarbe.domain.Event;
import com.ebarbe.domain.EventType;
import com.ebarbe.domain.Person;
import com.ebarbe.service.dto.EventDTO;
import com.ebarbe.service.dto.EventTypeDTO;
import com.ebarbe.service.dto.PersonDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Event} and its DTO {@link EventDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventMapper extends EntityMapper<EventDTO, Event> {
    @Mapping(target = "eventType", source = "eventType", qualifiedByName = "eventTypeId")
    @Mapping(target = "people", source = "people", qualifiedByName = "personIdSet")
    EventDTO toDto(Event s);

    @Mapping(target = "removePerson", ignore = true)
    Event toEntity(EventDTO eventDTO);

    @Named("eventTypeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EventTypeDTO toDtoEventTypeId(EventType eventType);

    @Named("personId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PersonDTO toDtoPersonId(Person person);

    @Named("personIdSet")
    default Set<PersonDTO> toDtoPersonIdSet(Set<Person> person) {
        return person.stream().map(this::toDtoPersonId).collect(Collectors.toSet());
    }
}
