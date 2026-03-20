package unileste.homefinance.DTOs.auth.Supabase.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupabaseRegisterUserData {
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("display_name")
    private String displayName;
    private String role;
    @JsonProperty("biometric_enabled")
    boolean biometricEnabled;
}
