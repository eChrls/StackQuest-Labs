INSERT INTO merchants (id, name) VALUES ('M1', 'Merchant One');
INSERT INTO merchants (id, name) VALUES ('M2', 'Merchant Two');

INSERT INTO payments (id, merchant_id, amount, status, provider_reference, created_at)
VALUES ('11111111-1111-4111-8111-111111111111', 'M1', 100.00, 'CAPTURED', 'PROVIDER-123', '2024-01-01T00:00:00Z');
INSERT INTO payments (id, merchant_id, amount, status, provider_reference, created_at)
VALUES ('22222222-2222-4222-8222-222222222222', 'M1', 50.00, 'CAPTURED', 'PROVIDER-456', '2024-01-02T00:00:00Z');
INSERT INTO payments (id, merchant_id, amount, status, provider_reference, created_at)
VALUES ('33333333-3333-4333-8333-333333333333', 'M1', 30.00, 'FAILED', 'PROVIDER-789', '2024-01-03T00:00:00Z');
INSERT INTO payments (id, merchant_id, amount, status, provider_reference, created_at)
VALUES ('44444444-4444-4444-8444-444444444444', 'M2', 200.00, 'CAPTURED', 'PROVIDER-999', '2024-01-04T00:00:00Z');
INSERT INTO payments (id, merchant_id, amount, status, provider_reference, created_at)
VALUES ('55555555-5555-4555-8555-555555555555', 'M1', 25.00, 'PENDING', NULL, '2024-01-05T00:00:00Z');
