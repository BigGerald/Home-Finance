package unileste.homefinance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import unileste.homefinance.DTOs.report.CategoryExpenseReportData;
import unileste.homefinance.DTOs.report.MonthExpensesReportData;
import unileste.homefinance.domain.constants.ExpenseStatus;
import unileste.homefinance.domain.entity.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    Optional<Expense> findById(UUID id);

    List<Expense> findByHouseId(UUID houseId);

    Optional<Expense> findByIdAndHouseId(UUID id, UUID houseId);

    @Query("""
                SELECT e FROM Expense e
                WHERE e.house.id = :houseId
                AND (:status IS NULL OR e.status = :status)
                AND (:responsibleId IS NULL OR e.responsibleId = :responsibleId)
                AND e.dueDate >= COALESCE(:startDate, e.dueDate)
                AND e.dueDate <= COALESCE(:endDate, e.dueDate)
            """)
    List<Expense> findWithFilters(
            UUID houseId,
            ExpenseStatus status,
            UUID responsibleId,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
                SELECT e FROM Expense e
                WHERE e.house.id = :houseId
                AND e.status = COALESCE(:status, e.status)
                AND e.dueDate >= COALESCE(:startDate, e.dueDate)
                AND e.dueDate <= COALESCE(:endDate, e.dueDate)
                ORDER BY e.dueDate ASC
            """)
    List<Expense> findPendingExpensesByHouseIdAndStatusAndDueDateBetween(
            UUID houseId,
            ExpenseStatus status,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Expense> findByStatusAndHouseIdAndDueDateBetween(ExpenseStatus status, UUID houseId, LocalDate startDate, LocalDate endDate);

    @Query("""
                SELECT new unileste.homefinance.DTOs.report.CategoryExpenseReportData(
                    c.name,
                    SUM(e.amount)
                )
                FROM Expense e
                LEFT JOIN e.category c
                WHERE e.house.id = :houseId
                AND e.dueDate BETWEEN :start AND :end
                GROUP BY c.name
            """)
    List<CategoryExpenseReportData> getCategoryExpenses(UUID houseId, LocalDate start, LocalDate end);

    @Query("""
    SELECT new unileste.homefinance.DTOs.report.MonthExpensesReportData(
        EXTRACT(MONTH FROM e.dueDate),
        SUM(e.amount)
    )
    FROM Expense e
    WHERE e.house.id = :houseId
      AND EXTRACT(YEAR FROM e.dueDate) = :year
    GROUP BY EXTRACT(MONTH FROM e.dueDate)
    ORDER BY EXTRACT(MONTH FROM e.dueDate)
""")
    List<MonthExpensesReportData> getMonthlyExpenses(UUID houseId, Integer year);

    @Query("""
                SELECT SUM(e.amount)
                FROM Expense e
                WHERE e.house.id = :houseId
                AND e.dueDate BETWEEN :start AND :end
            """)
    BigDecimal getMonthTotal(UUID houseId, LocalDate start, LocalDate end);

    @Query("""
                SELECT e FROM Expense e
                WHERE e.house.id = :houseId
                AND e.dueDate BETWEEN :start AND :end
                AND e.amount = (
                    SELECT MAX(e2.amount)
                    FROM Expense e2
                    WHERE e2.house.id = :houseId
                    AND e2.dueDate BETWEEN :start AND :end
                )
            """)
    List<Expense> findBiggestExpense(UUID houseId, LocalDate start, LocalDate end);
}
