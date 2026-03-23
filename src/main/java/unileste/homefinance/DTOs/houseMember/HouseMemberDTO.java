package unileste.homefinance.DTOs.houseMember;

import lombok.Data;
import unileste.homefinance.domain.constants.MemberRole;
import unileste.homefinance.domain.constants.MemberStatus;

@Data
public class HouseMemberDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String displayName;
    private String email;
    private MemberRole role;
    private MemberStatus status;
}
