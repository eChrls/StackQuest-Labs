CREATE TABLE merchant (id varchar(20) PRIMARY KEY, name varchar(120) NOT NULL, active boolean NOT NULL);
CREATE TABLE payment (id uuid PRIMARY KEY, merchant_id varchar(20) NOT NULL REFERENCES merchant(id), amount numeric(12,2) NOT NULL, status varchar(20) NOT NULL, created_at timestamptz NOT NULL, description varchar(255));
CREATE TABLE payment_audit (id uuid PRIMARY KEY, payment_id uuid NOT NULL REFERENCES payment(id), previous_status varchar(20), new_status varchar(20), created_at timestamptz NOT NULL);
CREATE INDEX payment_merchant_created_idx ON payment(merchant_id, created_at DESC);
