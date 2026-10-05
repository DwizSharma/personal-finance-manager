-- STEP 4: default categories. Safe to re-run (skips ones that already exist).
-- Optional: the app's CategorySeeder does the same on startup if the table is empty.
USE finance_manager;

INSERT INTO categories (name, type, icon)
SELECT v.name, v.type, 'tag'
FROM (
    SELECT 'Salary' AS name, 'INCOME'  AS type UNION ALL
    SELECT 'Freelance',      'INCOME'           UNION ALL
    SELECT 'Other Income',   'INCOME'           UNION ALL
    SELECT 'Groceries',      'EXPENSE'          UNION ALL
    SELECT 'Rent',           'EXPENSE'          UNION ALL
    SELECT 'Utilities',      'EXPENSE'          UNION ALL
    SELECT 'Entertainment',  'EXPENSE'          UNION ALL
    SELECT 'Transport',      'EXPENSE'          UNION ALL
    SELECT 'Healthcare',     'EXPENSE'          UNION ALL
    SELECT 'Miscellaneous',  'EXPENSE'
) AS v
WHERE NOT EXISTS (SELECT 1 FROM categories c WHERE c.name = v.name AND c.type = v.type);
