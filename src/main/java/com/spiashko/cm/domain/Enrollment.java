package com.spiashko.cm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Enrollment.
 */
@Entity
@Table(name = "enrollment")
public class Enrollment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "enrollment")
    @JsonIgnoreProperties(value = { "lesson", "enrollment" }, allowSetters = true)
    private Set<CompletedLesson> completedLessons = new HashSet<>();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @NotNull
    @JsonIgnoreProperties(value = { "modules" }, allowSetters = true)
    private Course course;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @NotNull
    private User student;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Enrollment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<CompletedLesson> getCompletedLessons() {
        return this.completedLessons;
    }

    public void setCompletedLessons(Set<CompletedLesson> completedLessons) {
        if (this.completedLessons != null) {
            this.completedLessons.forEach(i -> i.setEnrollment(null));
        }
        if (completedLessons != null) {
            completedLessons.forEach(i -> i.setEnrollment(this));
        }
        this.completedLessons = completedLessons;
    }

    public Enrollment completedLessons(Set<CompletedLesson> completedLessons) {
        this.setCompletedLessons(completedLessons);
        return this;
    }

    public Enrollment addCompletedLessons(CompletedLesson completedLesson) {
        this.completedLessons.add(completedLesson);
        completedLesson.setEnrollment(this);
        return this;
    }

    public Enrollment removeCompletedLessons(CompletedLesson completedLesson) {
        this.completedLessons.remove(completedLesson);
        completedLesson.setEnrollment(null);
        return this;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Enrollment course(Course course) {
        this.setCourse(course);
        return this;
    }

    public User getStudent() {
        return this.student;
    }

    public void setStudent(User user) {
        this.student = user;
    }

    public Enrollment student(User user) {
        this.setStudent(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Enrollment)) {
            return false;
        }
        return id != null && id.equals(((Enrollment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Enrollment{" +
            "id=" + getId() +
            "}";
    }
}
