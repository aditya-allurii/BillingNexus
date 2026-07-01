CREATE TABLE customers (
                           id UUID PRIMARY KEY,
                           tenant_id UUID NOT NULL REFERENCES tenants(id),
                           name VARCHAR(255) NOT NULL,
                           email VARCHAR(255) NOT NULL,
                           phone VARCHAR(50),
                           address VARCHAR(500),
                           active BOOLEAN NOT NULL DEFAULT TRUE,
                           created_at TIMESTAMP NOT NULL DEFAULT now(),
                           updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_customers_tenant_id ON customers(tenant_id);
CREATE INDEX idx_customers_email ON customers(email);