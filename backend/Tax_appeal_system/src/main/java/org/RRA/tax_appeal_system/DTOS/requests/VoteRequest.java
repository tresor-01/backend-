package org.RRA.tax_appeal_system.DTOS.requests;

import lombok.Data;
import org.RRA.tax_appeal_system.Enums.VoteDecision;

@Data
public class VoteRequest {
    private String committeeMemberId;
    private String committeeMemberName;
    private VoteDecision committeeDecision;
    private String voteComment;
}
