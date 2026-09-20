package com.example.policy_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.policy_service.model.Policy;

public interface PolicyRepository extends JpaRepository<Policy, UUID> {
	List<Policy> findByCustomerId(UUID customerId);
}
