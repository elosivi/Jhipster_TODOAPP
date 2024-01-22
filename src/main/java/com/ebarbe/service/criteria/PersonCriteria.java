package com.ebarbe.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ebarbe.domain.Person} entity. This class is used
 * in {@link com.ebarbe.web.rest.PersonResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /people?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PersonCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter description;

    private StringFilter pseudo;

    private StringFilter name;

    private LongFilter userId;

    private LongFilter eventId;

    private LongFilter relEventPersonId;

    private Boolean distinct;

    public PersonCriteria() {}

    public PersonCriteria(PersonCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.pseudo = other.pseudo == null ? null : other.pseudo.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.eventId = other.eventId == null ? null : other.eventId.copy();
        this.relEventPersonId = other.relEventPersonId == null ? null : other.relEventPersonId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public PersonCriteria copy() {
        return new PersonCriteria(this);
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

    public StringFilter getPseudo() {
        return pseudo;
    }

    public StringFilter pseudo() {
        if (pseudo == null) {
            pseudo = new StringFilter();
        }
        return pseudo;
    }

    public void setPseudo(StringFilter pseudo) {
        this.pseudo = pseudo;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
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
        final PersonCriteria that = (PersonCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(description, that.description) &&
            Objects.equals(pseudo, that.pseudo) &&
            Objects.equals(name, that.name) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(eventId, that.eventId) &&
            Objects.equals(relEventPersonId, that.relEventPersonId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, pseudo, name, userId, eventId, relEventPersonId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PersonCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (pseudo != null ? "pseudo=" + pseudo + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (eventId != null ? "eventId=" + eventId + ", " : "") +
            (relEventPersonId != null ? "relEventPersonId=" + relEventPersonId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
