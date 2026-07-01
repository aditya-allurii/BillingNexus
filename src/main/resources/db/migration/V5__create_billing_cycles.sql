CREATE TABLE billing_cycles (
                                id UUID PRIMARY KEY,
                                tenant_id UUID NOT NULL REFERENCES tenants(id),
                                subscription_id UUID NOT NULL REFERENCES subscriptions(id),
                                cycle_start DATE NOT NULL,
                                cycle_end DATE NOT NULL,
                                billed BOOLEAN NOT NULL DEFAULT FALSE,
                                created_at TIMESTAMP NOT NULL DEFAULT now(),
                                updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_billing_cycles_tenant_id ON billing_cycles(tenant_id);
CREATE INDEX idx_billing_cycles_subscription_id ON billing_cycles(subscription_id);
CREATE INDEX idx_billing_cycles_billed ON billing_cycles(billed);