package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerService customerService;

    private UUID customerId;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();

        customer = new Customer();
        customer.setId(customerId);
        customer.setFirstName("Marko");
        customer.setLastName("Markovic");
        customer.setEmail("marko@gmail.com");
    }

    @Test
    void shouldCreateCustomer() {
        when(repository.save(customer)).thenReturn(customer);

        Customer result = customerService.create(customer);

        assertNotNull(result);
        assertEquals(customerId, result.getId());
        assertEquals("Marko", result.getFirstName());
        assertEquals("Markovic", result.getLastName());

        verify(repository).save(customer);
    }

    @Test
    void shouldGetCustomerById() {
        when(repository.findById(customerId))
                .thenReturn(Optional.of(customer));

        Customer result = customerService.getById(customerId);

        assertNotNull(result);
        assertEquals(customerId, result.getId());
        assertEquals("Marko", result.getFirstName());

        verify(repository).findById(customerId);
    }

    @Test
    void shouldThrowExceptionWhenCustomerDoesNotExist() {
        when(repository.findById(customerId))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> customerService.getById(customerId)
        );

        assertEquals(404, exception.getStatusCode().value());
        assertTrue(exception.getReason().contains("Customer not found"));

        verify(repository).findById(customerId);
    }

    @Test
    void shouldGetAllCustomers() {
        Customer customer2 = new Customer();
        customer2.setId(UUID.randomUUID());

        when(repository.findAll())
                .thenReturn(List.of(customer, customer2));

        List<Customer> result = customerService.getAll();

        assertEquals(2, result.size());
        assertEquals(customerId, result.get(0).getId());

        verify(repository).findAll();
    }

    @Test
    void shouldUpdateCustomer() {
        Customer updated = new Customer();
        updated.setFirstName("Petar");
        updated.setLastName("Petrovic");
        updated.setEmail("petar@gmail.com");

        when(repository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(repository.save(customer))
                .thenReturn(customer);

        Customer result = customerService.update(customerId, updated);

        assertEquals("Petar", result.getFirstName());
        assertEquals("Petrovic", result.getLastName());
        assertEquals("petar@gmail.com", result.getEmail());

        verify(repository).findById(customerId);
        verify(repository).save(customer);
    }

    @Test
    void shouldDeleteCustomer() {
        customerService.delete(customerId);

        verify(repository).deleteById(customerId);
    }
}