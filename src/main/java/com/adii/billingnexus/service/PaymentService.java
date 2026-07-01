package com.adii.billingnexus.service;

import com.adii.billingnexus.entity.Invoice;
import com.adii.billingnexus.entity.Payment;
import com.adii.billingnexus.enums.PaymentStatus;
import com.adii.billingnexus.exception.ResourceNotFoundException;
import com.adii.billingnexus.multitenancy.TenantContext;
import com.adii.billingnexus.repository.InvoiceRepository;
import com.adii.billingnexus.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;

    public PaymentService(
            PaymentRepository paymentRepository,
            InvoiceRepository invoiceRepository,
            InvoiceService invoiceService
    ) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.invoiceService = invoiceService;
    }

    /**
     * Records a payment attempt against an invoice. In a real system
     * this method's body would call out to a payment gateway SDK and
     * use its response to determine success/failure. Here we accept
     * that outcome directly as a parameter for demonstration purposes —
     * the surrounding structure (record attempt, update invoice
     * accordingly) mirrors a real gateway integration.
     */
    @Transactional
    public Payment recordPayment(UUID invoiceId, BigDecimal amount, String method, boolean success) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));

        Payment payment = Payment.builder()
                .tenantId(TenantContext.getTenantId())
                .invoice(invoice)
                .amount(amount)
                .status(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .paymentMethod(method)
                .transactionReference(UUID.randomUUID().toString()) // simulates a gateway transaction ID
                .attemptedAt(LocalDateTime.now())
                .build();

        payment = paymentRepository.save(payment);

        if (success) {
            invoiceService.markPaid(invoiceId);
        } else {
            invoiceService.markFailed(invoiceId);
        }

        return payment;
    }
}