package com.example.expensetracker.service;
import com.example.expensetracker.dto.CategoryTotal;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;



@Service
public class ExpenseService {

    private final ExpenseRepository repo;

    public ExpenseService(ExpenseRepository repo) {
        this.repo = repo;
    }

    public List<Expense> findAll() {
        return repo.findAll();
    }

    public Expense findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Expense not found: " + id));
    }

    public Expense save(Expense expense) {
        return repo.save(expense);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public BigDecimal getTotalSpent(){
        return repo.findAll()
                .stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }
    public java.math.BigDecimal getTotalSpent(LocalDate start, LocalDate end) {
        return repo.sumByDateRange(start, end);
    }

    public List<CategoryTotal> getTotalsByCategory(LocalDate start, LocalDate end) {
        return repo.totalsByCategory(start, end);
    }

}
