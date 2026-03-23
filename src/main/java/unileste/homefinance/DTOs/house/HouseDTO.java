package unileste.homefinance.DTOs.house;

import lombok.Data;
import unileste.homefinance.DTOs.houseMember.HouseMemberDTO;

import java.math.BigDecimal;
import java.util.List;

@Data
public class HouseDTO {
    private String id;
    private String name;
    private String inviteCode;
    private String adminId;
    private BigDecimal balance;
    private List<HouseMemberDTO> members;
}
