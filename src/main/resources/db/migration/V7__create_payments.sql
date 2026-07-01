CREATE TABLE payments (
                          id UUID PRIMARY KEY,
                          tenant_id UUID NOT NULL REFERENCES tenants(id),
                          invoice_id UUID NOT NULL REFERENCES invoices(id),
                          amount NUMERIC(12, 2) NOT NULL,
                          status VARCHAR(50) NOT NULL,
                          payment_method VARCHAR(100),
                          transaction_reference VARCHAR(255),
                          attempted_at TIMESTAMP NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT now(),
                          updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_payments_tenant_id ON payments(tenant_id);
CREATE INDEX idx_payments_invoice_id ON payments(invoice_id);
CREATE INDEX idx_payments_status ON payments(status);