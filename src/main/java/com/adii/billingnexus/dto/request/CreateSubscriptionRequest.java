package com.adii.billingnexus.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateSubscriptionRequest(
        @NotNull UUID customerId,
        @NotNull UUID planId
) {
}