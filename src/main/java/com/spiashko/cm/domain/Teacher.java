package com.spiashko.cm.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

/**
 * A Teacher.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("TEACHER")
public class Teacher extends User {

    @OneToMany(mappedBy = "teacher")
    private Set<Course> courses;

}
