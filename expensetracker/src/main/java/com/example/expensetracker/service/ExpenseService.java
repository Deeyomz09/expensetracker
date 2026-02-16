package com.example.expensetracker.service;

import com.example.expensetracker.dto.CategoryTotal;
import com.example.expensetracker.entity.AppUser;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository repo;

    public ExpenseService(ExpenseRepository repo) {
        this.repo = repo;
    }

    // List expenses for logged-in user
    public List<Expense> findByUser(AppUser user) {
        return repo.findByUserOrderByExpenseDateDesc(user);
    }

    // Find single expense securely
    public Expense findByIdAndUser(Long id, AppUser user) {
        return repo.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found: " + id));
    }

    public Expense save(Expense expense) {
        return repo.save(expense);
    }

    public void delete(Long id, AppUser user) {
        Expense expense = findByIdAndUser(id, user);
        repo.delete(expense);
    }

    // Total spent (all time) per user
    public BigDecimal getTotalSpent(AppUser user) {
        return repo.totalSpentByUser(user);
    }

    // Total spent within date range per user
    public BigDecimal getTotalSpent(AppUser user, LocalDate start, LocalDate end) {
        return repo.sumByUserAndDateRange(user, start, end);
    }

    // Category totals per user within date range
    public List<CategoryTotal> getTotalsByCategory(AppUser user, LocalDate start, LocalDate end) {
        return repo.totalsByUserAndCategory(user, start, end);
    }
}
