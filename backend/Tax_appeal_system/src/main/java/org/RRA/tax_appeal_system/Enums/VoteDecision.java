package org.RRA.tax_appeal_system.Enums;

public enum VoteDecision {
    APPROVE("Approved"),
    REJECT("Rejected"),
    ABSTAIN("Abstained");

    private final String displayName;

    VoteDecision(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
