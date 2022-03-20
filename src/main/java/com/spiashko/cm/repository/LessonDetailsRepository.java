package com.spiashko.cm.repository;

import com.spiashko.cm.domain.LessonDetails;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LessonDetails entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LessonDetailsRepository extends JpaRepository<LessonDetails, Long> {}
