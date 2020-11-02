package com.spiashko.cm.domain;

import com.spiashko.cm.crudbase.entity.BaseJournalEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

/**
 * A CourseDetails.
 */
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "course_details")
public class CourseDetails extends BaseJournalEntity<UUID> {

    @Id
    private UUID id;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "summary", nullable = false)
    private String summary;

    //TODO create EntityById for Set<>
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "course_details_artifact",
        joinColumns = {@JoinColumn(name = "course_details_id")},
        inverseJoinColumns = {@JoinColumn(name = "artifact_id")}
    )
    private Set<Artifact> artifacts;

    @NotNull
    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id")
    private Course course;

}
