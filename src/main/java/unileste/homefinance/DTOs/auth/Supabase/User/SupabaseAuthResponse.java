package unileste.homefinance.DTOs.auth.Supabase.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupabaseAuthResponse {
    private String access_token;
    private String token_type;
    private Integer expires_in;
    private String refresh_token;
    private Map<String, Object> user;
}