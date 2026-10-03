# Personal Finance Manager

A Spring Boot web app for tracking personal income, expenses, and category budgets in real time,
matching the architecture described in the accompanying project presentation.

## Tech Stack
- Java 17, Spring Boot 3.2.5
- Spring Data JPA / Hibernate
- MySQL
- Spring MVC + Thymeleaf
- Spring Security (BCrypt password hashing, session-based login)
- Maven, embedded Tomcat

## Split transactions
On the Add Expense form, check "Split this expense across more than one category"
to divide one expense's total across several categories (e.g. a $120 supermarket
receipt as $100 Groceries + $20 Household Supplies), each with its own optional
note. Split amounts must add up to the expense total. The real-time budget check
and the Recent Expenses list both read from these per-category splits, so a
divided expense is attributed to each of its categories correctly rather than
counted entirely under its top-level category. Expenses you don't explicitly
split still get one split behind the scenes that mirrors their own
category/amount, so this never requires a separate code path. Any expenses
already in the database from before this feature existed are backfilled with a
default split automatically the next time the app starts (`SplitBackfillRunner`).

## Prerequisites
- JDK 17+
- Maven 3.8+
- MySQL Server running locally

## Setup

1. Create the database (or let the app auto-create it — see `application.properties`):
   ```sql
   CREATE DATABASE finance_manager;
   ```

2. Set `DB_USERNAME` and `DB_PASSWORD` in your environment (the datasource defaults to
   `root` with an empty password). The project does not store database credentials in source.

3. (Optional) Seed default categories:
   ```bash
   mysql -u root -p finance_manager < src/main/resources/data.sql
   ```

## Run

Start MySQL 8 and create the database (`CREATE DATABASE finance_manager;`). Configure
`DB_USERNAME` and `DB_PASSWORD` as environment variables in IntelliJ's Run Configuration
or in your shell. From the project root, run:

```bash
mvn spring-boot:run
```

In IntelliJ, open this directory as a Maven project, wait for dependency import, then
run `FinanceManagerApplication`. Open **http://localhost:8081/register** to create an
account, then log in at **http://localhost:8081/login**. The port is set in
`application.properties`.

## Project Structure

```
src/main/java/com/financemanager/
  FinanceManagerApplication.java   # main class
  config/
    SecurityConfig.java            # Spring Security setup
    SplitBackfillRunner.java       # gives pre-existing expenses a default split on startup
  entity/                          # User, Category, Income, Expense, Budget, TransactionSplit
  repository/                      # Spring Data JPA repositories
  service/                         # UserService, FinanceService, CustomUserDetailsService
  controller/                      # AuthController, DashboardController,
                                    # ExpenseController, IncomeController
src/main/resources/
  application.properties
  data.sql                         # optional seed categories
  schema-manual.sql                # optional manual DB setup reference
  templates/                       # Thymeleaf views + fragments/nav.html (shared sidebar)
  static/css/style.css
```

## Key Endpoints
| Method | Path         | Description                    |
|--------|--------------|---------------------------------|
| GET    | /register    | Registration form               |
| POST   | /register    | Create a new user                |
| GET    | /login       | Login form                       |
| GET    | /dashboard   | Net balance, budgets, history    |
| GET/POST | /addIncome | Log a new income entry          |
| GET/POST | /addExpense| Log a new expense (checks budget cap) |
| GET | /transactions | Search recent income and expense activity |
| GET/POST | /budgets | View spending progress and save monthly category limits |
| POST | /api/v1/upi/webhook | Parse and record an authenticated UPI payment notification |
| GET | /settings/theme | Save display preferences in browser local storage |
| GET | /reports/download?format=pdf | Download the current month's PDF report (CSV also supported) |

The app now includes account, savings goal, notification and UPI sync log mappings,
bank SMS parsing, a financial health score, theme preferences and the UPI simulator.
Use `src/main/resources/schema.sql` for the relational schema reference; Hibernate
continues to update existing installations through `ddl-auto=update`.

## Notes
- Passwords are hashed with BCrypt before storage — never stored in plain text.
- `spring.jpa.hibernate.ddl-auto=update` auto-creates/updates tables on startup;
  switch to a migration tool (Flyway/Liquibase) for production use.
- Budget-cap checking runs in `FinanceService.checkBudgetStatus()` and compares
  monthly spend per category — read from `TransactionSplitRepository`, so split
  expenses are counted correctly — against the `Budget` entity's `monthlyLimit`.
