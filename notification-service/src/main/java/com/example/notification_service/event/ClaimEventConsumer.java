package com.example.notification_service.event;


import com.example.notification_service.model.Notification;
import com.example.notification_service.service.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDateTime;
import java.util.function.Consumer;

@Configuration
public class ClaimEventConsumer {

    private final NotificationService notificationService;

    public ClaimEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Bean
    public Consumer<ClaimEvent> claimEvent() {
        return event -> {
            Notification notification = new Notification();
            notification.setCustomerId(event.getCustomerId());
            notification.setChannel("EMAIL");
            notification.setSentAt(LocalDateTime.now());
            notification.setStatus("SENT");

            if ("APPROVED".equals(event.getStatus())) {
                notification.setType("CLAIM_APPROVED");
                notification.setContent("Vasa prijava stete (ID: " + event.getClaimId() + ") je odobrena. Iznos isplate: " + event.getClaimAmount());
            } else if ("REJECTED".equals(event.getStatus())) {
                notification.setType("CLAIM_REJECTED");
                notification.setContent("Vasa prijava stete (ID: " + event.getClaimId() + ") je odbijena.");
            } else {
                return;
            }

            notificationService.create(notification);
            System.out.println("Notification created for claim: " + event.getClaimId());
        };
    }
}