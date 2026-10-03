package com.financemanager.config;

import com.financemanager.entity.Expense;
import com.financemanager.entity.TransactionSplit;
import com.financemanager.repository.ExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * One-time backfill for the Split Transactions feature: any {@link Expense} saved
 * before this feature existed has zero {@link TransactionSplit} rows. The real-time
 * budget check now reads from splits, so an un-migrated expense would silently vanish
 * from that number. This gives every such expense a single split mirroring its own
 * category/amount, then never touches it again. Safe to run on every startup —
 * expenses that already have a split are skipped.
 */
@Component
public class SplitBackfillRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SplitBackfillRunner.class);

    private final ExpenseRepository expenseRepository;

    public SplitBackfillRunner(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<Expense> all = expenseRepository.findAll();
        int backfilled = 0;
        for (Expense expense : all) {
            if (expense.getSplits() == null || expense.getSplits().isEmpty()) {
                expense.replaceSplits(List.of(
                        new TransactionSplit(expense.getCategory(), expense.getAmount(), null)));
                backfilled++;
            }
        }
        if (backfilled > 0) {
            expenseRepository.saveAll(all);
            log.info("Split Transactions backfill: added a default split to {} pre-existing expense(s).", backfilled);
        }
    }
}
