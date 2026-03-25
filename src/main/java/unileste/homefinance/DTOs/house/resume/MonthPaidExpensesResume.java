package unileste.homefinance.DTOs.house.resume;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MonthPaidExpensesResume {
    private BigDecimal amount;
    private Integer quantityPaid;
}
