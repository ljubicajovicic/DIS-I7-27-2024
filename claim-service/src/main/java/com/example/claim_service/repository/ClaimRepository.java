package com.example.claim_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.claim_service.model.Claim;

public interface ClaimRepository extends JpaRepository<Claim, UUID> {
	List<Claim> findByPolicyId(UUID policyId);
    List<Claim> findByCustomerId(UUID customerId);
}
