package com.ebarbe.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

/**
 * A DTO for the {@link com.ebarbe.domain.EventType} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventTypeDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[A-Z][a-z]+\\d$")
    private String label;

    @Size(min = 3, max = 300)
    private String description;

    private Duration duration;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventTypeDTO)) {
            return false;
        }

        EventTypeDTO eventTypeDTO = (EventTypeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, eventTypeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventTypeDTO{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", description='" + getDescription() + "'" +
            ", duration='" + getDuration() + "'" +
            "}";
    }
}
