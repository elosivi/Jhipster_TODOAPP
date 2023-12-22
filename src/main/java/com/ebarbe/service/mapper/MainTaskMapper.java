package com.ebarbe.service.mapper;

import com.ebarbe.domain.Category;
import com.ebarbe.domain.MainTask;
import com.ebarbe.domain.Person;
import com.ebarbe.domain.Status;
import com.ebarbe.service.dto.CategoryDTO;
import com.ebarbe.service.dto.MainTaskDTO;
import com.ebarbe.service.dto.PersonDTO;
import com.ebarbe.service.dto.StatusDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MainTask} and its DTO {@link MainTaskDTO}.
 */
@Mapper(componentModel = "spring")
public interface MainTaskMapper extends EntityMapper<MainTaskDTO, MainTask> {
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryId")
    @Mapping(target = "personOwner", source = "personOwner", qualifiedByName = "personId")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusId")
    MainTaskDTO toDto(MainTask s);

    @Named("categoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CategoryDTO toDtoCategoryId(Category category);

    @Named("personId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PersonDTO toDtoPersonId(Person person);

    @Named("statusId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StatusDTO toDtoStatusId(Status status);
}
