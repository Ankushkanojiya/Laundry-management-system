/* SAMPLE CUSTOMERS */
INSERT INTO customer (name, phone, registration_date) VALUES
('Raj Sharma', '9876543210', '2024-01-15'),
('Priya Patel', '8765432109', '2024-02-20');

/* SAMPLE ORDERS */
INSERT INTO laundry_order (customer_id, order_date, total_clothes, total_amount, status) VALUES
(1, CURRENT_DATE, 5, 75.00, 'PENDING'),  -- ₹15 per cloth
(2, CURRENT_DATE, 3, 45.00, 'COMPLETED');