package unileste.homefinance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import unileste.homefinance.DTOs.deafult.DefaultErrorResponse;
import unileste.homefinance.DTOs.user.EssentialUserDTO;
import unileste.homefinance.service.UserService;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuário", description = "Rotas relacionadas a dados do usuário")
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
                    ))
    })
    @GetMapping("/user/me")
    public ResponseEntity<EssentialUserDTO> getUser() {
        log.info("getUser() - [START]");
        EssentialUserDTO userData = userService.getUserData();
        log.info("getUser() - [END]");
        return ResponseEntity.ok(userData);
    }
}
