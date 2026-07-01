CREATE TABLE subscription_plans (
                                    id UUID PRIMARY KEY,
                                    tenant_id UUID NOT NULL REFERENCES tenants(id),
                                    name VARCHAR(255) NOT NULL,
                                    description VARCHAR(1000),
                                    price_per_cycle NUMERIC(12, 2) NOT NULL,
                                    cycle_days INT NOT NULL,
                                    trial_days INT NOT NULL DEFAULT 0,
                                    active BOOLEAN NOT NULL DEFAULT TRUE,
                                    created_at TIMESTAMP NOT NULL DEFAULT now(),
                                    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_subscription_plans_tenant_id ON subscription_plans(tenant_id);