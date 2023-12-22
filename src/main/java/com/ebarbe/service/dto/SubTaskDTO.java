package com.ebarbe.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.ebarbe.domain.SubTask} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SubTaskDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3, max = 300)
    private String description;

    @NotNull
    private LocalDate deadline;

    private LocalDate creation;

    private Double cost;

    private MainTaskDTO mainTask;

    private PersonDTO personDoer;

    private StatusDTO status;

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

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDate getCreation() {
        return creation;
    }

    public void setCreation(LocalDate creation) {
        this.creation = creation;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public MainTaskDTO getMainTask() {
        return mainTask;
    }

    public void setMainTask(MainTaskDTO mainTask) {
        this.mainTask = mainTask;
    }

    public PersonDTO getPersonDoer() {
        return personDoer;
    }

    public void setPersonDoer(PersonDTO personDoer) {
        this.personDoer = personDoer;
    }

    public StatusDTO getStatus() {
        return status;
    }

    public void setStatus(StatusDTO status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubTaskDTO)) {
            return false;
        }

        SubTaskDTO subTaskDTO = (SubTaskDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, subTaskDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubTaskDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", deadline='" + getDeadline() + "'" +
            ", creation='" + getCreation() + "'" +
            ", cost=" + getCost() +
            ", mainTask=" + getMainTask() +
            ", personDoer=" + getPersonDoer() +
            ", status=" + getStatus() +
            "}";
    }
}
