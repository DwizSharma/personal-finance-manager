package com.financemanager.service;

import com.financemanager.entity.*;
import com.financemanager.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UpiIntegrationService {
    private final ExpenseRepository expenses; private final CategoryRepository categories; private final UpiIntegrationLogRepository logs;
    private final FinanceService finance; private final BankSmsParser parser=new BankSmsParser();
    public UpiIntegrationService(ExpenseRepository e,CategoryRepository c,UpiIntegrationLogRepository l,FinanceService f){expenses=e;categories=c;logs=l;finance=f;}
    @Transactional
    public Expense ingest(User user,String raw) {
        BankSmsParser.Parsed parsed=parser.parse(raw);
        Matcher id=Pattern.compile("(?i)(?:txn(?:id)?|ref(?:erence)?)\\s*[:#]?\\s*([A-Za-z0-9-]+)").matcher(raw);
        String ref=id.find()?id.group(1):"UPI-"+java.util.UUID.randomUUID();
        String low=raw.toLowerCase(Locale.ROOT);
        UpiIntegrationLog.Provider provider=low.contains("phonepe")?UpiIntegrationLog.Provider.PHONEPE:low.contains("paytm")?UpiIntegrationLog.Provider.PAYTM:UpiIntegrationLog.Provider.GPAY;
        Category category=categories.findByType(Category.Type.EXPENSE).stream().filter(c->parsed.merchant().toLowerCase(Locale.ROOT).contains(c.getName().toLowerCase(Locale.ROOT))).findFirst().orElseGet(()->categories.findByType(Category.Type.EXPENSE).stream().filter(c->c.getName().equalsIgnoreCase("Miscellaneous")).findFirst().orElseThrow(()->new IllegalStateException("Add an expense category before syncing transactions")));
        Expense expense=finance.addExpense(user,category,parsed.amount(),parsed.merchant(),LocalDate.now());
        expense.setPaymentMethod(PaymentMethod.UPI); expense.setTransactionRefNo(ref); expenses.save(expense);
        logs.save(new UpiIntegrationLog(provider,null,parsed.amount(),UpiIntegrationLog.Status.SUCCESS,raw,user));
        return expense;
    }
}
