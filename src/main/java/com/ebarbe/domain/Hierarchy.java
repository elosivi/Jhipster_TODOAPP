package com.ebarbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

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
    @Pattern(regexp = "^[A-Z][a-z]+\\d$")
    @Column(name = "description", length = 50, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @JsonIgnoreProperties(value = { "user", "hierarchy", "events" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "hierarchy")
    @org.springframework.data.annotation.Transient
    private Person person;

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

    public Person getPerson() {
        return this.person;
    }

    public void setPerson(Person person) {
        if (this.person != null) {
            this.person.setHierarchy(null);
        }
        if (person != null) {
            person.setHierarchy(this);
        }
        this.person = person;
    }

    public Hierarchy person(Person person) {
        this.setPerson(person);
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
