CREATE TABLE tenants (
                         id UUID PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         email VARCHAR(255) NOT NULL UNIQUE,
                         password_hash VARCHAR(255) NOT NULL,
                         role VARCHAR(50) NOT NULL,
                         active BOOLEAN NOT NULL DEFAULT TRUE,
                         created_at TIMESTAMP NOT NULL DEFAULT now(),
                         updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_tenants_email ON tenants(email);