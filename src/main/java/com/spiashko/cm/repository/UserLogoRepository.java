package com.spiashko.cm.repository;

import com.spiashko.cm.domain.UserLogo;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the UserLogo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserLogoRepository extends JpaRepository<UserLogo, Long> {
}
