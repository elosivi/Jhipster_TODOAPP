package com.ebarbe.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.ebarbe.domain.Person} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PersonDTO implements Serializable {

    private Long id;

    @Size(min = 3, max = 300)
    private String description;

    private UserDTO user;

    private HierarchyDTO hierarchy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public HierarchyDTO getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(HierarchyDTO hierarchy) {
        this.hierarchy = hierarchy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PersonDTO)) {
            return false;
        }

        PersonDTO personDTO = (PersonDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, personDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PersonDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", user=" + getUser() +
            ", hierarchy=" + getHierarchy() +
            "}";
    }
}
