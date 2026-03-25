package unileste.homefinance.DTOs.house.resume;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PendingExpensesResume {
    private BigDecimal amount;
    private BigDecimal userPart;
}
