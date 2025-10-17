package org.RRA.tax_appeal_system.DTOS.responses;

import org.RRA.tax_appeal_system.Enums.MyCasesStatus;

public record MyCaseDTO(
        String caseId,
        String taxPayer,
        String submittedAt,
        String daysLeft,
        MyCasesStatus status
) {
}
