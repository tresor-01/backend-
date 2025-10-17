package org.RRA.tax_appeal_system.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.RRA.tax_appeal_system.DTOS.requests.VoteRequest;
import org.RRA.tax_appeal_system.Models.CommitteeVote;
import org.RRA.tax_appeal_system.Services.VotingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/appeals")
public class VotingController {
    private final VotingService votingService;
    @PostMapping("/{appealId}/vote")
    public ResponseEntity<Map<String, Object>> submitVote(@PathVariable String appealId, @Valid @RequestBody VoteRequest voteRequest) {
        try {
            CommitteeVote vote = votingService.submitVote(appealId, voteRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Vote submitted successfully");
            response.put("vote", Map.of("id", vote.getId(), "committeeMemberName", vote.getCommitteeMemberName(), "committeeDecision", vote.getCommitteeDecision(), "votedAt", vote.getVotedAt()));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }
}
