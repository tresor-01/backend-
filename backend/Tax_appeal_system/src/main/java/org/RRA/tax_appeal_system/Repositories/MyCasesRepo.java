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

    @Query("SELECT COUNT(mc) FROM MyCases mc WHERE mc.notePreparator = :preparator")
    int countTotalCasesByPreparator(@Param("preparator") String preparator);

    @Query("SELECT COUNT(mc) FROM MyCases mc WHERE mc.notePreparator = :preparator AND mc.status = :status")
    int countCasesByPreparatorAndStatus(@Param("preparator") String preparator, @Param("status") MyCasesStatus status);

    @Query("SELECT COUNT(mc) FROM MyCases mc WHERE mc.notePreparator = :preparator " +
            "AND mc.status = 'DISCUSSED'")
    int countReviewedCasesByPreparator(@Param("preparator") String preparator);

    @Query("SELECT FUNCTION('MONTH', mc.caseId.appealDate) as month,SUM(CASE WHEN mc.status = 'SUBMITTED' THEN 1 ELSE 0 END) as submittedCount, SUM(CASE WHEN mc.status = 'DISCUSSED' THEN 1 ELSE 0 END) as discussedCount FROM MyCases mc WHERE mc.notePreparator = :preparator " +
            "AND FUNCTION('YEAR', mc.caseId.appealDate) = FUNCTION('YEAR', CURRENT_DATE) GROUP BY FUNCTION('MONTH', mc.caseId.appealDate) ORDER BY month")
    List<Object[]> getCaseAnalyticsByMonth(@Param("preparator") String preparator);

    @Query("SELECT mc FROM MyCases mc WHERE mc.notePreparator = :preparator AND mc.caseId.appealDate >= :startDate")
    List<MyCases> findCasesThisWeek(@Param("preparator") String preparator, @Param("startDate") LocalDateTime startDate);
}
