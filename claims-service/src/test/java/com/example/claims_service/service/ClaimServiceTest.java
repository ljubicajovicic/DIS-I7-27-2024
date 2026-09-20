package com.example.claims_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.server.ResponseStatusException;

import com.example.claims_service.model.Claim;
import com.example.claims_service.repository.ClaimRepository;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock
    private ClaimRepository repository;

    @Mock
    private StreamBridge streamBridge;

    @InjectMocks
    private ClaimService claimService;

    private UUID claimId;
    private UUID policyId;
    private UUID customerId;
    private Claim claim;

    @BeforeEach
    void setUp() {
        claimId = UUID.randomUUID();
        policyId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        claim = new Claim();
        claim.setId(claimId);
        claim.setPolicyId(policyId);
        claim.setCustomerId(customerId);
        claim.setIncidentDate(LocalDate.now());
        claim.setReportedDate(LocalDateTime.now());
        claim.setDescription("Car accident");
        claim.setClaimAmount(new BigDecimal("1500.00"));
        claim.setStatus("PENDING");
    }

    @Test
    void shouldCreateClaim() {
        when(repository.save(claim)).thenReturn(claim);

        Claim result = claimService.create(claim);

        assertNotNull(result);
        assertEquals(claimId, result.getId());
        assertEquals(policyId, result.getPolicyId());
        assertEquals(customerId, result.getCustomerId());
        assertEquals("PENDING", result.getStatus());

        verify(repository).save(claim);
    }

    @Test
    void shouldGetClaimById() {
        when(repository.findById(claimId))
                .thenReturn(Optional.of(claim));

        Claim result = claimService.getById(claimId);

        assertNotNull(result);
        assertEquals(claimId, result.getId());
        assertEquals("PENDING", result.getStatus());

        verify(repository).findById(claimId);
    }

    @Test
    void shouldThrowExceptionWhenClaimDoesNotExist() {
        when(repository.findById(claimId))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> claimService.getById(claimId)
        );

        assertEquals(404, exception.getStatusCode().value());
        assertTrue(exception.getReason().contains("Claim not found"));

        verify(repository).findById(claimId);
    }

    @Test
    void shouldGetAllClaims() {
        Claim claim2 = new Claim();
        claim2.setId(UUID.randomUUID());

        when(repository.findAll())
                .thenReturn(List.of(claim, claim2));

        List<Claim> result = claimService.getAll();

        assertEquals(2, result.size());
        assertEquals(claimId, result.get(0).getId());

        verify(repository).findAll();
    }

    @Test
    void shouldGetClaimsByPolicyId() {
        when(repository.findByPolicyId(policyId))
                .thenReturn(List.of(claim));

        List<Claim> result = claimService.getByPolicyId(policyId);

        assertEquals(1, result.size());
        assertEquals(claimId, result.get(0).getId());

        verify(repository).findByPolicyId(policyId);
    }

    @Test
    void shouldGetClaimsByCustomerId() {
        when(repository.findByCustomerId(customerId))
                .thenReturn(List.of(claim));

        List<Claim> result = claimService.getByCustomerId(customerId);

        assertEquals(1, result.size());
        assertEquals(customerId, result.get(0).getCustomerId());

        verify(repository).findByCustomerId(customerId);
    }

    @Test
    void shouldUpdateClaimStatus() {
        when(repository.findById(claimId))
                .thenReturn(Optional.of(claim));

        when(repository.save(claim))
                .thenReturn(claim);

        Claim result = claimService.updateStatus(claimId, "PENDING");

        assertEquals("PENDING", result.getStatus());

        verify(repository).findById(claimId);
        verify(repository).save(claim);
        verify(streamBridge, never()).send(anyString(), any());
    }

    @Test
    void shouldUpdateClaimStatusAndPublishEventWhenApproved() {
        when(repository.findById(claimId))
                .thenReturn(Optional.of(claim));

        when(repository.save(claim))
                .thenReturn(claim);

        when(streamBridge.send(anyString(), any()))
                .thenReturn(true);

        Claim result = claimService.updateStatus(claimId, "APPROVED");

        assertEquals("APPROVED", result.getStatus());

        verify(repository).findById(claimId);
        verify(repository).save(claim);
        verify(streamBridge).send(eq("claimEvent-out-0"), any());
    }

    @Test
    void shouldDeleteClaim() {
        claimService.delete(claimId);

        verify(repository).deleteById(claimId);
    }
}