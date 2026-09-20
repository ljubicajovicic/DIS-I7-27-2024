package com.example.policy_service.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.policy_service.model.Policy;
import com.example.policy_service.service.PolicyService;

@RestController
@RequestMapping("/policy")
public class PolicyController {
	
	private final PolicyService service;
	
	public PolicyController(PolicyService service) {
		this.service = service;
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Policy create (@RequestBody Policy policy) {
		return service.create(policy);
	}
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<Policy> getAll() {
		return service.getAll();
	}
	
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Policy getById(@PathVariable("id") UUID id) {
		return service.getById(id);
	}
	
	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Policy update(@PathVariable("id") UUID id, @RequestBody Policy policy) {
		return service.update(id, policy);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") UUID id) {
		service.delete(id);
	}

}
