CREATE TABLE invoices (
                          id UUID PRIMARY KEY,
                          tenant_id UUID NOT NULL REFERENCES tenants(id),
                          subscription_id UUID NOT NULL REFERENCES subscriptions(id),
                          billing_cycle_id UUID NOT NULL REFERENCES billing_cycles(id),
                          amount NUMERIC(12, 2) NOT NULL,
                          status VARCHAR(50) NOT NULL,
                          issue_date DATE NOT NULL,
                          due_date DATE NOT NULL,
                          paid_at TIMESTAMP,
                          created_at TIMESTAMP NOT NULL DEFAULT now(),
                          updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_invoices_tenant_id ON invoices(tenant_id);
CREATE INDEX idx_invoices_subscription_id ON invoices(subscription_id);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoices_due_date ON invoices(due_date);