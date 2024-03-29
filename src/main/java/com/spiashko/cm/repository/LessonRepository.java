package com.spiashko.cm.repository;

import com.spiashko.cm.domain.Lesson;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Lesson entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {}
