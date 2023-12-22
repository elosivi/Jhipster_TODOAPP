package com.ebarbe.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ebarbe.domain.EventType} entity. This class is used
 * in {@link com.ebarbe.web.rest.EventTypeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /event-types?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventTypeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter label;

    private StringFilter description;

    private DurationFilter duration;

    private LongFilter eventId;

    private Boolean distinct;

    public EventTypeCriteria() {}

    public EventTypeCriteria(EventTypeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.label = other.label == null ? null : other.label.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.duration = other.duration == null ? null : other.duration.copy();
        this.eventId = other.eventId == null ? null : other.eventId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public EventTypeCriteria copy() {
        return new EventTypeCriteria(this);
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

    public DurationFilter getDuration() {
        return duration;
    }

    public DurationFilter duration() {
        if (duration == null) {
            duration = new DurationFilter();
        }
        return duration;
    }

    public void setDuration(DurationFilter duration) {
        this.duration = duration;
    }

    public LongFilter getEventId() {
        return eventId;
    }

    public LongFilter eventId() {
        if (eventId == null) {
            eventId = new LongFilter();
        }
        return eventId;
    }

    public void setEventId(LongFilter eventId) {
        this.eventId = eventId;
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
        final EventTypeCriteria that = (EventTypeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(label, that.label) &&
            Objects.equals(description, that.description) &&
            Objects.equals(duration, that.duration) &&
            Objects.equals(eventId, that.eventId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label, description, duration, eventId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventTypeCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (label != null ? "label=" + label + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (duration != null ? "duration=" + duration + ", " : "") +
            (eventId != null ? "eventId=" + eventId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
