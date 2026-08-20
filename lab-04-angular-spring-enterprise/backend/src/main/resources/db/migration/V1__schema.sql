CREATE TABLE customer (id uuid PRIMARY KEY, email varchar(200) NOT NULL, active boolean NOT NULL);
CREATE TABLE support_request (id uuid PRIMARY KEY, customer_id uuid NOT NULL REFERENCES customer(id), subject varchar(200) NOT NULL, priority varchar(20) NOT NULL, status varchar(20) NOT NULL);
INSERT INTO customer VALUES ('11111111-1111-1111-1111-111111111111','ana@example.test',true),('22222222-2222-2222-2222-222222222222','inactive@example.test',false);
INSERT INTO support_request VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa','11111111-1111-1111-1111-111111111111','Cannot login','HIGH','OPEN');
