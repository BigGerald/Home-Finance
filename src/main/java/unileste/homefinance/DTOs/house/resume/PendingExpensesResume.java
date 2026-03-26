package unileste.homefinance.DTOs.house.resume;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PendingExpensesResume {
    private BigDecimal amount;
    private BigDecimal userPart;
}
