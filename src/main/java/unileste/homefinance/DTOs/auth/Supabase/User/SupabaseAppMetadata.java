package unileste.homefinance.DTOs.auth.Supabase.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupabaseAppMetadata {
    private String provider;
    private List<String> providers;
}
