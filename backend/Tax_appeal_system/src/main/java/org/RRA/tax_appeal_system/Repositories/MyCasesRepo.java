package org.RRA.tax_appeal_system.Repositories;

import org.RRA.tax_appeal_system.Enums.MyCasesStatus;
import org.RRA.tax_appeal_system.Models.MyCases;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MyCasesRepo extends JpaRepository<MyCases, UUID> {

    List<MyCases> findByNotePreparator(String notePreparator);

    @Query("SELECT mc FROM MyCases mc WHERE mc.caseId.caseId = :caseId")
    Optional<MyCases> findByCaseId(@Param("caseId") UUID caseId);

    int countByStatus(MyCasesStatus status);

    @Query("SELECT COUNT(mc) FROM MyCases mc WHERE mc.status IN ('DISCUSSED')")
    int countReviewedCases();

    @Query("SELECT MONTH(mc.caseId.appealDate) as month, " +
            "SUM(CASE WHEN mc.status = 'DISCUSSED' THEN 1 ELSE 0 END) as justified, " +
            "SUM(CASE WHEN mc.status = 'SUBMITTED' THEN 1 ELSE 0 END) as unjustified " +
            "FROM MyCases mc " +
            "WHERE YEAR(mc.caseId.appealDate) = YEAR(CURRENT_DATE) " +
            "GROUP BY MONTH(mc.caseId.appealDate) " +
            "ORDER BY month")
    List<Object[]> getCaseAnalyticsByMonth();

    @Query("SELECT mc FROM MyCases mc WHERE mc.caseId.appealDate >= :startOfWeek")
    List<MyCases> findCasesThisWeek(@Param("startOfWeek") LocalDateTime startOfWeek);

}
