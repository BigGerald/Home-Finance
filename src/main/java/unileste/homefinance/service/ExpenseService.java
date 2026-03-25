package unileste.homefinance.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import unileste.homefinance.DTOs.expense.CreateExpenseRequestBody;
import unileste.homefinance.DTOs.expense.ExpenseDTO;
import unileste.homefinance.DTOs.expense.UpdateExpenseStatusRequest;
import unileste.homefinance.domain.constants.ExpenseStatus;
import unileste.homefinance.domain.constants.MemberStatus;
import unileste.homefinance.domain.entity.Category;
import unileste.homefinance.domain.entity.Expense;
import unileste.homefinance.domain.entity.House;
import unileste.homefinance.domain.entity.HouseMember;
import unileste.homefinance.exceptions.ExpenseException;
import unileste.homefinance.mapper.ExpenseMapper;
import unileste.homefinance.repository.CategoryRepository;
import unileste.homefinance.repository.ExpenseRepository;
import unileste.homefinance.repository.HouseMemberRepository;
import unileste.homefinance.utils.JwtUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final HouseMemberRepository houseMemberRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final ExpenseMapper expenseMapper;

    @Transactional
    public List<ExpenseDTO> getHouseExpenses(ExpenseStatus status, Integer month, Integer year, String responsibleUserId) {
        UUID requestUserId = UUID.fromString(jwtUtils.getUserId());
        List<Expense> expenses;
        log.info("getAllHouseExpenses() - [START] - for user {}", requestUserId);
        log.info("getAllHouseExpenses() - Find User HouseMemberData");
        HouseMember requestHouseMemberData = houseMemberRepository.findByUserIdAndStatus(requestUserId, MemberStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.error("getAllHouseExpenses() - HouseMember not found");
                    return new ExpenseException("User is not an active member of any house");
                });
        if (status == null && month == null && year == null && (responsibleUserId == null || responsibleUserId.isEmpty())) {
            log.info("getAllHouseExpenses() - No filters provided, returning all house expenses");
            expenses = expenseRepository.findByHouseId(requestHouseMemberData.getHouse().getId());
        } else {
            log.info("getAllHouseExpenses() - Filters provided, applying filters to query");
            expenses = expenseRepository.findWithFilters(
                    requestHouseMemberData.getHouse().getId(),
                    status,
                    responsibleUserId != null && !responsibleUserId.isEmpty() ? UUID.fromString(responsibleUserId) : null,
                    getStartDateByMonthAndYear(month, year),
                    getEndDateByMonthAndYear(month, year)
            );
        }
        if (expenses.isEmpty()) {
            log.info("getAllHouseExpenses() - No expenses found");
            return new ArrayList<>();
        }
        log.info("getAllHouseExpenses() - Returning expenses, {} expenses found", expenses.size());
        return expenses.stream().map(expenseMapper::expenseToExpenseDTO).toList();
    }

    @Transactional
    public ExpenseDTO createExpense(CreateExpenseRequestBody requestBody) {
        UUID requestUserId = UUID.fromString(jwtUtils.getUserId());
        log.info("createExpense() - [START] - for user {}", requestUserId);
        log.info("createExpense() - Validating request body");
        requestBody.validateRequest();
        log.info("createExpense() - Request body validated, finding user house member data");
        HouseMember houseMemberData = houseMemberRepository.findByUserIdAndStatus(requestUserId, MemberStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.error("createExpense() - HouseMember not found for user {}", requestUserId);
                    return new ExpenseException("User is not an active member of any house");
                });
        log.info("createExpense() - House member data found, creating expense entity");
        log.info("createExpense() - Finding expense category with id {}", requestBody.getCategoryId());
        Category expenseCategory = categoryRepository.findById(UUID.fromString(requestBody.getCategoryId()))
                .orElseThrow(() -> {
                    log.error("createExpense() - Category not found with id {}", requestBody.getCategoryId());
                    return new ExpenseException("Category not found");
                });
        log.info("createExpense() - Expense category found, validating split users");
        validateIfUsersBelongToHouse(requestBody.getSplitUsersIds(), houseMemberData.getHouse());
        validateIfUsersExists(requestBody.getSplitUsersIds());
        log.info("createExpense() - Split users validated, creating expense entity");
        Expense expense = Expense.builder()
                .title(requestBody.getTitle())
                .description(requestBody.getDescription())
                .amount(requestBody.getAmount())
                .dueDate(requestBody.getDueDate())
                .house(houseMemberData.getHouse())
                .creatorId(requestUserId)
                .responsibleId(requestBody.getResponsibleId() != null ? UUID.fromString(requestBody.getResponsibleId()) : null)
                .status(ExpenseStatus.PENDING)
                .category(expenseCategory)
                .build();
        expense.addSplits(requestBody.getSplitUsersIds(), requestBody.getAmount());
        Expense expenseSaved = expenseRepository.save(expense);
        log.info("createExpense() - Expense saved for expense {}", expense.getId());
        return expenseMapper.expenseToExpenseDTO(expenseSaved);
    }

    @Transactional
    public ExpenseDTO getExpenseById(UUID expenseId) {
        log.info("getExpenseById() - [START] - expense with id {}", expenseId);
        HouseMember requestHouseMember = houseMemberRepository.findByUserIdAndStatus(UUID.fromString(jwtUtils.getUserId()), MemberStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.error("getExpenseById() - HouseMember not found for user {}", jwtUtils.getUserId());
                    return new ExpenseException("User is not an active member of any house");
                });
        Expense expense = expenseRepository.findByIdAndHouseId(expenseId, requestHouseMember.getHouse().getId()).orElseThrow(() -> {
            log.error("getExpenseById() - Expense not found with id {}", expenseId);
            return new EntityNotFoundException("Expense not found");
        });
        log.info("getExpenseById() - [END] - Expense found for expense {}", expense.getId());
        return expenseMapper.expenseToExpenseDTO(expense);
    }

    public ExpenseDTO updateExpenseStatus(UUID expenseId, UpdateExpenseStatusRequest  updateExpenseStatusRequest) {
        log.info("updateExpenseStatus() - [START] - expense with id {} to status {}", expenseId, updateExpenseStatusRequest.getStatus().getValue());
        HouseMember requestHouseMember = houseMemberRepository.findByUserIdAndStatus(UUID.fromString(jwtUtils.getUserId()), MemberStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.error("updateExpenseStatus() - HouseMember not found for user {}", jwtUtils.getUserId());
                    return new ExpenseException("User is not an active member of any house");
                });
        Expense expense = expenseRepository.findByIdAndHouseId(expenseId, requestHouseMember.getHouse().getId()).orElseThrow(() -> {
            log.error("updateExpenseStatus() - Expense not found with id {}", expenseId);
            return new EntityNotFoundException("Expense not found");
        });
        if(expense.getStatus() == updateExpenseStatusRequest.getStatus()) {
            log.info("updateExpenseStatus() - [END] - Expense already has status {}, no update needed for expense {}", updateExpenseStatusRequest.getStatus().getValue(), expense.getId());
            return expenseMapper.expenseToExpenseDTO(expense);
        }
        expense.setStatus(updateExpenseStatusRequest.getStatus());
        Expense updatedExpense = expenseRepository.save(expense);
        log.info("updateExpenseStatus() - [END] - Expense updated for expense {} - new status: {}", updatedExpense.getId(),  updatedExpense.getStatus());
        return expenseMapper.expenseToExpenseDTO(updatedExpense);
    }

    private void validateIfUsersBelongToHouse(List<String> usersIds, House house) {
        for (String userId : usersIds) {
            if (house.getMembers().stream().noneMatch(member -> member.getUserId().toString().equals(userId) && member.getStatus() == MemberStatus.ACTIVE)) {
                log.error("validateIfUsersBelongToHouse() - User with id {} does not belong to house with id {}", userId, house.getId());
                throw new ExpenseException("User with id " + userId + " does not belong to the house");
            }
        }
    }

    private LocalDate getStartDateByMonthAndYear(Integer month, Integer year) {
        if (month == null && year == null) {
            return null;
        }
        if (month != null && month < 1) {
            throw new IllegalArgumentException("Month cannot be less than 1");
        }
        if (year != null && year < 1) {
            throw new IllegalArgumentException("Year cannot be less than 1");
        }
        if (month != null && year == null) {
            return LocalDate.of(LocalDate.now().getYear(), month, 1);
        }
        if (month == null) {
            return LocalDate.of(LocalDate.now().getYear(), 1, 1);
        }
        return LocalDate.of(year, month, 1);
    }

    private LocalDate getEndDateByMonthAndYear(Integer month, Integer year) {
        if (month == null && year == null) {
            return null;
        }
        if (month != null && month < 1) {
            throw new IllegalArgumentException("Month cannot be less than 1");
        }
        if (year != null && year < 1) {
            throw new IllegalArgumentException("Year cannot be less than 1");
        }
        if (month != null && year == null) {
            return LocalDate.of(LocalDate.now().getYear(), month, LocalDate.of(LocalDate.now().getYear(), month, 1).lengthOfMonth());
        }
        if (month == null) {
            return LocalDate.of(LocalDate.now().getYear(), 12, LocalDate.of(LocalDate.now().getYear(), 12, 1).lengthOfMonth());
        }
        return LocalDate.of(year, month, LocalDate.of(year, month, 1).lengthOfMonth());
    }


    private void validateIfUsersExists(List<String> usersIds) {
        for (String userId : usersIds) {
            try {
                userService.getUserById(userId);
            } catch (Exception e) {
                log.error("validateIfUsersExists() - User not found with id {}", userId);
                throw new ExpenseException("User not found with id: " + userId);
            }
        }
    }
}
