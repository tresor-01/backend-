package org.RRA.tax_appeal_system.Models;

import jakarta.persistence.*;
import lombok.Data;
import org.RRA.tax_appeal_system.Enums.VoteDecision;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name ="committee_votes")
@Data
public class CommitteeVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "appeal_id", nullable = false)
    private Appeals appeal;

    @Column(nullable = false)
    private UUID committeeMemberId;

    @Column(nullable = false)
    private String committeeMemberName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteDecision committeeDecision;
    @Column()
    private String voteComment;

    @Column(nullable = false)
    private LocalDateTime votedAt = LocalDateTime.now();

}
