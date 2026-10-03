package com.financemanager.repository;

import com.financemanager.entity.TransactionSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Category-attribution queries read through here rather than through {@code Expense}
 * directly, since a split expense's amount is spread across categories other than its
 * own {@code category} field. Every expense (split or not) has at least one split row,
 * so this gives correct per-category totals regardless of whether any given expense
 * was divided.
 */
public interface TransactionSplitRepository extends JpaRepository<TransactionSplit, Long> {

    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM TransactionSplit s " +
           "WHERE s.expense.user.id = :userId AND s.category.id = :categoryId " +
           "AND s.expense.date BETWEEN :start AND :end")
    BigDecimal sumByUserAndCategoryAndDateRange(@Param("userId") Long userId,
                                                 @Param("categoryId") Long categoryId,
                                                 @Param("start") LocalDate start,
                                                 @Param("end") LocalDate end);
}
