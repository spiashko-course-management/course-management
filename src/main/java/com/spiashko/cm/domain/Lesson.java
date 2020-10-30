package com.spiashko.cm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.spiashko.cm.domain.enumeration.LessonType;

/**
 * A Lesson.
 */
@Entity
@Table(name = "lesson")
public class Lesson implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "cm_order", nullable = false)
    private Integer order;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private LessonType type;

    @OneToMany(mappedBy = "lesson")
    private Set<Artifact> artifacts = new HashSet<>();

    @OneToOne(mappedBy = "lesson")
    @JsonIgnore
    private LessonDetails lessonDetails;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "lessons", allowSetters = true)
    private Module module;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrder() {
        return order;
    }

    public Lesson order(Integer order) {
        this.order = order;
        return this;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public Lesson title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LessonType getType() {
        return type;
    }

    public Lesson type(LessonType type) {
        this.type = type;
        return this;
    }

    public void setType(LessonType type) {
        this.type = type;
    }

    public Set<Artifact> getArtifacts() {
        return artifacts;
    }

    public Lesson artifacts(Set<Artifact> artifacts) {
        this.artifacts = artifacts;
        return this;
    }

    public Lesson addArtifacts(Artifact artifact) {
        this.artifacts.add(artifact);
        artifact.setLesson(this);
        return this;
    }

    public Lesson removeArtifacts(Artifact artifact) {
        this.artifacts.remove(artifact);
        artifact.setLesson(null);
        return this;
    }

    public void setArtifacts(Set<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    public LessonDetails getLessonDetails() {
        return lessonDetails;
    }

    public Lesson lessonDetails(LessonDetails lessonDetails) {
        this.lessonDetails = lessonDetails;
        return this;
    }

    public void setLessonDetails(LessonDetails lessonDetails) {
        this.lessonDetails = lessonDetails;
    }

    public Module getModule() {
        return module;
    }

    public Lesson module(Module module) {
        this.module = module;
        return this;
    }

    public void setModule(Module module) {
        this.module = module;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lesson)) {
            return false;
        }
        return id != null && id.equals(((Lesson) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Lesson{" +
            "id=" + getId() +
            ", order=" + getOrder() +
            ", title='" + getTitle() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
