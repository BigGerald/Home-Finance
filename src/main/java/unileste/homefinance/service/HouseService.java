package unileste.homefinance.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import unileste.homefinance.DTOs.house.CreateHouseRequestBody;
import unileste.homefinance.DTOs.house.HouseDTO;
import unileste.homefinance.DTOs.house.LeaveHouseResponse;
import unileste.homefinance.domain.constants.MemberRole;
import unileste.homefinance.domain.constants.MemberStatus;
import unileste.homefinance.domain.entity.House;
import unileste.homefinance.domain.entity.HouseMember;
import unileste.homefinance.exceptions.HouseException;
import unileste.homefinance.exceptions.HouseNotFoundException;
import unileste.homefinance.mapper.HouseMemberMapper;
import unileste.homefinance.repository.HouseMemberRepository;
import unileste.homefinance.repository.HouseRepository;
import unileste.homefinance.utils.JwtUtils;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HouseService {
    private final JwtUtils jwtUtils;
    private final HouseRepository houseRepository;
    private final HouseMemberMapper houseMemberMapper;
    private final HouseMemberRepository houseMemberRepository;

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
        return buildHouseResponse(newHouseSaved);
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
        return buildHouseResponse(houseEntityData);
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
        return buildHouseResponse(houseEntityData);
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
    public void removeAdminFromHouse(HouseMember memberData) {
        log.info("removeAdminFromHouse() - [START] - userId: {}", memberData.getUserId());
        log.info("removeAdminFromHouse() - validating if user is the administrator");
        if (!houseMemberRepository.existsByUserIdAndHouseIdAndRoleAndStatus(memberData.getUserId(), memberData.getHouse().getId(), MemberRole.ADMIN, MemberStatus.ACTIVE)) {
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

    @Transactional
    public void removeMemberFromHouse(HouseMember memberData) {
        log.info("removeMemberFromHouse() - [START] - userId: {}", memberData.getUserId());
        log.info("removeMemberFromHouse() - validating if user is a member");
        if (!houseMemberRepository.existsByUserIdAndHouseIdAndRoleAndStatus(memberData.getUserId(), memberData.getHouse().getId(), MemberRole.ADMIN, MemberStatus.ACTIVE)) {
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

    private HouseDTO buildHouseResponse(House house) {
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
