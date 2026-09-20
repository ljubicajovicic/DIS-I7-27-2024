package com.example.notification_service.controller;

import com.example.notification_service.model.Notification;
import com.example.notification_service.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Notification create(@RequestBody Notification notification) {
        return service.create(notification);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Notification getById(@PathVariable("id") UUID id) {
        return service.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Notification> getAll(
            @RequestParam(value = "customerId", required = false) UUID customerId) {

        if (customerId != null) {
            return service.getByCustomerId(customerId);
        }
        return service.getAll();
    }
}