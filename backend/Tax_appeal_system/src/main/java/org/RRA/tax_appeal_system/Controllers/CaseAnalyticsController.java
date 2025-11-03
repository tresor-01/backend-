package org.RRA.tax_appeal_system.Controllers;

import org.RRA.tax_appeal_system.Models.CaseAnalytics;
import org.RRA.tax_appeal_system.Services.CaseAnalyticsService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*") // Allow frontend to call it
public class CaseAnalyticsController {

    private final CaseAnalyticsService service;

    public CaseAnalyticsController(CaseAnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/cases")
    public List<CaseAnalytics> getAllCaseAnalytics() {
        return service.getAllAnalytics();
    }
}
