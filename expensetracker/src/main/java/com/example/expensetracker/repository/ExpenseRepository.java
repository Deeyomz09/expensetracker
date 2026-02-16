package com.example.expensetracker.repository;

import com.example.expensetracker.dto.CategoryTotal;
import com.example.expensetracker.entity.AppUser;
import com.example.expensetracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // List page (user-scoped)
    List<Expense> findByUserOrderByExpenseDateDesc(AppUser user);

    // For edit/delete security (prevents accessing others' expenses by id)
    Optional<Expense> findByIdAndUser(Long id, AppUser user);

    // Total spent (all time, user-scoped)
    @Query("select coalesce(sum(e.amount), 0) from Expense e where e.user = :user")
    BigDecimal totalSpentByUser(@Param("user") AppUser user);

    // Total spent within date range (user-scoped)
    @Query("""
        select coalesce(sum(e.amount), 0)
        from Expense e
        where e.user = :user
          and e.expenseDate between :start and :end
    """)
    BigDecimal sumByUserAndDateRange(@Param("user") AppUser user,
                                     @Param("start") LocalDate start,
                                     @Param("end") LocalDate end);

    // Category totals within date range (user-scoped)
    @Query("""
        select new com.example.expensetracker.dto.CategoryTotal(
            e.category,
            coalesce(sum(e.amount), 0)
        )
        from Expense e
        where e.user = :user
          and e.expenseDate between :start and :end
        group by e.category
        order by sum(e.amount) desc
    """)
    List<CategoryTotal> totalsByUserAndCategory(@Param("user") AppUser user,
                                                @Param("start") LocalDate start,
                                                @Param("end") LocalDate end);
}
