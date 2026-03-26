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
import org.springframework.web.bind.annotation.*;
import unileste.homefinance.DTOs.auth.Login.RegisterUserDTO;
import unileste.homefinance.DTOs.auth.Supabase.User.AllUsersRequestResponse;
import unileste.homefinance.DTOs.auth.Supabase.User.SupabaseUser;
import unileste.homefinance.DTOs.deafult.DefaultErrorResponse;
import unileste.homefinance.DTOs.user.EssentialUserDTO;
import unileste.homefinance.service.AuthService;
import unileste.homefinance.service.UserService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Endpoints relacionados à administração do sistema, como gerenciamento de usuários, configurações globais e monitoramento. Não se refere a adiministradores das casas e sim da API/Sistema")
public class AdminController {

    private final UserService userService;
    private final AuthService authService;

    @Operation(
            summary = "Registrar novo administrador",
            description = "Cria uma nova conta de administrador no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Administrador criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)
                    ))
    })
    @PostMapping("/auth/register/new-admin")
    public ResponseEntity<SupabaseUser> adminSignUp(@RequestBody RegisterUserDTO request) {
        log.info("adminSignUp() - [START]");
        SupabaseUser newUser = authService.adminSignUp(request);
        log.info("adminSignUp() - [END]");
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @Operation(summary = "Obter usuário por ID", description = "Retorna os dados essenciais de um usuário específico com base no ID fornecido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "400", description = "ID de usuário inválido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)
                    ))
    })
    @GetMapping("/user")
    public ResponseEntity<EssentialUserDTO> getUserById(@PathVariable String userId) {
        log.info("getUserById() - [START] - userId = {}", userId);
        EssentialUserDTO user = userService.getUserById(userId);
        log.info("getUserById() - [END] - userId = {}", userId);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Obter todos os usuários", description = "Retorna uma lista de todos os usuários registrados no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "403", description = "Acesso proibido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)))
    })
    @GetMapping("/user/all")
    public ResponseEntity<AllUsersRequestResponse> getAllUsers() {
        log.info("getAllUsers() - [START]");
        AllUsersRequestResponse response = userService.getAllUsers();
        log.info("getAllUsers() - [END]");
        return ResponseEntity.ok(response);
    }

}
