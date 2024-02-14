package com.ebarbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Person.
 */
@Entity
@Table(name = "person")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "person")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(min = 3, max = 300)
    @Column(name = "description", length = 300)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @Size(min = 3, max = 50)
    @Column(name = "pseudo", length = 50)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String pseudo;

    @Size(min = 3, max = 250)
    @Column(name = "name", length = 250)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User user;

    // @ManyToMany(fetch = FetchType.LAZY, mappedBy = "person")
    // @org.springframework.data.annotation.Transient
    @Transient // no relation for update ou creation
    @JoinTable(
        name = "rel_event__person",
        joinColumns = @JoinColumn(name = "person_id"),
        inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    @JsonIgnoreProperties(value = { "eventType", "person", "relEventPeople" }, allowSetters = true)
    private Set<Event> events = new HashSet<>();

    @OneToMany(mappedBy = "person")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "event", "person", "hierarchies" }, allowSetters = true)
    private Set<RelEventPerson> relEventPeople = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Person id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public Person description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPseudo() {
        return this.pseudo;
    }

    public Person pseudo(String pseudo) {
        this.setPseudo(pseudo);
        return this;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getName() {
        return this.name;
    }

    public Person name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Person user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Event> getEvents() {
        return this.events;
    }

    public void setEvents(Set<Event> events) {
        if (this.events != null) {
            this.events.forEach(i -> i.removePerson(this));
        }
        if (events != null) {
            events.forEach(i -> i.addPerson(this));
        }
        this.events = events;
    }

    public Person events(Set<Event> events) {
        this.setEvents(events);
        return this;
    }

    public Person addEvent(Event event) {
        this.events.add(event);
        event.getPeople().add(this);
        return this;
    }

    public Person removeEvent(Event event) {
        this.events.remove(event);
        event.getPeople().remove(this);
        return this;
    }

    public Set<RelEventPerson> getRelEventPeople() {
        return this.relEventPeople;
    }

    public void setRelEventPeople(Set<RelEventPerson> relEventPeople) {
        if (this.relEventPeople != null) {
            this.relEventPeople.forEach(i -> i.setPerson(null));
        }
        if (relEventPeople != null) {
            relEventPeople.forEach(i -> i.setPerson(this));
        }
        this.relEventPeople = relEventPeople;
    }

    public Person relEventPeople(Set<RelEventPerson> relEventPeople) {
        this.setRelEventPeople(relEventPeople);
        return this;
    }

    public Person addRelEventPerson(RelEventPerson relEventPerson) {
        this.relEventPeople.add(relEventPerson);
        relEventPerson.setPerson(this);
        return this;
    }

    public Person removeRelEventPerson(RelEventPerson relEventPerson) {
        this.relEventPeople.remove(relEventPerson);
        relEventPerson.setPerson(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Person)) {
            return false;
        }
        return getId() != null && getId().equals(((Person) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Person{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", pseudo='" + getPseudo() + "'" +
            ", name='" + getName() + "'" +
            "}";
    }
}
