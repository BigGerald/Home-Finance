package unileste.homefinance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unileste.homefinance.DTOs.deafult.DefaultErrorResponse;
import unileste.homefinance.DTOs.user.EssentialUserWithBiometricInfoDTO;
import unileste.homefinance.DTOs.user.UpdateUserBiometricResponse;
import unileste.homefinance.service.UserService;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuário", description = "Rotas relacionadas a dados do usuário")
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Dados do usuário logado",
            description = "Retorna os dados do usuário autenticado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)
                    ))
    })
    @GetMapping("/me")
    public ResponseEntity<EssentialUserWithBiometricInfoDTO> getUser() {
        log.info("getUser() - [START]");
        EssentialUserWithBiometricInfoDTO userData = userService.getUserData();
        log.info("getUser() - [END]");
        return ResponseEntity.ok(userData);
    }

    @Operation(summary = "Ativar/Desativar biometria", description = "Ativa ou desativa a autenticação por biometria para o usuário autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status de biometria atualizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)
                    ))
    })
    @PatchMapping("/biometric/status")
    public ResponseEntity<UpdateUserBiometricResponse> updateUserBiometricStatus(@PathParam("isEnabled") boolean isEnabled) {
        log.info("updateUserBiometricStatus() - [START]");
        UpdateUserBiometricResponse response = userService.updateUserBiometricStatus(isEnabled);
        log.info("updateUserBiometricStatus() - [END]");
        return ResponseEntity.ok(response);
    }
}
