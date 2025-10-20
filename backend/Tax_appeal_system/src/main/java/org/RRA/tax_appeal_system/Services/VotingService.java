package org.RRA.tax_appeal_system.Services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.RRA.tax_appeal_system.DTOS.requests.VoteRequest;
import org.RRA.tax_appeal_system.Enums.AppealStatus;
import org.RRA.tax_appeal_system.Enums.MyCasesStatus;
import org.RRA.tax_appeal_system.Enums.Privilege;
import org.RRA.tax_appeal_system.Models.Appeals;
import org.RRA.tax_appeal_system.Models.CommitteeVote;
import org.RRA.tax_appeal_system.Models.UserInfo;
import org.RRA.tax_appeal_system.Repositories.AppealsRepo;
import org.RRA.tax_appeal_system.Repositories.CommitteeVoteRepository;
import org.RRA.tax_appeal_system.Repositories.MyCasesRepo;
import org.RRA.tax_appeal_system.Repositories.UserInfoRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VotingService {
    private final AppealsRepo appealRepository;
    private final CommitteeVoteRepository voteRepository;
    private final UserInfoRepo userInfoRepository;
    private final MyCasesRepo myCasesRepo;

    public List<Appeals> getAllAppeals() {
        return appealRepository.findAll();
    }

    public Appeals getAppealById(UUID appealId) {
        return appealRepository.findById(appealId).orElseThrow(() -> new RuntimeException("Appeal not found with id: " + appealId));
    }

    @Transactional
    public CommitteeVote submitVote(UUID appealId, VoteRequest voteRequest) {
        Appeals appeal = getAppealById(appealId);

        UserInfo userInfo = userInfoRepository.findById(voteRequest.getCommitteeMemberId()).orElseThrow(() -> new RuntimeException("User not found"));

        boolean hasComment = voteRequest.getVoteComment() != null && !voteRequest.getVoteComment().trim().isEmpty();

        if (hasComment && userInfo.getCommitteeRole() != Privilege.COMMITTEE_LEADER) {
            throw new RuntimeException("Only COMMITTEE_LEADER can add vote comments");
        }

        voteRepository.findByAppealIdAndCommitteeMemberId(appealId, voteRequest.getCommitteeMemberId()).ifPresent(vote -> {throw new RuntimeException("Committee member has already voted on this appeal");});

        CommitteeVote vote = new CommitteeVote();
        vote.setAppeal(appeal);
        vote.setCommitteeMemberId(voteRequest.getCommitteeMemberId());
        vote.setCommitteeMemberName(voteRequest.getCommitteeMemberName());
        vote.setCommitteeDecision(voteRequest.getCommitteeDecision());
        if (userInfo.getCommitteeRole() == Privilege.COMMITTEE_LEADER && hasComment) {
            vote.setVoteComment(voteRequest.getVoteComment());}

        CommitteeVote savedVote = voteRepository.save(vote);
        updateMyCasesStatus(appeal, MyCasesStatus.DISCUSSED);
        return savedVote;
    }

    private void updateMyCasesStatus(Appeals appeal, MyCasesStatus status) {
        if (appeal.getCaseInfo() != null) {
            myCasesRepo.findByCaseId(appeal.getCaseInfo().getCaseId()).ifPresent(myCase -> {myCase.setStatus(status);myCasesRepo.save(myCase);
            });
        }
    }
}
