package com.example.notification_service.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.notification_service.model.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(properties = "eureka.client.enabled=false")
@AutoConfigureMockMvc
class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Notification createNotification() throws Exception {
        Notification notification = new Notification();

        notification.setCustomerId(UUID.randomUUID());
        notification.setChannel("EMAIL");
        notification.setType("CLAIM_APPROVED");
        notification.setContent("Your claim has been approved.");
        notification.setSentAt(LocalDateTime.now());
        notification.setStatus("SENT");

        String response = mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, Notification.class);
    }

    @Test
    void getAllNotificationsShouldReturnOk() throws Exception {
        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk());
    }

    @Test
    void createNotificationShouldReturnCreated() throws Exception {
        Notification notification = new Notification();

        notification.setCustomerId(UUID.randomUUID());
        notification.setChannel("EMAIL");
        notification.setType("CLAIM_APPROVED");
        notification.setContent("Your claim has been approved.");
        notification.setSentAt(LocalDateTime.now());
        notification.setStatus("SENT");

        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isCreated());
    }

    @Test
    void getNotificationByIdShouldReturnOk() throws Exception {
        Notification created = createNotification();

        mockMvc.perform(get("/notifications/" + created.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void getNotificationsByCustomerIdShouldReturnOk() throws Exception {
        Notification created = createNotification();

        mockMvc.perform(get("/notifications")
                .param("customerId", created.getCustomerId().toString()))
                .andExpect(status().isOk());
    }
}
