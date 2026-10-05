-- Optional seed data. Enable by adding:
--   spring.sql.init.mode=always
-- to application.properties (with ddl-auto=update, run this manually the first time
-- if you don't want Spring re-running it every start).

INSERT INTO categories (name, type) SELECT 'Salary', 'INCOME' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Salary' AND type='INCOME');
INSERT INTO categories (name, type) SELECT 'Freelance', 'INCOME' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Freelance' AND type='INCOME');
INSERT INTO categories (name, type) SELECT 'Other Income', 'INCOME' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Other Income' AND type='INCOME');
INSERT INTO categories (name, type) SELECT 'Groceries', 'EXPENSE' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Groceries' AND type='EXPENSE');
INSERT INTO categories (name, type) SELECT 'Rent', 'EXPENSE' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Rent' AND type='EXPENSE');
INSERT INTO categories (name, type) SELECT 'Utilities', 'EXPENSE' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Utilities' AND type='EXPENSE');
INSERT INTO categories (name, type) SELECT 'Entertainment', 'EXPENSE' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Entertainment' AND type='EXPENSE');
INSERT INTO categories (name, type) SELECT 'Transport', 'EXPENSE' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Transport' AND type='EXPENSE');
INSERT INTO categories (name, type) SELECT 'Healthcare', 'EXPENSE' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Healthcare' AND type='EXPENSE');
INSERT INTO categories (name, type) SELECT 'Miscellaneous', 'EXPENSE' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name='Miscellaneous' AND type='EXPENSE');
/*
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
*/
