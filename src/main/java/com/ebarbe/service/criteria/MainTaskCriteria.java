package com.ebarbe.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ebarbe.domain.MainTask} entity. This class is used
 * in {@link com.ebarbe.web.rest.MainTaskResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /main-tasks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MainTaskCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter description;

    private LocalDateFilter deadline;

    private LocalDateFilter creation;

    private DoubleFilter cost;

    private LongFilter categoryId;

    private LongFilter personOwnerId;

    private LongFilter statusId;

    private LongFilter subTaskId;

    private Boolean distinct;

    public MainTaskCriteria() {}

    public MainTaskCriteria(MainTaskCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.deadline = other.deadline == null ? null : other.deadline.copy();
        this.creation = other.creation == null ? null : other.creation.copy();
        this.cost = other.cost == null ? null : other.cost.copy();
        this.categoryId = other.categoryId == null ? null : other.categoryId.copy();
        this.personOwnerId = other.personOwnerId == null ? null : other.personOwnerId.copy();
        this.statusId = other.statusId == null ? null : other.statusId.copy();
        this.subTaskId = other.subTaskId == null ? null : other.subTaskId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public MainTaskCriteria copy() {
        return new MainTaskCriteria(this);
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

    public LocalDateFilter getDeadline() {
        return deadline;
    }

    public LocalDateFilter deadline() {
        if (deadline == null) {
            deadline = new LocalDateFilter();
        }
        return deadline;
    }

    public void setDeadline(LocalDateFilter deadline) {
        this.deadline = deadline;
    }

    public LocalDateFilter getCreation() {
        return creation;
    }

    public LocalDateFilter creation() {
        if (creation == null) {
            creation = new LocalDateFilter();
        }
        return creation;
    }

    public void setCreation(LocalDateFilter creation) {
        this.creation = creation;
    }

    public DoubleFilter getCost() {
        return cost;
    }

    public DoubleFilter cost() {
        if (cost == null) {
            cost = new DoubleFilter();
        }
        return cost;
    }

    public void setCost(DoubleFilter cost) {
        this.cost = cost;
    }

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public LongFilter categoryId() {
        if (categoryId == null) {
            categoryId = new LongFilter();
        }
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
    }

    public LongFilter getPersonOwnerId() {
        return personOwnerId;
    }

    public LongFilter personOwnerId() {
        if (personOwnerId == null) {
            personOwnerId = new LongFilter();
        }
        return personOwnerId;
    }

    public void setPersonOwnerId(LongFilter personOwnerId) {
        this.personOwnerId = personOwnerId;
    }

    public LongFilter getStatusId() {
        return statusId;
    }

    public LongFilter statusId() {
        if (statusId == null) {
            statusId = new LongFilter();
        }
        return statusId;
    }

    public void setStatusId(LongFilter statusId) {
        this.statusId = statusId;
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
        final MainTaskCriteria that = (MainTaskCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(description, that.description) &&
            Objects.equals(deadline, that.deadline) &&
            Objects.equals(creation, that.creation) &&
            Objects.equals(cost, that.cost) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(personOwnerId, that.personOwnerId) &&
            Objects.equals(statusId, that.statusId) &&
            Objects.equals(subTaskId, that.subTaskId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, deadline, creation, cost, categoryId, personOwnerId, statusId, subTaskId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MainTaskCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (deadline != null ? "deadline=" + deadline + ", " : "") +
            (creation != null ? "creation=" + creation + ", " : "") +
            (cost != null ? "cost=" + cost + ", " : "") +
            (categoryId != null ? "categoryId=" + categoryId + ", " : "") +
            (personOwnerId != null ? "personOwnerId=" + personOwnerId + ", " : "") +
            (statusId != null ? "statusId=" + statusId + ", " : "") +
            (subTaskId != null ? "subTaskId=" + subTaskId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
