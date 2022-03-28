package com.spiashko.cm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Type;

/**
 * A CourseExtraInfo.
 */
@Entity
@Table(name = "course_extra_info")
public class CourseExtraInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    /**
     * basically md file
     */
    @Schema(description = "basically md file", required = true)
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "summary", nullable = false)
    private String summary;

    @JsonIgnoreProperties(value = { "modules", "teacher" }, allowSetters = true)
    @OneToOne(optional = false)
    @NotNull
    @MapsId
    @JoinColumn(name = "id")
    private Course course;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CourseExtraInfo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSummary() {
        return this.summary;
    }

    public CourseExtraInfo summary(String summary) {
        this.setSummary(summary);
        return this;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public CourseExtraInfo course(Course course) {
        this.setCourse(course);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseExtraInfo)) {
            return false;
        }
        return id != null && id.equals(((CourseExtraInfo) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CourseExtraInfo{" +
            "id=" + getId() +
            ", summary='" + getSummary() + "'" +
            "}";
    }
}
