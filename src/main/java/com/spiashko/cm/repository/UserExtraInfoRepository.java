package com.spiashko.cm.repository;

import com.spiashko.cm.domain.UserExtraInfo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the UserExtraInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserExtraInfoRepository extends JpaRepository<UserExtraInfo, String> {}
