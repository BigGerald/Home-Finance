package unileste.homefinance.DTOs.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import unileste.homefinance.DTOs.auth.Supabase.User.SupabaseUser;

@Data
@Builder
@AllArgsConstructor
public class EssentialUserWithBiometricInfoDTO {
    private String id;
    private String role;
    private String email;
    private boolean emailConfirmed;
    private String firstName;
    private String lastName;
    private String displayName;
    private boolean biometricEnabled;

    public EssentialUserWithBiometricInfoDTO(SupabaseUser supabaseUser) {
        this.id = supabaseUser.getId();
        this.role = supabaseUser.getUser_metadata().getRole();
        this.email = supabaseUser.getEmail();
        this.emailConfirmed = supabaseUser.getUser_metadata().isEmailVerified();
        this.firstName = supabaseUser.getUser_metadata().getFirstName();
        this.lastName = supabaseUser.getUser_metadata().getLastName();
        this.displayName = supabaseUser.getUser_metadata().getDisplayName();
        this.biometricEnabled = supabaseUser.getUser_metadata().isBiometricEnabled();
    }
}
