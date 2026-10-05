-- STEP 5 (optional): sanity checks after the app has started once and you registered.
USE finance_manager;

SHOW TABLES;
SELECT id, username, email, role, budget_alert_threshold, LEFT(password_hash, 7) AS hash_prefix FROM users;  -- hash_prefix should be $2a$10$
SELECT type, COUNT(*) AS n FROM categories GROUP BY type;

-- Finds any other leftover NOT NULL columns on `users` that have no default and that the app does not fill.
-- Expected: only id (auto-increment), username, email, password_hash, created_at/role are handled by the app.
SELECT COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT, EXTRA
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'finance_manager' AND TABLE_NAME = 'users'
  AND IS_NULLABLE = 'NO' AND COLUMN_DEFAULT IS NULL AND EXTRA NOT LIKE '%auto_increment%'
  AND COLUMN_NAME NOT IN ('username','email','password_hash','role','created_at');
