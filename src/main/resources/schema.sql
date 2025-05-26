/* CLEANUP (optional - only if you want to reset) */
DROP TABLE IF EXISTS laundry_order;
DROP TABLE IF EXISTS customer;

/* CUSTOMER TABLE */
CREATE TABLE customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    phone VARCHAR(20) NOT NULL UNIQUE,
    registration_date DATE
);

/* ORDER TABLE (avoid SQL keyword conflict) */
CREATE TABLE laundry_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    order_date DATE NOT NULL,
    total_clothes INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING','IN_PROGRESS','COMPLETED')),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);