package com.adii.billingnexus.controller;

import com.adii.billingnexus.dto.request.CreateSubscriptionRequest;
import com.adii.billingnexus.dto.response.SubscriptionResponse;
import com.adii.billingnexus.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_MANAGER')")
    public ResponseEntity<SubscriptionResponse> create(@Valid @RequestBody CreateSubscriptionRequest request) {
        return ResponseEntity.ok(subscriptionService.createSubscription(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_MANAGER', 'VIEWER')")
    public ResponseEntity<SubscriptionResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(subscriptionService.getSubscription(id));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_MANAGER')")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        subscriptionService.cancelSubscription(id);
        return ResponseEntity.noContent().build();
    }
}