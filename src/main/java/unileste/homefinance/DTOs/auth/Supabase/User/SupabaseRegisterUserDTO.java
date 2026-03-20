package unileste.homefinance.DTOs.auth.Supabase.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import proj.integr.construcaocivil.DTOs.auth.Login.RegisterUserDTO;
import proj.integr.construcaocivil.constants.UserTypes;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupabaseRegisterUserDTO {
    private String email;
    private String password;
    private SupabaseRegisterUserData data;

    public SupabaseRegisterUserDTO(RegisterUserDTO register, UserTypes type) {
        this.email = register.getEmail();
        this.password = register.getPassword();
        this.data = SupabaseRegisterUserData.builder()
                .displayName(register.getDisplayName())
                .firstName(register.getFirstName())
                .lastName(register.getLastName())
                .role(type.getValue())
                .build();
    }
}
