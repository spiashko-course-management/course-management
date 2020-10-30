package com.spiashko.cm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
    private Long id;

    @OneToMany(mappedBy = "enrollment")
    private Set<CompletedLesson> completedLessons = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "enrollments", allowSetters = true)
    private Course course;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "enrollments", allowSetters = true)
    private Student student;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<CompletedLesson> getCompletedLessons() {
        return completedLessons;
    }

    public Enrollment completedLessons(Set<CompletedLesson> completedLessons) {
        this.completedLessons = completedLessons;
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

    public void setCompletedLessons(Set<CompletedLesson> completedLessons) {
        this.completedLessons = completedLessons;
    }

    public Course getCourse() {
        return course;
    }

    public Enrollment course(Course course) {
        this.course = course;
        return this;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Student getStudent() {
        return student;
    }

    public Enrollment student(Student student) {
        this.student = student;
        return this;
    }

    public void setStudent(Student student) {
        this.student = student;
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
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Enrollment{" +
            "id=" + getId() +
            "}";
    }
}
