package com.example.policy_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.example.policy_service.model.Policy;
import com.example.policy_service.repository.PolicyRepository;

@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    @Mock
    private PolicyRepository repository;

    @InjectMocks
    private PolicyService policyService;

    private UUID policyId;
    private UUID customerId;
    private Policy policy;

    @BeforeEach
    void setUp() {
        policyId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        policy = new Policy();
        policy.setId(policyId);
        policy.setPolicyNumber("POL-001");
        policy.setCustomerId(customerId);
        policy.setType("LIFE");
        policy.setEffectiveDate(LocalDate.now());
        policy.setExpirationDate(LocalDate.now().plusYears(1));
        policy.setPremiumAmount(new BigDecimal("1200.00"));
        policy.setStatus("ACTIVE");
    }

    @Test
    void shouldCreatePolicy() {
        when(repository.save(policy)).thenReturn(policy);

        Policy result = policyService.create(policy);

        assertNotNull(result);
        assertEquals(policyId, result.getId());
        assertEquals("POL-001", result.getPolicyNumber());
        assertEquals(customerId, result.getCustomerId());
        assertEquals("LIFE", result.getType());
        assertEquals(new BigDecimal("1200.00"), result.getPremiumAmount());
        assertEquals("ACTIVE", result.getStatus());

        verify(repository).save(policy);
    }

    @Test
    void shouldGetPolicyById() {
        when(repository.findById(policyId))
                .thenReturn(Optional.of(policy));

        Policy result = policyService.getById(policyId);

        assertNotNull(result);
        assertEquals(policyId, result.getId());
        assertEquals("POL-001", result.getPolicyNumber());
        assertEquals("ACTIVE", result.getStatus());

        verify(repository).findById(policyId);
    }

    @Test
    void shouldThrowExceptionWhenPolicyDoesNotExist() {
        when(repository.findById(policyId))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> policyService.getById(policyId)
        );

        assertEquals(404, exception.getStatusCode().value());
        assertTrue(exception.getReason().contains("Policy not found"));

        verify(repository).findById(policyId);
    }

    @Test
    void shouldGetAllPolicies() {
        Policy policy2 = new Policy();
        policy2.setId(UUID.randomUUID());

        when(repository.findAll())
                .thenReturn(List.of(policy, policy2));

        List<Policy> result = policyService.getAll();

        assertEquals(2, result.size());
        assertEquals(policyId, result.get(0).getId());

        verify(repository).findAll();
    }

    @Test
    void shouldUpdatePolicy() {
        when(repository.findById(policyId))
                .thenReturn(Optional.of(policy));

        Policy updatedPolicy = new Policy();
        updatedPolicy.setPolicyNumber("POL-002");
        updatedPolicy.setCustomerId(customerId);
        updatedPolicy.setType("HEALTH");
        updatedPolicy.setEffectiveDate(LocalDate.of(2026, 10, 1));
        updatedPolicy.setExpirationDate(LocalDate.of(2027, 10, 1));
        updatedPolicy.setPremiumAmount(new BigDecimal("1500.00"));
        updatedPolicy.setStatus("EXPIRED");

        when(repository.save(policy))
                .thenReturn(policy);

        Policy result = policyService.update(policyId, updatedPolicy);

        assertNotNull(result);
        assertEquals(policyId, result.getId());
        assertEquals("POL-002", result.getPolicyNumber());
        assertEquals(customerId, result.getCustomerId());
        assertEquals("HEALTH", result.getType());
        assertEquals(LocalDate.of(2026, 10, 1), result.getEffectiveDate());
        assertEquals(LocalDate.of(2027, 10, 1), result.getExpirationDate());
        assertEquals(new BigDecimal("1500.00"), result.getPremiumAmount());
        assertEquals("EXPIRED", result.getStatus());

        verify(repository).findById(policyId);
        verify(repository).save(policy);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingPolicy() {
        when(repository.findById(policyId))
                .thenReturn(Optional.empty());

        Policy updatedPolicy = new Policy();
        updatedPolicy.setPolicyNumber("POL-002");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> policyService.update(policyId, updatedPolicy)
        );

        assertEquals(404, exception.getStatusCode().value());
        assertTrue(exception.getReason().contains("Policy not found"));

        verify(repository).findById(policyId);
        verify(repository, never()).save(any(Policy.class));
    }

    @Test
    void shouldDeletePolicy() {
        policyService.delete(policyId);

        verify(repository).deleteById(policyId);
    }
}
