package com.spiashko.cm.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spiashko.cm.crudbase.entity.BaseJournalEntity;
import com.spiashko.cm.domain.enumeration.LessonType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

/**
 * A Lesson.
 */
@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "lesson")
public class Lesson extends BaseJournalEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @Column(name = "cm_order", nullable = false)
    private Integer order;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private LessonType type;

    //TODO need to check if this lazy really lazy most probably we will need to remove it
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = "lesson", allowSetters = true)
    private LessonDetails lessonDetails;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JsonIgnoreProperties(value = "lessons", allowSetters = true)
    private Module module;

    // optional relations not related to aggregate but may be used for view

    @OneToMany(mappedBy = "lesson")
    private Set<CompletedLesson> completedLessons;

}
