package unileste.homefinance.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import unileste.homefinance.DTOs.houseMember.HouseMemberDTO;
import unileste.homefinance.DTOs.user.EssentialUserDTO;
import unileste.homefinance.domain.entity.HouseMember;
import unileste.homefinance.service.UserService;

@Component
@RequiredArgsConstructor
public class HouseMemberMapper {

    private final UserService userService;

    public HouseMemberDTO toDTO(HouseMember member) {
        HouseMemberDTO dto = new HouseMemberDTO();
        dto.setId(member.getUserId().toString());
        dto.setRole(member.getRole());
        dto.setStatus(member.getStatus());

        EssentialUserDTO userData = userService.getUserById(member.getUserId().toString());
        dto.setFirstName(userData.getFirstName());
        dto.setLastName(userData.getLastName());
        dto.setDisplayName(userData.getDisplayName());
        dto.setEmail(userData.getEmail());
        return dto;
    }
}
