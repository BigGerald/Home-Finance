package unileste.homefinance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import unileste.homefinance.DTOs.deafult.DefaultErrorResponse;
import unileste.homefinance.DTOs.house.CreateHouseRequestBody;
import unileste.homefinance.DTOs.house.HouseDTO;
import unileste.homefinance.service.HouseService;

@RestController
@RequiredArgsConstructor
@Slf4j
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
    @PostMapping("/house/create")
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
    @GetMapping("/house/my-house")
    public ResponseEntity<HouseDTO> getActiveHouse() {
        log.info("getActiveHouse() - [START]");
        HouseDTO houseData = houseService.getActiveHouseOfUser();
        log.info("getActiveHouse() - [END]");
        return ResponseEntity.status(HttpStatus.OK).body(houseData);
    }
}
