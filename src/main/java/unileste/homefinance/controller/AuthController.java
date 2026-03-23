package unileste.homefinance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import unileste.homefinance.DTOs.deafult.DefaultErrorResponse;
import unileste.homefinance.service.AuthService;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticação", description = "Rotas de autenticação e registro de usuários")
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Registrar novo usuário",
            description = "Cria uma nova conta de usuário comum no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)
                    ))
    })
    @PostMapping("/public/auth/register")
    public ResponseEntity<SupabaseUser> commonUserSignUp(@RequestBody RegisterUserDTO request) {
        log.info("commonUserSignUp() - [START]");
        SupabaseUser newUser = authService.commonUserSignUp(request);
        log.info("commonUserSignUp() - [END]");
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @Operation(
            summary = "Login de usuário",
            description = "Autentica um usuário e retorna um token de acesso"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido"),
            @ApiResponse(responseCode = "400", description = "Credenciais inválidas",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)
                    ))
    })
    @PostMapping("/public/auth/login")
    public ResponseEntity<SupabaseAuthResponse> login(@RequestBody LoginDTO request) {
        log.info("login() - [START]");
        SupabaseAuthResponse response = authService.signIn(request.getEmail(), request.getPassword());
        log.info("login() - [END]");
        return ResponseEntity.ok(response);
    }
}
