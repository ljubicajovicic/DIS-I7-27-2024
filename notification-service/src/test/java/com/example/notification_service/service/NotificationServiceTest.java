package com.example.notification_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

import com.example.notification_service.model.Notification;
import com.example.notification_service.repository.NotificationRepository;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository repository;

    @InjectMocks
    private NotificationService notificationService;

    private UUID notificationId;
    private UUID customerId;
    private Notification notification;

    @BeforeEach
    void setUp() {
        notificationId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        notification = new Notification();
        notification.setId(notificationId);
        notification.setCustomerId(customerId);
        notification.setChannel("EMAIL");
        notification.setType("CLAIM_APPROVED");
        notification.setContent("Your claim has been approved.");
        notification.setSentAt(LocalDateTime.now());
        notification.setStatus("SENT");
    }

    @Test
    void shouldCreateNotification() {
        when(repository.save(notification)).thenReturn(notification);

        Notification result = notificationService.create(notification);

        assertNotNull(result);
        assertEquals(notificationId, result.getId());
        assertEquals(customerId, result.getCustomerId());
        assertEquals("EMAIL", result.getChannel());
        assertEquals("CLAIM_APPROVED", result.getType());
        assertEquals("SENT", result.getStatus());

        verify(repository).save(notification);
    }

    @Test
    void shouldGetNotificationById() {
        when(repository.findById(notificationId))
                .thenReturn(Optional.of(notification));

        Notification result = notificationService.getById(notificationId);

        assertNotNull(result);
        assertEquals(notificationId, result.getId());
        assertEquals(customerId, result.getCustomerId());
        assertEquals("CLAIM_APPROVED", result.getType());

        verify(repository).findById(notificationId);
    }

    @Test
    void shouldThrowExceptionWhenNotificationDoesNotExist() {
        when(repository.findById(notificationId))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> notificationService.getById(notificationId)
        );

        assertEquals(404, exception.getStatusCode().value());
        assertTrue(exception.getReason().contains("Notification not found"));

        verify(repository).findById(notificationId);
    }

    @Test
    void shouldGetAllNotifications() {
        Notification notification2 = new Notification();
        notification2.setId(UUID.randomUUID());

        when(repository.findAll())
                .thenReturn(List.of(notification, notification2));

        List<Notification> result = notificationService.getAll();

        assertEquals(2, result.size());
        assertEquals(notificationId, result.get(0).getId());

        verify(repository).findAll();
    }

    @Test
    void shouldGetNotificationsByCustomerId() {
        when(repository.findByCustomerId(customerId))
                .thenReturn(List.of(notification));

        List<Notification> result =
                notificationService.getByCustomerId(customerId);

        assertEquals(1, result.size());
        assertEquals(customerId, result.get(0).getCustomerId());

        verify(repository).findByCustomerId(customerId);
    }
}
