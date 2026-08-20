CREATE TABLE merchant (id varchar(20) PRIMARY KEY, name varchar(120) NOT NULL, active boolean NOT NULL);
CREATE TABLE payment (id uuid PRIMARY KEY, merchant_id varchar(20) NOT NULL REFERENCES merchant(id), amount numeric(19,2) NOT NULL, status varchar(20) NOT NULL, provider_reference varchar(120), processed boolean NOT NULL, created_at timestamptz NOT NULL);
CREATE TABLE processing_audit (id uuid PRIMARY KEY, payment_id uuid NOT NULL, action varchar(80) NOT NULL, created_at timestamptz NOT NULL);
CREATE TABLE refund (id uuid PRIMARY KEY, payment_id uuid NOT NULL REFERENCES payment(id), amount numeric(19,2) NOT NULL, created_at timestamptz NOT NULL);
INSERT INTO merchant VALUES ('M1','Acme Shop',true),('M2','Example Store',true),('M3','Old Merchant',false);
INSERT INTO payment VALUES
('11111111-1111-1111-1111-111111111111','M1',100.00,'CAPTURED','prov-100',false,'2025-01-01T10:00:00Z'),
('22222222-2222-2222-2222-222222222222','M1',50.00,'PENDING',NULL,false,'2025-01-02T10:00:00Z'),
('33333333-3333-3333-3333-333333333333','M2',75.00,'FAILED',NULL,false,'2025-01-03T10:00:00Z'),
('44444444-4444-4444-4444-444444444444','M2',25.00,'REFUNDED','prov-25',true,'2025-01-04T10:00:00Z'),
('55555555-5555-5555-5555-555555555555','M3',80.00,'CAPTURED','prov-old',false,'2025-01-05T10:00:00Z');
