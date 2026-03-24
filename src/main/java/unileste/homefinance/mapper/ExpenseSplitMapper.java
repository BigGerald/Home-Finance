package unileste.homefinance.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import unileste.homefinance.DTOs.expense.ExpenseSplitDTO;
import unileste.homefinance.domain.entity.ExpenseSplit;
import unileste.homefinance.service.UserService;

@Component
@RequiredArgsConstructor
public class ExpenseSplitMapper {
    private final UserService userService;

    public ExpenseSplitDTO toExpenseSplitDTO(ExpenseSplit expenseSplit) {
        ExpenseSplitDTO expenseSplitDTO = new ExpenseSplitDTO();
        expenseSplitDTO.setUserId(expenseSplit.getUserId().toString());
        expenseSplitDTO.setUserName(userService.getUserById(expenseSplit.getUserId().toString()).getDisplayName());
        expenseSplitDTO.setStatus(expenseSplit.getStatus());
        expenseSplitDTO.setAmount(expenseSplit.getAmount());
        return expenseSplitDTO;
    }
}
