package com.adii.billingnexus.dto.response;

import com.adii.billingnexus.enums.SubscriptionStatus;

import java.time.LocalDate;
import java.util.UUID;

public record SubscriptionResponse(
        UUID id,
        UUID customerId,
        String customerName,
        UUID planId,
        String planName,
        SubscriptionStatus status,
        LocalDate startDate,
        LocalDate trialEndDate,
        LocalDate currentPeriodStart,
        LocalDate currentPeriodEnd
) {
}