package unileste.homefinance.DTOs.expense;

import lombok.Data;
import unileste.homefinance.domain.constants.ExpenseStatus;

@Data
public class UpdateExpenseStatusRequest {
    private ExpenseStatus status;
}
