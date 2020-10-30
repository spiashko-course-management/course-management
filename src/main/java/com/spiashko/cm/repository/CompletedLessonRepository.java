package com.spiashko.cm.repository;

import com.spiashko.cm.domain.CompletedLesson;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the CompletedLesson entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompletedLessonRepository extends JpaRepository<CompletedLesson, Long> {
}
