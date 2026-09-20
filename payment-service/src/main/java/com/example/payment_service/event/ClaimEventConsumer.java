package com.example.payment_service.event;

import com.example.payment_service.model.Payment;
import com.example.payment_service.service.PaymentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDateTime;
import java.util.function.Consumer;

@Configuration
public class ClaimEventConsumer {

    private final PaymentService paymentService;

    public ClaimEventConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Bean
    public Consumer<ClaimEvent> claimEvent() {
        return event -> {
            if ("APPROVED".equals(event.getStatus())) {
                Payment payout = new Payment();
                payout.setClaimId(event.getClaimId());
                payout.setPolicyId(event.getPolicyId());
                payout.setCustomerId(event.getCustomerId());
                payout.setType("PAYOUT");
                payout.setAmount(event.getClaimAmount());
                payout.setStatus("PENDING");
                payout.setPaymentDate(LocalDateTime.now());

                paymentService.create(payout);

                System.out.println("Auto-created PAYOUT for approved claim: " + event.getClaimId());
            }
        };
    }
}