package com.example.claims_service.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.cloud.stream.function.StreamBridge;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.claims_service.model.Claim;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(properties = {
	    "eureka.client.enabled=false",
	    "spring.cloud.stream.enabled=false"
	})@AutoConfigureMockMvc
class ClaimControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockitoBean
    private StreamBridge streamBridge;

    private Claim createClaim() throws Exception {
        Claim claim = new Claim();

        claim.setPolicyId(UUID.randomUUID());
        claim.setCustomerId(UUID.randomUUID());
        claim.setIncidentDate(LocalDate.now());
        claim.setReportedDate(LocalDateTime.now());
        claim.setDescription("Car accident");
        claim.setClaimAmount(new BigDecimal("1500.00"));
        claim.setStatus("PENDING");

        String response = mockMvc.perform(post("/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(claim)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, Claim.class);
    }

    @Test
    void getAllClaimsShouldReturnOk() throws Exception {
        mockMvc.perform(get("/claims"))
                .andExpect(status().isOk());
    }

    @Test
    void createClaimShouldReturnCreated() throws Exception {
        Claim claim = new Claim();

        claim.setPolicyId(UUID.randomUUID());
        claim.setCustomerId(UUID.randomUUID());
        claim.setIncidentDate(LocalDate.now());
        claim.setReportedDate(LocalDateTime.now());
        claim.setDescription("Car accident");
        claim.setClaimAmount(new BigDecimal("1500.00"));
        claim.setStatus("PENDING");

        mockMvc.perform(post("/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(claim)))
                .andExpect(status().isCreated());
    }

    @Test
    void getClaimByIdShouldReturnOk() throws Exception {
        Claim created = createClaim();

        mockMvc.perform(get("/claims/" + created.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void getClaimsByPolicyIdShouldReturnOk() throws Exception {
        Claim created = createClaim();

        mockMvc.perform(get("/claims")
                .param("policyId", created.getPolicyId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getClaimsByCustomerIdShouldReturnOk() throws Exception {
        Claim created = createClaim();

        mockMvc.perform(get("/claims")
                .param("customerId", created.getCustomerId().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void updateClaimStatusShouldReturnOk() throws Exception {
        Claim created = createClaim();

        String statusJson = """
                {
                    "status": "APPROVED"
                }
                """;

        mockMvc.perform(put("/claims/" + created.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(statusJson))
                .andExpect(status().isOk());
    }

    @Test
    void deleteClaimShouldReturnNoContent() throws Exception {
        Claim created = createClaim();

        mockMvc.perform(delete("/claims/" + created.getId()))
                .andExpect(status().isNoContent());
    }
}