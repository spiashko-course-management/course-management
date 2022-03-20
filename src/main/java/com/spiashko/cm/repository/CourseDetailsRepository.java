package com.spiashko.cm.repository;

import com.spiashko.cm.domain.CourseDetails;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the CourseDetails entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CourseDetailsRepository extends JpaRepository<CourseDetails, Long> {}
