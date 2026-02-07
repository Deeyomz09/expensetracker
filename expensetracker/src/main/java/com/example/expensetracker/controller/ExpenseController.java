package com.example.expensetracker.controller;

import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.NumberFormat;
import java.util.Locale;


@Controller
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/expenses";
    }

    @GetMapping("/expenses")
    public String list(Model model) {

        var expenses = service.findAll();
        var total = service.getTotalSpent();

        // Format as NT$ 1,234.00
        NumberFormat twdFormat = NumberFormat.getCurrencyInstance(Locale.TAIWAN);
        String formattedTotal = twdFormat.format(total);

        model.addAttribute("expenses", expenses);
        model.addAttribute("totalSpent", formattedTotal);

        return "expenses/list";
    }


    @GetMapping("/expenses/new")
    public String createForm(Model model) {
        model.addAttribute("expense", new Expense());
        return "expenses/form";
    }

    @PostMapping("/expenses")
    public String create(@Valid @ModelAttribute("expense") Expense expense,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "expenses/form";
        }
        service.save(expense);
        return "redirect:/expenses";
    }

    @GetMapping("/expenses/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("expense", service.findById(id));
        return "expenses/form";
    }

    @PostMapping("/expenses/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("expense") Expense expense,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "expenses/form";
        }
        expense.setId(id);
        service.save(expense);
        return "redirect:/expenses";
    }

    @PostMapping("/expenses/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/expenses";
    }
}
