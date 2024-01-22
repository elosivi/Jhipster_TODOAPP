package com.ebarbe.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.ebarbe.domain.RelEventPerson} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RelEventPersonDTO implements Serializable {

    private Long id;

    private String participation;

    private EventDTO event;

    private PersonDTO person;

    private HierarchyDTO hierarchy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParticipation() {
        return participation;
    }

    public void setParticipation(String participation) {
        this.participation = participation;
    }

    public EventDTO getEvent() {
        return event;
    }

    public void setEvent(EventDTO event) {
        this.event = event;
    }

    public PersonDTO getPerson() {
        return person;
    }

    public void setPerson(PersonDTO person) {
        this.person = person;
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
        if (!(o instanceof RelEventPersonDTO)) {
            return false;
        }

        RelEventPersonDTO relEventPersonDTO = (RelEventPersonDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, relEventPersonDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RelEventPersonDTO{" +
            " event=" + getEvent().getLabel() +
            "and person=" + getPerson().getPseudo() +
            ", hierarchy=" + getHierarchy().getDescription() +
            ", participation=" + getParticipation() +
            "}";
    }
}
