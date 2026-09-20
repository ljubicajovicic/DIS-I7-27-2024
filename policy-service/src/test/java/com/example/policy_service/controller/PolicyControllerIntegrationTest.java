package com.example.policy_service.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.policy_service.model.Policy;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class PolicyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Policy createPolicy() throws Exception {
        Policy policy = new Policy();

        policy.setPolicyNumber("POL-" + UUID.randomUUID());
        policy.setCustomerId(UUID.randomUUID());
        policy.setType("LIFE");
        policy.setEffectiveDate(LocalDate.now());
        policy.setExpirationDate(LocalDate.now().plusYears(1));
        policy.setPremiumAmount(new BigDecimal("1200.00"));
        policy.setStatus("ACTIVE");

        String response = mockMvc.perform(post("/policy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(policy)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, Policy.class);
    }

    @Test
    void getAllPoliciesShouldReturnOk() throws Exception {
        mockMvc.perform(get("/policy"))
                .andExpect(status().isOk());
    }

    @Test
    void createPolicyShouldReturnCreated() throws Exception {
        Policy policy = new Policy();

        policy.setPolicyNumber("POL-" + UUID.randomUUID());
        policy.setCustomerId(UUID.randomUUID());
        policy.setType("LIFE");
        policy.setEffectiveDate(LocalDate.now());
        policy.setExpirationDate(LocalDate.now().plusYears(1));
        policy.setPremiumAmount(new BigDecimal("1200.00"));
        policy.setStatus("ACTIVE");

        mockMvc.perform(post("/policy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(policy)))
                .andExpect(status().isCreated());
    }

    @Test
    void getPolicyByIdShouldReturnOk() throws Exception {
        Policy created = createPolicy();

        mockMvc.perform(get("/policy/" + created.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void updatePolicyShouldReturnOk() throws Exception {
        Policy created = createPolicy();

        Policy updatedPolicy = new Policy();

        updatedPolicy.setPolicyNumber("POL-UPDATED");
        updatedPolicy.setCustomerId(created.getCustomerId());
        updatedPolicy.setType("HEALTH");
        updatedPolicy.setEffectiveDate(LocalDate.now());
        updatedPolicy.setExpirationDate(LocalDate.now().plusYears(1));
        updatedPolicy.setPremiumAmount(new BigDecimal("1500.00"));
        updatedPolicy.setStatus("ACTIVE");

        mockMvc.perform(put("/policy/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPolicy)))
                .andExpect(status().isOk());
    }

    @Test
    void deletePolicyShouldReturnNoContent() throws Exception {
        Policy created = createPolicy();

        mockMvc.perform(delete("/policy/" + created.getId()))
                .andExpect(status().isNoContent());
    }
}
