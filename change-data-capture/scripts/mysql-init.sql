-- Enable binlog for CDC (MySQL configuration required)
-- Add to my.cnf or my.ini:
-- [mysqld]
-- server-id=1
-- log_bin=mysql-bin
-- binlog_format=ROW
-- binlog_row_image=FULL
-- expire_logs_days=10

-- Create databases
CREATE DATABASE IF NOT EXISTS order_db;
CREATE DATABASE IF NOT EXISTS inventory_db;
CREATE DATABASE IF NOT EXISTS billing_db;

-- Use inventory_db to create sample products
USE inventory_db;

CREATE TABLE IF NOT EXISTS products (
    product_id VARCHAR(50) PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    available_stock INT NOT NULL
);

-- Insert sample products
INSERT INTO products (product_id, product_name, available_stock) VALUES
('PROD-001', 'Laptop', 50),
('PROD-002', 'Mouse', 200),
('PROD-003', 'Keyboard', 150),
('PROD-004', 'Monitor', 75),
('PROD-005', 'Headphones', 100)
ON DUPLICATE KEY UPDATE available_stock=available_stock;
