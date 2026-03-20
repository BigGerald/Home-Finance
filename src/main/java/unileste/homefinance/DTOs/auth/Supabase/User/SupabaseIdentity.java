package unileste.homefinance.DTOs.auth.Supabase.User;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupabaseIdentity {
    @JsonProperty("identity_id")
    private String identityId;
    private String id;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("identity_data")
    private SupabaseIdentityData identityData;
    private String provider;
    @JsonProperty("last_sign_in_at")
    private String lastSignInAt;
    @JsonProperty("created_at")
    private String createdAt;
    @JsonProperty("updated_at")
    private String updatedAt;
    private String email;
}
