package com.spiashko.cm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Module.
 */
@Entity
@Table(name = "module")
public class Module implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @OneToMany(mappedBy = "module")
    @JsonIgnoreProperties(value = { "module" }, allowSetters = true)
    private Set<Lesson> lessons = new HashSet<>();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @NotNull
    @JsonIgnoreProperties(value = { "modules", "teacher" }, allowSetters = true)
    private Course course;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Module id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Module title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Lesson> getLessons() {
        return this.lessons;
    }

    public void setLessons(Set<Lesson> lessons) {
        if (this.lessons != null) {
            this.lessons.forEach(i -> i.setModule(null));
        }
        if (lessons != null) {
            lessons.forEach(i -> i.setModule(this));
        }
        this.lessons = lessons;
    }

    public Module lessons(Set<Lesson> lessons) {
        this.setLessons(lessons);
        return this;
    }

    public Module addLessons(Lesson lesson) {
        this.lessons.add(lesson);
        lesson.setModule(this);
        return this;
    }

    public Module removeLessons(Lesson lesson) {
        this.lessons.remove(lesson);
        lesson.setModule(null);
        return this;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Module course(Course course) {
        this.setCourse(course);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Module)) {
            return false;
        }
        return id != null && id.equals(((Module) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Module{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            "}";
    }
}
