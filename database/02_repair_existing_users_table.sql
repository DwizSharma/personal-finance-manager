-- STEP 2 (ONLY if your database already existed from an older version of the app).
-- Fixes:  SQL Error 1364: Field 'budget_alert_threshold' doesn't have a default value
-- Gives users.budget_alert_threshold a DEFAULT of 80 (adds the column if it is missing).
-- Safe to run more than once. Skip this on a brand-new database (03 creates it correctly).
USE finance_manager;

SET @has_users := (SELECT COUNT(*) FROM information_schema.TABLES
                   WHERE TABLE_SCHEMA = 'finance_manager' AND TABLE_NAME = 'users');
SET @has_col := (SELECT COUNT(*) FROM information_schema.COLUMNS
                 WHERE TABLE_SCHEMA = 'finance_manager' AND TABLE_NAME = 'users'
                   AND COLUMN_NAME = 'budget_alert_threshold');

SET @ddl := CASE
  WHEN @has_users = 0 THEN 'SELECT ''users table not found - run 03_create_tables.sql instead'' AS info'
  WHEN @has_col   = 0 THEN 'ALTER TABLE users ADD COLUMN budget_alert_threshold INT NOT NULL DEFAULT 80'
  ELSE                     'ALTER TABLE users MODIFY COLUMN budget_alert_threshold INT NOT NULL DEFAULT 80'
END;
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
