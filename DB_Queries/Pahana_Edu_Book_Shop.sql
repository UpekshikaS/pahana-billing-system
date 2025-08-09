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