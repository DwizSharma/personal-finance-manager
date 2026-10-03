package com.financemanager.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod = PaymentMethod.CASH;

    @Column(length = 120, unique = true)
    private String transactionRefNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    /**
     * How this expense's total is actually allocated across categories. A plain
     * (non-split) expense still has exactly one row here, mirroring {@link #category}
     * and {@link #amount}, so every reporting/budget query can read from splits alone.
     */
    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TransactionSplit> splits = new ArrayList<>();

    public Expense() {}

    public Expense(BigDecimal amount, String description, LocalDate date, User user, Category category) {
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.user = user;
        this.category = category;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getTransactionRefNo() { return transactionRefNo; }
    public void setTransactionRefNo(String transactionRefNo) { this.transactionRefNo = transactionRefNo; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public List<TransactionSplit> getSplits() { return splits; }
    public void setSplits(List<TransactionSplit> splits) { this.splits = splits; }

    /** True when this expense's amount is divided across more than one category. */
    public boolean isSplit() { return splits != null && splits.size() > 1; }

    /** Replaces the current splits in place (keeps the managed collection instance for orphanRemoval). */
    public void replaceSplits(List<TransactionSplit> newSplits) {
        this.splits.clear();
        for (TransactionSplit s : newSplits) {
            s.setExpense(this);
            this.splits.add(s);
        }
    }
}
