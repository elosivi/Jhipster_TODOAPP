package com.ebarbe.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ebarbe.domain.Event} entity. This class is used
 * in {@link com.ebarbe.web.rest.EventResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /events?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter label;

    private StringFilter description;

    private StringFilter theme;

    private LocalDateFilter dateStart;

    private LocalDateFilter dateEnd;

    private StringFilter place;

    private StringFilter placeDetails;

    private StringFilter adress;

    private StringFilter note;

    private LongFilter eventTypeId;

    private LongFilter personId;

    private LongFilter relEventPersonId;

    private Boolean distinct;

    public EventCriteria() {}

    public EventCriteria(EventCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.label = other.label == null ? null : other.label.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.theme = other.theme == null ? null : other.theme.copy();
        this.dateStart = other.dateStart == null ? null : other.dateStart.copy();
        this.dateEnd = other.dateEnd == null ? null : other.dateEnd.copy();
        this.place = other.place == null ? null : other.place.copy();
        this.placeDetails = other.placeDetails == null ? null : other.placeDetails.copy();
        this.adress = other.adress == null ? null : other.adress.copy();
        this.note = other.note == null ? null : other.note.copy();
        this.eventTypeId = other.eventTypeId == null ? null : other.eventTypeId.copy();
        this.personId = other.personId == null ? null : other.personId.copy();
        this.relEventPersonId = other.relEventPersonId == null ? null : other.relEventPersonId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public EventCriteria copy() {
        return new EventCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getLabel() {
        return label;
    }

    public StringFilter label() {
        if (label == null) {
            label = new StringFilter();
        }
        return label;
    }

    public void setLabel(StringFilter label) {
        this.label = label;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getTheme() {
        return theme;
    }

    public StringFilter theme() {
        if (theme == null) {
            theme = new StringFilter();
        }
        return theme;
    }

    public void setTheme(StringFilter theme) {
        this.theme = theme;
    }

    public LocalDateFilter getDateStart() {
        return dateStart;
    }

    public LocalDateFilter dateStart() {
        if (dateStart == null) {
            dateStart = new LocalDateFilter();
        }
        return dateStart;
    }

    public void setDateStart(LocalDateFilter dateStart) {
        this.dateStart = dateStart;
    }

    public LocalDateFilter getDateEnd() {
        return dateEnd;
    }

    public LocalDateFilter dateEnd() {
        if (dateEnd == null) {
            dateEnd = new LocalDateFilter();
        }
        return dateEnd;
    }

    public void setDateEnd(LocalDateFilter dateEnd) {
        this.dateEnd = dateEnd;
    }

    public StringFilter getPlace() {
        return place;
    }

    public StringFilter place() {
        if (place == null) {
            place = new StringFilter();
        }
        return place;
    }

    public void setPlace(StringFilter place) {
        this.place = place;
    }

    public StringFilter getPlaceDetails() {
        return placeDetails;
    }

    public StringFilter placeDetails() {
        if (placeDetails == null) {
            placeDetails = new StringFilter();
        }
        return placeDetails;
    }

    public void setPlaceDetails(StringFilter placeDetails) {
        this.placeDetails = placeDetails;
    }

    public StringFilter getAdress() {
        return adress;
    }

    public StringFilter adress() {
        if (adress == null) {
            adress = new StringFilter();
        }
        return adress;
    }

    public void setAdress(StringFilter adress) {
        this.adress = adress;
    }

    public StringFilter getNote() {
        return note;
    }

    public StringFilter note() {
        if (note == null) {
            note = new StringFilter();
        }
        return note;
    }

    public void setNote(StringFilter note) {
        this.note = note;
    }

    public LongFilter getEventTypeId() {
        return eventTypeId;
    }

    public LongFilter eventTypeId() {
        if (eventTypeId == null) {
            eventTypeId = new LongFilter();
        }
        return eventTypeId;
    }

    public void setEventTypeId(LongFilter eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public LongFilter getPersonId() {
        return personId;
    }

    public LongFilter personId() {
        if (personId == null) {
            personId = new LongFilter();
        }
        return personId;
    }

    public void setPersonId(LongFilter personId) {
        this.personId = personId;
    }

    public LongFilter getRelEventPersonId() {
        return relEventPersonId;
    }

    public LongFilter relEventPersonId() {
        if (relEventPersonId == null) {
            relEventPersonId = new LongFilter();
        }
        return relEventPersonId;
    }

    public void setRelEventPersonId(LongFilter relEventPersonId) {
        this.relEventPersonId = relEventPersonId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EventCriteria that = (EventCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(label, that.label) &&
            Objects.equals(description, that.description) &&
            Objects.equals(theme, that.theme) &&
            Objects.equals(dateStart, that.dateStart) &&
            Objects.equals(dateEnd, that.dateEnd) &&
            Objects.equals(place, that.place) &&
            Objects.equals(placeDetails, that.placeDetails) &&
            Objects.equals(adress, that.adress) &&
            Objects.equals(note, that.note) &&
            Objects.equals(eventTypeId, that.eventTypeId) &&
            Objects.equals(personId, that.personId) &&
            Objects.equals(relEventPersonId, that.relEventPersonId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            label,
            description,
            theme,
            dateStart,
            dateEnd,
            place,
            placeDetails,
            adress,
            note,
            eventTypeId,
            personId,
            relEventPersonId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (label != null ? "label=" + label + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (theme != null ? "theme=" + theme + ", " : "") +
            (dateStart != null ? "dateStart=" + dateStart + ", " : "") +
            (dateEnd != null ? "dateEnd=" + dateEnd + ", " : "") +
            (place != null ? "place=" + place + ", " : "") +
            (placeDetails != null ? "placeDetails=" + placeDetails + ", " : "") +
            (adress != null ? "adress=" + adress + ", " : "") +
            (note != null ? "note=" + note + ", " : "") +
            (eventTypeId != null ? "eventTypeId=" + eventTypeId + ", " : "") +
            (personId != null ? "personId=" + personId + ", " : "") +
            (relEventPersonId != null ? "relEventPersonId=" + relEventPersonId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
