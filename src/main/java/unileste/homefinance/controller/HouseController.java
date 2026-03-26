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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unileste.homefinance.DTOs.deafult.DefaultErrorResponse;
import unileste.homefinance.DTOs.house.CreateHouseRequestBody;
import unileste.homefinance.DTOs.house.HouseDTO;
import unileste.homefinance.DTOs.house.LeaveHouseResponse;
import unileste.homefinance.DTOs.house.resume.HouseResumeDTO;
import unileste.homefinance.service.HouseService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/house")
@Tag(name = "House", description = "Endpoints relacionados a controle das residencias")
public class HouseController {
    private final HouseService houseService;

    @Operation(
            summary = "Criar nova residência",
            description = "Endpoint para criar uma nova residência. O usuário que fizer a requisição se tornará o administrador da residência criada."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Residência criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida, por exemplo, nome da residência ausente ou vazio",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "401", description = "Não autorizado, token de autenticação ausente ou inválido",
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
    @PostMapping("/create")
    public ResponseEntity<HouseDTO> createNewHouse(@RequestBody CreateHouseRequestBody createHouseRequestBody) {
        log.info("createNewHouse() - [START]");
        HouseDTO newHouseDTO = houseService.createNewHouse(createHouseRequestBody);
        log.info("createNewHouse() - [END]");
        return ResponseEntity.status(HttpStatus.CREATED).body(newHouseDTO);
    }

    @Operation(summary = "Obter residência ativa do usuário", description = "Endpoint para obter os dados da residência ativa do usuário autenticado. Retorna os detalhes da residência, incluindo nome, código de convite e saldo atual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Residência ativa obtida com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado, token de autenticação ausente ou inválido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Residência ativa não encontrada para o usuário",
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
    @GetMapping("/my-house")
    public ResponseEntity<HouseDTO> getActiveHouse() {
        log.info("getActiveHouse() - [START]");
        HouseDTO houseData = houseService.getActiveHouseOfUser();
        log.info("getActiveHouse() - [END]");
        return ResponseEntity.status(HttpStatus.OK).body(houseData);
    }

    @Operation(summary = "Entrar em uma residência usando código de convite", description = "Endpoint para o usuário entrar em uma residência existente usando um código de convite válido. O usuário se tornará um membro ativo da residência associada ao código de convite fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário entrou na residência com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida, por exemplo, código de convite ausente ou vazio",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "401", description = "Não autorizado, token de autenticação ausente ou inválido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Nenhuma residência encontrada para o código de convite fornecido",
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
    @PostMapping("/join")
    public ResponseEntity<HouseDTO> joinHouse(@PathParam("inviteCode") String inviteCode) {
        log.info("joinHouse() - [START]");
        HouseDTO houseData = houseService.joinHouseWithInviteCode(inviteCode);
        log.info("joinHouse() - [END]");
        return ResponseEntity.ok(houseData);
    }

    @Operation(summary = "Deixar residência atual",
            description = "Endpoint para o usuário deixar a residência ativa atual. O usuário será removido como membro da residência e não terá mais acesso aos dados e funcionalidades associadas a essa residência. Caso o Usuario seja administrador, o membro mais antigo se torna o administrador, caso não existam outros membros mais na casa, ela sera excluida permanentemente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário deixou a residência com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado, token de autenticação ausente ou inválido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Residência ativa não encontrada para o usuário",
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
    @DeleteMapping("/leave")
    public ResponseEntity<LeaveHouseResponse> leaveHouse() {
        log.info("leaveHouse() - [START]");
        LeaveHouseResponse response = houseService.leaveActualHouse();
        log.info("leaveHouse() - [END]");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remover membro da residência (apenas para administradores)",
            description = "Endpoint para o administrador da residência remover um membro específico da residência ativa. O administrador deve fornecer o ID do usuário a ser removido. O membro removido perderá acesso aos dados e funcionalidades associadas à residência. Apenas administradores têm permissão para acessar este endpoint.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membro removido da residência com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado, token de autenticação ausente ou inválido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "403", description = "Proibido, o usuário autenticado não é o administrador da residência ativa",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Requisição inválida, por exemplo, ID do usuário ausente ou inválido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Residência ativa não encontrada para o usuário ou usuário a ser removido não encontrado na residência",
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
    @DeleteMapping("/remove-member")
    public ResponseEntity<LeaveHouseResponse> removeMemberFromHouse(@PathParam("userId") String userId) {
        log.info("removeMemberFromHouse() - [START]");
        LeaveHouseResponse response = houseService.removeMemberFromHouseByHouseAdmin(UUID.fromString(userId));
        log.info("removeMemberFromHouse() - [END]");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obter resumo financeiro da residência",
            description = "Endpoint para obter um resumo financeiro da residência ativa do usuário. O resumo inclui informações como saldo total, despesas pendentes do mes, despesas pagas no mes e outras métricas financeiras relevantes para a residência.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resumo financeiro da residência obtido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado, token de autenticação ausente ou inválido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DefaultErrorResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Residência ativa não encontrada para o usuário",
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
    @GetMapping("/resume")
    private ResponseEntity<HouseResumeDTO> getHouseResume() {
        log.info("getHouseResume() - [START]");
        HouseResumeDTO houseResumeDTO = houseService.getHouseResume();
        log.info("getHouseResume() - [END]");
        return ResponseEntity.ok(houseResumeDTO);
    }
}
