package org.RRA.tax_appeal_system.Services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.RRA.tax_appeal_system.DTOS.DashboardAnalyticsDto;
import org.RRA.tax_appeal_system.DTOS.responses.MyCaseDTO;
import org.RRA.tax_appeal_system.Enums.MyCasesStatus;
import org.RRA.tax_appeal_system.Models.CaseInfo;
import org.RRA.tax_appeal_system.Models.MyCases;
import org.RRA.tax_appeal_system.Repositories.MyCasesRepo;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Service
@RequiredArgsConstructor
public class MyCasesService {
    private final MyCasesRepo myCasesRepo;


    @Transactional
    public List<MyCaseDTO> getMyCasesByPreparator(String notePreparator) {
        List<MyCases> myCasesList = myCasesRepo.findByNotePreparator(notePreparator);

        return myCasesList.stream()
                .map(this::convertToMyCaseDTO)
                .toList();
    }


    public DashboardAnalyticsDto getDashboardAnalytics() {
        DashboardAnalyticsDto analytics = new DashboardAnalyticsDto();

        int totalCases = (int) myCasesRepo.count();
        analytics.setTotalCases(totalCases);

        int pendingCases = myCasesRepo.countByStatus(MyCasesStatus.SUBMITTED);
        analytics.setPendingCases(pendingCases);

        int reviewedCases = myCasesRepo.countReviewedCases();
        double reviewRate = totalCases > 0 ? (reviewedCases * 100.0 / totalCases) : 0.0;
        analytics.setReviewRate(Math.round(reviewRate * 100.0) / 100.0);

        List<Object[]> monthlyData = myCasesRepo.getCaseAnalyticsByMonth();
        List<DashboardAnalyticsDto.CaseAnalyticsData> caseAnalytics = new ArrayList<>();

        for (Object[] data : monthlyData) {
            int month = ((Number) data[0]).intValue();
            long justifiedCount = ((Number) data[1]).longValue();
            long unjustifiedCount = ((Number) data[2]).longValue();

            String monthName = java.time.Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            caseAnalytics.add(new DashboardAnalyticsDto.CaseAnalyticsData(monthName, (int) justifiedCount, (int) unjustifiedCount));
        }
        analytics.setCaseAnalytics(caseAnalytics);
        int totalMembers = 7;
        int availableMembers = 7;
        analytics.setAttendanceList(
                new DashboardAnalyticsDto.AttendanceData(totalMembers, availableMembers)
        );

        return analytics;
    }

    public List<MyCaseDTO> getCasesForThisWeek() {
        LocalDateTime startOfWeek = LocalDateTime.now()
                .with(java.time.DayOfWeek.MONDAY)
                .toLocalDate()
                .atStartOfDay();

        List<MyCases> weekCases = myCasesRepo.findCasesThisWeek(startOfWeek);

        return weekCases.stream()
                .map(this::convertToMyCaseDTO)
                .toList();
    }

    private MyCaseDTO convertToMyCaseDTO(MyCases myCase) {
        CaseInfo caseInfo = myCase.getCaseId();

        long milliseconds = caseInfo.getAppealExpireDate().getTime() - System.currentTimeMillis();
        int daysLeft = milliseconds > 0 ? (int) (milliseconds / (1000 * 60 * 60 * 24)) : 0;

        String submittedAt = new SimpleDateFormat("yyyy-MM-dd").format(caseInfo.getAppealDate());

        return new MyCaseDTO(
                caseInfo.getCaseId(),
                caseInfo.getTin(),
                submittedAt,
                String.valueOf(daysLeft),
                myCase.getStatus()
        );
    }
}