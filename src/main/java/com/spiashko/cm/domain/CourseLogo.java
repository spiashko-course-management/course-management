package com.spiashko.cm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;

/**
 * A CourseLogo.
 */
@Entity
@Table(name = "course_logo")
public class CourseLogo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @OneToOne(optional = false)
    @NotNull

    @MapsId
    @JoinColumn(name = "id")
    private Logo logo;

    @OneToOne(mappedBy = "courseLogo")
    @JsonIgnore
    private Course course;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Logo getLogo() {
        return logo;
    }

    public CourseLogo logo(Logo logo) {
        this.logo = logo;
        return this;
    }

    public void setLogo(Logo logo) {
        this.logo = logo;
    }

    public Course getCourse() {
        return course;
    }

    public CourseLogo course(Course course) {
        this.course = course;
        return this;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseLogo)) {
            return false;
        }
        return id != null && id.equals(((CourseLogo) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseLogo{" +
            "id=" + getId() +
            "}";
    }
}
