package unileste.homefinance.DTOs.expenseSplit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unileste.homefinance.domain.constants.ExpenseStatus;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ExpenseSplitDTO {
    private String id;
    private String userId;
    private String userName;
    private ExpenseStatus status;
    private BigDecimal amount;
}
