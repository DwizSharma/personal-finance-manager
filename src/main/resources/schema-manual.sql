-- ============================================
-- Personal Finance Manager - Database Setup
-- Run this in MySQL Workbench or the mysql CLI
-- ============================================

CREATE DATABASE IF NOT EXISTS finance_manager;
USE finance_manager;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

-- Categories table (shared by income and expense entries)
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type ENUM('INCOME', 'EXPENSE') NOT NULL
);

-- Income table
CREATE TABLE IF NOT EXISTS incomes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(12,2) NOT NULL,
    description VARCHAR(255),
    date DATE NOT NULL,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Expenses table
CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(12,2) NOT NULL,
    description VARCHAR(255),
    date DATE NOT NULL,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Budgets table (monthly cap per user per category)
CREATE TABLE IF NOT EXISTS budgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    monthly_limit DECIMAL(12,2) NOT NULL,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Split transactions: every expense has at least one row here (a plain expense
-- has exactly one, mirroring its own category/amount); a split expense has 2+.
-- The real-time budget check reads from this table rather than expenses.category
-- directly, so a split expense is attributed to each of its categories correctly.
CREATE TABLE IF NOT EXISTS transaction_splits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    expense_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    note VARCHAR(255),
    FOREIGN KEY (expense_id) REFERENCES expenses(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
-- If your expenses table pre-dates this feature, backfill one split per existing
-- expense (the app also does this automatically on startup via SplitBackfillRunner):
-- INSERT INTO transaction_splits (expense_id, category_id, amount)
--   SELECT id, category_id, amount FROM expenses e
--   WHERE NOT EXISTS (SELECT 1 FROM transaction_splits s WHERE s.expense_id = e.id);

-- Seed default categories
INSERT INTO categories (name, type) VALUES
  ('Salary', 'INCOME'),
  ('Freelance', 'INCOME'),
  ('Other Income', 'INCOME'),
  ('Groceries', 'EXPENSE'),
  ('Rent', 'EXPENSE'),
  ('Utilities', 'EXPENSE'),
  ('Entertainment', 'EXPENSE'),
  ('Transport', 'EXPENSE'),
  ('Healthcare', 'EXPENSE'),
  ('Miscellaneous', 'EXPENSE');
