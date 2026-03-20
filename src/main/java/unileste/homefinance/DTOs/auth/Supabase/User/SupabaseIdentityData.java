package unileste.homefinance.DTOs.auth.Supabase.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupabaseIdentityData {
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("display_name")
    private String displayName;
    private String email;
    @JsonProperty("email_verified")
    private boolean emailVerified;
    @JsonProperty("phone_verified")
    private boolean phoneVerified;
    private String sub;
    private String role;
}
