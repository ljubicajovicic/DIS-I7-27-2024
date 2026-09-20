package com.example.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.model.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(properties = "eureka.client.enabled=false")
@AutoConfigureMockMvc
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer createCustomer() throws Exception {
        Customer customer = new Customer();
        customer.setFirstName("Marko");
        customer.setLastName("Markovic");
        customer.setEmail("marko" + UUID.randomUUID() + "@gmail.com");

        String response = mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, Customer.class);
    }

    @Test
    void getAllCustomersShouldReturnOk() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk());
    }

    @Test
    void getCustomerByIdShouldReturnOk() throws Exception {
        Customer created = createCustomer();

        mockMvc.perform(get("/customers/" + created.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void createCustomerShouldReturnCreated() throws Exception {
        Customer customer = new Customer();
        customer.setFirstName("Marko");
        customer.setLastName("Markovic");
        customer.setEmail("marko" + UUID.randomUUID() + "@gmail.com");

        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateCustomerShouldReturnOk() throws Exception {
        Customer created = createCustomer();

        Customer updated = new Customer();
        updated.setFirstName("Petar");
        updated.setLastName("Petrovic");
        updated.setEmail("petar" + UUID.randomUUID() + "@gmail.com");

        mockMvc.perform(put("/customers/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCustomerShouldReturnNoContent() throws Exception {
        Customer created = createCustomer();

        mockMvc.perform(delete("/customers/" + created.getId()))
                .andExpect(status().isNoContent());
    }
}