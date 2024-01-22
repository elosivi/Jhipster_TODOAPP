package com.ebarbe.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.ebarbe.domain.Hierarchy} entity. This class is used
 * in {@link com.ebarbe.web.rest.HierarchyResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /hierarchies?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HierarchyCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter description;

    private LongFilter relEventPersonId;

    private Boolean distinct;

    public HierarchyCriteria() {}

    public HierarchyCriteria(HierarchyCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.relEventPersonId = other.relEventPersonId == null ? null : other.relEventPersonId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public HierarchyCriteria copy() {
        return new HierarchyCriteria(this);
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
        final HierarchyCriteria that = (HierarchyCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(description, that.description) &&
            Objects.equals(relEventPersonId, that.relEventPersonId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, relEventPersonId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HierarchyCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (relEventPersonId != null ? "relEventPersonId=" + relEventPersonId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
