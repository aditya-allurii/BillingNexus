package com.adii.billingnexus.service;

import com.adii.billingnexus.dto.request.CreateSubscriptionRequest;
import com.adii.billingnexus.dto.response.SubscriptionResponse;
import com.adii.billingnexus.entity.Customer;
import com.adii.billingnexus.entity.Subscription;
import com.adii.billingnexus.entity.SubscriptionPlan;
import com.adii.billingnexus.enums.SubscriptionStatus;
import com.adii.billingnexus.exception.InvalidStateTransitionException;
import com.adii.billingnexus.exception.ResourceNotFoundException;
import com.adii.billingnexus.multitenancy.TenantContext;
import com.adii.billingnexus.repository.CustomerRepository;
import com.adii.billingnexus.repository.SubscriptionPlanRepository;
import com.adii.billingnexus.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CustomerRepository customerRepository;
    private final SubscriptionPlanRepository planRepository;

    private static final Map<SubscriptionStatus, Set<SubscriptionStatus>> VALID_TRANSITIONS =
            new EnumMap<>(SubscriptionStatus.class);

    static {
        VALID_TRANSITIONS.put(SubscriptionStatus.TRIAL,
                EnumSet.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.CANCELLED, SubscriptionStatus.EXPIRED));
        VALID_TRANSITIONS.put(SubscriptionStatus.ACTIVE,
                EnumSet.of(SubscriptionStatus.PAST_DUE, SubscriptionStatus.CANCELLED, SubscriptionStatus.EXPIRED));
        VALID_TRANSITIONS.put(SubscriptionStatus.PAST_DUE,
                EnumSet.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.SUSPENDED, SubscriptionStatus.CANCELLED));
        VALID_TRANSITIONS.put(SubscriptionStatus.SUSPENDED,
                EnumSet.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.CANCELLED));
        // CANCELLED and EXPIRED are terminal states — empty sets mean
        // no transitions out are ever allowed.
        VALID_TRANSITIONS.put(SubscriptionStatus.CANCELLED, EnumSet.noneOf(SubscriptionStatus.class));
        VALID_TRANSITIONS.put(SubscriptionStatus.EXPIRED, EnumSet.noneOf(SubscriptionStatus.class));
    }

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            CustomerRepository customerRepository,
            SubscriptionPlanRepository planRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.customerRepository = customerRepository;
        this.planRepository = planRepository;
    }

    @Transactional
    public SubscriptionResponse createSubscription(CreateSubscriptionRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + request.customerId()));
        SubscriptionPlan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found: " + request.planId()));

        LocalDate today = LocalDate.now();
        boolean hasTrial = plan.getTrialDays() > 0;

        Subscription subscription = Subscription.builder()
                .tenantId(TenantContext.getTenantId())
                .customer(customer)
                .plan(plan)
                .status(hasTrial ? SubscriptionStatus.TRIAL : SubscriptionStatus.ACTIVE)
                .startDate(today)
                .trialEndDate(hasTrial ? today.plusDays(plan.getTrialDays()) : null)
                .currentPeriodStart(today)
                .currentPeriodEnd(today.plusDays(plan.getCycleDays()))
                .build();

        subscription = subscriptionRepository.save(subscription);
        return toResponse(subscription);
    }

    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscription(UUID id) {
        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + id));
        return toResponse(subscription);
    }

    @Transactional
    public void transitionStatus(UUID subscriptionId, SubscriptionStatus newStatus) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found: " + subscriptionId));

        SubscriptionStatus currentStatus = subscription.getStatus();
        Set<SubscriptionStatus> allowedNextStates = VALID_TRANSITIONS.get(currentStatus);

        if (allowedNextStates == null || !allowedNextStates.contains(newStatus)) {
            throw new InvalidStateTransitionException(
                    "Cannot transition subscription from " + currentStatus + " to " + newStatus);
        }

        subscription.setStatus(newStatus);

        if (newStatus == SubscriptionStatus.CANCELLED) {
            subscription.setCancelledAt(LocalDateTime.now());
        } else if (newStatus == SubscriptionStatus.EXPIRED) {
            subscription.setExpiredAt(LocalDateTime.now());
        }

        subscriptionRepository.save(subscription);
    }

    @Transactional
    public void cancelSubscription(UUID id) {
        transitionStatus(id, SubscriptionStatus.CANCELLED);
    }

    private SubscriptionResponse toResponse(Subscription s) {
        return new SubscriptionResponse(
                s.getId(),
                s.getCustomer().getId(),
                s.getCustomer().getName(),
                s.getPlan().getId(),
                s.getPlan().getName(),
                s.getStatus(),
                s.getStartDate(),
                s.getTrialEndDate(),
                s.getCurrentPeriodStart(),
                s.getCurrentPeriodEnd()
        );
    }
}