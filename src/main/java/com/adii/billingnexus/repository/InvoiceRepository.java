package com.adii.billingnexus.repository;

import com.adii.billingnexus.entity.Invoice;
import com.adii.billingnexus.entity.Subscription;
import com.adii.billingnexus.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    List<Invoice> findBySubscription(Subscription subscription);

    List<Invoice> findByStatus(InvoiceStatus status);
}