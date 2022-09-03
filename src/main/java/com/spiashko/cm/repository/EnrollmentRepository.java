package com.spiashko.cm.repository;

import com.spiashko.cm.domain.Enrollment;

import java.util.List;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Enrollment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>, JpaSpecificationExecutor<Enrollment> {
    @Query("select enrollment from Enrollment enrollment where enrollment.student.login = ?#{principal.preferredUsername}")
    List<Enrollment> findByStudentIsCurrentUser();
}
