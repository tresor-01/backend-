package org.RRA.tax_appeal_system.Models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Appeals {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "appeal_id")
    private String appealId;

    @Column(nullable = false,name = "appeal_points")
    private String appealPoint;

    @Lob
    @Column(nullable = false,name = "summarised_problem")
    private String summarisedProblem;

    @Lob
    @Column(nullable = false,name = "auditors_opinion")
    private String auditorsOpinion;

    @Lob
    @Column(nullable = false,name = "proposed_solution")
    private String proposedSolution;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tax_audited_id",nullable = false)
    private TaxAudited taxAuditedId;

}
