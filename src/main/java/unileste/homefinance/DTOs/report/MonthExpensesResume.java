package unileste.homefinance.DTOs.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import unileste.homefinance.domain.constants.ExpenseStatus;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthExpensesResume {
    private BigDecimal monthTotalExpenses;
    private String biggestExpenseTitle;
    private ExpenseStatus  expenseStatus;
}
