package com.adii.billingnexus.repository;

import com.adii.billingnexus.entity.Invoice;
import com.adii.billingnexus.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByInvoice(Invoice invoice);
}