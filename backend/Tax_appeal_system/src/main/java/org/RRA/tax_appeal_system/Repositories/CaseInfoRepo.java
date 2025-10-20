package org.RRA.tax_appeal_system.Repositories;

import org.RRA.tax_appeal_system.Models.CaseInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaseInfoRepo extends JpaRepository<CaseInfo, String> {
    boolean existsByCaseId(String caseId);
    Optional<CaseInfo> findByCaseId(String caseId);

}
