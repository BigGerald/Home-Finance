package unileste.homefinance.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import unileste.homefinance.DTOs.house.CreateHouseRequestBody;
import unileste.homefinance.DTOs.house.HouseDTO;
import unileste.homefinance.DTOs.house.LeaveHouseResponse;
import unileste.homefinance.DTOs.house.UpdateHouseBalanceResponse;
import unileste.homefinance.DTOs.house.resume.*;
import unileste.homefinance.domain.constants.ExpenseStatus;
import unileste.homefinance.domain.constants.MemberRole;
import unileste.homefinance.domain.constants.MemberStatus;
import unileste.homefinance.domain.constants.TransactionType;
import unileste.homefinance.domain.entity.*;
import unileste.homefinance.exceptions.HouseException;
import unileste.homefinance.exceptions.HouseNotFoundException;
import unileste.homefinance.mapper.HouseMemberMapper;
import unileste.homefinance.repository.ExpenseRepository;
import unileste.homefinance.repository.HouseBalanceTransactionRepository;
import unileste.homefinance.repository.HouseMemberRepository;
import unileste.homefinance.repository.HouseRepository;
import unileste.homefinance.utils.JwtUtils;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HouseService {
    private final JwtUtils jwtUtils;
    private final HouseRepository houseRepository;
    private final HouseMemberMapper houseMemberMapper;
    private final HouseMemberRepository houseMemberRepository;
    private final ExpenseRepository expenseRepository;
    private final HouseBalanceTransactionRepository houseBalanceTransactionRepository;
    private final UserService userService;

    public HouseDTO createNewHouse(CreateHouseRequestBody request) {
        log.info("createNewHouse() - [START] - userId: {} | houseName: {}", jwtUtils.getUserId(), request.getName());
        log.info("createNewHouse() - validating request body");
        request.validateCreateHouseRequestBody();
        log.info("createNewHouse() -  request is Valid");
        log.info("createNewHouse() - validating if user is active in other house");
        if (houseMemberRepository.existsByUserIdAndStatus(UUID.fromString(jwtUtils.getUserId()), MemberStatus.ACTIVE)) {
            log.error("createNewHouse() - user {} is already active in another house", jwtUtils.getUserId());
            throw new HouseException("User is already active in another house. Please leave the current house before creating a new one.");
        }
        House newHouseData = buildNewHouseEntity(request.getName());
        House newHouseSaved = houseRepository.save(newHouseData);
        log.info("createNewHouse() - [END] - successfully created house - houseId: {}", newHouseSaved.getId());
        return buildHouseResponseWithActiveMembers(newHouseSaved);
    }

    @Transactional
    public HouseDTO getActiveHouseOfUser() {
        log.info("getActiveHouseOfUser() - [START] - userId: {}", jwtUtils.getUserId());
        log.info("getActiveHouseOfUser() - validating if user has a active house");
        HouseMember memberData = houseMemberRepository.findByUserIdAndStatus(UUID.fromString(jwtUtils.getUserId()), MemberStatus.ACTIVE).
                orElseThrow(() -> new HouseNotFoundException("User is not active in a house"));
        log.info("getActiveHouseOfUser() - user has an active house - houseId: {}", memberData.getHouse().getId());
        log.info("getActiveHouseOfUser() - retrieving house data from database");
        House houseEntityData = houseRepository.findById(memberData.getHouse().getId())
                .orElseThrow(() -> new HouseNotFoundException("House not found for the active membership"));
        log.info("getActiveHouseOfUser() - [END] - successfully retrieved active house for user - houseId: {}", houseEntityData.getId());
        return buildHouseResponseWithActiveMembers(houseEntityData);
    }

    @Transactional
    public HouseDTO joinHouseWithInviteCode(String inviteCode) {
        log.info("joinHouseWithInviteCode() - [START] - userId: {} - inviteCode: {}", jwtUtils.getUserId(), inviteCode);
        log.info("joinHouseWithInviteCode() - validating if user is active in other house");
        if (houseMemberRepository.existsByUserIdAndStatus(UUID.fromString(jwtUtils.getUserId()), MemberStatus.ACTIVE)) {
            log.error("joinHouseWithInviteCode() - user is already active in another house");
            throw new HouseException("User is already active in another house. Please leave the current house before joining a new one.");
        }
        log.info("joinHouseWithInviteCode() - user is not active in another house");
        log.info("joinHouseWithInviteCode() - searching house by the invite code");
        House houseEntityData = houseRepository.findByInviteCode(inviteCode).orElseThrow(() -> {
            log.error("joinHouseWithInviteCode() - no house found with the provided invite code: {}", inviteCode);
            return new HouseNotFoundException("No house found with the provided invite code");
        });
        log.info("joinHouseWithInviteCode() - house found for the invite code - houseId: {}", houseEntityData.getId());
        houseEntityData.addMember(HouseMember.builder()
                .userId(UUID.fromString(jwtUtils.getUserId()))
                .role(MemberRole.MEMBER)
                .status(MemberStatus.ACTIVE)
                .joinedAt(java.time.LocalDateTime.now())
                .build());
        houseRepository.save(houseEntityData);
        log.info("joinHouseWithInviteCode() - [END] - user successfully joined the house");
        return buildHouseResponseWithActiveMembers(houseEntityData);
    }

    @Transactional
    public LeaveHouseResponse leaveActualHouse() {
        log.info("leaveHouse() - [START] - userId: {}", jwtUtils.getUserId());
        log.info("leaveHouse() - validating if user is active in a house");
        HouseMember memberData = houseMemberRepository.findByUserIdAndStatus(UUID.fromString(jwtUtils.getUserId()), MemberStatus.ACTIVE).orElseThrow(() -> {
            log.error("leaveHouse() - no active house membership found for user");
            return new HouseNotFoundException("No active house membership found for user");
        });
        log.info("leaveHouse() - user is active in a house, houseId: {}", memberData.getHouse().getId());
        log.info("leaveHouse() - validating if User is the administrator");
        if (memberData.getRole().equals(MemberRole.ADMIN)) {
            try {
                log.info("leaveHouse() - user is the administrator");
                removeAdminFromHouse(memberData);
            } catch (Exception e) {
                log.error("leaveHouse() - error while removing admin from house - error: {}", e.getMessage());
                throw new HouseException("Error while leaving the house - error: " + e.getMessage());
            }
        }
        if (memberData.getRole().equals(MemberRole.MEMBER)) {
            try {
                log.info("leaveHouse() - user is a member");
                removeMemberFromHouse(memberData);
            } catch (Exception e) {
                log.error("leaveHouse() - error while removing member from house - error: {}", e.getMessage());
                throw new HouseException("Error while leaving the house - error: " + e.getMessage());
            }
        }
        return new LeaveHouseResponse("User has left the house successfully");
    }

    @Transactional
    public LeaveHouseResponse removeMemberFromHouseByHouseAdmin(UUID userId) {
        UUID adminId = UUID.fromString(jwtUtils.getUserId());
        log.info("removeMemberFromHouseByHouseAdmin() - [START] - adminId: {} - userId: {}", adminId, userId.toString());
        if (adminId.equals(userId)) {
            throw new HouseException("Members cannot remove themselves, use the leaveHouse method");
        }
        log.info("removeMemberFromHouseByHouseAdmin() - validating if request was made by the house administrator");
        HouseMember adminData = houseMemberRepository.findByUserIdAndRoleAndStatus(adminId, MemberRole.ADMIN, MemberStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.error("removeMemberFromHouseByHouseAdmin() - user is not the house administrator");
                    return new HouseException("User is not a house administrator");
                });
        log.info("removeMemberFromHouseByHouseAdmin() - user is a house administrator");
        log.info("removeMemberFromHouseByHouseAdmin() - validating if the member to be removed belongs to the administrator's house");
        House administratorHouse = adminData.getHouse();
        List<HouseMember> administratorHouseMembers = administratorHouse.getMembers();
        HouseMember memberData = administratorHouseMembers.stream()
                .filter(member -> member.getUserId().equals(userId))
                .filter(member -> member.getStatus() == MemberStatus.ACTIVE)
                .findFirst()
                .orElseThrow(() -> new HouseException("The member to be removed does not belong to the administrator's house"));
        log.info("removeMemberFromHouseByHouseAdmin() - member to be removed found in the administrator's house - userId: {}", memberData.getUserId());
        removeMemberFromHouse(memberData);
        return new LeaveHouseResponse("Member has been removed from the house successfully");
    }

    @Transactional
    public HouseResumeDTO getHouseResume() {
        UUID requestUserId = UUID.fromString(jwtUtils.getUserId());
        HouseResumeDTO houseResume = new HouseResumeDTO();
        log.info("getHouseResume() - [START] - userId: {}", requestUserId);
        log.info("getHouseResume() - validating if user has a active house");
        HouseMember memberData = houseMemberRepository.findByUserIdAndStatus(requestUserId, MemberStatus.ACTIVE).
                orElseThrow(() -> new HouseNotFoundException("User is not active in a house"));
        House houseData = memberData.getHouse();
        log.info("getHouseResume() - user has an active house - houseId: {}", houseData.getId());
        houseResume.setHouseId(houseData.getId().toString());
        houseResume.setInviteCode(houseData.getInviteCode());
        houseResume.setHouseName(houseData.getName());
        houseResume.setBalance(houseData.getBalance());
        log.info("getHouseResume() - finding house month pending expenses");
        PendingExpensesResume pendingExpensesResume = calculatePendingExpensesResume(requestUserId, houseData.getId());
        log.info("getHouseResume() - house month pending expenses calculated successfully");
        houseResume.setPendingExpenses(pendingExpensesResume);
        log.info("getHouseResume() - finding house month paid expenses");
        List<Expense> monthPaidExpenses = expenseRepository.findByStatusAndHouseIdAndDueDateBetween(ExpenseStatus.PAID, houseData.getId(), LocalDate.now().withDayOfMonth(1), LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
        log.info("getHouseResume() - house month paid expenses found successfully");
        MonthPaidExpensesResume monthPaidExpensesResume = calculateMonthPaidExpensesResume(monthPaidExpenses);
        log.info("getHouseResume() - house month paid expenses calculated successfully");
        houseResume.setMonthPaidExpenses(monthPaidExpensesResume);
        log.info("getHouseResume() - finding next week PENDING expenses");
        houseResume.setNextWeekExpenses(getNextWeekExpensesResume(houseData.getId()));
        log.info("getHouseResume() - next week PENDING expenses found successfully");
        log.info("getHouseResume() - finding users debits in the house");
        houseResume.setUsersDebits(getThisMonthUserDebits(houseData.getId()));
        log.info("getHouseResume() - users debits calculated successfully");
        log.info("getHouseResume() - [END]");
        return houseResume;
    }

    @Transactional
    public UpdateHouseBalanceResponse manualUpdateHouseBalance(BigDecimal valueToAdd,  BigDecimal valueToSubtract) {
        UUID requestUserId = UUID.fromString(jwtUtils.getUserId());
        log.info("manualUpdateHouseBalance() - [START] - userId: {} - valueToAdd: {} - valueToSubtract: {}", requestUserId, valueToAdd, valueToSubtract);
        log.info("manualUpdateHouseBalance() - validating request");
        TransactionType transactionType = validateUpdateHouseBalanceRequest(valueToAdd, valueToSubtract);
        log.info("manualUpdateHouseBalance() - request validated - trying to do {} operation", transactionType);
        log.info("manualUpdateHouseBalance() - validating if user has a active house");
        HouseMember memberData = houseMemberRepository.findByUserIdAndStatus(requestUserId, MemberStatus.ACTIVE).
                orElseThrow(() -> new HouseNotFoundException("User is not active in a house"));
        House houseData = memberData.getHouse();
        log.info("manualUpdateHouseBalance() - user has an active house - houseId: {}", houseData.getId());
        if(transactionType.equals(TransactionType.MANUAL_REMOVE) && houseData.getBalance().compareTo(valueToSubtract) < 0) {
            log.error("manualUpdateHouseBalance() - cannot subtract value from house balance because the value is greater than the current balance");
            throw new HouseException("Cannot subtract value from house balance because the value is greater than the current balance");
        }
        HouseBalanceTransaction newTransaction = HouseBalanceTransaction.builder()
                .amount(transactionType.equals(TransactionType.MANUAL_ADD) ? valueToAdd : valueToSubtract)
                .type(transactionType)
                .userId(requestUserId)
                .house(houseData)
                .description("Manual balance update")
                .build();

        houseBalanceTransactionRepository.save(newTransaction);
        log.info("manualUpdateHouseBalance() - transaction saved successfully");
        if(transactionType.equals(TransactionType.MANUAL_ADD)) {
            houseData.setBalance(houseData.getBalance().add(valueToAdd));
        } else {
            houseData.setBalance(houseData.getBalance().subtract(valueToSubtract));
        }
        houseRepository.save(houseData);
        log.info("manualUpdateHouseBalance() - [END] -  house balance updated successfully - new balance: {}", houseData.getBalance());
        return new UpdateHouseBalanceResponse("House balance updated successfully", houseData.getBalance());
    }

    private TransactionType validateUpdateHouseBalanceRequest(BigDecimal valueToAdd, BigDecimal valueToSubtract) {
        if(valueToAdd != null && valueToSubtract != null) {
            log.error("validateUpdateHouseBalanceRequest() - User are trying to add and subtract balance at the same time");
            throw new HouseException("You cannot add and subtract balance at the same time, please choose one of the options");
        }
        if(valueToAdd == null && valueToSubtract == null) {
            log.error("validateUpdateHouseBalanceRequest() - User did not provide any value to update the balance");
            throw new HouseException("You must provide a value to update the balance, please choose one of the options - add or subtract");
        }
        if(valueToAdd != null) {
            if(valueToAdd.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("You must provide a positive value to add or subtract");
            }
            return TransactionType.MANUAL_ADD;
        }
        if(valueToSubtract.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("You must provide a positive value to subtract the balance");
        }
        return TransactionType.MANUAL_REMOVE;
    }

    private List<UserDebitsResume> getThisMonthUserDebits(UUID houseId) {

        List<Expense> monthExpenses = expenseRepository.findPendingExpensesByHouseIdAndStatusAndDueDateBetween(
                houseId,
                null,
                LocalDate.now().withDayOfMonth(1),
                LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
        );

        Map<UUID, BigDecimal> userDebitsMap = new HashMap<>();

        for (Expense expense : monthExpenses) {
            for (ExpenseSplit split : expense.getSplits()) {
                if (split.getStatus() == ExpenseStatus.PENDING) {
                    userDebitsMap.merge(
                            split.getUserId(),
                            split.getAmount(),
                            BigDecimal::add
                    );
                } else {
                    userDebitsMap.merge(
                            split.getUserId(),
                            BigDecimal.ZERO,
                            BigDecimal::add
                    );
                }
            }
        }

        return userDebitsMap.entrySet().stream()
                .map(entry -> {
                    UUID userId = entry.getKey();
                    if (houseMemberRepository.existsByUserIdAndHouseIdAndStatus(userId, houseId, MemberStatus.ACTIVE)) {
                        BigDecimal total = entry.getValue();
                        String displayName = userService.getUserById(userId.toString()).getDisplayName();
                        return new UserDebitsResume(displayName, total);
                    }
                    return null;
                })
                .toList();
    }

    private List<ExpenseResume> getNextWeekExpensesResume(UUID houseId) {
        List<Expense> pendingExpenses = expenseRepository.findByStatusAndHouseIdAndDueDateBetween(ExpenseStatus.PENDING, houseId, LocalDate.now(), LocalDate.now().plusDays(7));
        return pendingExpenses.stream()
                .map(expense -> ExpenseResume.builder()
                        .id(expense.getId().toString())
                        .title(expense.getTitle())
                        .amount(expense.getAmount())
                        .dueDate(expense.getDueDate())
                        .expenseStatus(expense.getStatus())
                        .responsibleName(expense.getResponsibleId() != null ? userService.getUserById(expense.getResponsibleId().toString()).getDisplayName() : null)
                        .build())
                .toList();
    }

    private PendingExpensesResume calculatePendingExpensesResume(UUID userId, UUID houseId) {
        List<Expense> pendingMonthExpenses = expenseRepository.findPendingExpensesByHouseIdAndStatusAndDueDateBetween(
                houseId,
                ExpenseStatus.PENDING,
                LocalDate.now().withDayOfMonth(1),
                LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
        );
        BigDecimal totalAmount = pendingMonthExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal userPart = pendingMonthExpenses.stream()
                .flatMap(expense -> expense.getSplits().stream())
                .filter(split -> split.getUserId().equals(userId) && split.getStatus() == ExpenseStatus.PENDING)
                .map(ExpenseSplit::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new PendingExpensesResume(totalAmount, userPart);
    }

    private MonthPaidExpensesResume calculateMonthPaidExpensesResume(List<Expense> paidExpenses) {
        BigDecimal amount = paidExpenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new MonthPaidExpensesResume(amount, paidExpenses.size());
    }

    private void removeAdminFromHouse(HouseMember memberData) {
        log.info("removeAdminFromHouse() - [START] - userId: {}", memberData.getUserId());
        log.info("removeAdminFromHouse() - validating if user is the administrator");
        if (houseMemberRepository.existsByUserIdAndHouseIdAndRoleAndStatus(memberData.getUserId(), memberData.getHouse().getId(), MemberRole.ADMIN, MemberStatus.ACTIVE)) {
            log.error("removeAdminFromHouse() - user is not the administrator");
            throw new HouseException("User is not the house administrator");
        }
        log.info("removeAdminFromHouse() - user is the administrator");
        log.info("removeAdminFromHouse() - getting the house info");
        House houseEntityData = houseRepository.findById(memberData.getHouse().getId()).orElseThrow(() -> {
            log.error("removeAdminFromHouse() - no house found for the active membership");
            return new HouseNotFoundException("House not found for the active membership");
        });
        if (houseEntityData.getMembers().stream().filter(member -> member.getStatus().equals(MemberStatus.ACTIVE)).count() == 1) {
            log.info("removeAdminFromHouse() - the administrator is the unique member in the house");
            log.info("removeAdminFromHouse() - the house will be deleted");
            houseRepository.delete(houseEntityData);
            return;
        }
        log.info("removeAdminFromHouse() - the administrator is not the unique member in the house");
        log.info("removeAdminFromHouse() - searching for a new administrator among the members");
        log.info("removeAdminFromHouse() - the oldest member will be the new administrator");
        removeAdminAndChangeHouseAdminToOldestMemberInAHouse(houseEntityData);
        removeUserExpensesResponsibilityByHouse(houseEntityData, memberData.getUserId());
        houseRepository.save(houseEntityData);
        log.info("removeAdminFromHouse() - [END] - administrator removed and new administrator assigned successfully");
    }

    private void removeMemberFromHouse(HouseMember memberData) {
        log.info("removeMemberFromHouse() - [START] - userId: {}", memberData.getUserId());
        log.info("removeMemberFromHouse() - validating if user is a member");
        if (houseMemberRepository.existsByUserIdAndHouseIdAndRoleAndStatus(memberData.getUserId(), memberData.getHouse().getId(), MemberRole.ADMIN, MemberStatus.ACTIVE)) {
            log.error("removeMemberFromHouse() - user is the house administrator");
            throw new HouseException("User is the house administrator, use the right method to leave the member");
        }
        log.info("removeMemberFromHouse() - user is not the administrator");
        House houseEntityData = memberData.getHouse();
        log.info("removeMemberFromHouse() - removing the member expenses responsibilities");
        removeUserExpensesResponsibilityByHouse(houseEntityData, memberData.getUserId());
        houseEntityData.getMembers().stream().filter(member -> member.getUserId().equals(memberData.getUserId()))
                .findFirst().ifPresent(member -> {
                    member.setStatus(MemberStatus.LEFT);
                    member.setLeftAt(LocalDateTime.now());
                });
        houseRepository.save(houseEntityData);
    }

    private void removeAdminAndChangeHouseAdminToOldestMemberInAHouse(House houseEntity) {
        HouseMember oldAdmin = houseEntity.getMembers().stream()
                .filter(member -> member.getRole().equals(MemberRole.ADMIN))
                .filter(member -> member.getStatus().equals(MemberStatus.ACTIVE))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("changeAdminToOldestMemberInAHouse() - no active administrator found in the house");
                    return new HouseException("No active administrator found in the house");
                });
        HouseMember newAdmin = houseEntity.getMembers().stream()
                .filter(member -> member.getStatus().equals(MemberStatus.ACTIVE))
                .filter(member -> !member.getUserId().equals(oldAdmin.getUserId()))
                .min(java.util.Comparator.comparing(HouseMember::getJoinedAt))
                .orElseThrow(() -> {
                    log.error("changeAdminToOldestMemberInAHouse() - no eligible member found to be the new administrator");
                    return new HouseException("No eligible member found to be the new administrator");
                });
        log.info("changeAdminToOldestMemberInAHouse() - new administrator found - userId: {}", newAdmin.getUserId());
        log.info("changeAdminToOldestMemberInAHouse() - updating the new administrator role");
        houseEntity.setAdminId(newAdmin.getUserId());
        oldAdmin.setStatus(MemberStatus.LEFT);
        oldAdmin.setLeftAt(LocalDateTime.now());
        newAdmin.setRole(MemberRole.ADMIN);
    }

    private void removeUserExpensesResponsibilityByHouse(House houseEntityData, UUID userId) {
        houseEntityData.getExpenses().forEach(expense -> {
            if (userId.equals(expense.getResponsibleId())) {
                expense.setResponsibleId(null);
            }
        });
    }

    private HouseDTO buildHouseResponseWithActiveMembers(House house) {
        HouseDTO houseDTO = new HouseDTO();
        houseDTO.setId(house.getId().toString());
        houseDTO.setName(house.getName());
        houseDTO.setInviteCode(house.getInviteCode());
        houseDTO.setAdminId(house.getAdminId().toString());
        houseDTO.setBalance(house.getBalance());
        houseDTO.setMembers(house.getMembers().stream()
                .filter(member -> member.getStatus().equals(MemberStatus.ACTIVE))
                .map(houseMemberMapper::toDTO)
                .toList());
        return houseDTO;
    }

    private HouseDTO buildCompleteHouseResponse(House house) {
        HouseDTO houseDTO = new HouseDTO();
        houseDTO.setId(house.getId().toString());
        houseDTO.setName(house.getName());
        houseDTO.setInviteCode(house.getInviteCode());
        houseDTO.setAdminId(house.getAdminId().toString());
        houseDTO.setBalance(house.getBalance());
        houseDTO.setMembers(house.getMembers().stream()
                .map(houseMemberMapper::toDTO)
                .toList());
        return houseDTO;
    }

    private House buildNewHouseEntity(String houseName) {
        House newHouse = new House();
        newHouse.setName(houseName);
        newHouse.setInviteCode(getNewInviteCode());
        newHouse.setBalance(new BigDecimal(0));
        newHouse.setAdminId(UUID.fromString(jwtUtils.getUserId()));
        newHouse.addMember(HouseMember.builder()
                .userId(UUID.fromString(jwtUtils.getUserId()))
                .role(MemberRole.ADMIN)
                .status(MemberStatus.ACTIVE)
                .joinedAt(java.time.LocalDateTime.now())
                .build());
        return newHouse;
    }

    private String getNewInviteCode() {
        String inviteCode = generateInviteCode();
        while (houseRepository.existsByInviteCode(inviteCode)) {
            inviteCode = generateInviteCode();
        }
        return inviteCode;
    }

    private String generateInviteCode() {
        String inviteCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int inviteLength = 5;
        SecureRandom secureRandom = new SecureRandom();

        StringBuilder code = new StringBuilder(inviteLength);
        for (int i = 0; i < inviteLength; i++) {
            int index = secureRandom.nextInt(inviteCharacters.length());
            code.append(inviteCharacters.charAt(index));
        }
        return code.toString();
    }
}
