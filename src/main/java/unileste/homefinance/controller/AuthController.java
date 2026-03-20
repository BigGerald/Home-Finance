package unileste.homefinance.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import unileste.homefinance.DTOs.auth.Login.LoginDTO;
import unileste.homefinance.DTOs.auth.Login.RegisterUserDTO;
import unileste.homefinance.DTOs.auth.Supabase.User.SupabaseAuthResponse;
import unileste.homefinance.DTOs.auth.Supabase.User.SupabaseUser;
import unileste.homefinance.service.AuthService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/admin/auth/register/new-admin")
    public ResponseEntity<SupabaseUser> adminSignUp(@RequestBody RegisterUserDTO request) {
        log.info("adminSignUp() - [START]");
        SupabaseUser newUser = authService.adminSignUp(request);
        log.info("adminSignUp() - [END]");
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PostMapping("/public/auth/register")
    public ResponseEntity<SupabaseUser> commonUserSignUp(@RequestBody RegisterUserDTO request) {
        log.info("commonUserSignUp() - [START]");
        SupabaseUser newUser = authService.commonUserSignUp(request);
        log.info("commonUserSignUp() - [END]");
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PostMapping("/public/auth/login")
    public ResponseEntity<SupabaseAuthResponse> login(@RequestBody LoginDTO request) {
        log.info("login() - [START]");
        SupabaseAuthResponse response = authService.signIn(request.getEmail(), request.getPassword());
        log.info("login() - [END]");
        return ResponseEntity.ok(response);
    }
}
