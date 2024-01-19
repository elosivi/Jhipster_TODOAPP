package com.ebarbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A RelEventPerson.
 */
@Entity
@Table(name = "rel_event__person", uniqueConstraints = @UniqueConstraint(columnNames = { "event_id", "person_id" }))
@org.springframework.data.elasticsearch.annotations.Document(indexName = "releventperson")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RelEventPerson implements Serializable {

    @Id
    public Long id;

    @Column(name = "participation")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String participation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    @JsonIgnoreProperties(value = { "eventType", "person", "relEventPeople" }, allowSetters = true)
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    @JsonIgnoreProperties(value = { "user", "event", "relEventPeople" }, allowSetters = true)
    private Person person;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hierarchy_id")
    @JsonIgnoreProperties(value = { "relEventPeople" }, allowSetters = true)
    private Hierarchy hierarchy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String createId() {
        if (this.event != null && this.event.getId() != null && this.person != null && this.person.getId() != null) {
            return this.event.getId() + "-" + this.person.getId();
        }
        return null;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParticipation() {
        return this.participation;
    }

    public RelEventPerson participation(String participation) {
        this.setParticipation(participation);
        return this;
    }

    public void setParticipation(String participation) {
        this.participation = participation;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Hierarchy getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(Hierarchy hierarchy) {
        this.hierarchy = hierarchy;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelEventPerson that = (RelEventPerson) o;
        return (
            Objects.equals(participation, that.participation) &&
            Objects.equals(event, that.event) &&
            Objects.equals(person, that.person) &&
            Objects.equals(hierarchy, that.hierarchy)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(participation, event, person, hierarchy);
    }

    @Override
    public String toString() {
        return (
            "RelEventPerson{" +
            "participation='" +
            participation +
            '\'' +
            ", event=" +
            event +
            ", person=" +
            person +
            ", hierarchy=" +
            hierarchy +
            '}'
        );
    }
}
