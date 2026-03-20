package unileste.homefinance.DTOs.auth.Supabase.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupabaseUser {
    private String id;
    private String aud;
    private String role;
    private String email;
    private String email_confirmed_at;
    private String phone;
    private String confirmation_sent_at;
    private String confirmed_at;
    private String last_sign_in_at;
    private SupabaseAppMetadata app_metadata;
    private SupabaseUserMetadata user_metadata;
    private List<SupabaseIdentity> identities;
    private String created_at;
    private String updated_at;
    private boolean is_anonymous;
}
