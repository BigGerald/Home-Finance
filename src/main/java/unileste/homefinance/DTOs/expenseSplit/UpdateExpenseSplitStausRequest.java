package unileste.homefinance.DTOs.expenseSplit;

import lombok.Data;
import unileste.homefinance.domain.constants.ExpenseStatus;

@Data
public class UpdateExpenseSplitStausRequest {
    private ExpenseStatus status;
}
