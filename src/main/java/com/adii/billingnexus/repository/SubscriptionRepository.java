package com.adii.billingnexus.repository;

import com.adii.billingnexus.entity.Subscription;
import com.adii.billingnexus.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    List<Subscription> findByStatusAndTrialEndDateLessThanEqual(
            SubscriptionStatus status, LocalDate date);

    List<Subscription> findByStatusAndCurrentPeriodEndLessThanEqual(
            SubscriptionStatus status, LocalDate date);

    List<Subscription> findByStatus(SubscriptionStatus status);
}