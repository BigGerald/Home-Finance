package unileste.homefinance.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unileste.homefinance.DTOs.house.CreateHouseRequestBody;
import unileste.homefinance.DTOs.house.HouseDTO;
import unileste.homefinance.DTOs.house.LeaveHouseResponse;
import unileste.homefinance.DTOs.house.UpdateHouseBalanceResponse;
import unileste.homefinance.DTOs.house.resume.HouseResumeDTO;
import unileste.homefinance.DTOs.house.resume.PendingExpensesResume;
import unileste.homefinance.DTOs.house.resume.MonthPaidExpensesResume;
import unileste.homefinance.DTOs.house.resume.UserDebitsResume;
import unileste.homefinance.DTOs.house.resume.ExpenseResume;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HouseService Tests")
class HouseServiceTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private HouseRepository houseRepository;

    @Mock
    private HouseMemberMapper houseMemberMapper;

    @Mock
    private HouseMemberRepository houseMemberRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private HouseBalanceTransactionRepository houseBalanceTransactionRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private HouseService houseService;

    private UUID userId;
    private UUID houseId;
    private String inviteCode;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        houseId = UUID.randomUUID();
        inviteCode = "ABC123";
    }

    // ======================= createNewHouse Tests =======================

    @Test
    @DisplayName("Should create a new house successfully")
    void testCreateNewHouse_Success() {
        // Arrange
        when(jwtUtils.getUserId()).thenReturn(userId.toString());
        when(houseMemberRepository.existsByUserIdAndStatus(userId, MemberStatus.ACTIVE)).thenReturn(false);
        when(houseRepository.existsByInviteCode(anyString())).thenReturn(false);

        House savedHouse = new House();
        savedHouse.setId(houseId);
        savedHouse.setName("Test House");
        savedHouse.setInviteCode("ABC123");
        savedHouse.setBalance(BigDecimal.ZERO);
        savedHouse.setAdminId(userId);
        savedHouse.setMembers(new ArrayList<>());

        when(houseRepository.save(any(House.class))).thenReturn(savedHouse);

        CreateHouseRequestBody request = new CreateHouseRequestBody();
        request.setName("Test House");

        // Act
        HouseDTO result = houseService.createNewHouse(request);

        // Assert
        assertNotNull(result);
        assertEquals("Test House", result.getName());
        assertEquals(userId.toString(), result.getAdminId());
        verify(houseRepository, times(1)).save(any(House.class));
        verify(houseMemberRepository, times(1)).existsByUserIdAndStatus(userId, MemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should throw exception when user is already active in another house")
    void testCreateNewHouse_UserAlreadyActive() {
        // Arrange
        when(jwtUtils.getUserId()).thenReturn(userId.toString());
        when(houseMemberRepository.existsByUserIdAndStatus(userId, MemberStatus.ACTIVE)).thenReturn(true);

        CreateHouseRequestBody request = new CreateHouseRequestBody();
        request.setName("Test House");

        // Act & Assert
        assertThrows(HouseException.class, () -> houseService.createNewHouse(request));
        verify(houseRepository, never()).save(any(House.class));
    }

    @Test
    @DisplayName("Should throw exception when house name is invalid")
    void testCreateNewHouse_InvalidHouseName() {
        // Arrange
        CreateHouseRequestBody request = new CreateHouseRequestBody();
        request.setName("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> houseService.createNewHouse(request));
    }

    // ======================= getActiveHouseOfUser Tests =======================

    @Test
    @DisplayName("Should retrieve active house of user successfully")
    void testGetActiveHouseOfUser_Success() {
        // Arrange
        when(jwtUtils.getUserId()).thenReturn(userId.toString());

        HouseMember houseMember = new HouseMember();
        houseMember.setUserId(userId);
        houseMember.setStatus(MemberStatus.ACTIVE);

        House house = new House();
        house.setId(houseId);
        house.setName("Test House");
        house.setInviteCode("ABC123");
        house.setAdminId(userId);
        house.setBalance(BigDecimal.valueOf(100));
        house.setMembers(List.of(houseMember));
        houseMember.setHouse(house);

        when(houseMemberRepository.findByUserIdAndStatus(userId, MemberStatus.ACTIVE))
                .thenReturn(Optional.of(houseMember));
        when(houseRepository.findById(houseId))
                .thenReturn(Optional.of(house));

        // Act
        HouseDTO result = houseService.getActiveHouseOfUser();

        // Assert
        assertNotNull(result);
        assertEquals(houseId.toString(), result.getId());
        verify(houseMemberRepository, times(1)).findByUserIdAndStatus(userId, MemberStatus.ACTIVE);
        verify(houseRepository, times(1)).findById(houseId);
    }

    @Test
    @DisplayName("Should throw exception when user has no active house")
    void testGetActiveHouseOfUser_NoActiveHouse() {
        // Arrange
        when(jwtUtils.getUserId()).thenReturn(userId.toString());
        when(houseMemberRepository.findByUserIdAndStatus(userId, MemberStatus.ACTIVE))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(HouseNotFoundException.class, () -> houseService.getActiveHouseOfUser());
    }

    // ======================= joinHouseWithInviteCode Tests =======================

    @Test
    @DisplayName("Should join house with invite code successfully")
    void testJoinHouseWithInviteCode_Success() {
        // Arrange
        when(jwtUtils.getUserId()).thenReturn(userId.toString());
        when(houseMemberRepository.existsByUserIdAndStatus(userId, MemberStatus.ACTIVE)).thenReturn(false);

        House house = new House();
        house.setId(houseId);
        house.setName("Test House");
        house.setInviteCode(inviteCode);
        house.setAdminId(UUID.randomUUID());
        house.setBalance(BigDecimal.ZERO);
        house.setMembers(new ArrayList<>());

        when(houseRepository.findByInviteCode(inviteCode)).thenReturn(Optional.of(house));
        when(houseRepository.save(any(House.class))).thenReturn(house);

        // Act
        HouseDTO result = houseService.joinHouseWithInviteCode(inviteCode);

        // Assert
        assertNotNull(result);
        assertEquals(inviteCode, result.getInviteCode());
        verify(houseRepository, times(1)).findByInviteCode(inviteCode);
        verify(houseRepository, times(1)).save(any(House.class));
    }

    @Test
    @DisplayName("Should throw exception when user is already active in another house")
    void testJoinHouseWithInviteCode_UserAlreadyActive() {
        // Arrange
        when(jwtUtils.getUserId()).thenReturn(userId.toString());
        when(houseMemberRepository.existsByUserIdAndStatus(userId, MemberStatus.ACTIVE)).thenReturn(true);

        // Act & Assert
        assertThrows(HouseException.class, () -> houseService.joinHouseWithInviteCode(inviteCode));
        verify(houseRepository, never()).findByInviteCode(inviteCode);
    }

    @Test
    @DisplayName("Should throw exception when invite code does not exist")
    void testJoinHouseWithInviteCode_InvalidInviteCode() {
        // Arrange
        when(jwtUtils.getUserId()).thenReturn(userId.toString());
        when(houseMemberRepository.existsByUserIdAndStatus(userId, MemberStatus.ACTIVE)).thenReturn(false);
        when(houseRepository.findByInviteCode(inviteCode)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(HouseNotFoundException.class, () -> houseService.joinHouseWithInviteCode(inviteCode));
    }

    @Test
    @DisplayName("Should throw exception when user has no active house")
    void testLeaveActualHouse_NoActiveHouse() {
        // Arrange
        when(jwtUtils.getUserId()).thenReturn(userId.toString());
        when(houseMemberRepository.findByUserIdAndStatus(userId, MemberStatus.ACTIVE))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(HouseNotFoundException.class, () -> houseService.leaveActualHouse());
    }

    @Test
    @DisplayName("Should throw exception when admin tries to remove themselves")
    void testRemoveMemberFromHouseByHouseAdmin_CannotRemoveSelf() {
        // Arrange
        when(jwtUtils.getUserId()).thenReturn(userId.toString());

        // Act & Assert
        assertThrows(HouseException.class, () -> houseService.removeMemberFromHouseByHouseAdmin(userId));
    }

    @Test
    @DisplayName("Should throw exception when user is not an admin")
    void testRemoveMemberFromHouseByHouseAdmin_NotAnAdmin() {
        // Arrange
        UUID memberId = UUID.randomUUID();
        UUID memberToRemoveId = UUID.randomUUID();

        when(jwtUtils.getUserId()).thenReturn(memberId.toString());
        when(houseMemberRepository.findByUserIdAndRoleAndStatus(memberId, MemberRole.ADMIN, MemberStatus.ACTIVE))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(HouseException.class, () -> houseService.removeMemberFromHouseByHouseAdmin(memberToRemoveId));
    }

    @Test
    @DisplayName("Should throw exception when user has no active house in getHouseResume")
    void testGetHouseResume_NoActiveHouse() {
        // Arrange
        when(jwtUtils.getUserId()).thenReturn(userId.toString());
        when(houseMemberRepository.findByUserIdAndStatus(userId, MemberStatus.ACTIVE))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(HouseNotFoundException.class, () -> houseService.getHouseResume());
    }

    // ======================= manualUpdateHouseBalance Tests =======================

    @Test
    @DisplayName("Should add balance to house successfully")
    void testManualUpdateHouseBalance_AddBalance_Success() {
        // Arrange
        when(jwtUtils.getUserId()).thenReturn(userId.toString());

        HouseMember houseMember = HouseMember.builder()
                .userId(userId)
                .status(MemberStatus.ACTIVE)
                .build();

        House house = new House();
        house.setId(houseId);
        house.setBalance(BigDecimal.valueOf(100));
        houseMember.setHouse(house);

        when(houseMemberRepository.findByUserIdAndStatus(userId, MemberStatus.ACTIVE))
                .thenReturn(Optional.of(houseMember));
        when(houseBalanceTransactionRepository.save(any(HouseBalanceTransaction.class)))
                .thenReturn(new HouseBalanceTransaction());
        when(houseRepository.save(any(House.class))).thenReturn(house);

        // Act
        UpdateHouseBalanceResponse result = houseService.manualUpdateHouseBalance(BigDecimal.valueOf(50), null);

        // Assert
        assertNotNull(result);
        assertEquals("House balance updated successfully", result.getMessage());
        assertEquals(BigDecimal.valueOf(150), result.getNewBalance());
        verify(houseBalanceTransactionRepository, times(1)).save(any(HouseBalanceTransaction.class));
        verify(houseRepository, times(1)).save(any(House.class));
    }

    @Test
    @DisplayName("Should subtract balance from house successfully")
    void testManualUpdateHouseBalance_SubtractBalance_Success() {
        // Arrange
        when(jwtUtils.getUserId()).thenReturn(userId.toString());

        HouseMember houseMember = HouseMember.builder()
                .userId(userId)
                .status(MemberStatus.ACTIVE)
                .build();

        House house = new House();
        house.setId(houseId);
        house.setBalance(BigDecimal.valueOf(100));
        houseMember.setHouse(house);

        when(houseMemberRepository.findByUserIdAndStatus(userId, MemberStatus.ACTIVE))
                .thenReturn(Optional.of(houseMember));
        when(houseBalanceTransactionRepository.save(any(HouseBalanceTransaction.class)))
                .thenReturn(new HouseBalanceTransaction());
        when(houseRepository.save(any(House.class))).thenReturn(house);

        // Act
        UpdateHouseBalanceResponse result = houseService.manualUpdateHouseBalance(null, BigDecimal.valueOf(30));

        // Assert
        assertNotNull(result);
        assertEquals("House balance updated successfully", result.getMessage());
        assertEquals(BigDecimal.valueOf(70), result.getNewBalance());
        verify(houseBalanceTransactionRepository, times(1)).save(any(HouseBalanceTransaction.class));
        verify(houseRepository, times(1)).save(any(House.class));
    }

    @Test
    @DisplayName("Should throw exception when both add and subtract values are provided")
    void testManualUpdateHouseBalance_BothValuesProvided() {
        // Arrange
        when(jwtUtils.getUserId()).thenReturn(userId.toString());

        // Act & Assert
        assertThrows(HouseException.class, () ->
            houseService.manualUpdateHouseBalance(BigDecimal.valueOf(50), BigDecimal.valueOf(30))
        );
    }

    @Test
    @DisplayName("Should throw exception when no value is provided")
    void testManualUpdateHouseBalance_NoValueProvided() {
        // Arrange
        when(jwtUtils.getUserId()).thenReturn(userId.toString());

        // Act & Assert
        assertThrows(HouseException.class, () ->
            houseService.manualUpdateHouseBalance(null, null)
        );
    }

    @Test
    @DisplayName("Should throw exception when trying to subtract more than balance")
    void testManualUpdateHouseBalance_InsufficientBalance() {
        // Arrange
        when(jwtUtils.getUserId()).thenReturn(userId.toString());

        HouseMember houseMember = HouseMember.builder()
                .userId(userId)
                .status(MemberStatus.ACTIVE)
                .build();

        House house = new House();
        house.setId(houseId);
        house.setBalance(BigDecimal.valueOf(50));
        houseMember.setHouse(house);

        when(houseMemberRepository.findByUserIdAndStatus(userId, MemberStatus.ACTIVE))
                .thenReturn(Optional.of(houseMember));

        // Act & Assert
        assertThrows(HouseException.class, () ->
            houseService.manualUpdateHouseBalance(null, BigDecimal.valueOf(100))
        );
    }

    @Test
    @DisplayName("Should throw exception when add value is negative or zero")
    void testManualUpdateHouseBalance_NegativeAddValue() {
        // Arrange
        when(jwtUtils.getUserId()).thenReturn(userId.toString());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            houseService.manualUpdateHouseBalance(BigDecimal.valueOf(-50), null)
        );
    }

    @Test
    @DisplayName("Should throw exception when subtract value is negative or zero")
    void testManualUpdateHouseBalance_NegativeSubtractValue() {
        // Arrange
        when(jwtUtils.getUserId()).thenReturn(userId.toString());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            houseService.manualUpdateHouseBalance(null, BigDecimal.valueOf(-50))
        );
    }

    @Test
    @DisplayName("Should throw exception when user has no active house in manualUpdateHouseBalance")
    void testManualUpdateHouseBalance_NoActiveHouse() {
        // Arrange
        when(jwtUtils.getUserId()).thenReturn(userId.toString());
        when(houseMemberRepository.findByUserIdAndStatus(userId, MemberStatus.ACTIVE))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(HouseNotFoundException.class, () ->
            houseService.manualUpdateHouseBalance(BigDecimal.valueOf(50), null)
        );
    }
}

