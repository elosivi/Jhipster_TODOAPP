package com.ebarbe.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.ebarbe.domain.Status} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StatusDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3, max = 100)
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
        if (!(o instanceof StatusDTO)) {
            return false;
        }

        StatusDTO statusDTO = (StatusDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, statusDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StatusDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
