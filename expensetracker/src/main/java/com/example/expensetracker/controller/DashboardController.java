package com.example.expensetracker.controller;

import com.example.expensetracker.dto.CategoryTotal;
import com.example.expensetracker.service.ExpenseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;

@Controller
public class DashboardController {

    private final ExpenseService service;

    public DashboardController(ExpenseService service) {
        this.service = service;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        YearMonth thisMonth = YearMonth.now();
        YearMonth lastMonth = thisMonth.minusMonths(1);

        LocalDate thisStart = thisMonth.atDay(1);
        LocalDate thisEnd = thisMonth.atEndOfMonth();

        LocalDate lastStart = lastMonth.atDay(1);
        LocalDate lastEnd = lastMonth.atEndOfMonth();

        var totalThisMonth = service.getTotalSpent(thisStart, thisEnd);
        var totalLastMonth = service.getTotalSpent(lastStart, lastEnd);


        List<CategoryTotal> categoryTotals = service.getTotalsByCategory(thisStart, thisEnd);

        var chartLabels = categoryTotals.stream()
                .map(CategoryTotal::category)
                .toList();

        var chartTotals = categoryTotals.stream()
                .map(ct -> ct.total().doubleValue())
                .toList();
        NumberFormat twd = NumberFormat.getCurrencyInstance(Locale.TAIWAN);
        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartTotals", chartTotals);
        model.addAttribute("monthLabel", thisMonth.toString()); // e.g. 2026-02
        model.addAttribute("totalThisMonth", twd.format(totalThisMonth));
        model.addAttribute("totalLastMonth", twd.format(totalLastMonth));
        model.addAttribute("categoryTotals", categoryTotals);

        return "dashboard";
    }
}
