package com.ebarbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Hierarchy.
 */
@Entity
@Table(name = "hierarchy")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "hierarchy")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Hierarchy implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "description", length = 50, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "hierarchies")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "events", "people", "hierarchies" }, allowSetters = true)
    private Set<RelEventPerson> relEventPeople = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Hierarchy id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public Hierarchy description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<RelEventPerson> getRelEventPeople() {
        return this.relEventPeople;
    }

    public void setRelEventPeople(Set<RelEventPerson> relEventPeople) {
        if (this.relEventPeople != null) {
            this.relEventPeople.forEach(i -> i.removeHierarchy(this));
        }
        if (relEventPeople != null) {
            relEventPeople.forEach(i -> i.addHierarchy(this));
        }
        this.relEventPeople = relEventPeople;
    }

    public Hierarchy relEventPeople(Set<RelEventPerson> relEventPeople) {
        this.setRelEventPeople(relEventPeople);
        return this;
    }

    public Hierarchy addRelEventPerson(RelEventPerson relEventPerson) {
        this.relEventPeople.add(relEventPerson);
        relEventPerson.getHierarchies().add(this);
        return this;
    }

    public Hierarchy removeRelEventPerson(RelEventPerson relEventPerson) {
        this.relEventPeople.remove(relEventPerson);
        relEventPerson.getHierarchies().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Hierarchy)) {
            return false;
        }
        return getId() != null && getId().equals(((Hierarchy) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Hierarchy{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
