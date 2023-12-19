package com.ebarbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * A MainTask.
 */
@Entity
@Table(name = "main_task")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "maintask")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MainTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(min = 3, max = 100)
    @Column(name = "description", length = 100)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @NotNull
    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Column(name = "creation")
    private LocalDate creation;

    @Column(name = "cost")
    private Double cost;

    @JsonIgnoreProperties(value = { "mainTask", "subTask" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "mainTasks" }, allowSetters = true)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "hierarchy", "events" }, allowSetters = true)
    private Person personOwner;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "mainTask")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "status", "mainTask", "personDoer" }, allowSetters = true)
    private Set<SubTask> subTasks = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MainTask id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public MainTask description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDeadline() {
        return this.deadline;
    }

    public MainTask deadline(LocalDate deadline) {
        this.setDeadline(deadline);
        return this;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDate getCreation() {
        return this.creation;
    }

    public MainTask creation(LocalDate creation) {
        this.setCreation(creation);
        return this;
    }

    public void setCreation(LocalDate creation) {
        this.creation = creation;
    }

    public Double getCost() {
        return this.cost;
    }

    public MainTask cost(Double cost) {
        this.setCost(cost);
        return this;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public MainTask status(Status status) {
        this.setStatus(status);
        return this;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public MainTask category(Category category) {
        this.setCategory(category);
        return this;
    }

    public Person getPersonOwner() {
        return this.personOwner;
    }

    public void setPersonOwner(Person person) {
        this.personOwner = person;
    }

    public MainTask personOwner(Person person) {
        this.setPersonOwner(person);
        return this;
    }

    public Set<SubTask> getSubTasks() {
        return this.subTasks;
    }

    public void setSubTasks(Set<SubTask> subTasks) {
        if (this.subTasks != null) {
            this.subTasks.forEach(i -> i.setMainTask(null));
        }
        if (subTasks != null) {
            subTasks.forEach(i -> i.setMainTask(this));
        }
        this.subTasks = subTasks;
    }

    public MainTask subTasks(Set<SubTask> subTasks) {
        this.setSubTasks(subTasks);
        return this;
    }

    public MainTask addSubTask(SubTask subTask) {
        this.subTasks.add(subTask);
        subTask.setMainTask(this);
        return this;
    }

    public MainTask removeSubTask(SubTask subTask) {
        this.subTasks.remove(subTask);
        subTask.setMainTask(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MainTask)) {
            return false;
        }
        return getId() != null && getId().equals(((MainTask) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MainTask{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", deadline='" + getDeadline() + "'" +
            ", creation='" + getCreation() + "'" +
            ", cost=" + getCost() +
            "}";
    }
}
