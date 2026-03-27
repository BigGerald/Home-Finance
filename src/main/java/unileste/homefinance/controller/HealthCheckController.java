package unileste.homefinance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @Operation(summary = "Health Check Endpoint", description = "Rota para verificar se api esta disponivel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "API está saudável e disponível"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor, API pode estar indisponível")
    })
    @GetMapping("/public/health")
    public ResponseEntity<Void> healthCheck() {
        return ResponseEntity.noContent().build();
    }
}
