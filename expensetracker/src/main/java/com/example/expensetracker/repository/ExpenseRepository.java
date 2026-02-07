package com.example.expensetracker.repository;

import com.example.expensetracker.dto.CategoryTotal;
import com.example.expensetracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("""
        select coalesce(sum(e.amount), 0)
        from Expense e
        where e.expenseDate between :start and :end
    """)
    BigDecimal sumByDateRange(@Param("start") LocalDate start,
                              @Param("end") LocalDate end);

    @Query("""
        select new com.example.expensetracker.dto.CategoryTotal(e.category, coalesce(sum(e.amount), 0))
        from Expense e
        where e.expenseDate between :start and :end
        group by e.category
        order by sum(e.amount) desc
    """)
    List<CategoryTotal> totalsByCategory(@Param("start") LocalDate start,
                                         @Param("end") LocalDate end);
}
