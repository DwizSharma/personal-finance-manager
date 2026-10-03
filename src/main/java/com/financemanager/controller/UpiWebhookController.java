package com.financemanager.controller;
import com.financemanager.entity.User; import com.financemanager.service.*; import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.security.core.userdetails.UserDetails; import org.springframework.web.bind.annotation.*; import java.util.Map;
@RestController @RequestMapping("/api/v1/upi")
public class UpiWebhookController {
 private final UpiIntegrationService upi; private final UserService users; private final FinanceService finance;
 public UpiWebhookController(UpiIntegrationService u,UserService s,FinanceService f){upi=u;users=s;finance=f;}
 @PostMapping("/webhook") public Map<String,Object> ingest(@AuthenticationPrincipal UserDetails p,@RequestBody Map<String,String> body){User user=users.findByUsername(p.getUsername());var expense=upi.ingest(user,body.getOrDefault("message",""));String budgetAlert=finance.checkBudgetStatus(user.getId(),expense.getCategory().getId(),expense.getDate());return Map.of("success",true,"expenseId",expense.getId(),"amount",expense.getAmount(),"merchant",expense.getDescription(),"budgetAlert",budgetAlert);}
}
