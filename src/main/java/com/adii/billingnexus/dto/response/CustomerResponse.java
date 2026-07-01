package com.adii.billingnexus.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String email,
        String phone,
        String address,
        boolean active,
        LocalDateTime createdAt
) {
}