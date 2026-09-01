package com.ceoms.repository;

import com.ceoms.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByEventId(Long eventId);

    @Query("SELECT COALESCE(SUM(b.approvedAmount), 0) FROM Budget b")
    BigDecimal sumApprovedBudgets();

    @Query("SELECT COALESCE(SUM(b.actualSpending), 0) FROM Budget b")
    BigDecimal sumActualSpending();
}
