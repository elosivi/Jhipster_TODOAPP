package com.ebarbe.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.ebarbe.domain.Hierarchy} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HierarchyDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[A-Z][a-z]+\\d$")
    private String description;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HierarchyDTO)) {
            return false;
        }

        HierarchyDTO hierarchyDTO = (HierarchyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, hierarchyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HierarchyDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
