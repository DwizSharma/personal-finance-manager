package com.financemanager.controller;

import com.financemanager.entity.*;
import com.financemanager.repository.*;
import com.financemanager.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.YearMonth;

@Controller
public class FinancePagesController {
 private final UserService users; private final FinanceService finance; private final CategoryRepository categories; private final AccountRepository accounts; private final SavingsGoalRepository goals; private final NotificationRepository notifications; private final BudgetRepository budgets; private final TransactionSplitRepository splits;
 public FinancePagesController(UserService u,FinanceService f,CategoryRepository c,AccountRepository a,SavingsGoalRepository g,NotificationRepository n,BudgetRepository b,TransactionSplitRepository splits){users=u;finance=f;categories=c;accounts=a;goals=g;notifications=n;budgets=b;this.splits=splits;}
 private User current(UserDetails principal){return users.findByUsername(principal.getUsername());}
 @GetMapping("/transactions") public String transactions(@AuthenticationPrincipal UserDetails p,Model m){User u=current(p);m.addAttribute("user",u);m.addAttribute("categories",categories.findAll());m.addAttribute("accounts",accounts.findByUserId(u.getId()));m.addAttribute("expenses",finance.getExpenseHistory(u.getId()));m.addAttribute("incomes",finance.getIncomeHistory(u.getId()));return "transactions";}
 @GetMapping("/budgets") public String budgetPage(@AuthenticationPrincipal UserDetails p,Model m){User u=current(p);var rows=finance.getBudgets(u.getId());YearMonth period=YearMonth.now();var stats=rows.stream().map(b->{var spent=splits.sumByUserAndCategoryAndDateRange(u.getId(),b.getCategory().getId(),period.atDay(1),period.atEndOfMonth());int pct=b.getMonthlyLimit().signum()==0?0:spent.multiply(BigDecimal.valueOf(100)).divide(b.getMonthlyLimit(),0,java.math.RoundingMode.HALF_UP).intValue();return java.util.Map.of("name",b.getCategory().getName(),"limit",b.getMonthlyLimit(),"spent",spent,"percent",Math.max(0,pct),"tone",pct>=100?"danger":pct>=75?"warning":"success");}).toList();m.addAttribute("user",u);m.addAttribute("budgetStats",stats);m.addAttribute("budgets",rows);m.addAttribute("categories",categories.findByType(Category.Type.EXPENSE));m.addAttribute("alerts",notifications.findByUserIdOrderByCreatedAtDesc(u.getId()));return "budgets";}
 @PostMapping("/budgets") public String saveBudget(@AuthenticationPrincipal UserDetails p,@RequestParam Long categoryId,@RequestParam BigDecimal monthlyLimit){User u=current(p);Category c=categories.findById(categoryId).orElseThrow();Budget b=budgets.findByUserIdAndCategoryId(u.getId(),categoryId).orElseGet(Budget::new);b.setUser(u);b.setCategory(c);b.setMonthlyLimit(monthlyLimit);b.setMonth(YearMonth.now().getMonthValue());b.setYear(YearMonth.now().getYear());budgets.save(b);return "redirect:/budgets";}
 @GetMapping("/settings/theme") public String theme(){return "theme-settings";}
 @GetMapping("/integrations/upi") public String upi(){return "upi-simulator";}
 @PostMapping("/accounts") public String addAccount(@AuthenticationPrincipal UserDetails p,@RequestParam String accountName,@RequestParam Account.Type accountType,@RequestParam(defaultValue="0") BigDecimal balance,@RequestParam(defaultValue="INR") String currency){Account a=new Account(accountName,accountType,balance,currency,current(p));accounts.save(a);return "redirect:/transactions";}
 @PostMapping("/goals") public String addGoal(@AuthenticationPrincipal UserDetails p,@RequestParam String goalName,@RequestParam BigDecimal targetAmount,@RequestParam(required=false) java.time.LocalDate deadline){SavingsGoal g=new SavingsGoal();g.setGoalName(goalName);g.setTargetAmount(targetAmount);g.setDeadline(deadline);g.setUser(current(p));goals.save(g);return "redirect:/dashboard";}
}
