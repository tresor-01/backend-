package org.RRA.tax_appeal_system.Services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.RRA.tax_appeal_system.DTOS.responses.MyCaseDTO;
import org.RRA.tax_appeal_system.Models.CaseInfo;
import org.RRA.tax_appeal_system.Models.MyCases;
import org.RRA.tax_appeal_system.Repositories.MyCasesRepo;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.List;


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

    private MyCaseDTO convertToMyCaseDTO(MyCases myCase) {
        CaseInfo caseInfo = myCase.getCaseId();

        // Calculate days left
        long milliseconds = caseInfo.getAppealExpireDate().getTime() - System.currentTimeMillis();
        int daysLeft = milliseconds > 0 ? (int) (milliseconds / (1000 * 60 * 60 * 24)) : 0;

        // Format date
        String submittedAt = new SimpleDateFormat("yyyy-MM-dd").format(caseInfo.getAppealDate());

        return new MyCaseDTO(
                caseInfo.getCaseId(),
                caseInfo.getTin(),
                submittedAt,
                String.valueOf(daysLeft), // Just the number as string
                myCase.getStatus()
        );
    }
}
