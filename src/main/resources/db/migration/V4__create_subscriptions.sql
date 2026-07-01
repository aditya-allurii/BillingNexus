CREATE TABLE subscriptions (
                               id UUID PRIMARY KEY,
                               tenant_id UUID NOT NULL REFERENCES tenants(id),
                               version BIGINT NOT NULL DEFAULT 0,
                               customer_id UUID NOT NULL REFERENCES customers(id),
                               plan_id UUID NOT NULL REFERENCES subscription_plans(id),
                               status VARCHAR(50) NOT NULL,
                               start_date DATE NOT NULL,
                               trial_end_date DATE,
                               current_period_start DATE,
                               current_period_end DATE,
                               cancelled_at TIMESTAMP,
                               expired_at TIMESTAMP,
                               created_at TIMESTAMP NOT NULL DEFAULT now(),
                               updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_subscriptions_tenant_id ON subscriptions(tenant_id);
CREATE INDEX idx_subscriptions_customer_id ON subscriptions(customer_id);
CREATE INDEX idx_subscriptions_status ON subscriptions(status);
CREATE INDEX idx_subscriptions_trial_end_date ON subscriptions(trial_end_date);
CREATE INDEX idx_subscriptions_current_period_end ON subscriptions(current_period_end);