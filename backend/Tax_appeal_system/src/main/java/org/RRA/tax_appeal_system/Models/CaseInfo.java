package org.RRA.tax_appeal_system.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CaseInfo {
    @Id
    @Column(unique = true,nullable = false,name = "case_id")
    private UUID caseId;

    @Column(nullable = false,name = "auditor_names")
    private String auditorsNames;

    @Column(nullable = false, name = "tax_assessment_acknowledgement_date_by_taxpayer")
    private Date taxAssessmentAcknowledgementDateByTaxpayer;

    @Column(nullable = false,name = "tax_assessment_time")
    private String taxAssessmentTime;

    @Column(nullable = false,name = "appeal_date")
    private Date appealDate;

    @Column(nullable = false,name = "appeal_expire_date")
    private Date appealExpireDate;

    @Column(nullable = false, name = "case_presenter")
    private String casePresenter;

    @Column(nullable = false,name = "tin")
    private String tin;

    @Column(nullable = false,name = "attachment_link")
    private String attachmentLink;

    @OneToMany(mappedBy = "caseId")
    private List<TaxAudited> taxAudited;

    @OneToOne(mappedBy = "caseId",cascade = CascadeType.ALL)
    private MyCases myCases;
}

