package com.ebarbe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

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

    @JsonIgnoreProperties(value = { "status", "category", "personOwner", "subTasks" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "status")
    @org.springframework.data.annotation.Transient
    private MainTask mainTask;

    @JsonIgnoreProperties(value = { "status", "mainTask", "personDoer" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "status")
    @org.springframework.data.annotation.Transient
    private SubTask subTask;

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

    public MainTask getMainTask() {
        return this.mainTask;
    }

    public void setMainTask(MainTask mainTask) {
        if (this.mainTask != null) {
            this.mainTask.setStatus(null);
        }
        if (mainTask != null) {
            mainTask.setStatus(this);
        }
        this.mainTask = mainTask;
    }

    public Status mainTask(MainTask mainTask) {
        this.setMainTask(mainTask);
        return this;
    }

    public SubTask getSubTask() {
        return this.subTask;
    }

    public void setSubTask(SubTask subTask) {
        if (this.subTask != null) {
            this.subTask.setStatus(null);
        }
        if (subTask != null) {
            subTask.setStatus(this);
        }
        this.subTask = subTask;
    }

    public Status subTask(SubTask subTask) {
        this.setSubTask(subTask);
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
