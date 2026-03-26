package unileste.homefinance.DTOs.house.resume;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unileste.homefinance.domain.constants.ExpenseStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResume {
    private String id;
    private String title;
    private LocalDate dueDate;
    private BigDecimal amount;
    private ExpenseStatus expenseStatus;
    private String responsibleName;
}
