package com.financemanager.service;

import com.financemanager.entity.*;
import com.financemanager.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class FinanceService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final TransactionSplitRepository transactionSplitRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Autowired
    public FinanceService(IncomeRepository incomeRepository,
                           ExpenseRepository expenseRepository,
                           BudgetRepository budgetRepository,
                           TransactionSplitRepository transactionSplitRepository,
                           NotificationRepository notificationRepository,
                           UserRepository userRepository) {
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.transactionSplitRepository = transactionSplitRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Income addIncome(User user, Category category, BigDecimal amount, String description, LocalDate date) {
        Income income = new Income(amount, description, date, user, category);
        return incomeRepository.save(income);
    }

    /**
     * Adds an expense and returns a warning message if it pushes the user
     * over their monthly budget cap for that category (Slide 9: real-time budget alerts).
     */
    public Expense addExpense(User user, Category category, BigDecimal amount, String description, LocalDate date) {
        return addExpense(user, category, amount, description, date, null);
    }

    /**
     * Adds an expense. If {@code splitInputs} has 2+ entries, the expense's total is
     * divided across those categories (a "split transaction", e.g. one supermarket
     * receipt covering both Groceries and Household Supplies); the split amounts must
     * add up to {@code amount}. Otherwise the expense gets a single split mirroring
     * its own category/amount, so downstream reporting always reads from splits.
     */
    @Transactional
    public Expense addExpense(User user, Category category, BigDecimal amount, String description,
                               LocalDate date, List<SplitInput> splitInputs) {
        Expense expense = new Expense(amount, description, date, user, category);
        expense.replaceSplits(buildSplits(amount, category, splitInputs));
        return expenseRepository.save(expense);
    }

    /** One category/amount slice supplied by the "split this expense" form. */
    public record SplitInput(Long categoryId, Category category, BigDecimal amount, String note) {}

    private List<TransactionSplit> buildSplits(BigDecimal totalAmount, Category defaultCategory, List<SplitInput> splitInputs) {
        List<TransactionSplit> splits = new ArrayList<>();
        if (splitInputs == null || splitInputs.size() < 2) {
            splits.add(new TransactionSplit(defaultCategory, totalAmount, null));
            return splits;
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (SplitInput s : splitInputs) {
            splits.add(new TransactionSplit(s.category(), s.amount(), s.note()));
            sum = sum.add(s.amount());
        }
        // Allow a 1-cent tolerance for rounding; anything more means the split doesn't add up.
        if (sum.subtract(totalAmount).abs().compareTo(new BigDecimal("0.01")) > 0) {
            throw new IllegalArgumentException(
                    String.format("Split amounts (%.2f) must add up to the total (%.2f).", sum, totalAmount));
        }
        return splits;
    }

    /** Real-time budget-cap check (Slide 9 / Slide 13). Called right after an expense is logged. */
    public String checkBudgetStatus(Long userId, Long categoryId, LocalDate date) {
        YearMonth month = YearMonth.from(date);
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        BigDecimal spent = transactionSplitRepository.sumByUserAndCategoryAndDateRange(userId, categoryId, start, end);

        return budgetRepository.findByUserIdAndCategoryId(userId, categoryId)
                .map(budget -> {
                    if (spent.compareTo(budget.getMonthlyLimit()) > 0) {
                        String message = String.format("Budget exceeded! Spent %.2f of %.2f limit.", spent, budget.getMonthlyLimit());
                        notificationRepository.save(new Notification(message, userFor(userId)));
                        return message;
                    }
                    return String.format("Within budget: %.2f of %.2f used.",
                            spent, budget.getMonthlyLimit());
                })
                .orElse("No budget set for this category.");
    }

    private User userFor(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("User not found for budget notification"));
    }

    /** Real-time net balance = total income - total expenses (all-time). */
    public BigDecimal getNetBalance(Long userId) {
        LocalDate start = LocalDate.of(1970, 1, 1);
        LocalDate end = LocalDate.now();
        BigDecimal totalIncome = incomeRepository.sumByUserAndDateRange(userId, start, end);
        BigDecimal totalExpense = expenseRepository.sumByUserAndDateRange(userId, start, end);
        return totalIncome.subtract(totalExpense);
    }

    /** Monthly aggregate summary for the dashboard. */
    public BigDecimal getMonthlyIncome(Long userId, YearMonth month) {
        return incomeRepository.sumByUserAndDateRange(userId, month.atDay(1), month.atEndOfMonth());
    }

    public BigDecimal getMonthlyExpense(Long userId, YearMonth month) {
        return expenseRepository.sumByUserAndDateRange(userId, month.atDay(1), month.atEndOfMonth());
    }

    public List<Income> getIncomeHistory(Long userId) {
        return incomeRepository.findByUserIdOrderByDateDesc(userId);
    }

    public List<Expense> getExpenseHistory(Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId);
    }

    public List<Budget> getBudgets(Long userId) {
        return budgetRepository.findByUserId(userId);
    }
}
