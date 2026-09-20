package com.example.payment_service.service;

import com.example.payment_service.model.Payment;
import com.example.payment_service.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public Payment create(Payment payment) {
        if ("PAYOUT".equals(payment.getType()) && payment.getClaimId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PAYOUT payment must have a claimId");
        }
        return repository.save(payment);
    }

    public Payment getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found with id: " + id));
    }

    public List<Payment> getAll() {
        return repository.findAll();
    }

    public List<Payment> getByClaimId(UUID claimId) {
        return repository.findByClaimId(claimId);
    }

    public List<Payment> getByPolicyId(UUID policyId) {
        return repository.findByPolicyId(policyId);
    }

    public List<Payment> getByCustomerId(UUID customerId) {
        return repository.findByCustomerId(customerId);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}