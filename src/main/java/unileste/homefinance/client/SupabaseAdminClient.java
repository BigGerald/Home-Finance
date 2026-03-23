package unileste.homefinance.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import unileste.homefinance.DTOs.auth.Supabase.User.AllUsersRequestResponse;
import unileste.homefinance.DTOs.auth.Supabase.User.SupabaseUpdateUserDataRequest;
import unileste.homefinance.DTOs.auth.Supabase.User.SupabaseUser;
import unileste.homefinance.client.config.SupabaseAdminFeignConfig;

@FeignClient(name = "supabaseAdminAuth", url = "${supabase.url}", configuration = SupabaseAdminFeignConfig.class)
public interface SupabaseAdminClient {

    @GetMapping("/auth/v1/admin/users")
    ResponseEntity<AllUsersRequestResponse> getAllUSers();

    @GetMapping("/auth/v1/admin/users/{id}")
    ResponseEntity<SupabaseUser> getUserById(@PathVariable("id") String id);

    @PutMapping("/auth/v1/admin/users/{id}")
    ResponseEntity<SupabaseUser> updateUserDataById(@PathVariable("id") String id, SupabaseUpdateUserDataRequest requestBody);
}
