package com.example.notification_service.event;

import static org.junit.jupiter.api.Assertions.*;
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

import com.example.notification_service.model.Notification;
import com.example.notification_service.service.NotificationService;

@ExtendWith(MockitoExtension.class)
class ClaimEventConsumerTest {

    @Mock
    private NotificationService notificationService;

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
    void shouldCreateApprovedClaimNotification() {
        ClaimEvent event = new ClaimEvent(
                claimId,
                policyId,
                customerId,
                new BigDecimal("1500.00"),
                "APPROVED"
        );

        Consumer<ClaimEvent> consumer = claimEventConsumer.claimEvent();

        consumer.accept(event);

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationService).create(captor.capture());

        Notification notification = captor.getValue();

        assertEquals(customerId, notification.getCustomerId());
        assertEquals("EMAIL", notification.getChannel());
        assertEquals("CLAIM_APPROVED", notification.getType());
        assertEquals("SENT", notification.getStatus());
        assertNotNull(notification.getSentAt());

        assertTrue(notification.getContent().contains(claimId.toString()));
        assertTrue(notification.getContent().contains("1500.00"));
    }

    @Test
    void shouldCreateRejectedClaimNotification() {
        ClaimEvent event = new ClaimEvent(
                claimId,
                policyId,
                customerId,
                new BigDecimal("1500.00"),
                "REJECTED"
        );

        Consumer<ClaimEvent> consumer = claimEventConsumer.claimEvent();

        consumer.accept(event);

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);

        verify(notificationService).create(captor.capture());

        Notification notification = captor.getValue();

        assertEquals(customerId, notification.getCustomerId());
        assertEquals("EMAIL", notification.getChannel());
        assertEquals("CLAIM_REJECTED", notification.getType());
        assertEquals("SENT", notification.getStatus());
        assertNotNull(notification.getSentAt());

        assertTrue(notification.getContent().contains(claimId.toString()));
    }

    @Test
    void shouldNotCreateNotificationForUnknownStatus() {
        ClaimEvent event = new ClaimEvent(
                claimId,
                policyId,
                customerId,
                new BigDecimal("1500.00"),
                "PENDING"
        );

        Consumer<ClaimEvent> consumer = claimEventConsumer.claimEvent();

        consumer.accept(event);

        verify(notificationService, never()).create(any(Notification.class));
    }
}
