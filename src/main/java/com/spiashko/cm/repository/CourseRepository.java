package com.spiashko.cm.repository;

import com.spiashko.cm.domain.Course;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Course entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("select course from Course course where course.teacher.login = ?#{principal.preferredUsername}")
    List<Course> findByTeacherIsCurrentUser();
}
