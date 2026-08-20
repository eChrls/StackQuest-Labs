CREATE TABLE IF NOT EXISTS customers (id INT PRIMARY KEY AUTO_INCREMENT, email VARCHAR(200) NOT NULL, active BOOLEAN NOT NULL);
CREATE TABLE IF NOT EXISTS tasks (id INT PRIMARY KEY AUTO_INCREMENT, customer_id INT NOT NULL, title VARCHAR(200) NOT NULL, status VARCHAR(30) NOT NULL, note TEXT NULL, FOREIGN KEY(customer_id) REFERENCES customers(id));
INSERT IGNORE INTO customers VALUES (1,'ana@example.test',1),(2,'blocked@example.test',0);
INSERT IGNORE INTO tasks VALUES (1,1,'Prepare invoice','OPEN',NULL),(2,1,'Archive contract','DONE','completed');
