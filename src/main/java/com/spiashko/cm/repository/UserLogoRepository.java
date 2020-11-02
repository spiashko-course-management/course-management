package com.spiashko.cm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the UserLogo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserLogoRepository extends JpaRepository<UserLogo, Long> {
}
