package org.RRA.tax_appeal_system.Repositories;

import org.RRA.tax_appeal_system.Models.MyCases;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MyCasesRepo extends JpaRepository<MyCases, UUID> {

    List<MyCases> findByNotePreparator(String notePreparator);
    @Query("SELECT mc FROM MyCases mc WHERE mc.caseId.caseId = :caseId")
    Optional<MyCases> findByCaseId(@Param("caseId") UUID caseId);
}
