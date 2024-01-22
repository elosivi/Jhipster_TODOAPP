package com.ebarbe.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.ebarbe.domain.MainTask} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MainTaskDTO implements Serializable {

    private Long id;

    @Size(min = 3, max = 100)
    private String description;

    @NotNull
    private LocalDate deadline;

    private LocalDate creation;

    private Double cost;

    private CategoryDTO category;

    private PersonDTO personOwner;

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

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public PersonDTO getPersonOwner() {
        return personOwner;
    }

    public void setPersonOwner(PersonDTO personOwner) {
        this.personOwner = personOwner;
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
        if (!(o instanceof MainTaskDTO)) {
            return false;
        }

        MainTaskDTO mainTaskDTO = (MainTaskDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mainTaskDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MainTaskDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", deadline='" + getDeadline() + "'" +
            ", creation='" + getCreation() + "'" +
            ", cost=" + getCost() +
            ", category=" + getCategory() +
            ", personOwner=" + getPersonOwner() +
            ", status=" + getStatus() +
            "}";
    }
}
