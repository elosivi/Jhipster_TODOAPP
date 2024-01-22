package com.ebarbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Status.
 */
@Entity
@Table(name = "status")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "status")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Status implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 100)
    @Column(name = "description", length = 100, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "status")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "category", "personOwner", "status", "subTasks" }, allowSetters = true)
    private Set<MainTask> mainTasks = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "status")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "mainTask", "personDoer", "status" }, allowSetters = true)
    private Set<SubTask> subTasks = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Status id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public Status description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<MainTask> getMainTasks() {
        return this.mainTasks;
    }

    public void setMainTasks(Set<MainTask> mainTasks) {
        if (this.mainTasks != null) {
            this.mainTasks.forEach(i -> i.setStatus(null));
        }
        if (mainTasks != null) {
            mainTasks.forEach(i -> i.setStatus(this));
        }
        this.mainTasks = mainTasks;
    }

    public Status mainTasks(Set<MainTask> mainTasks) {
        this.setMainTasks(mainTasks);
        return this;
    }

    public Status addMainTask(MainTask mainTask) {
        this.mainTasks.add(mainTask);
        mainTask.setStatus(this);
        return this;
    }

    public Status removeMainTask(MainTask mainTask) {
        this.mainTasks.remove(mainTask);
        mainTask.setStatus(null);
        return this;
    }

    public Set<SubTask> getSubTasks() {
        return this.subTasks;
    }

    public void setSubTasks(Set<SubTask> subTasks) {
        if (this.subTasks != null) {
            this.subTasks.forEach(i -> i.setStatus(null));
        }
        if (subTasks != null) {
            subTasks.forEach(i -> i.setStatus(this));
        }
        this.subTasks = subTasks;
    }

    public Status subTasks(Set<SubTask> subTasks) {
        this.setSubTasks(subTasks);
        return this;
    }

    public Status addSubTask(SubTask subTask) {
        this.subTasks.add(subTask);
        subTask.setStatus(this);
        return this;
    }

    public Status removeSubTask(SubTask subTask) {
        this.subTasks.remove(subTask);
        subTask.setStatus(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Status)) {
            return false;
        }
        return getId() != null && getId().equals(((Status) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Status{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
