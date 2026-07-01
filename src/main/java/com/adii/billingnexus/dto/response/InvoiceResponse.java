package com.adii.billingnexus.dto.response;

import com.adii.billingnexus.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record InvoiceResponse(
        UUID id,
        UUID subscriptionId,
        BigDecimal amount,
        InvoiceStatus status,
        LocalDate issueDate,
        LocalDate dueDate
) {
}