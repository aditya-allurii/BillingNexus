package com.adii.billingnexus.service;

import com.adii.billingnexus.dto.response.InvoiceResponse;
import com.adii.billingnexus.entity.BillingCycle;
import com.adii.billingnexus.entity.Invoice;
import com.adii.billingnexus.entity.Subscription;
import com.adii.billingnexus.enums.InvoiceStatus;
import com.adii.billingnexus.exception.ResourceNotFoundException;
import com.adii.billingnexus.multitenancy.TenantContext;
import com.adii.billingnexus.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * Generates an invoice for a completed billing cycle.
     * Called exclusively by BillingScheduler.renewalCheck() — never
     * directly exposed via a controller. Invoices should only ever
     * exist as a system-generated consequence of a real billing
     * cycle, never manually fabricated through the API.
     */
    @Transactional
    public Invoice generateInvoice(Subscription subscription, BillingCycle billingCycle) {
        Invoice invoice = Invoice.builder()
                .tenantId(TenantContext.getTenantId())
                .subscription(subscription)
                .billingCycle(billingCycle)
                .amount(subscription.getPlan().getPricePerCycle())
                .status(InvoiceStatus.PENDING)
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7)) // 7-day payment window
                .build();

        return invoiceRepository.save(invoice);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoice(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + id));
        return toResponse(invoice);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void markPaid(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());
        invoiceRepository.save(invoice);
    }

    @Transactional
    public void markFailed(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));
        invoice.setStatus(InvoiceStatus.FAILED);
        invoiceRepository.save(invoice);
    }

    private InvoiceResponse toResponse(Invoice i) {
        return new InvoiceResponse(
                i.getId(), i.getSubscription().getId(), i.getAmount(),
                i.getStatus(), i.getIssueDate(), i.getDueDate());
    }
}