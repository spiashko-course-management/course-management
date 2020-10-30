package com.spiashko.cm.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;

/**
 * A ExtendedUser.
 */
@Entity
@Table(name = "extended_user")
public class ExtendedUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @OneToOne(optional = false)
    @NotNull

    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @OneToOne
    @JoinColumn(unique = true)
    private UserLogo userLogo;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public ExtendedUser user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserLogo getUserLogo() {
        return userLogo;
    }

    public ExtendedUser userLogo(UserLogo userLogo) {
        this.userLogo = userLogo;
        return this;
    }

    public void setUserLogo(UserLogo userLogo) {
        this.userLogo = userLogo;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExtendedUser)) {
            return false;
        }
        return id != null && id.equals(((ExtendedUser) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExtendedUser{" +
            "id=" + getId() +
            "}";
    }
}
