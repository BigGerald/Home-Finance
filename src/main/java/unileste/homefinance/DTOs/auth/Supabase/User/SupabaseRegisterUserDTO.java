package unileste.homefinance.DTOs.auth.Supabase.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unileste.homefinance.DTOs.auth.Login.RegisterUserDTO;
import unileste.homefinance.domain.constants.UserTypes;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupabaseRegisterUserDTO {
    private String email;
    private String password;
    private SupabaseRegisterUserData data;

    public SupabaseRegisterUserDTO(RegisterUserDTO register, UserTypes type, boolean isBiometricEnabled) {
        this.email = register.getEmail();
        this.password = register.getPassword();
        this.data = SupabaseRegisterUserData.builder()
                .displayName(register.getDisplayName())
                .firstName(register.getFirstName())
                .lastName(register.getLastName())
                .role(type.getValue())
                .biometricEnabled(isBiometricEnabled)
                .build();
    }
}
