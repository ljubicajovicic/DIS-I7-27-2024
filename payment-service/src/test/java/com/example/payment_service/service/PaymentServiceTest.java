package com.example.payment_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
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
import org.springframework.web.server.ResponseStatusException;

import com.example.payment_service.model.Payment;
import com.example.payment_service.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository repository;

    @InjectMocks
    private PaymentService paymentService;

    private UUID paymentId;
    private UUID claimId;
    private UUID policyId;
    private UUID customerId;
    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        claimId = UUID.randomUUID();
        policyId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        payment = new Payment();
        payment.setId(paymentId);
        payment.setClaimId(claimId);
        payment.setPolicyId(policyId);
        payment.setCustomerId(customerId);
        payment.setType("PAYOUT");
        payment.setAmount(new BigDecimal("1500.00"));
        payment.setStatus("PENDING");
        payment.setPaymentDate(LocalDateTime.now());
    }

    @Test
    void shouldCreatePayment() {
        when(repository.save(payment)).thenReturn(payment);

        Payment result = paymentService.create(payment);

        assertNotNull(result);
        assertEquals(paymentId, result.getId());
        assertEquals(claimId, result.getClaimId());
        assertEquals(policyId, result.getPolicyId());
        assertEquals(customerId, result.getCustomerId());
        assertEquals("PAYOUT", result.getType());
        assertEquals(new BigDecimal("1500.00"), result.getAmount());
        assertEquals("PENDING", result.getStatus());

        verify(repository).save(payment);
    }

    @Test
    void shouldRejectPayoutWithoutClaimId() {
        Payment invalidPayment = new Payment();
        invalidPayment.setType("PAYOUT");
        invalidPayment.setAmount(new BigDecimal("1500.00"));
        invalidPayment.setStatus("PENDING");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> paymentService.create(invalidPayment)
        );

        assertEquals(400, exception.getStatusCode().value());
        assertTrue(exception.getReason().contains("PAYOUT payment must have a claimId"));

        verify(repository, never()).save(any(Payment.class));
    }

    @Test
    void shouldCreateNonPayoutPaymentWithoutClaimId() {
        Payment paymentWithoutClaim = new Payment();
        paymentWithoutClaim.setType("PREMIUM");
        paymentWithoutClaim.setAmount(new BigDecimal("500.00"));
        paymentWithoutClaim.setStatus("COMPLETED");

        when(repository.save(paymentWithoutClaim))
                .thenReturn(paymentWithoutClaim);

        Payment result = paymentService.create(paymentWithoutClaim);

        assertNotNull(result);
        assertEquals("PREMIUM", result.getType());
        assertNull(result.getClaimId());

        verify(repository).save(paymentWithoutClaim);
    }

    @Test
    void shouldGetPaymentById() {
        when(repository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        Payment result = paymentService.getById(paymentId);

        assertNotNull(result);
        assertEquals(paymentId, result.getId());
        assertEquals(claimId, result.getClaimId());

        verify(repository).findById(paymentId);
    }

    @Test
    void shouldThrowExceptionWhenPaymentDoesNotExist() {
        when(repository.findById(paymentId))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> paymentService.getById(paymentId)
        );

        assertEquals(404, exception.getStatusCode().value());
        assertTrue(exception.getReason().contains("Payment not found"));

        verify(repository).findById(paymentId);
    }

    @Test
    void shouldGetAllPayments() {
        Payment payment2 = new Payment();
        payment2.setId(UUID.randomUUID());

        when(repository.findAll())
                .thenReturn(List.of(payment, payment2));

        List<Payment> result = paymentService.getAll();

        assertEquals(2, result.size());
        assertEquals(paymentId, result.get(0).getId());

        verify(repository).findAll();
    }

    @Test
    void shouldGetPaymentsByClaimId() {
        when(repository.findByClaimId(claimId))
                .thenReturn(List.of(payment));

        List<Payment> result = paymentService.getByClaimId(claimId);

        assertEquals(1, result.size());
        assertEquals(claimId, result.get(0).getClaimId());

        verify(repository).findByClaimId(claimId);
    }

    @Test
    void shouldGetPaymentsByPolicyId() {
        when(repository.findByPolicyId(policyId))
                .thenReturn(List.of(payment));

        List<Payment> result = paymentService.getByPolicyId(policyId);

        assertEquals(1, result.size());
        assertEquals(policyId, result.get(0).getPolicyId());

        verify(repository).findByPolicyId(policyId);
    }

    @Test
    void shouldGetPaymentsByCustomerId() {
        when(repository.findByCustomerId(customerId))
                .thenReturn(List.of(payment));

        List<Payment> result = paymentService.getByCustomerId(customerId);

        assertEquals(1, result.size());
        assertEquals(customerId, result.get(0).getCustomerId());

        verify(repository).findByCustomerId(customerId);
    }

    @Test
    void shouldDeletePayment() {
        paymentService.delete(paymentId);

        verify(repository).deleteById(paymentId);
    }
}
