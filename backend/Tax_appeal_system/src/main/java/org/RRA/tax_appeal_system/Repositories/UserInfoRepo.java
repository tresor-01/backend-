package org.RRA.tax_appeal_system.Repositories;

import org.RRA.tax_appeal_system.Models.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserInfoRepo extends JpaRepository<UserInfo, UUID> {
    Optional<UserInfo> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(int phoneNumber);
}
