package com.spiashko.cm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A CompletedLesson.
 */
@Entity
@Table(name = "completed_lesson")
public class CompletedLesson implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @NotNull
    @JsonIgnoreProperties(value = { "module" }, allowSetters = true)
    private Lesson lesson;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @NotNull
    @JsonIgnoreProperties(value = { "completedLessons", "course", "student" }, allowSetters = true)
    private Enrollment enrollment;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CompletedLesson id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Lesson getLesson() {
        return this.lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public CompletedLesson lesson(Lesson lesson) {
        this.setLesson(lesson);
        return this;
    }

    public Enrollment getEnrollment() {
        return this.enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public CompletedLesson enrollment(Enrollment enrollment) {
        this.setEnrollment(enrollment);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompletedLesson)) {
            return false;
        }
        return id != null && id.equals(((CompletedLesson) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CompletedLesson{" +
            "id=" + getId() +
            "}";
    }
}
