package unileste.homefinance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import unileste.homefinance.DTOs.report.CategoryExpenseReportData;
import unileste.homefinance.DTOs.report.ExpensesReportResponse;
import unileste.homefinance.DTOs.report.MonthExpensesReportData;
import unileste.homefinance.DTOs.report.MonthExpensesResume;
import unileste.homefinance.domain.constants.MemberStatus;
import unileste.homefinance.domain.entity.Expense;
import unileste.homefinance.domain.entity.House;
import unileste.homefinance.domain.entity.HouseMember;
import unileste.homefinance.exceptions.HouseNotFoundException;
import unileste.homefinance.repository.ExpenseRepository;
import unileste.homefinance.repository.HouseMemberRepository;
import unileste.homefinance.utils.JwtUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ExpenseRepository expenseRepository;
    private final HouseMemberRepository houseMemberRepository;
    private final JwtUtils jwtUtils;

    public ExpensesReportResponse getActualMonthReport() {

        UUID requestUserId = UUID.fromString(jwtUtils.getUserId());
        log.info("getActualMonthReport() - [START] - userId = {}", requestUserId);
        House house = getUserActiveHouse(requestUserId);
        UUID houseId = house.getId();
        log.info("getActualMonthReport() - user has active house");
        LocalDate start = getMonthStart();
        LocalDate end = getMonthEnd();
        log.info("getActualMonthReport() - building report for period: {} to {}", start, end);

        List<CategoryExpenseReportData> categoryExpenses = getCategoryExpenses(houseId, start, end);
        List<MonthExpensesReportData> monthlyExpenses = getMonthlyExpenses(houseId, start.getYear());
        BigDecimal monthTotal = getMonthTotal(houseId, start, end);
        Expense biggestExpense = getBiggestExpense(houseId, start, end);
        MonthExpensesResume resume = buildMonthResume(monthTotal, biggestExpense);

        log.info("getActualMonthReport() - [END] - report generated successfully");

        return new ExpensesReportResponse(
                categoryExpenses,
                monthlyExpenses,
                resume
        );
    }

    private House getUserActiveHouse(UUID userId) {
        log.info("getUserActiveHouse() - [START] - userId = {}", userId);
        HouseMember member = houseMemberRepository
                .findByUserIdAndStatus(userId, MemberStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.error("getUserActiveHouse() - user is not active in any house");
                    return new HouseNotFoundException("User is not active in a house");
                });
        log.info("getUserActiveHouse() - [END] - houseId = {}", member.getHouse().getId());
        return member.getHouse();
    }

    private LocalDate getMonthStart() {
        return LocalDate.now().withDayOfMonth(1);
    }

    private LocalDate getMonthEnd() {
        return LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
    }

    private List<CategoryExpenseReportData> getCategoryExpenses(UUID houseId, LocalDate start, LocalDate end) {
        log.info("getCategoryExpenses() - [START] - houseId = {}", houseId);
        List<CategoryExpenseReportData> result =
                expenseRepository.getCategoryExpenses(houseId, start, end);
        log.info("getCategoryExpenses() - [END] - {} categories found", result.size());
        return result;
    }

    private List<MonthExpensesReportData> getMonthlyExpenses(UUID houseId, int year) {
        log.info("getMonthlyExpenses() - [START] - houseId = {}", houseId);
        List<MonthExpensesReportData> result =
                expenseRepository.getMonthlyExpenses(houseId, year);
        log.info("getMonthlyExpenses() - [END] - {} months found for year {}", result.size(), year);
        return result;
    }

    private BigDecimal getMonthTotal(UUID houseId, LocalDate start, LocalDate end) {
        log.info("getMonthTotal() - [START] - houseId = {}", houseId);
        BigDecimal total =
                expenseRepository.getMonthTotal(houseId, start, end);
        if (total == null) {
            log.info("getMonthTotal() - total is null, setting to ZERO");
            total = BigDecimal.ZERO;
        }
        log.info("getMonthTotal() - [END] - total = {}", total);
        return total;
    }


    private Expense getBiggestExpense(UUID houseId, LocalDate start, LocalDate end) {
        log.info("getBiggestExpense() - [START] - houseId = {}", houseId);
        List<Expense> expenses =
                expenseRepository.findBiggestExpense(houseId, start, end);
        if (expenses.isEmpty()) {
            log.info("getBiggestExpense() - no expenses found");
            return null;
        }
        Expense biggest = expenses.get(0);
        log.info("getBiggestExpense() - [END] - biggestExpenseId = {}", biggest.getId());
        return biggest;
    }

    private MonthExpensesResume buildMonthResume(BigDecimal total, Expense biggest) {
        log.info("buildMonthResume() - [START]");
        MonthExpensesResume resume = new MonthExpensesResume();
        resume.setMonthTotalExpenses(total);
        if (biggest != null) {
            log.info("buildMonthResume() - biggest expense found - {}", biggest.getTitle());
            resume.setBiggestExpenseTitle(biggest.getTitle());
            resume.setExpenseStatus(biggest.getStatus());
        } else {
            log.info("buildMonthResume() - no biggest expense found");
        }
        log.info("buildMonthResume() - [END]");
        return resume;
    }
}