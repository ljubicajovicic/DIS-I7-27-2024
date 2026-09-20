package com.example.payment_service.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.payment_service.model.Payment;
import com.example.payment_service.service.PaymentService;

@ExtendWith(MockitoExtension.class)
class ClaimEventConsumerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private ClaimEventConsumer claimEventConsumer;

    private UUID claimId;
    private UUID policyId;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        claimId = UUID.randomUUID();
        policyId = UUID.randomUUID();
        customerId = UUID.randomUUID();
    }

    @Test
    void shouldCreatePayoutForApprovedClaim() {
        ClaimEvent event = new ClaimEvent(
                claimId,
                policyId,
                customerId,
                new BigDecimal("1500.00"),
                "APPROVED"
        );

        Consumer<ClaimEvent> consumer = claimEventConsumer.claimEvent();

        consumer.accept(event);

        ArgumentCaptor<Payment> captor =
                ArgumentCaptor.forClass(Payment.class);

        verify(paymentService).create(captor.capture());

        Payment payout = captor.getValue();

        assertEquals(claimId, payout.getClaimId());
        assertEquals(policyId, payout.getPolicyId());
        assertEquals(customerId, payout.getCustomerId());
        assertEquals("PAYOUT", payout.getType());
        assertEquals(new BigDecimal("1500.00"), payout.getAmount());
        assertEquals("PENDING", payout.getStatus());
        assertNotNull(payout.getPaymentDate());
    }

    @Test
    void shouldNotCreatePayoutForRejectedClaim() {
        ClaimEvent event = new ClaimEvent(
                claimId,
                policyId,
                customerId,
                new BigDecimal("1500.00"),
                "REJECTED"
        );

        Consumer<ClaimEvent> consumer = claimEventConsumer.claimEvent();

        consumer.accept(event);

        verify(paymentService, never()).create(any(Payment.class));
    }

    @Test
    void shouldNotCreatePayoutForUnknownStatus() {
        ClaimEvent event = new ClaimEvent(
                claimId,
                policyId,
                customerId,
                new BigDecimal("1500.00"),
                "PENDING"
        );

        Consumer<ClaimEvent> consumer = claimEventConsumer.claimEvent();

        consumer.accept(event);

        verify(paymentService, never()).create(any(Payment.class));
    }
}