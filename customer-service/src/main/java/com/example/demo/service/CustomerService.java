package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;

@Service
public class CustomerService {
	
	private final CustomerRepository repository;
	
	public CustomerService(CustomerRepository repository) {
		this.repository = repository;
	}
	
	public Customer create(Customer customer) {
		return repository.save(customer);
	}
	
	public Customer getById(UUID id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found with id: " + id));
	}
	
	public List<Customer> getAll() {
		return repository.findAll();
	}
	
	public Customer update(UUID id, Customer updated) {
		Customer existing = getById(id);
		existing.setFirstName(updated.getFirstName());
		existing.setLastName(updated.getLastName());
		existing.setDateOfBirth(updated.getDateOfBirth());
		existing.setEmail(updated.getEmail());
		existing.setPhone(updated.getPhone());
		existing.setEmail(updated.getEmail());
		existing.setAddress(updated.getAddress());
		existing.setJmbg(updated.getJmbg());
		return repository.save(existing);
	}
	
	public void delete(UUID id) {
		repository.deleteById(id);
	}
	

}
