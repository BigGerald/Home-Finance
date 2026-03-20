package unileste.homefinance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import unileste.homefinance.DTOs.auth.Login.LoginDTO;
import unileste.homefinance.DTOs.auth.Login.RegisterUserDTO;
import unileste.homefinance.DTOs.auth.Supabase.User.SupabaseAuthResponse;
import unileste.homefinance.DTOs.auth.Supabase.User.SupabaseRegisterUserDTO;
import unileste.homefinance.DTOs.auth.Supabase.User.SupabaseUser;
import unileste.homefinance.client.SupabaseAuthClient;
import unileste.homefinance.constants.UserTypes;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    public SupabaseUser adminSignUp(RegisterUserDTO registerUserDTO) {
        log.info("adminSignUp() - Received admin sign-up request for email: {}", registerUserDTO.getEmail());
        SupabaseUser newUSer = supabaseAuthClient.signUp( new SupabaseRegisterUserDTO(registerUserDTO, UserTypes.ADMIN) ).getBody();
        log.info("adminSignUp() - User created with ID: {}", newUSer.getId());
        return newUSer;
    }

    private final SupabaseAuthClient  supabaseAuthClient;
    public SupabaseUser commonUserSignUp(RegisterUserDTO registerUserDTO) {
        log.info("commonUserSignUp() - Received user sign-up request for email: {}", registerUserDTO.getEmail());
        SupabaseUser newUser = supabaseAuthClient.signUp( new SupabaseRegisterUserDTO( registerUserDTO, UserTypes.USER)).getBody();
        log.info("commonUserSignUp() - User created with ID: {}", newUser.getId());
        return newUser;
    }

    public SupabaseAuthResponse signIn(String email, String password) {
        log.info("signIn() - Attempting to sign in user with email: {}", email);
        SupabaseAuthResponse response =  supabaseAuthClient.signIn(new LoginDTO(email, password)).getBody();
        log.info("signIn() - User signed in successfully, received access token");
        return response;
    }
}
