package com.financemanager.controller;

import com.financemanager.entity.User;
import com.financemanager.service.FinanceService;
import com.financemanager.service.UserService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.YearMonth;
import java.util.List;

@Controller
public class ReportController {
    private final UserService users;
    private final FinanceService finance;
    public ReportController(UserService users, FinanceService finance) { this.users=users; this.finance=finance; }

    @GetMapping("/reports/download")
    public ResponseEntity<byte[]> download(@AuthenticationPrincipal UserDetails principal,
                                           @RequestParam(defaultValue="csv") String format) throws IOException {
        User user=users.findByUsername(principal.getUsername());
        YearMonth month=YearMonth.now();
        List<String> lines=List.of("Personal Finance Report - "+month,
                "Income: "+finance.getMonthlyIncome(user.getId(),month),
                "Expenses: "+finance.getMonthlyExpense(user.getId(),month),
                "Net: "+finance.getMonthlyIncome(user.getId(),month).subtract(finance.getMonthlyExpense(user.getId(),month)));
        byte[] bytes; MediaType type; String filename;
        if("pdf".equalsIgnoreCase(format)) {
            try(PDDocument document=new PDDocument(); ByteArrayOutputStream output=new ByteArrayOutputStream()) {
                document.addPage(new PDPage());
                try(PDPageContentStream content=new PDPageContentStream(document,document.getPage(0))) {
                    content.beginText(); content.setFont(PDType1Font.HELVETICA,12); content.newLineAtOffset(55,740);
                    for(String line:lines){content.showText(line);content.newLineAtOffset(0,-22);} content.endText();
                }
                document.save(output); bytes=output.toByteArray();
            }
            type=MediaType.APPLICATION_PDF; filename="finance-report.pdf";
        } else {
            String csv="metric,amount\n"+String.join("\n",lines.subList(1,lines.size()).stream().map(line->line.replace(": ",",")).toList())+"\n";
            bytes=csv.getBytes(StandardCharsets.UTF_8); type=MediaType.parseMediaType("text/csv"); filename="finance-report.csv";
        }
        return ResponseEntity.ok().contentType(type).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+filename+"\"").body(bytes);
    }
}
