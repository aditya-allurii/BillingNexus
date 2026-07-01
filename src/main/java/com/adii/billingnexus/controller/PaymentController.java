package com.adii.billingnexus.controller;

import com.adii.billingnexus.entity.Payment;
import com.adii.billingnexus.service.PaymentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public record RecordPaymentRequest(
            @NotNull UUID invoiceId,
            @NotNull @Positive BigDecimal amount,
            String method,
            boolean success
    ) {}

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_MANAGER')")
    public ResponseEntity<Payment> recordPayment(@Valid @RequestBody RecordPaymentRequest request) {
        Payment payment = paymentService.recordPayment(
                request.invoiceId(),
                request.amount(),
                request.method(),
                request.success()
        );
        return ResponseEntity.ok(payment);
    }
}