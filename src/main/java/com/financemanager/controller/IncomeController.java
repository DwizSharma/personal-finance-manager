package com.financemanager.controller;

import com.financemanager.entity.Category;
import com.financemanager.entity.User;
import com.financemanager.repository.CategoryRepository;
import com.financemanager.repository.AccountRepository;
import com.financemanager.repository.IncomeRepository;
import com.financemanager.service.FinanceService;
import com.financemanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class IncomeController {

    private final FinanceService financeService;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;
    private final IncomeRepository incomeRepository;

    @Autowired
    public IncomeController(FinanceService financeService, UserService userService,
                             CategoryRepository categoryRepository, AccountRepository accountRepository, IncomeRepository incomeRepository) {
        this.financeService = financeService;
        this.userService = userService;
        this.categoryRepository = categoryRepository;
        this.accountRepository = accountRepository;
        this.incomeRepository = incomeRepository;
    }

    @GetMapping("/addIncome")
    public String showForm(@AuthenticationPrincipal UserDetails principal, Model model) {
        model.addAttribute("categories", categoryRepository.findByType(Category.Type.INCOME));
        model.addAttribute("accounts", accountRepository.findByUserId(userService.findByUsername(principal.getUsername()).getId()));
        return "add-income"; // templates/add-income.html
    }

    @PostMapping("/addIncome")
    @Transactional
    public String addIncome(@AuthenticationPrincipal UserDetails principal,
                             @RequestParam BigDecimal amount,
                             @RequestParam String description,
                             @RequestParam LocalDate date,
                             @RequestParam Long categoryId, @RequestParam(required=false) Long accountId) {
        User user = userService.findByUsername(principal.getUsername());
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category"));

        var account=accountId==null?null:accountRepository.findByIdAndUserId(accountId,user.getId()).orElseThrow(()->new IllegalArgumentException("Invalid account"));
        var income=financeService.addIncome(user, category, amount, description, date);
        if(account!=null) { income.setAccount(account); account.setBalance(account.getBalance().add(amount)); accountRepository.save(account); }
        incomeRepository.save(income);

        return "redirect:/dashboard";
    }
}
