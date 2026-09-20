package com.example.notification_service.event;

import java.math.BigDecimal;
import java.util.UUID;

public class ClaimEvent {

    private UUID claimId;
    private UUID policyId;
    private UUID customerId;
    private BigDecimal claimAmount;
    private String status; // "APPROVED" ili "REJECTED"

    public ClaimEvent() {
    }

    public ClaimEvent(UUID claimId, UUID policyId, UUID customerId, BigDecimal claimAmount, String status) {
        this.claimId = claimId;
        this.policyId = policyId;
        this.customerId = customerId;
        this.claimAmount = claimAmount;
        this.status = status;
    }

    public UUID getClaimId() {
        return claimId;
    }

    public void setClaimId(UUID claimId) {
        this.claimId = claimId;
    }

    public UUID getPolicyId() {
        return policyId;
    }

    public void setPolicyId(UUID policyId) {
        this.policyId = policyId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getClaimAmount() {
        return claimAmount;
    }

    public void setClaimAmount(BigDecimal claimAmount) {
        this.claimAmount = claimAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}