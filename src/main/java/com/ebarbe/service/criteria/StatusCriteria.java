package com.ebarbe.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ebarbe.domain.Status} entity. This class is used
 * in {@link com.ebarbe.web.rest.StatusResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /statuses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StatusCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter description;

    private LongFilter mainTaskId;

    private LongFilter subTaskId;

    private Boolean distinct;

    public StatusCriteria() {}

    public StatusCriteria(StatusCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.mainTaskId = other.mainTaskId == null ? null : other.mainTaskId.copy();
        this.subTaskId = other.subTaskId == null ? null : other.subTaskId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public StatusCriteria copy() {
        return new StatusCriteria(this);
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

    public LongFilter getMainTaskId() {
        return mainTaskId;
    }

    public LongFilter mainTaskId() {
        if (mainTaskId == null) {
            mainTaskId = new LongFilter();
        }
        return mainTaskId;
    }

    public void setMainTaskId(LongFilter mainTaskId) {
        this.mainTaskId = mainTaskId;
    }

    public LongFilter getSubTaskId() {
        return subTaskId;
    }

    public LongFilter subTaskId() {
        if (subTaskId == null) {
            subTaskId = new LongFilter();
        }
        return subTaskId;
    }

    public void setSubTaskId(LongFilter subTaskId) {
        this.subTaskId = subTaskId;
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
        final StatusCriteria that = (StatusCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(description, that.description) &&
            Objects.equals(mainTaskId, that.mainTaskId) &&
            Objects.equals(subTaskId, that.subTaskId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, mainTaskId, subTaskId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StatusCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (mainTaskId != null ? "mainTaskId=" + mainTaskId + ", " : "") +
            (subTaskId != null ? "subTaskId=" + subTaskId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
