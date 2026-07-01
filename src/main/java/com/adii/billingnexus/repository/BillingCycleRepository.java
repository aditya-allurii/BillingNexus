package com.adii.billingnexus.repository;

import com.adii.billingnexus.entity.BillingCycle;
import com.adii.billingnexus.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BillingCycleRepository extends JpaRepository<BillingCycle, UUID> {

    List<BillingCycle> findBySubscription(Subscription subscription);

    List<BillingCycle> findByBilledFalse();
}