package com.ebarbe.service.mapper;

import com.ebarbe.domain.MainTask;
import com.ebarbe.domain.Person;
import com.ebarbe.domain.Status;
import com.ebarbe.domain.SubTask;
import com.ebarbe.service.dto.MainTaskDTO;
import com.ebarbe.service.dto.PersonDTO;
import com.ebarbe.service.dto.StatusDTO;
import com.ebarbe.service.dto.SubTaskDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SubTask} and its DTO {@link SubTaskDTO}.
 */
@Mapper(componentModel = "spring")
public interface SubTaskMapper extends EntityMapper<SubTaskDTO, SubTask> {
    @Mapping(target = "mainTask", source = "mainTask", qualifiedByName = "mainTaskId")
    @Mapping(target = "personDoer", source = "personDoer", qualifiedByName = "personId")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusId")
    SubTaskDTO toDto(SubTask s);

    @Named("mainTaskId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    MainTaskDTO toDtoMainTaskId(MainTask mainTask);

    @Named("personId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PersonDTO toDtoPersonId(Person person);

    @Named("statusId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StatusDTO toDtoStatusId(Status status);
}
