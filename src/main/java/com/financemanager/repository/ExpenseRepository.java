package com.financemanager.repository;

import com.financemanager.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // A plain @EntityGraph on a JOIN FETCH'd collection + ORDER BY would return one
    // row per split (duplicate Expense objects) for any actually-split expense, so
    // this uses an explicit JPQL "SELECT DISTINCT" instead, which Hibernate resolves
    // at the object level for fetch-joined root entities.
    @Query("SELECT DISTINCT e FROM Expense e " +
           "LEFT JOIN FETCH e.category " +
           "LEFT JOIN FETCH e.splits s " +
           "LEFT JOIN FETCH s.category " +
           "WHERE e.user.id = :userId ORDER BY e.date DESC")
    List<Expense> findByUserIdOrderByDateDesc(@Param("userId") Long userId);

    // Total spend regardless of category — this one stays on Expense.amount since it
    // doesn't need per-category attribution. Per-category totals (the real-time budget
    // check) read from TransactionSplitRepository instead, since a split expense's
    // amount is divided across categories other than its own.
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e " +
           "WHERE e.user.id = :userId AND e.date BETWEEN :start AND :end")
    BigDecimal sumByUserAndDateRange(@Param("userId") Long userId,
                                      @Param("start") LocalDate start,
                                      @Param("end") LocalDate end);
}
