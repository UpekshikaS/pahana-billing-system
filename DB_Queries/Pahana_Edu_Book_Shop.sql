-- Database: pahanaedu_bookshop
create Database pahanaedu_bookshop;
use pahanaedu_bookshop;


-- Table: users
CREATE TABLE `users` (
    `user_id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `role` ENUM('ADMIN', 'CASHIER') NOT NULL,
    `name` VARCHAR(100),
    `email` VARCHAR(100),
    `phone` VARCHAR(20),
    `address` VARCHAR(255),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table: customers
CREATE TABLE `customers` (
    `customer_id` INT AUTO_INCREMENT PRIMARY KEY,
    `account_number` VARCHAR(50) NOT NULL UNIQUE,
    `name` VARCHAR(100) NOT NULL,
    `address` VARCHAR(255),
    `telephone` VARCHAR(20),
    `units_consumed` DECIMAL(10, 2) DEFAULT 0.00, -- Placeholder for potential future use, currently not directly used in billing
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table: products
CREATE TABLE `products` (
    `product_id` INT AUTO_INCREMENT PRIMARY KEY,
    `item_id` VARCHAR(50) NOT NULL UNIQUE, -- For scanning
    `name` VARCHAR(255) NOT NULL,
    `description` TEXT,
    `price` DECIMAL(10, 2) NOT NULL,
    `stock_quantity` INT NOT NULL DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table: invoices
CREATE TABLE `invoices` (
    `invoice_id` INT AUTO_INCREMENT PRIMARY KEY,
    `invoice_number` VARCHAR(50) NOT NULL UNIQUE,
    `customer_id` INT,
    `user_id` INT, -- Cashier who generated the invoice
    `invoice_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `total_amount` DECIMAL(10, 2) NOT NULL,
    `discount_amount` DECIMAL(10, 2) DEFAULT 0.00,
    `net_amount` DECIMAL(10, 2) NOT NULL,
    `payment_status` ENUM('PENDING', 'PAID', 'CANCELLED') DEFAULT 'PENDING',
    FOREIGN KEY (`customer_id`) REFERENCES `customers`(`customer_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
);

-- Table: invoice_items
CREATE TABLE `invoice_items` (
    `invoice_item_id` INT AUTO_INCREMENT PRIMARY KEY,
    `invoice_id` INT NOT NULL,
    `product_id` INT NOT NULL,
    `quantity` INT NOT NULL,
    `unit_price` DECIMAL(10, 2) NOT NULL,
    `item_total` DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (`invoice_id`) REFERENCES `invoices`(`invoice_id`),
    FOREIGN KEY (`product_id`) REFERENCES `products`(`product_id`)
);

-- Table: payments
CREATE TABLE `payments` (
    `payment_id` INT AUTO_INCREMENT PRIMARY KEY,
    `invoice_id` INT NOT NULL,
    `payment_method` VARCHAR(50) NOT NULL,
    `amount_paid` DECIMAL(10, 2) NOT NULL,
    `payment_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `transaction_id` VARCHAR(100), -- For card payments, etc.
    FOREIGN KEY (`invoice_id`) REFERENCES `invoices`(`invoice_id`)
);

-- Table: returns
CREATE TABLE `returns` (
    `return_id` INT AUTO_INCREMENT PRIMARY KEY,
    `invoice_item_id` INT NOT NULL,
    `quantity_returned` INT NOT NULL,
    `return_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `reason` TEXT,
    `processed_by_user_id` INT,
    FOREIGN KEY (`invoice_item_id`) REFERENCES `invoice_items`(`invoice_item_id`),
    FOREIGN KEY (`processed_by_user_id`) REFERENCES `users`(`user_id`)
);

-- Table: notifications
CREATE TABLE `notifications` (
    `notification_id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT, -- Null for general notifications
    `message` TEXT NOT NULL,
    `is_read` BOOLEAN DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
);

-- Table: discounts
CREATE TABLE `discounts` (
    `discount_id` INT AUTO_INCREMENT PRIMARY KEY,
    `code` VARCHAR(50) UNIQUE,
    `name` VARCHAR(100) NOT NULL,
    `type` ENUM('PERCENTAGE', 'FIXED') NOT NULL,
    `value` DECIMAL(10, 2) NOT NULL,
    `min_amount` DECIMAL(10, 2) DEFAULT 0.00,
    `start_date` DATE,
    `end_date` DATE,
    `is_active` BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert table data
-- Insert User
INSERT INTO `users` (`user_id`, `username`, `password_hash`, `role`, `name`, `email`, `phone`, `address`, `created_at`, `updated_at`)
VALUES
    (1, 'admin', '$2a$10$f5aw7hihsU0yvVCzXtRtieVL74EzjKKWQxBKiAJ6PuIvtYTezkRyy', 'ADMIN', 'Administrator', 'admin@gmail.com', '0112345986', 'No 110, Main Street, Colombo', '2025-08-01 14:00:51', '2025-08-05 22:44:28'),
    (6, 'cash', '$2a$10$oHmu4Gr233QK9CjYJJYiBOk4ipjNk1sfGZihew6qOYBAnzGckTqc2', 'CASHIER', 'Cashier', 'cashier@gmail.com', '0719876543', '1405 W. 178th Street, Colombo', '2025-08-01 22:48:18', '2025-08-08 23:11:00');


-- Insert Customers
INSERT INTO `customers` (`customer_id`, `account_number`, `name`, `address`, `telephone`, `units_consumed`, `created_at`, `updated_at`)
VALUES
    (1, 'CUST-001', 'Amal Gamage', '123 Galle Road, Colombo 03', '0771234510', 15.00, '2025-08-01 14:00:55', '2025-08-21 06:08:15'),
    (2, 'CUST-002', 'Bimal Jayakodi', '45 Marine Drive, Dehiwala', '0719876562', 30.00, '2025-08-01 14:00:55', '2025-08-20 22:38:01'),
    (3, 'CUST-003', 'Kamal Perera', '78 High Level Road, Nugegoda', '0765551234', 9.00, '2025-08-01 14:00:55', '2025-08-20 22:38:44'),
    (4, 'CUST-004', 'Fathima Rizwan', '90 Hill Street, Kandy', '0754448899', 6.00, '2025-08-01 14:00:55', '2025-08-13 21:49:19');

-- Insert Products
INSERT INTO `products` (`product_id`, `item_id`, `name`, `description`, `price`, `stock_quantity`, `created_at`, `updated_at`)
VALUES
    (1, 'BK-1001', 'Maths Grade 10', 'Mathematics textbook for Grade 10', 950.00, 1, '2025-08-01 14:00:59', '2025-08-21 00:03:25'),
    (2, 'BK-1002', 'Science Grade 11', 'Science textbook for Grade 11', 1100.00, 99, '2025-08-01 14:00:59', '2025-08-21 06:08:15'),
    (3, 'ST-2001', 'Blue Pen', 'Pack of 10 blue pens', 150.00, 30, '2025-08-01 14:00:59', '2025-08-20 22:38:01'),
    (4, 'ST-2002', 'Notebook A5', '200 pages ruled notebook', 200.00, 103, '2025-08-01 14:00:59', '2025-08-14 21:23:53'),
    (5, 'BK-1003', 'English Grade 9', 'English textbook for Grade 9', 890.00, 20, '2025-08-01 14:00:59', '2025-08-15 04:39:31');

-- Insert Invoices
INSERT INTO `invoices` (`invoice_id`, `invoice_number`, `customer_id`, `user_id`, `invoice_date`, `total_amount`, `discount_amount`, `net_amount`, `payment_status`)
VALUES
    (1, 'INV-20250810-1754799824', 1, 1, '2025-08-10 09:53:44', 1100.00, 10.00, 1090.00, 'PAID'),
    (2, 'INV-20250810-1754800804', 1, 1, '2025-08-10 10:10:04', 950.00, 10.00, 940.00, 'PAID'),
    (3, 'INV-20250811-1754880303', 4, 1, '2025-08-11 08:15:03', 3000.00, 0.00, 3000.00, 'PAID'),
    (4, 'INV-20250811-1754880406', 4, 1, '2025-08-11 08:16:46', 1900.00, 0.00, 1900.00, 'PAID'),
    (5, 'INV-20250813-1755101539', 1, 1, '2025-08-13 21:42:19', 950.00, 50.00, 900.00, 'PAID'),
    (6, 'INV-20250814-1755186833', 1, 1, '2025-08-14 21:23:53', 3960.00, 10.00, 3950.00, 'PAID'),
    (7, 'INV-20250814-1755187015', 3, 1, '2025-08-14 21:26:55', 2140.00, 40.00, 2100.00, 'PAID'),
    (8, 'INV-20250815-1755212971', 2, 1, '2025-08-15 04:39:31', 3740.00, 40.00, 3700.00, 'PAID'),
    (9, 'INV-20250820-1755709681', 2, 1, '2025-08-20 22:38:01', 3700.00, 0.00, 3700.00, 'PAID'),
    (10, 'INV-20250820-1755709724', 3, 1, '2025-08-20 22:38:44', 1620.00, 20.00, 1600.00, 'PAID');

-- Insert Notifications
INSERT INTO `notifications` (`notification_id`, `user_id`, `message`, `is_read`, `created_at`)
VALUES
    (1, NULL, 'Customer \'Bimal Jayakodi\' has been updated.',1, '2025-08-20 22:39:55'),    
    (2, NULL, 'A new customer \'Mosh Hamadani\' has been added.',0, '2025-08-20 22:40:57'),
    (3, NULL, 'Customer \'Mosh Hamadani\' has been deleted.',0, '2025-08-20 22:40:21');
