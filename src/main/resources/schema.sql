CREATE DATABASE IF NOT EXISTS parking;
USE parking;

CREATE TABLE IF NOT EXISTS part_payments (
    payment_id VARCHAR(20) PRIMARY KEY,
    vehicle_number VARCHAR(20) NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    paid_amount DECIMAL(10, 2) NOT NULL,
    balance_amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    payment_date DATETIME NOT NULL,
    notes VARCHAR(255)
);
