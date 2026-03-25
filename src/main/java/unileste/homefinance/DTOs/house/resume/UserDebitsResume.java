package unileste.homefinance.DTOs.house.resume;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDebitsResume {
    private String displayName;
    private BigDecimal amount;
}
