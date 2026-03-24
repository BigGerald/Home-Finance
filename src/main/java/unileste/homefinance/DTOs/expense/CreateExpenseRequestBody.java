package unileste.homefinance.DTOs.expense;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateExpenseRequestBody {

    private String title;
    private String description;
    private BigDecimal amount;
    private String categoryId;
    private LocalDate dueDate;
    private String responsibleId;
    private List<String> splitUsersIds;

    public void validateRequest() {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title is required");
        }
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Amount is required");
        }
        if (categoryId == null || categoryId.isEmpty()) {
            throw new IllegalArgumentException("CategoryId is required");
        }
        if(dueDate == null) {
            throw new IllegalArgumentException("DueDate is required");
        }
        if(dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("DueDate needs to be after now");
        }
        if(splitUsersIds == null || splitUsersIds.isEmpty()) {
            throw new IllegalArgumentException("At least one user is required to split the expense");
        }
    }
}
