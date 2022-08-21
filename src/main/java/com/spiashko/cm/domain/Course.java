package com.spiashko.cm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

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
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 50)
    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @NotNull
    @Size(min = 3, max = 200)
    @Column(name = "image_url", length = 200, nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "course")
    @JsonIgnoreProperties(value = { "lessons", "course" }, allowSetters = true)
    private Set<Module> modules = new HashSet<>();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @NotNull
    private User teacher;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Course id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Course title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public Course imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Set<Module> getModules() {
        return this.modules;
    }

    public void setModules(Set<Module> modules) {
        if (this.modules != null) {
            this.modules.forEach(i -> i.setCourse(null));
        }
        if (modules != null) {
            modules.forEach(i -> i.setCourse(this));
        }
        this.modules = modules;
    }

    public Course modules(Set<Module> modules) {
        this.setModules(modules);
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

    public User getTeacher() {
        return this.teacher;
    }

    public void setTeacher(User user) {
        this.teacher = user;
    }

    public Course teacher(User user) {
        this.setTeacher(user);
        return this;
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
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Course{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            "}";
    }
}
