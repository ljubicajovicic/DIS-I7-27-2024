package com.example.payment_service.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.payment_service.model.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(properties = "eureka.client.enabled=false")
@AutoConfigureMockMvc
class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Payment createPayment() throws Exception {
        Payment payment = new Payment();

        payment.setClaimId(UUID.randomUUID());
        payment.setPolicyId(UUID.randomUUID());
        payment.setCustomerId(UUID.randomUUID());
        payment.setType("PAYOUT");
        payment.setAmount(new BigDecimal("1500.00"));
        payment.setStatus("PENDING");
        payment.setPaymentDate(LocalDateTime.now());

        String response = mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, Payment.class);
    }

    @Test
    void getAllPaymentsShouldReturnOk() throws Exception {
        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk());
    }

    @Test
    void createPaymentShouldReturnCreated() throws Exception {
        Payment payment = new Payment();

        payment.setClaimId(UUID.randomUUID());
        payment.setPolicyId(UUID.randomUUID());
        payment.setCustomerId(UUID.randomUUID());
        payment.setType("PAYOUT");
        payment.setAmount(new BigDecimal("1500.00"));
        payment.setStatus("PENDING");
        payment.setPaymentDate(LocalDateTime.now());

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isCreated());
    }

    @Test
    void createPayoutWithoutClaimIdShouldReturnBadRequest() throws Exception {
        Payment payment = new Payment();

        payment.setPolicyId(UUID.randomUUID());
        payment.setCustomerId(UUID.randomUUID());
        payment.setType("PAYOUT");
        payment.setAmount(new BigDecimal("1500.00"));
        payment.setStatus("PENDING");
        payment.setPaymentDate(LocalDateTime.now());

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPaymentByIdShouldReturnOk() throws Exception {
        Payment created = createPayment();

        mockMvc.perform(get("/payments/" + created.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void getPaymentsByClaimIdShouldReturnOk() throws Exception {
        Payment created = createPayment();

        mockMvc.perform(get("/payments")
                .param("claimId", created.getClaimId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getPaymentsByPolicyIdShouldReturnOk() throws Exception {
        Payment created = createPayment();

        mockMvc.perform(get("/payments")
                .param("policyId", created.getPolicyId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getPaymentsByCustomerIdShouldReturnOk() throws Exception {
        Payment created = createPayment();

        mockMvc.perform(get("/payments")
                .param("customerId", created.getCustomerId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void deletePaymentShouldReturnNoContent() throws Exception {
        Payment created = createPayment();

        mockMvc.perform(delete("/payments/" + created.getId()))
                .andExpect(status().isNoContent());
    }
}

