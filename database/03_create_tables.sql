-- STEP 3: create all tables (parents before children, so foreign keys resolve).
-- Matches the JPA entities. Safe to re-run (IF NOT EXISTS).
USE finance_manager;

-- 3.1 users  (no dependencies)
CREATE TABLE IF NOT EXISTS users (
    id                     BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username               VARCHAR(255)  NOT NULL UNIQUE,
    email                  VARCHAR(255)  NOT NULL UNIQUE,
    password_hash          VARCHAR(255)  NOT NULL,
    role                   VARCHAR(20)   NOT NULL DEFAULT 'ROLE_USER',
    created_at             DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    budget_alert_threshold INT           NOT NULL DEFAULT 80
) ENGINE=InnoDB;

-- 3.2 categories  (no dependencies)
CREATE TABLE IF NOT EXISTS categories (
    id    BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    type  VARCHAR(20)  NOT NULL,            -- 'INCOME' | 'EXPENSE'
    icon  VARCHAR(30)  DEFAULT 'tag'
) ENGINE=InnoDB;

-- 3.3 accounts  (-> users)
CREATE TABLE IF NOT EXISTS accounts (
    id            BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    account_name  VARCHAR(255)   NOT NULL,
    account_type  VARCHAR(30)    NOT NULL,
    balance       DECIMAL(19,2)  NOT NULL DEFAULT 0,
    currency      VARCHAR(3)     NOT NULL DEFAULT 'INR',
    user_id       BIGINT         NOT NULL,
    CONSTRAINT fk_account_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3.4 budgets  (-> users, categories)
CREATE TABLE IF NOT EXISTS budgets (
    id             BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    monthly_limit  DECIMAL(19,2)  NOT NULL,
    month          INT            NOT NULL,
    year           INT            NOT NULL,
    user_id        BIGINT         NOT NULL,
    category_id    BIGINT         NOT NULL,
    CONSTRAINT fk_budget_user     FOREIGN KEY (user_id)     REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_budget_category FOREIGN KEY (category_id) REFERENCES categories(id)
) ENGINE=InnoDB;

-- 3.5 incomes  (-> users, categories, accounts)
CREATE TABLE IF NOT EXISTS incomes (
    id           BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    amount       DECIMAL(19,2)  NOT NULL,
    description  VARCHAR(255),
    date         DATE           NOT NULL,
    user_id      BIGINT         NOT NULL,
    category_id  BIGINT         NOT NULL,
    account_id   BIGINT,
    CONSTRAINT fk_income_user     FOREIGN KEY (user_id)     REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_income_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_income_account  FOREIGN KEY (account_id)  REFERENCES accounts(id)
) ENGINE=InnoDB;

-- 3.6 expenses  (-> users, categories, accounts)
CREATE TABLE IF NOT EXISTS expenses (
    id                  BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    amount              DECIMAL(19,2)  NOT NULL,
    description         VARCHAR(255),
    date                DATE           NOT NULL,
    user_id             BIGINT         NOT NULL,
    category_id         BIGINT         NOT NULL,
    account_id          BIGINT,
    payment_method      VARCHAR(30)    DEFAULT 'CASH',
    transaction_ref_no  VARCHAR(120)   UNIQUE,
    CONSTRAINT fk_expense_user     FOREIGN KEY (user_id)     REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_expense_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_expense_account  FOREIGN KEY (account_id)  REFERENCES accounts(id)
) ENGINE=InnoDB;

-- 3.7 transaction_splits  (-> expenses, categories)
CREATE TABLE IF NOT EXISTS transaction_splits (
    id           BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    expense_id   BIGINT         NOT NULL,
    category_id  BIGINT         NOT NULL,
    amount       DECIMAL(19,2)  NOT NULL,
    note         VARCHAR(255),
    CONSTRAINT fk_split_expense  FOREIGN KEY (expense_id)  REFERENCES expenses(id) ON DELETE CASCADE,
    CONSTRAINT fk_split_category FOREIGN KEY (category_id) REFERENCES categories(id)
) ENGINE=InnoDB;

-- 3.8 savings_goals  (-> users)
CREATE TABLE IF NOT EXISTS savings_goals (
    id              BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    goal_name       VARCHAR(255)   NOT NULL,
    target_amount   DECIMAL(19,2)  NOT NULL,
    current_amount  DECIMAL(19,2)  NOT NULL DEFAULT 0,
    deadline        DATE,
    user_id         BIGINT         NOT NULL,
    CONSTRAINT fk_goal_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3.9 notifications  (-> users)
CREATE TABLE IF NOT EXISTS notifications (
    id          BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    message     VARCHAR(500)  NOT NULL,
    is_read     BIT           NOT NULL DEFAULT 0,
    created_at  DATETIME(6)   NOT NULL,
    user_id     BIGINT        NOT NULL,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3.10 upi_integration_logs  (-> users)
CREATE TABLE IF NOT EXISTS upi_integration_logs (
    id            BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    app_provider  VARCHAR(20)    NOT NULL,
    upi_id        VARCHAR(255),
    amount        DECIMAL(19,2)  NOT NULL,
    status        VARCHAR(20)    NOT NULL,
    raw_payload   TEXT,
    timestamp     DATETIME(6)    NOT NULL,
    user_id       BIGINT         NOT NULL,
    CONSTRAINT fk_upi_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;
