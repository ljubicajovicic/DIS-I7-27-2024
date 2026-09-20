package com.example.policy_service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.policy_service.model.Policy;
import com.example.policy_service.repository.PolicyRepository;

@Service
public class PolicyService {
	
	private final PolicyRepository repository;
	
	public PolicyService(PolicyRepository repository) {
		this.repository = repository;
	}
	
	public Policy create(Policy policy) {
		return repository.save(policy);
	}
	
	public Policy getById(UUID id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found with id: " + id));
	}
	
	public List<Policy> getAll() {
		return repository.findAll();
	}
	
	public Policy update(UUID id, Policy updated) {
		Policy existing = getById(id);
		existing.setPolicyNumber(updated.getPolicyNumber());
		existing.setEffectiveDate(updated.getEffectiveDate());
		existing.setExpirationDate(updated.getExpirationDate());
		existing.setPremiumAmount(updated.getPremiumAmount());
		existing.setStatus(updated.getStatus());
		existing.setType(updated.getType());
		existing.setCustomerId(updated.getCustomerId());
		return repository.save(existing);
	}
	
	public void delete(UUID id) {
		repository.deleteById(id);
	}
}
