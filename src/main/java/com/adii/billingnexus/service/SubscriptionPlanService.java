package com.adii.billingnexus.service;

import com.adii.billingnexus.dto.request.CreatePlanRequest;
import com.adii.billingnexus.dto.response.SubscriptionPlanResponse;
import com.adii.billingnexus.entity.SubscriptionPlan;
import com.adii.billingnexus.exception.ResourceNotFoundException;
import com.adii.billingnexus.multitenancy.TenantContext;
import com.adii.billingnexus.repository.SubscriptionPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository planRepository;

    public SubscriptionPlanService(SubscriptionPlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Transactional
    public SubscriptionPlanResponse createPlan(CreatePlanRequest request) {
        SubscriptionPlan plan = SubscriptionPlan.builder()
                .tenantId(TenantContext.getTenantId())
                .name(request.name())
                .description(request.description())
                .pricePerCycle(request.pricePerCycle())
                .cycleDays(request.cycleDays())
                .trialDays(request.trialDays())
                .build();

        plan = planRepository.save(plan);
        return toResponse(plan);
    }

    @Transactional(readOnly = true)
    public SubscriptionPlanResponse getPlan(UUID id) {
        SubscriptionPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found: " + id));
        return toResponse(plan);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionPlanResponse> getAllPlans() {
        return planRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private SubscriptionPlanResponse toResponse(SubscriptionPlan p) {
        return new SubscriptionPlanResponse(
                p.getId(), p.getName(), p.getDescription(), p.getPricePerCycle(),
                p.getCycleDays(), p.getTrialDays(), p.isActive());
    }
}