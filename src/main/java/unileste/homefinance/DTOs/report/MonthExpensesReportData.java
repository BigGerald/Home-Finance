package unileste.homefinance.DTOs.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class MonthExpensesReportData {
    private String month;
    private BigDecimal totalExpense;

    public MonthExpensesReportData(Integer month, BigDecimal totalExpense) {
        this.month = monthNumberToMonthName(month);
        this.totalExpense = totalExpense;
    }

    private String monthNumberToMonthName(Integer month) {
        return switch (month) {
            case 1 -> "Jan";
            case 2 -> "Feb";
            case 3 -> "Mar";
            case 4 -> "Apr";
            case 5 -> "May";
            case 6 -> "Jun";
            case 7 -> "Jul";
            case 8 -> "Aug";
            case 9 -> "Sep";
            case 10 -> "Oct";
            case 11 -> "Nov";
            case 12 -> "Dec";
            default -> "";
        };
    }
}
