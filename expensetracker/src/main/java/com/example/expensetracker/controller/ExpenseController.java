package com.example.expensetracker.controller;

import com.example.expensetracker.entity.AppUser;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.repository.AppUserRepository;
import com.example.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class ExpenseController {

    private final ExpenseService service;
    private final AppUserRepository appUserRepository;

    public ExpenseController(ExpenseService service, AppUserRepository appUserRepository) {
        this.service = service;
        this.appUserRepository = appUserRepository;
    }

    private AppUser currentUser(Principal principal) {
        return appUserRepository.findByEmail(principal.getName())
                .orElseThrow();
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/expenses";
    }

    @GetMapping("/expenses")
    public String list(Model model, Principal principal) {
        AppUser user = currentUser(principal);

        model.addAttribute("expenses", service.findByUser(user));
        model.addAttribute("totalSpent", service.getTotalSpent(user));

        return "expenses/list";
    }

    @GetMapping("/expenses/new")
    public String createForm(Model model) {
        model.addAttribute("expense", new Expense());
        return "expenses/form";
    }

    @PostMapping("/expenses")
    public String create(@Valid @ModelAttribute("expense") Expense expense,
                         BindingResult result,
                         Principal principal) {

        if (result.hasErrors()) return "expenses/form";

        AppUser user = currentUser(principal);
        expense.setUser(user);

        service.save(expense);
        return "redirect:/expenses";
    }

    @GetMapping("/expenses/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Principal principal) {
        AppUser user = currentUser(principal);

        Expense expense = service.findByIdAndUser(id, user); // ✅ secure
        model.addAttribute("expense", expense);

        return "expenses/form";
    }

    @PostMapping("/expenses/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("expense") Expense formExpense,
                         BindingResult result,
                         Principal principal) {

        if (result.hasErrors()) return "expenses/form";

        AppUser user = currentUser(principal);

        // ✅ load the existing row for this user
        Expense existing = service.findByIdAndUser(id, user);

        // ✅ update allowed fields only
        existing.setDescription(formExpense.getDescription());
        existing.setCategory(formExpense.getCategory());
        existing.setExpenseDate(formExpense.getExpenseDate());
        existing.setAmount(formExpense.getAmount());

        service.save(existing);

        return "redirect:/expenses";
    }

    @PostMapping("/expenses/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        AppUser user = currentUser(principal);

        service.delete(id, user); // ✅ FIX for your compile error
        return "redirect:/expenses";
    }
}
