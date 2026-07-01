package com.adii.billingnexus.scheduler;

import com.adii.billingnexus.entity.BillingCycle;
import com.adii.billingnexus.entity.Subscription;
import com.adii.billingnexus.enums.SubscriptionStatus;
import com.adii.billingnexus.repository.BillingCycleRepository;
import com.adii.billingnexus.repository.SubscriptionRepository;
import com.adii.billingnexus.service.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Component
public class BillingScheduler {

    private static final Logger log = LoggerFactory.getLogger(BillingScheduler.class);

    private final SubscriptionRepository subscriptionRepository;
    private final BillingCycleRepository billingCycleRepository;
    private final InvoiceService invoiceService;

    public BillingScheduler(
            SubscriptionRepository subscriptionRepository,
            BillingCycleRepository billingCycleRepository,
            InvoiceService invoiceService
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.billingCycleRepository = billingCycleRepository;
        this.invoiceService = invoiceService;
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void renewalCheck() {
        log.info("Running renewalCheck...");
        LocalDate today = LocalDate.now();

        List<Subscription> dueSubscriptions =
                subscriptionRepository.findByStatusAndCurrentPeriodEndLessThanEqual(
                        SubscriptionStatus.ACTIVE, today);

        for (Subscription subscription : dueSubscriptions) {
            BillingCycle cycle = BillingCycle.builder()
                    .tenantId(subscription.getTenantId())
                    .subscription(subscription)
                    .cycleStart(subscription.getCurrentPeriodStart())
                    .cycleEnd(subscription.getCurrentPeriodEnd())
                    .billed(false)
                    .build();
            cycle = billingCycleRepository.save(cycle);

            invoiceService.generateInvoice(subscription, cycle);

            cycle.setBilled(true);
            billingCycleRepository.save(cycle);

            int cycleDays = subscription.getPlan().getCycleDays();
            subscription.setCurrentPeriodStart(subscription.getCurrentPeriodEnd());
            subscription.setCurrentPeriodEnd(subscription.getCurrentPeriodEnd().plusDays(cycleDays));
            subscriptionRepository.save(subscription);

            log.info("Renewed subscription {} — new period {} to {}",
                    subscription.getId(), subscription.getCurrentPeriodStart(), subscription.getCurrentPeriodEnd());
        }
    }

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void trialConversion() {
        log.info("Running trialConversion...");
        LocalDate today = LocalDate.now();

        List<Subscription> expiredTrials =
                subscriptionRepository.findByStatusAndTrialEndDateLessThanEqual(
                        SubscriptionStatus.TRIAL, today);

        for (Subscription subscription : expiredTrials) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscriptionRepository.save(subscription);
            log.info("Converted subscription {} from TRIAL to ACTIVE", subscription.getId());
        }
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void gracePeriodExpiry() {
        log.info("Running gracePeriodExpiry...");
        LocalDate cutoff = LocalDate.now().minusDays(7);

        List<Subscription> pastDueSubscriptions =
                subscriptionRepository.findByStatus(SubscriptionStatus.PAST_DUE);

        for (Subscription subscription : pastDueSubscriptions) {
            if (subscription.getCurrentPeriodEnd() != null
                    && subscription.getCurrentPeriodEnd().isBefore(cutoff)) {
                subscription.setStatus(SubscriptionStatus.SUSPENDED);
                subscriptionRepository.save(subscription);
                log.info("Suspended subscription {} after grace period expiry", subscription.getId());
            }
        }
    }
}