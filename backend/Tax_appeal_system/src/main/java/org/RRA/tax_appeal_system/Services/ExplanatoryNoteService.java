package org.RRA.tax_appeal_system.Services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.RRA.tax_appeal_system.DTOS.requests.AppealDetailsDTO;
import org.RRA.tax_appeal_system.DTOS.requests.AuditedTaxDTO;
import org.RRA.tax_appeal_system.DTOS.requests.ExplanatoryNoteDTO;
import org.RRA.tax_appeal_system.DTOS.responses.GenericResponse;
import org.RRA.tax_appeal_system.Exceptions.DuplicateCaseSubmissionException;
import org.RRA.tax_appeal_system.Models.Appeals;
import org.RRA.tax_appeal_system.Models.CaseInfo;
import org.RRA.tax_appeal_system.Models.TaxAudited;
import org.RRA.tax_appeal_system.Repositories.AppealsRepo;
import org.RRA.tax_appeal_system.Repositories.CaseInfoRepo;
import org.RRA.tax_appeal_system.Repositories.TaxAuditedRepo;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExplanatoryNoteService {

    private final CaseInfoRepo caseInfoRepo;
    private final AppealsRepo appealsRepo;
    private final TaxAuditedRepo taxAuditedRepo;


    //Generating a new explanatory note
    @Transactional
    public GenericResponse generateExplanatoryNote(ExplanatoryNoteDTO explanatoryNote) {

        // Check if an explanatory note wit the same case Id it was not submitted already
        if(caseInfoRepo.existsByCaseId(explanatoryNote.caseId())) {
            throw new DuplicateCaseSubmissionException(explanatoryNote.caseId());
        }


        // saving for Case Info
        CaseInfo caseInfoEntity = CaseInfo.builder()
                .caseId(explanatoryNote.caseId())
                .auditorsNames(explanatoryNote.auditorsName())
                .taxAssessmentAcknowledgementDateByTaxpayer(explanatoryNote.taxAssessmentAcknowledgementDateByTaxpayer())
                .taxAssessmentTime(explanatoryNote.taxAssessmentTime())
                .appealDate(explanatoryNote.appealDate())
                .appealExpireDate(explanatoryNote.appealExpireDate())
                .casePresenter(explanatoryNote.casePresenter())
                .tin(explanatoryNote.tin())
                .attachmentLink(explanatoryNote.attachmentLink())
                .build();

        CaseInfo savedCaseInfo = caseInfoRepo.save(caseInfoEntity);

        //Saving for audited Tax by mapping through them
        for (AuditedTaxDTO auditedTaxDTO: explanatoryNote.taxAudited()) {

            TaxAudited taxAuditedEntity = TaxAudited.builder()
                    .auditedTaxType(auditedTaxDTO.taxTypeAudited())
                    .principalAmountToBePaid(auditedTaxDTO.principalAmountToBePaid())
                    .understatementFines(auditedTaxDTO.understatementFines())
                    .fixedAdministrativeFines(auditedTaxDTO.fixedAdministrativeFines())
                    .dischargedAmount(auditedTaxDTO.dischargedAmount())
                    .totalTaxAndFinesToBePaid(auditedTaxDTO.totalTaxAndFinesToBePaid())
                    .caseId(savedCaseInfo)
                    .build();

            TaxAudited savedTaxAudited = taxAuditedRepo.save(taxAuditedEntity);


            //saving for appeals by mapping through them
            for (AppealDetailsDTO appealDetailsDTO : auditedTaxDTO.appeals()){
                Appeals appealsEntity =  Appeals.builder()
                        .appealPoint(appealDetailsDTO.appealPoint())
                        .summarisedProblem(appealDetailsDTO.summarisedProblem())
                        .auditorsOpinion(appealDetailsDTO.auditorsOpinion())
                        .proposedSolution(appealDetailsDTO.proposedSolution())
                        .taxAuditedId(savedTaxAudited)
                        .build();


                appealsRepo.save(appealsEntity);
            }
        }
        return new GenericResponse(200,"ExplanatoryNote with case ID "+ explanatoryNote.caseId() +" generated successfully");
    }
}
