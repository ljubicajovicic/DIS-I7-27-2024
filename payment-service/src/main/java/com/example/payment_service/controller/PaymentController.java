package com.example.payment_service.controller;

import com.example.payment_service.model.Payment;
import com.example.payment_service.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Payment create(@RequestBody Payment payment) {
        return service.create(payment);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Payment getById(@PathVariable("id") UUID id) {
        return service.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Payment> getAll(
            @RequestParam(value = "claimId", required = false) UUID claimId,
            @RequestParam(value = "policyId", required = false) UUID policyId,
            @RequestParam(value = "customerId", required = false) UUID customerId) {

        if (claimId != null) {
            return service.getByClaimId(claimId);
        }
        if (policyId != null) {
            return service.getByPolicyId(policyId);
        }
        if (customerId != null) {
            return service.getByCustomerId(customerId);
        }
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") UUID id) {
        service.delete(id);
    }
}