package unileste.homefinance.DTOs.auth.Supabase.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupabaseOptions {
    private String emailRedirectTo;
}
