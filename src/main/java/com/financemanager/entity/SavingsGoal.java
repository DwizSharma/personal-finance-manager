package com.financemanager.entity;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.LocalDate;
@Entity @Table(name="savings_goals")
public class SavingsGoal {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String goalName;
 @Column(nullable=false,precision=19,scale=2) private BigDecimal targetAmount;
 @Column(nullable=false,precision=19,scale=2) private BigDecimal currentAmount=BigDecimal.ZERO;
 private LocalDate deadline;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id",nullable=false) private User user;
 public SavingsGoal(){} public Long getId(){return id;} public String getGoalName(){return goalName;} public void setGoalName(String v){goalName=v;}
 public BigDecimal getTargetAmount(){return targetAmount;} public void setTargetAmount(BigDecimal v){targetAmount=v;} public BigDecimal getCurrentAmount(){return currentAmount;} public void setCurrentAmount(BigDecimal v){currentAmount=v;}
 public LocalDate getDeadline(){return deadline;} public void setDeadline(LocalDate v){deadline=v;} public User getUser(){return user;} public void setUser(User v){user=v;}
}
