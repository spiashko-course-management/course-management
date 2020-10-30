package com.spiashko.cm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;

/**
 * A UserLogo.
 */
@Entity
@Table(name = "user_logo")
public class UserLogo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @OneToOne(optional = false)
    @NotNull

    @MapsId
    @JoinColumn(name = "id")
    private Logo logo;

    @OneToOne(mappedBy = "userLogo")
    @JsonIgnore
    private ExtendedUser extendedUser;

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

    public UserLogo logo(Logo logo) {
        this.logo = logo;
        return this;
    }

    public void setLogo(Logo logo) {
        this.logo = logo;
    }

    public ExtendedUser getExtendedUser() {
        return extendedUser;
    }

    public UserLogo extendedUser(ExtendedUser extendedUser) {
        this.extendedUser = extendedUser;
        return this;
    }

    public void setExtendedUser(ExtendedUser extendedUser) {
        this.extendedUser = extendedUser;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserLogo)) {
            return false;
        }
        return id != null && id.equals(((UserLogo) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserLogo{" +
            "id=" + getId() +
            "}";
    }
}
