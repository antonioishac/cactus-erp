package br.com.cactusdigital.erp.financeiro.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.financeiro.dto.MovimentoCaixaBancoRequest;
import br.com.cactusdigital.erp.financeiro.dto.MovimentoCaixaBancoResponse;
import br.com.cactusdigital.erp.financeiro.service.MovimentoCaixaBancoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Movimento de Caixa e Banco", description = "Ledger append-only — toda mudança de saldo de ContaBancaria nasce aqui, nunca por UPDATE direto")
@RestController
@RequestMapping("/api/v1/movimentos-caixa-banco")
@RequiredArgsConstructor
public class MovimentoCaixaBancoController {

    private final MovimentoCaixaBancoService service;

    @Operation(summary = "Listar movimentos de caixa/banco", description = "Filtra por contaBancariaId (opcional), mais recente primeiro")
    @ApiResponse(responseCode = "200", description = "Página de movimentos")
    @GetMapping
    @PreAuthorize("hasAuthority('CAIXA_CONSULTAR')")
    public PageResponse<MovimentoCaixaBancoResponse> listar(@RequestParam(required = false) Long contaBancariaId, Pageable pageable) {
        return PageResponse.of(service.listar(contaBancariaId, pageable));
    }

    @Operation(summary = "Lançamento manual de ajuste", description = "Tarifa bancária, IOF, correção de saldo — só ENTRADA/SAIDA (transferência tem endpoint próprio em Contas Bancárias/Tesouraria)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Movimento registrado"),
            @ApiResponse(responseCode = "404", description = "Conta bancária não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Conta bancária inativa", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Valor inválido ou tipo de movimento não aceito pra lançamento manual", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CAIXA_MOVIMENTAR')")
    public ResponseEntity<MovimentoCaixaBancoResponse> registrarManual(@Valid @RequestBody MovimentoCaixaBancoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarManual(request));
    }
}
