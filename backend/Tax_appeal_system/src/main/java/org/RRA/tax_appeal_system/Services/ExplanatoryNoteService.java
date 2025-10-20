package org.RRA.tax_appeal_system.Services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.RRA.tax_appeal_system.DTOS.requests.AppealDetailsDTO;
import org.RRA.tax_appeal_system.DTOS.requests.AuditedTaxDTO;
import org.RRA.tax_appeal_system.DTOS.requests.ExplanatoryNoteDTO;
import org.RRA.tax_appeal_system.Enums.MyCasesStatus;
import org.RRA.tax_appeal_system.Exceptions.CaseNotFoundException;
import org.RRA.tax_appeal_system.Exceptions.DuplicateCaseSubmissionException;
import org.RRA.tax_appeal_system.Models.Appeals;
import org.RRA.tax_appeal_system.Models.CaseInfo;
import org.RRA.tax_appeal_system.Models.MyCases;
import org.RRA.tax_appeal_system.Models.TaxAudited;
import org.RRA.tax_appeal_system.Repositories.AppealsRepo;
import org.RRA.tax_appeal_system.Repositories.CaseInfoRepo;
import org.RRA.tax_appeal_system.Repositories.MyCasesRepo;
import org.RRA.tax_appeal_system.Repositories.TaxAuditedRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExplanatoryNoteService {

    private final CaseInfoRepo caseInfoRepo;
    private final MyCasesRepo myCasesRepo;
    private final AppealsRepo appealsRepo;
    private final TaxAuditedRepo taxAuditedRepo;


    //Generating a new explanatory note
    @Transactional
    public void generateExplanatoryNote(ExplanatoryNoteDTO explanatoryNote,String notePreparatorEmail) {

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

        // saving in MyCases Entity
        saveMyCases(explanatoryNote.caseId(), notePreparatorEmail);

    }

    @Transactional
    public ExplanatoryNoteDTO getExplanatoryNoteByCaseId(String caseId) {
        // Find the case info by caseId
        CaseInfo caseInfo = caseInfoRepo.findByCaseId(caseId)
                .orElseThrow(() -> new CaseNotFoundException("Case with ID " + caseId + " not found"));

        // Find all tax audited records for this case
        List<TaxAudited> taxAuditedList = taxAuditedRepo.findByCaseId(caseInfo);

        // Convert to DTOs
        List<AuditedTaxDTO> taxAuditedDTOs = taxAuditedList.stream()
                .map(this::convertToAuditedTaxDTO)
                .collect(Collectors.toList());

        // Build and return the main explanatory note response
        return new ExplanatoryNoteDTO(
                caseInfo.getCaseId(),
                caseInfo.getAuditorsNames(),
                caseInfo.getTaxAssessmentAcknowledgementDateByTaxpayer(),
                caseInfo.getTaxAssessmentTime(),
                caseInfo.getAppealDate(),
                caseInfo.getAppealExpireDate(),
                caseInfo.getCasePresenter(),
                caseInfo.getTin(),
                caseInfo.getAttachmentLink(),
                taxAuditedDTOs
        );
    }

    private AuditedTaxDTO convertToAuditedTaxDTO(TaxAudited taxAudited) {
        // Find all appeals for this tax audited record
        List<Appeals> appealsList = appealsRepo.findByTaxAuditedId(taxAudited);

        // Convert appeals to DTOs
        List<AppealDetailsDTO> appealDTOs = appealsList.stream()
                .map(this::convertToAppealDetailsDTO)
                .collect(Collectors.toList());

        return new AuditedTaxDTO(
                taxAudited.getAuditedTaxType(),
                taxAudited.getPrincipalAmountToBePaid(),
                taxAudited.getUnderstatementFines(),
                taxAudited.getFixedAdministrativeFines(),
                taxAudited.getDischargedAmount(),
                taxAudited.getTotalTaxAndFinesToBePaid(),
                appealDTOs
        );
    }

    private AppealDetailsDTO convertToAppealDetailsDTO(Appeals appeal) {
        return new AppealDetailsDTO(
                appeal.getAppealPoint(),
                appeal.getSummarisedProblem(),
                appeal.getAuditorsOpinion(),
                appeal.getProposedSolution()
        );
    }


    @Transactional
    public void saveMyCases(String caseId,String email){
        CaseInfo caseInfo = caseInfoRepo.findByCaseId(caseId).orElseThrow();

        // saving also in myCases Entity
        MyCases myCasesEntity = MyCases.builder()
                .caseId(caseInfo)
                .notePreparator(email)
                .status(MyCasesStatus.SUBMITTED)
                .build();
        myCasesRepo.save(myCasesEntity);
    }

}
