package com.ebarbe.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.ebarbe.domain.Event} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[A-Z][a-z]+\\d$")
    private String label;

    @Size(min = 3, max = 300)
    private String description;

    @Size(min = 3, max = 300)
    private String theme;

    @NotNull
    private LocalDate dateStart;

    @NotNull
    private LocalDate dateEnd;

    @NotNull
    private String place;

    private String placeDetails;

    private String adress;

    @Size(min = 3, max = 300)
    private String note;

    private EventTypeDTO eventType;

    private Set<PersonDTO> people = new HashSet<>();

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

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public LocalDate getDateStart() {
        return dateStart;
    }

    public void setDateStart(LocalDate dateStart) {
        this.dateStart = dateStart;
    }

    public LocalDate getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(LocalDate dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPlaceDetails() {
        return placeDetails;
    }

    public void setPlaceDetails(String placeDetails) {
        this.placeDetails = placeDetails;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public EventTypeDTO getEventType() {
        return eventType;
    }

    public void setEventType(EventTypeDTO eventType) {
        this.eventType = eventType;
    }

    public Set<PersonDTO> getPeople() {
        return people;
    }

    public void setPeople(Set<PersonDTO> people) {
        this.people = people;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventDTO)) {
            return false;
        }

        EventDTO eventDTO = (EventDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, eventDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventDTO{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", description='" + getDescription() + "'" +
            ", theme='" + getTheme() + "'" +
            ", dateStart='" + getDateStart() + "'" +
            ", dateEnd='" + getDateEnd() + "'" +
            ", place='" + getPlace() + "'" +
            ", placeDetails='" + getPlaceDetails() + "'" +
            ", adress='" + getAdress() + "'" +
            ", note='" + getNote() + "'" +
            ", eventType=" + getEventType() +
            ", people=" + getPeople() +
            "}";
    }
}
