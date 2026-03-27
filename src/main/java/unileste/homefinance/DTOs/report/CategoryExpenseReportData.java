package unileste.homefinance.DTOs.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CategoryExpenseReportData {
    private String category;
    private BigDecimal totalAmount;
}
