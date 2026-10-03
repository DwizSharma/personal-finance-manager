package com.financemanager.controller;

import com.financemanager.entity.User;
import com.financemanager.service.FinanceService;
import com.financemanager.service.UserService;
import com.financemanager.service.FinancialHealthService;
import com.financemanager.repository.SavingsGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.YearMonth;

@Controller
public class DashboardController {

    private final FinanceService financeService;
    private final UserService userService;
    private final FinancialHealthService healthService;
    private final SavingsGoalRepository goals;

    @Autowired
    public DashboardController(FinanceService financeService, UserService userService, FinancialHealthService healthService, SavingsGoalRepository goals) {
        this.financeService = financeService;
        this.userService = userService;
        this.healthService = healthService;
        this.goals = goals;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userService.findByUsername(principal.getUsername());
        YearMonth currentMonth = YearMonth.now();

        model.addAttribute("user", user);
        model.addAttribute("healthScore", healthService.score(user.getId()));
        model.addAttribute("savingsGoals", goals.findByUserId(user.getId()));
        model.addAttribute("netBalance", financeService.getNetBalance(user.getId()));
        model.addAttribute("monthlyIncome", financeService.getMonthlyIncome(user.getId(), currentMonth));
        model.addAttribute("monthlyExpense", financeService.getMonthlyExpense(user.getId(), currentMonth));
        model.addAttribute("incomeHistory", financeService.getIncomeHistory(user.getId()));
        model.addAttribute("expenseHistory", financeService.getExpenseHistory(user.getId()));
        model.addAttribute("budgets", financeService.getBudgets(user.getId()));

        return "dashboard"; // templates/dashboard.html
    }
}
