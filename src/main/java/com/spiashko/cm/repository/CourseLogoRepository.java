package com.spiashko.cm.repository;

import com.spiashko.cm.domain.CourseLogo;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the CourseLogo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CourseLogoRepository extends JpaRepository<CourseLogo, Long> {
}
