package org.RRA.tax_appeal_system.DTOS.requests;

import java.util.List;

public record AuditedTaxDTO(
        String taxTypeAudited,
        int principalAmountToBePaid,
        int understatementFines,
        int fixedAdministrativeFines,
        int dischargedAmount,
        int totalTaxAndFinesToBePaid,
        List<AppealDetailsDTO> appeals
) {
}
