package unileste.homefinance.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import unileste.homefinance.DTOs.house.CreateHouseRequestBody;
import unileste.homefinance.DTOs.house.HouseDTO;
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
