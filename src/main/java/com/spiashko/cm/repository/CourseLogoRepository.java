package com.spiashko.cm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the CourseLogo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CourseLogoRepository extends JpaRepository<CourseLogo, Long> {
}
