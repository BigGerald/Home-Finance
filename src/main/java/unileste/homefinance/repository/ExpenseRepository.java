package unileste.homefinance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import unileste.homefinance.domain.constants.ExpenseStatus;
import unileste.homefinance.domain.entity.Expense;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    List<Expense> findByHouseId(UUID houseId);

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
}
