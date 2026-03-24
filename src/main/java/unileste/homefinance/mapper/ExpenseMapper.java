package unileste.homefinance.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import unileste.homefinance.DTOs.expense.ExpenseDTO;
import unileste.homefinance.DTOs.expense.ResponsibleDTO;
import unileste.homefinance.domain.entity.Expense;
import unileste.homefinance.service.UserService;

@Component
@RequiredArgsConstructor
public class ExpenseMapper {
    private final UserService userService;
    private final ExpenseSplitMapper  expenseSplitMapper;

    public ExpenseDTO  expenseToExpenseDTO(Expense expense) {
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setId(expense.getId().toString());
        expenseDTO.setDescription(expense.getDescription());
        expenseDTO.setAmount(expense.getAmount());
        expenseDTO.setStatus(expense.getStatus());
        expenseDTO.setDueDate(expense.getDueDate());
        expenseDTO.setCategoryName(expense.getCategory().getName());
        expenseDTO.setResponsible(ResponsibleDTO.builder()
                        .id(expense.getResponsibleId() != null ? expense.getResponsibleId().toString() : null)
                        .displayName(expense.getResponsibleId() != null ? userService.getUserById(expense.getResponsibleId().toString()).getDisplayName() : null)
                .build());
        expenseDTO.setSplits(expense.getSplits().stream().map(expenseSplitMapper::toExpenseSplitDTO).toList());
        return expenseDTO;
    }
}
