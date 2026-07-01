package com.adii.billingnexus.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record SubscriptionPlanResponse(
        UUID id,
        String name,
        String description,
        BigDecimal pricePerCycle,
        int cycleDays,
        int trialDays,
        boolean active
) {
}