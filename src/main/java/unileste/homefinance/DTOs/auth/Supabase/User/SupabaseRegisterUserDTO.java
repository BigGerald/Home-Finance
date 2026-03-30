package unileste.homefinance.DTOs.auth.Supabase.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unileste.homefinance.DTOs.auth.Login.RegisterUserDTO;
import unileste.homefinance.domain.constants.UserTypes;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class SupabaseRegisterUserDTO {
    private String email;
    private String password;
    private SupabaseRegisterUserData data;
    private SupabaseOptions options;

    public SupabaseRegisterUserDTO(RegisterUserDTO register, UserTypes type, boolean isBiometricEnabled, SupabaseOptions options) {
        this.email = register.getEmail();
        this.password = register.getPassword();
        this.data = SupabaseRegisterUserData.builder()
                .displayName(register.getDisplayName())
                .firstName(register.getFirstName())
                .lastName(register.getLastName())
                .role(type.getValue())
                .biometricEnabled(isBiometricEnabled)
                .build();
        this.options = options;
    }
}
