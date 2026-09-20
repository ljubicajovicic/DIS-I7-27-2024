package com.example.claim_service.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;

import com.example.claim_service.event.ClaimEvent;
import com.example.claim_service.model.Claim;
import com.example.claim_service.repository.ClaimRepository;

import java.util.List;

@Service
public class ClaimService {

	private final ClaimRepository repository;
	private final StreamBridge streamBridge;

    public ClaimService(ClaimRepository repository, StreamBridge streamBridge) {
        this.repository = repository;
        this.streamBridge = streamBridge;
    }

    public Claim create(Claim claim) {
        return repository.save(claim);
    }

    public Claim getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Claim not found with id: " + id));
    }

    public List<Claim> getAll() {
        return repository.findAll();
    }

    public List<Claim> getByPolicyId(UUID policyId) {
        return repository.findByPolicyId(policyId);
    }

    public List<Claim> getByCustomerId(UUID customerId) {
        return repository.findByCustomerId(customerId);
    }
    
    public Claim updateStatus(UUID id, String status) {
        Claim existing = getById(id);
        existing.setStatus(status);
        Claim saved = repository.save(existing);

        if ("APPROVED".equals(status) || "REJECTED".equals(status)) {
            ClaimEvent event = new ClaimEvent(
                    saved.getId(),
                    saved.getPolicyId(),
                    saved.getCustomerId(),
                    saved.getClaimAmount(),
                    saved.getStatus()
            );
            streamBridge.send("claimEvent-out-0", event);
        }

        return saved;
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
