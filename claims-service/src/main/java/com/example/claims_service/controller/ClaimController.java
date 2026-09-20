package com.example.claims_service.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.claims_service.model.Claim;
import com.example.claims_service.service.ClaimService;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/claims")
public class ClaimController {
	private final ClaimService service;

    public ClaimController(ClaimService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Claim create(@RequestBody Claim claim) {
        return service.create(claim);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Claim getById(@PathVariable("id") UUID id) {
        return service.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Claim> getAll(
            @RequestParam(value = "policyId", required = false) UUID policyId,
            @RequestParam(value = "customerId", required = false) UUID customerId) {

        if (policyId != null) {
            return service.getByPolicyId(policyId);
        }
        if (customerId != null) {
            return service.getByCustomerId(customerId);
        }
        return service.getAll();
    }

    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public Claim updateStatus(@PathVariable("id") UUID id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return service.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") UUID id) {
        service.delete(id);
    }
}
