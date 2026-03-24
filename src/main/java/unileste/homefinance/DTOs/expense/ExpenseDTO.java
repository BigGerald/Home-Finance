package unileste.homefinance.DTOs.expense;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import unileste.homefinance.domain.constants.ExpenseStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDTO {
    private String id;
    private String description;
    private BigDecimal amount;
    private ExpenseStatus status;
    private LocalDate dueDate;
    private String categoryName;
    private ResponsibleDTO responsible;
    private List<ExpenseSplitDTO> splits;
}
