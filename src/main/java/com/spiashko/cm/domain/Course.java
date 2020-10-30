package com.spiashko.cm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * A Course.
 */
@Entity
@Table(name = "course")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    private BigDecimal price;

    @OneToOne
    @JoinColumn(unique = true)
    private CourseLogo courseLogo;

    @OneToMany(mappedBy = "course")
    private Set<Enrollment> enrollments = new HashSet<>();

    @OneToMany(mappedBy = "course")
    private Set<Module> modules = new HashSet<>();

    @OneToMany(mappedBy = "course")
    private Set<Artifact> artifacts = new HashSet<>();

    @OneToMany(mappedBy = "course")
    private Set<Payment> payments = new HashSet<>();

    @OneToOne(mappedBy = "course")
    @JsonIgnore
    private CourseDetails courseDetails;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "courses", allowSetters = true)
    private Teacher teacher;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Course title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Course price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public CourseLogo getCourseLogo() {
        return courseLogo;
    }

    public Course courseLogo(CourseLogo courseLogo) {
        this.courseLogo = courseLogo;
        return this;
    }

    public void setCourseLogo(CourseLogo courseLogo) {
        this.courseLogo = courseLogo;
    }

    public Set<Enrollment> getEnrollments() {
        return enrollments;
    }

    public Course enrollments(Set<Enrollment> enrollments) {
        this.enrollments = enrollments;
        return this;
    }

    public Course addEnrollments(Enrollment enrollment) {
        this.enrollments.add(enrollment);
        enrollment.setCourse(this);
        return this;
    }

    public Course removeEnrollments(Enrollment enrollment) {
        this.enrollments.remove(enrollment);
        enrollment.setCourse(null);
        return this;
    }

    public void setEnrollments(Set<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public Set<Module> getModules() {
        return modules;
    }

    public Course modules(Set<Module> modules) {
        this.modules = modules;
        return this;
    }

    public Course addModules(Module module) {
        this.modules.add(module);
        module.setCourse(this);
        return this;
    }

    public Course removeModules(Module module) {
        this.modules.remove(module);
        module.setCourse(null);
        return this;
    }

    public void setModules(Set<Module> modules) {
        this.modules = modules;
    }

    public Set<Artifact> getArtifacts() {
        return artifacts;
    }

    public Course artifacts(Set<Artifact> artifacts) {
        this.artifacts = artifacts;
        return this;
    }

    public Course addArtifacts(Artifact artifact) {
        this.artifacts.add(artifact);
        artifact.setCourse(this);
        return this;
    }

    public Course removeArtifacts(Artifact artifact) {
        this.artifacts.remove(artifact);
        artifact.setCourse(null);
        return this;
    }

    public void setArtifacts(Set<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    public Set<Payment> getPayments() {
        return payments;
    }

    public Course payments(Set<Payment> payments) {
        this.payments = payments;
        return this;
    }

    public Course addPayments(Payment payment) {
        this.payments.add(payment);
        payment.setCourse(this);
        return this;
    }

    public Course removePayments(Payment payment) {
        this.payments.remove(payment);
        payment.setCourse(null);
        return this;
    }

    public void setPayments(Set<Payment> payments) {
        this.payments = payments;
    }

    public CourseDetails getCourseDetails() {
        return courseDetails;
    }

    public Course courseDetails(CourseDetails courseDetails) {
        this.courseDetails = courseDetails;
        return this;
    }

    public void setCourseDetails(CourseDetails courseDetails) {
        this.courseDetails = courseDetails;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public Course teacher(Teacher teacher) {
        this.teacher = teacher;
        return this;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Course)) {
            return false;
        }
        return id != null && id.equals(((Course) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Course{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", price=" + getPrice() +
            "}";
    }
}
