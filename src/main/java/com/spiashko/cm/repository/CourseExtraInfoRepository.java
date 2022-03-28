package com.spiashko.cm.repository;

import com.spiashko.cm.domain.CourseExtraInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the CourseExtraInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CourseExtraInfoRepository extends JpaRepository<CourseExtraInfo, Long> {}
