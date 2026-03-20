package unileste.homefinance.DTOs.auth.Supabase.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import unileste.homefinance.DTOs.user.UpdateUserDataRequest;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SupabaseUpdateUserDataRequest {
    private String email;
    private SupabaseRegisterUserData data;

    public SupabaseUpdateUserDataRequest(UpdateUserDataRequest updateUserDataRequest) {
        this.email = updateUserDataRequest.getEmail();
        this.data = SupabaseRegisterUserData.builder()
                .lastName(updateUserDataRequest.getLastName())
                .firstName(updateUserDataRequest.getFirstName())
                .displayName(updateUserDataRequest.getDisplayName())
                .build();
    }
}
