package unileste.homefinance.DTOs.house.resume;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class HouseResumeDTO {
    private String houseId;
    private String inviteCode;
    private String houseName;
    private BigDecimal balance;
    private PendingExpensesResume pendingExpenses;
    private MonthPaidExpensesResume monthPaidExpenses;
    private List<ExpenseResume> pendingMonthExpenses;
    private List<UserDebitsResume>  usersDebits;
}
