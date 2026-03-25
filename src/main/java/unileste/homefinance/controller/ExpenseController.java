package unileste.homefinance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import unileste.homefinance.DTOs.expense.CreateExpenseRequestBody;
import unileste.homefinance.DTOs.expense.ExpenseDTO;
import unileste.homefinance.DTOs.expense.UpdateExpenseStatusRequest;
import unileste.homefinance.DTOs.expenseSplit.ExpenseSplitDTO;
import unileste.homefinance.DTOs.expenseSplit.UpdateExpenseSplitStausRequest;
import unileste.homefinance.domain.constants.ExpenseStatus;
import unileste.homefinance.domain.entity.Category;
import unileste.homefinance.service.CategoryService;
import unileste.homefinance.service.ExpenseService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Expenses", description = "Endpoints relacionados a despesas")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final CategoryService categoryService;

    @Operation(summary = "Registrar nova despesa",
            description = "Registra uma nova despesa para a casa do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Despesa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida, como dados de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado, usuário não autenticado",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class)))
    })
    @PostMapping()
    public ResponseEntity<ExpenseDTO> createExpense(@RequestBody CreateExpenseRequestBody requestBody) {
        log.info("createExpense() - [START]");
        ExpenseDTO response = expenseService.createExpense(requestBody);
        log.info("createExpense() - [END]");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Obter despesas da casa",
            description = "Retorna uma lista de despesas da casa do usuário autenticado, com opções de filtro por status, mês, ano e responsável.",
    parameters = {
            @Parameter(name = "status", description = "Status da despesa para filtrar (PENDING, PAID)"),
            @Parameter(name = "month", description = "Mês para filtrar despesas (1-12)"),
            @Parameter(name = "year", description = "Ano para filtrar despesas ( 2024)"),
            @Parameter(name = "responsibleId", description = "ID do responsável para filtrar despesas")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Despesas obtidas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida, como combinação inválida de filtros",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado, usuário não autenticado",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class)))
    })
    @GetMapping()
    public ResponseEntity<List<ExpenseDTO>> getExpenses(@PathParam("status") String status, @PathParam("month") Integer month, @PathParam("year") Integer year, @PathParam("responsibleId") String responsibleId) {
        log.info("getExpenses() - [START]");
        List<ExpenseDTO> response = expenseService.getHouseExpenses(
                status != null && !status.isEmpty() ? ExpenseStatus.valueOf(status) : null,
                month,
                year,
                responsibleId
        );
        log.info("getExpenses() - [END]");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obter despesa por ID",
            description = "Retorna os detalhes de uma despesa específica da casa do usuário autenticado, identificada pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Despesa obtida com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida, como ID em formato inválido",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado, usuário não autenticado",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Despesa não encontrada",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDTO> getExpenseById(@PathVariable(name = "id") UUID id) {
        log.info("getExpenseById() - [START]");
        ExpenseDTO response = expenseService.getExpenseById(id);
        log.info("getExpenseById() - [END]");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obter categorias de despesas",
            description = "Retorna uma lista de categorias de despesas disponíveis para a casa do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categorias obtidas com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado, usuário não autenticado",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class)))
    })
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getCategories() {
        log.info("getCategories() - [START]");
        List<Category> response = categoryService.findAll();
        log.info("getCategories() - [END]");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar status de despesa",
            description = "Atualiza o status de uma despesa específica da casa do usuário autenticado, identificada pelo ID.",
            parameters = {@Parameter(name = "id", description = "ID da despesa a ser atualizada", required = true)})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status da despesa atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida, como ID em formato inválido ou status inválido",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado, usuário não autenticado",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Despesa não encontrada",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class)))
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<ExpenseDTO> updateExpenseStatus(@PathVariable UUID id, @RequestBody UpdateExpenseStatusRequest updateExpenseStatusRequest) {
        log.info("updateExpenseStatus() - [START]");
        ExpenseDTO response = expenseService.updateExpenseStatus(id, updateExpenseStatusRequest);
        log.info("updateExpenseStatus() - [END]");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar status de divisão de despesa",
            description = "Atualiza o status de uma divisão específica de despesa da casa do usuário autenticado, identificada pelo ID.",
            parameters = {@Parameter(name = "id", description = "ID da divisão de despesa a ser atualizada", required = true)})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status da divisão de despesa atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida, como ID em formato inválido ou status inválido",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado, usuário não autenticado",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Divisão de despesa não encontrada",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(schema = @Schema(implementation = DefaultErrorResponse.class)))
    })
    @PatchMapping("/split/{id}/status")
    public ResponseEntity<ExpenseSplitDTO> updateExpenseSplitStatus(@PathVariable UUID id, @RequestBody UpdateExpenseSplitStausRequest updateExpenseSplitStausRequest) {
        log.info("updateExpenseSplitStatus() - [START]");
        ExpenseSplitDTO response = expenseService.updateExpenseSplitStatus(id, updateExpenseSplitStausRequest);
        log.info("updateExpenseSplitStatus() - [END]");
        return ResponseEntity.ok(response);
    }

}
