package com.adii.billingnexus.multitenancy;

import jakarta.persistence.EntityManager;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
public class TenantAwareAspect {

    private final EntityManager entityManager;

    public TenantAwareAspect(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Before("execution(* com.adii.billingnexus.service..*(..))")
    public void enableTenantFilter() {
        UUID tenantId = TenantContext.getTenantId();

        if (tenantId != null) {
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("tenantFilter")
                    .setParameter("tenantId", tenantId.toString());
        }
    }
}