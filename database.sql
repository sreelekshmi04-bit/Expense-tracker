-- ============================================================================
-- Expense Tracker System — Single-User MySQL Database Schema
-- College OOP Project (Java, MySQL, JDBC, Servlets)
-- Compatible with MySQL 8.0, 8.4+
-- ============================================================================

CREATE DATABASE IF NOT EXISTS expense_tracker_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE expense_tracker_db;

-- 1. Transactions Table (Unified storage for Income and Expense)
-- Implements OOP discriminator: 'INCOME' vs 'EXPENSE'
CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type ENUM('INCOME', 'EXPENSE') NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    transaction_date DATE NOT NULL,
    description VARCHAR(255) NOT NULL,
    category_or_source VARCHAR(60) NOT NULL,
    payment_method VARCHAR(30) DEFAULT 'Cash', -- Cash, UPI, Card, Bank Transfer
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_tx_date (transaction_date),
    INDEX idx_tx_type (type),
    INDEX idx_tx_cat (category_or_source)
) ENGINE=InnoDB;

-- 2. Monthly Category Budgets Table
-- Stores user-defined spending limits per expense category for a given month (YYYY-MM)
CREATE TABLE IF NOT EXISTS budgets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(60) NOT NULL,
    amount_limit DECIMAL(12, 2) NOT NULL,
    month_year VARCHAR(7) NOT NULL, -- Format: 'YYYY-MM', e.g., '2026-10'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_category_month UNIQUE (category, month_year),
    INDEX idx_budget_lookup (month_year, category)
) ENGINE=InnoDB;

-- 3. Smart Spending Alerts Table
-- Persists real-time alerts generated from live budget checks
CREATE TABLE IF NOT EXISTS alerts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(60) NOT NULL,
    month_year VARCHAR(7) NOT NULL,
    alert_type ENUM('WARNING', 'OVERSPENT') NOT NULL,
    threshold_percent DECIMAL(5, 2) NOT NULL,
    spent_amount DECIMAL(12, 2) NOT NULL,
    budget_limit DECIMAL(12, 2) NOT NULL,
    difference_amount DECIMAL(12, 2) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_alerts_status (is_read, created_at)
) ENGINE=InnoDB;

-- ============================================================================
-- INITIAL STARTER DATA (Matches Project Presentation Example)
-- ============================================================================

-- Monthly Budgets (October 2026)
INSERT INTO budgets (category, amount_limit, month_year) VALUES
('Food', 3000.00, '2026-10'),
('Transport', 1500.00, '2026-10'),
('Shopping', 2500.00, '2026-10'),
('Bills', 4000.00, '2026-10'),
('Education', 2000.00, '2026-10')
ON DUPLICATE KEY UPDATE amount_limit = VALUES(amount_limit);

-- Income Entries
INSERT INTO transactions (type, amount, transaction_date, description, category_or_source, payment_method) VALUES
('INCOME', 25000.00, '2026-10-01', 'Monthly Internship Stipend', 'Salary', 'Bank Transfer'),
('INCOME', 5000.00, '2026-10-05', 'Merit Scholarship Grant', 'Scholarship', 'Bank Transfer'),
('INCOME', 3500.00, '2026-10-10', 'Web Design Project for Club', 'Freelance', 'UPI');

-- Expense Entries (Demonstrating Food spending exceeding 3,000 limit)
-- First expense: 1,000. Second expense: 1,200. Third expense: 1,200 -> Total Food: 3,400 (Exceeded by 400!)
INSERT INTO transactions (type, amount, transaction_date, description, category_or_source, payment_method) VALUES
('EXPENSE', 1000.00, '2026-10-02', 'Hostel Mess & Groceries', 'Food', 'UPI'),
('EXPENSE', 1200.00, '2026-10-04', 'College Canteen & Snacks', 'Food', 'Cash'),
('EXPENSE', 1200.00, '2026-10-08', 'Weekend Outing with Team', 'Food', 'UPI'), -- Total Food: 3,400 (> 3,000)
('EXPENSE', 700.00, '2026-10-03', 'Bus Pass & Auto Rickshaw', 'Transport', 'UPI'),
('EXPENSE', 550.00, '2026-10-09', 'Metro Smart Card Recharge', 'Transport', 'Card'), -- Total Transport: 1,250 (83.3% of 1,500 -> Warning!)
('EXPENSE', 1800.00, '2026-10-06', 'Reference Textbooks & Notebooks', 'Education', 'UPI'),
('EXPENSE', 2200.00, '2026-10-07', 'Electricity & Internet WiFi', 'Bills', 'Bank Transfer'),
('EXPENSE', 1200.00, '2026-10-12', 'Running Shoes', 'Shopping', 'Card');

-- Smart Spending Alerts
INSERT INTO alerts (category, month_year, alert_type, threshold_percent, spent_amount, budget_limit, difference_amount, message, is_read) VALUES
(
    'Food',
    '2026-10',
    'OVERSPENT',
    113.33,
    3400.00,
    3000.00,
    400.00,
    'Food Budget Exceeded! You have spent ₹3,400 out of your ₹3,000 budget. You are ₹400 over budget. Consider reducing your remaining Food expenses this month.',
    FALSE
),
(
    'Transport',
    '2026-10',
    'WARNING',
    83.33,
    1250.00,
    1500.00,
    -250.00,
    'Transport Budget Warning! You have spent ₹1,250 out of your ₹1,500 budget (83.3% used). You have ₹250 remaining for this month.',
    FALSE
);
