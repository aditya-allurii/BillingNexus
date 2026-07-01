package com.adii.billingnexus.controller;

import com.adii.billingnexus.dto.request.CreatePlanRequest;
import com.adii.billingnexus.dto.response.SubscriptionPlanResponse;
import com.adii.billingnexus.service.SubscriptionPlanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/plans")
public class SubscriptionPlanController {

    private final SubscriptionPlanService planService;

    public SubscriptionPlanController(SubscriptionPlanService planService) {
        this.planService = planService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionPlanResponse> create(@Valid @RequestBody CreatePlanRequest request) {
        return ResponseEntity.ok(planService.createPlan(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_MANAGER', 'VIEWER')")
    public ResponseEntity<SubscriptionPlanResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(planService.getPlan(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_MANAGER', 'VIEWER')")
    public ResponseEntity<List<SubscriptionPlanResponse>> getAll() {
        return ResponseEntity.ok(planService.getAllPlans());
    }
}