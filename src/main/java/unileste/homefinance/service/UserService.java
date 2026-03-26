package unileste.homefinance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import unileste.homefinance.DTOs.auth.Supabase.User.AllUsersRequestResponse;
import unileste.homefinance.DTOs.auth.Supabase.User.SupabaseRegisterUserData;
import unileste.homefinance.DTOs.auth.Supabase.User.SupabaseUpdateUserDataRequest;
import unileste.homefinance.DTOs.auth.Supabase.User.SupabaseUser;
import unileste.homefinance.DTOs.user.EssentialUserDTO;
import unileste.homefinance.DTOs.user.EssentialUserWithBiometricInfoDTO;
import unileste.homefinance.DTOs.user.UpdateUserBiometricResponse;
import unileste.homefinance.client.SupabaseAdminClient;
import unileste.homefinance.client.SupabaseAuthClient;
import unileste.homefinance.utils.JwtUtils;

import java.util.UUID;

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

    public EssentialUserWithBiometricInfoDTO getUserData() {
        log.info("getUserData() - [START] - userId: {}", jwtUtils.getUserId());
        SupabaseUser supabaseUser = supabaseAuthClient.getUser().getBody();
        EssentialUserWithBiometricInfoDTO essentialUserDTO = new EssentialUserWithBiometricInfoDTO(supabaseUser);
        log.info("getUserData() - [END]");
        return essentialUserDTO;
    }

    public EssentialUserDTO getUserById(String userId) {
        log.info("getUserById() - [START] - userId: {}", userId);
        SupabaseUser supabaseUser = supabaseAdminClient.getUserById(userId).getBody();
        EssentialUserDTO essentialUserDTO = new EssentialUserDTO(supabaseUser);
        log.info("getUserById() - [END]");
        return essentialUserDTO;
    }

    public UpdateUserBiometricResponse updateUserBiometricStatus(boolean isEnabled) {
        UUID requestUserId = UUID.fromString(jwtUtils.getUserId());
        log.info("updateUserBiometricStatus() - [START] - userId: {}, isEnabled: {}", requestUserId, isEnabled);
        SupabaseUpdateUserDataRequest request = SupabaseUpdateUserDataRequest.builder()
                .data(SupabaseRegisterUserData.builder()
                        .biometricEnabled(isEnabled).build())
                .build();
        SupabaseUser newUserData = supabaseAuthClient.updateUserData(request).getBody();
        UpdateUserBiometricResponse response = new UpdateUserBiometricResponse("Biometric status successfully updated",newUserData.getUser_metadata().isBiometricEnabled());
        log.info("updateUserBiometricStatus() - [END]");
        return response;
    }
}
