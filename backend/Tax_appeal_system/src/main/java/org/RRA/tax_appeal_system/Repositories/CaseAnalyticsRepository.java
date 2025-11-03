package org.RRA.tax_appeal_system.Repositories;

import org.RRA.tax_appeal_system.Models.CaseAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseAnalyticsRepository extends JpaRepository<CaseAnalytics, Long> {
}
