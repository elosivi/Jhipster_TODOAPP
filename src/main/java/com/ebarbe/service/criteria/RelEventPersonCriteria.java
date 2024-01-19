package com.ebarbe.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ebarbe.domain.RelEventPerson} entity. This class is used
 * in {@link com.ebarbe.web.rest.RelEventPersonResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /rel-event-people?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RelEventPersonCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter participation;

    private LongFilter eventId;

    private LongFilter personId;

    private LongFilter hierarchyId;

    private Boolean distinct;

    public RelEventPersonCriteria() {}

    public RelEventPersonCriteria(RelEventPersonCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.participation = other.participation == null ? null : other.participation.copy();
        this.eventId = other.eventId == null ? null : other.eventId.copy();
        this.personId = other.personId == null ? null : other.personId.copy();
        this.hierarchyId = other.hierarchyId == null ? null : other.hierarchyId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public RelEventPersonCriteria copy() {
        return new RelEventPersonCriteria(this);
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

    public StringFilter getParticipation() {
        return participation;
    }

    public StringFilter participation() {
        if (participation == null) {
            participation = new StringFilter();
        }
        return participation;
    }

    public void setParticipation(StringFilter participation) {
        this.participation = participation;
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

    public LongFilter getHierarchyId() {
        return hierarchyId;
    }

    public LongFilter hierarchyId() {
        if (hierarchyId == null) {
            hierarchyId = new LongFilter();
        }
        return hierarchyId;
    }

    public void setHierarchyId(LongFilter hierarchyId) {
        this.hierarchyId = hierarchyId;
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
        final RelEventPersonCriteria that = (RelEventPersonCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(participation, that.participation) &&
            Objects.equals(eventId, that.eventId) &&
            Objects.equals(personId, that.personId) &&
            Objects.equals(hierarchyId, that.hierarchyId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, participation, eventId, personId, hierarchyId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RelEventPersonCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (participation != null ? "participation=" + participation + ", " : "") +
            (eventId != null ? "eventId=" + eventId + ", " : "") +
            (personId != null ? "personId=" + personId + ", " : "") +
            (hierarchyId != null ? "hierarchyId=" + hierarchyId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
