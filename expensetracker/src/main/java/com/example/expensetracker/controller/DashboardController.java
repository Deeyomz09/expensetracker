package com.example.expensetracker.controller;

import com.example.expensetracker.dto.CategoryTotal;
import com.example.expensetracker.entity.AppUser;
import com.example.expensetracker.repository.AppUserRepository;
import com.example.expensetracker.service.ExpenseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;

@Controller
public class DashboardController {

    private final ExpenseService service;
    private final AppUserRepository userRepo;

    public DashboardController(ExpenseService service, AppUserRepository userRepo) {
        this.service = service;
        this.userRepo = userRepo;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {

        AppUser user = userRepo.findByEmail(principal.getName())
                .orElseThrow();

        YearMonth thisMonth = YearMonth.now();
        YearMonth lastMonth = thisMonth.minusMonths(1);

        LocalDate thisStart = thisMonth.atDay(1);
        LocalDate thisEnd = thisMonth.atEndOfMonth();

        LocalDate lastStart = lastMonth.atDay(1);
        LocalDate lastEnd = lastMonth.atEndOfMonth();

        var totalThisMonth = service.getTotalSpent(user, thisStart, thisEnd);
        var totalLastMonth = service.getTotalSpent(user, lastStart, lastEnd);

        List<CategoryTotal> categoryTotals = service.getTotalsByCategory(user, thisStart, thisEnd);

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
