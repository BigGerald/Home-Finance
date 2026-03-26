package unileste.homefinance.DTOs.house;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateHouseBalanceResponse {
    private String message;
    private BigDecimal newBalance;
}
