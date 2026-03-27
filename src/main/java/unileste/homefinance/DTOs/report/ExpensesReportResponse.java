package unileste.homefinance.DTOs.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpensesReportResponse {
    private List<CategoryExpenseReportData> categoryExpenses; //gastos do mes atual por categoria
    private List<MonthExpensesReportData> monthlyExpenses; //gastos totais dos ultimos meses e do mes atual
    private MonthExpensesResume monthExpensesResume; //resumo do mes atual
}
