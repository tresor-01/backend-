package org.RRA.tax_appeal_system.Services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.RRA.tax_appeal_system.DTOS.requests.VoteRequest;
import org.RRA.tax_appeal_system.Models.Appeals;
import org.RRA.tax_appeal_system.Models.CommitteeVote;
import org.RRA.tax_appeal_system.Repositories.AppealsRepo;
import org.RRA.tax_appeal_system.Repositories.CommitteeVoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VotingService {
    private final AppealsRepo appealRepository;
    private final CommitteeVoteRepository voteRepository;

    public List<Appeals> getAllAppeals() {
        return appealRepository.findAll();
    }

    public Appeals getAppealById(String appealId) {
        return appealRepository.findById(appealId).orElseThrow(() -> new RuntimeException("Appeal not found with id: " + appealId));
    }

    @Transactional
    public CommitteeVote submitVote(String appealId, VoteRequest voteRequest) {
        Appeals appeal = getAppealById(appealId);

        voteRepository.findByAppealIdAndCommitteeMemberId(appealId, voteRequest.getCommitteeMemberId()).ifPresent(vote -> {
            throw new RuntimeException("Committee member has already voted on this appeal");});

        CommitteeVote vote = new CommitteeVote();
        vote.setAppeal(appeal);
        vote.setCommitteeMemberId(voteRequest.getCommitteeMemberId());
        vote.setCommitteeMemberName(voteRequest.getCommitteeMemberName());
        vote.setCommitteeDecision(voteRequest.getCommitteeDecision());
        vote.setVoteComment(voteRequest.getVoteComment());

        return voteRepository.save(vote);
    }
}
