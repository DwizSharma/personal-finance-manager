package com.financemanager.service;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BankSmsParser {
    private static final Pattern AMOUNT = Pattern.compile("(?i)(?:rs\\.?|inr|₹)\\s*([0-9,]+(?:\\.[0-9]{1,2})?)|([0-9,]+(?:\\.[0-9]{1,2})?)\\s*(?:rs\\.?|inr)");
    private static final Pattern ACCOUNT = Pattern.compile("(?i)(?:a/c|account|xx|\\*{2,})\\s*(?:no\\.?\\s*)?(?:x{0,4}\\*{0,4})?(\\d{4})");
    private static final Pattern MERCHANT = Pattern.compile("(?i)(?:to|at|from)\\s+(.+?)(?:\\s+(?:on|via|ref|txn|for)\\b|[.;]|$)");
    public record Parsed(BigDecimal amount, boolean debit, String accountLastFour, String merchant) {}
    public Parsed parse(String text) {
        Matcher a=AMOUNT.matcher(text); if(!a.find()) throw new IllegalArgumentException("No transaction amount found");
        String number=a.group(1)!=null?a.group(1):a.group(2);
        Matcher acct=ACCOUNT.matcher(text); Matcher merchant=MERCHANT.matcher(text);
        return new Parsed(new BigDecimal(number.replace(",","")), Pattern.compile("(?i)\\b(debit|debited|paid|spent)\\b").matcher(text).find(), acct.find()?acct.group(1):null, merchant.find()?merchant.group(1).trim():"Bank transaction");
    }
}
