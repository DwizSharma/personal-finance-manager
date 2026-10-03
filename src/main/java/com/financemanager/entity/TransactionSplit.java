package com.financemanager.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * One slice of a split expense — e.g. a single $120 supermarket receipt split into
 * $100 "Groceries" and $20 "Household Supplies". Every {@link Expense} has at least
 * one split; expenses the user didn't explicitly divide have exactly one split whose
 * category/amount mirror the parent expense, so budget and category-total queries can
 * always read from splits instead of branching on whether an expense was divided.
 */
@Entity
@Table(name = "transaction_splits")
public class TransactionSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(nullable = false)
    private BigDecimal amount;

    @Column(length = 255)
    private String note;

    public TransactionSplit() {}

    public TransactionSplit(Category category, BigDecimal amount, String note) {
        this.category = category;
        this.amount = amount;
        this.note = note;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Expense getExpense() { return expense; }
    public void setExpense(Expense expense) { this.expense = expense; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
