package com.ebarbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * A EventType.
 */
@Entity
@Table(name = "event_type")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "eventtype")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "label", length = 50, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String label;

    @Size(min = 3, max = 300)
    @Column(name = "description", length = 300)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @Column(name = "duration")
    private Duration duration;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "eventType")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "eventType", "people", "relEventPeople" }, allowSetters = true)
    private Set<Event> events = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EventType id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public EventType label(String label) {
        this.setLabel(label);
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return this.description;
    }

    public EventType description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public EventType duration(Duration duration) {
        this.setDuration(duration);
        return this;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Set<Event> getEvents() {
        return this.events;
    }

    public void setEvents(Set<Event> events) {
        if (this.events != null) {
            this.events.forEach(i -> i.setEventType(null));
        }
        if (events != null) {
            events.forEach(i -> i.setEventType(this));
        }
        this.events = events;
    }

    public EventType events(Set<Event> events) {
        this.setEvents(events);
        return this;
    }

    public EventType addEvent(Event event) {
        this.events.add(event);
        event.setEventType(this);
        return this;
    }

    public EventType removeEvent(Event event) {
        this.events.remove(event);
        event.setEventType(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventType)) {
            return false;
        }
        return getId() != null && getId().equals(((EventType) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventType{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", description='" + getDescription() + "'" +
            ", duration='" + getDuration() + "'" +
            "}";
    }
}
