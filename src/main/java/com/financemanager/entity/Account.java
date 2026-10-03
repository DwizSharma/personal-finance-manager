package com.financemanager.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="accounts")
public class Account {
    public enum Type { SAVINGS, CHECKING, CREDIT_CARD, WALLET }
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String accountName;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Type accountType;
    @Column(nullable=false, precision=19, scale=2) private BigDecimal balance=BigDecimal.ZERO;
    @Column(nullable=false, length=3) private String currency="INR";
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id", nullable=false) private User user;
    public Account() {}
    public Account(String name, Type type, BigDecimal balance, String currency, User user) { this.accountName=name; this.accountType=type; this.balance=balance; this.currency=currency; this.user=user; }
    public Long getId(){return id;} public String getAccountName(){return accountName;} public void setAccountName(String v){accountName=v;}
    public Type getAccountType(){return accountType;} public void setAccountType(Type v){accountType=v;}
    public BigDecimal getBalance(){return balance;} public void setBalance(BigDecimal v){balance=v;}
    public String getCurrency(){return currency;} public void setCurrency(String v){currency=v;}
    public User getUser(){return user;} public void setUser(User v){user=v;}
}
