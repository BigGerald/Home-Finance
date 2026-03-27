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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unileste.homefinance.DTOs.deafult.DefaultErrorResponse;
import unileste.homefinance.DTOs.report.ExpensesReportResponse;
import unileste.homefinance.service.ReportService;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reports", description = "Endpoints para geração de relatórios financeiros")
public class ReportController {
    private final ReportService reportService;

    @Operation(summary = "Gerar relatório de despesas do mês atual",
            description = "Gera um relatório detalhado das despesas do mês atual, incluindo total por categoria, evolução mensal e resumo geral.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário ou casa não encontrados)",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class)))
    })
    @GetMapping("/expense-report")
    public ResponseEntity<ExpensesReportResponse> getExpenseReport() {
        log.info("getExpenseReport() - [START] - Requesting expense report");
        ExpensesReportResponse response = reportService.getActualMonthReport();
        log.info("getExpenseReport() - [END] - Expense report generated successfully");
        return ResponseEntity.ok(response);
    }
}
