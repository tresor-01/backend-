package org.RRA.tax_appeal_system.Services;

import org.RRA.tax_appeal_system.Models.CaseAnalytics;
import org.RRA.tax_appeal_system.Repositories.CaseAnalyticsRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CaseAnalyticsService {

    private final CaseAnalyticsRepository repository;

    public CaseAnalyticsService(CaseAnalyticsRepository repository) {
        this.repository = repository;
    }

    public List<CaseAnalytics> getAllAnalytics() {
        return repository.findAll();
    }
}
