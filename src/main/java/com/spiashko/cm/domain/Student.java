package com.spiashko.cm.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

/**
 * A Student.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User {

    @OneToMany(mappedBy = "student")
    private Set<Enrollment> enrollments;

    @OneToMany(mappedBy = "student")
    private Set<Payment> payments;

}
