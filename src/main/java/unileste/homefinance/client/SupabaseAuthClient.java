package unileste.homefinance.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unileste.homefinance.DTOs.auth.Login.LoginDTO;
import unileste.homefinance.DTOs.auth.PasswordRecover.RecoverPasswordRequest;
import unileste.homefinance.DTOs.auth.Supabase.User.SupabaseAuthResponse;
import unileste.homefinance.DTOs.auth.Supabase.User.SupabaseRegisterUserDTO;
import unileste.homefinance.DTOs.auth.Supabase.User.SupabaseUser;
import unileste.homefinance.client.config.SupabaseFeignConfig;

@FeignClient(
        name = "supabaseAuthClient",
        url = "${supabase.url}",
        configuration = SupabaseFeignConfig.class
)
public interface SupabaseAuthClient {

    @PostMapping("/auth/v1/signup")
    ResponseEntity<SupabaseUser> signUp(@RequestBody SupabaseRegisterUserDTO request);

    @PostMapping("/auth/v1/token?grant_type=password")
    ResponseEntity<SupabaseAuthResponse> signIn(@RequestBody LoginDTO request);

    @GetMapping("/auth/v1/user")
    ResponseEntity<SupabaseUser> getUser();

    @PutMapping("/auth/v1/user")
    ResponseEntity<SupabaseUser> updateUser(@RequestBody Object body);

    @PostMapping("/auth/v1/recover")
    ResponseEntity<Void> requestRecoverUserPassword(@RequestBody RecoverPasswordRequest requestBody);
}