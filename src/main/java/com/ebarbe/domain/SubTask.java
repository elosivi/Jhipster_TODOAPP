package com.ebarbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A SubTask.
 */
@Entity
@Table(name = "sub_task")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "subtask")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SubTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 300)
    @Column(name = "description", length = 300, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @NotNull
    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Column(name = "creation")
    private LocalDate creation;

    @Column(name = "cost")
    private Double cost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "category", "personOwner", "status", "subTasks" }, allowSetters = true)
    private MainTask mainTask;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "events", "relEventPeople" }, allowSetters = true)
    private Person personDoer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "mainTasks", "subTasks" }, allowSetters = true)
    private Status status;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SubTask id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public SubTask description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDeadline() {
        return this.deadline;
    }

    public SubTask deadline(LocalDate deadline) {
        this.setDeadline(deadline);
        return this;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDate getCreation() {
        return this.creation;
    }

    public SubTask creation(LocalDate creation) {
        this.setCreation(creation);
        return this;
    }

    public void setCreation(LocalDate creation) {
        this.creation = creation;
    }

    public Double getCost() {
        return this.cost;
    }

    public SubTask cost(Double cost) {
        this.setCost(cost);
        return this;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public MainTask getMainTask() {
        return this.mainTask;
    }

    public void setMainTask(MainTask mainTask) {
        this.mainTask = mainTask;
    }

    public SubTask mainTask(MainTask mainTask) {
        this.setMainTask(mainTask);
        return this;
    }

    public Person getPersonDoer() {
        return this.personDoer;
    }

    public void setPersonDoer(Person person) {
        this.personDoer = person;
    }

    public SubTask personDoer(Person person) {
        this.setPersonDoer(person);
        return this;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public SubTask status(Status status) {
        this.setStatus(status);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubTask)) {
            return false;
        }
        return getId() != null && getId().equals(((SubTask) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SubTask{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", deadline='" + getDeadline() + "'" +
            ", creation='" + getCreation() + "'" +
            ", cost=" + getCost() +
            "}";
    }
}
