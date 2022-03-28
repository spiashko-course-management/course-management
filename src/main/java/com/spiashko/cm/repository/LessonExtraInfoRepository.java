package com.spiashko.cm.repository;

import com.spiashko.cm.domain.LessonExtraInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the LessonExtraInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LessonExtraInfoRepository extends JpaRepository<LessonExtraInfo, Long> {}
