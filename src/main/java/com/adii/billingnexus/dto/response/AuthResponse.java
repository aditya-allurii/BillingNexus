package com.adii.billingnexus.dto.response;

import java.util.UUID;

public record AuthResponse(
        String token,
        UUID tenantId,
        String email,
        String role
) {
}