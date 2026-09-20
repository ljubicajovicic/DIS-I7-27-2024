package com.example.claim_service.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="claim")
public class Claim {
	
	@Id
    @GeneratedValue
    private UUID id;

    private UUID policyId;

    private UUID customerId;

    private LocalDate incidentDate;

    private LocalDateTime reportedDate;

    private String description;

    private BigDecimal claimAmount;

    private String status;
    
    public Claim() {
    }

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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

	public LocalDate getIncidentDate() {
		return incidentDate;
	}

	public void setIncidentDate(LocalDate incidentDate) {
		this.incidentDate = incidentDate;
	}

	public LocalDateTime getReportedDate() {
		return reportedDate;
	}

	public void setReportedDate(LocalDateTime reportedDate) {
		this.reportedDate = reportedDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
