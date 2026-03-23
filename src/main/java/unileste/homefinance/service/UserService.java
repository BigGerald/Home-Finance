package unileste.homefinance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import unileste.homefinance.DTOs.auth.Supabase.User.AllUsersRequestResponse;
import unileste.homefinance.DTOs.auth.Supabase.User.SupabaseUser;
import unileste.homefinance.DTOs.user.EssentialUserDTO;
import unileste.homefinance.client.SupabaseAdminClient;
import unileste.homefinance.client.SupabaseAuthClient;
import unileste.homefinance.utils.JwtUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final SupabaseAuthClient supabaseAuthClient;
    private final SupabaseAdminClient supabaseAdminClient;
    private final JwtUtils jwtUtils;


    public AllUsersRequestResponse getAllUsers() {
        log.info("getAllUsers() - [START]");
        AllUsersRequestResponse response = supabaseAdminClient.getAllUSers().getBody();
        log.info("getAllUsers() - [END] - total users: {}", response != null ? response.getUsers().size() : 0);
        return response;
    }
    public EssentialUserDTO getUserData(){
        log.info("getUserData() - [START] - userId: {}", jwtUtils.getUserId());
        SupabaseUser supabaseUser = supabaseAuthClient.getUser().getBody();
        EssentialUserDTO essentialUserDTO = new EssentialUserDTO(supabaseUser);
        log.info("getUserData() - [END]");
        return essentialUserDTO;
    }

    public EssentialUserDTO getUserById(String userId){
        log.info("getUserById() - [START] - userId: {}", userId);
        SupabaseUser supabaseUser = supabaseAdminClient.getUserById(userId).getBody();
        EssentialUserDTO essentialUserDTO = new EssentialUserDTO(supabaseUser);
        log.info("getUserById() - [END]");
        return essentialUserDTO;
    }
}
