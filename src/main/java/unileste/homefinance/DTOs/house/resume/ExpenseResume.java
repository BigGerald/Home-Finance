package unileste.homefinance.DTOs.house.resume;

import unileste.homefinance.domain.constants.ExpenseStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseResume {
    private String id;
    private LocalDate dueDate;
    private BigDecimal amount;
    private ExpenseStatus expenseStatus;
    private String responsibleName;
}
