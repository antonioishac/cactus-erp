package br.com.cactusdigital.erp.financeiro.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.financeiro.dto.AplicacaoFinanceiraRequest;
import br.com.cactusdigital.erp.financeiro.dto.AplicacaoFinanceiraResponse;
import br.com.cactusdigital.erp.financeiro.dto.ResgatarAplicacaoRequest;
import br.com.cactusdigital.erp.financeiro.service.AplicacaoFinanceiraService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Tesouraria — Aplicação Financeira", description = "Aplicação/resgate de curto prazo (CDB/fundo/Tesouro Direto/poupança) — debita/credita a conta corrente de origem")
@RestController
@RequestMapping("/api/v1/aplicacoes-financeiras")
@RequiredArgsConstructor
public class AplicacaoFinanceiraController {

    private final AplicacaoFinanceiraService service;

    @Operation(summary = "Listar aplicações financeiras", description = "Filtra por contaBancariaId (opcional)")
    @ApiResponse(responseCode = "200", description = "Página de aplicações financeiras")
    @GetMapping
    @PreAuthorize("hasAuthority('CAIXA_CONSULTAR')")
    public PageResponse<AplicacaoFinanceiraResponse> listar(@RequestParam(required = false) Long contaBancariaId, Pageable pageable) {
        return PageResponse.of(service.listar(contaBancariaId, pageable));
    }

    @Operation(summary = "Buscar aplicação financeira por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aplicação encontrada"),
            @ApiResponse(responseCode = "404", description = "Aplicação não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CAIXA_CONSULTAR')")
    public AplicacaoFinanceiraResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Aplicar", description = "Debita a conta bancária de origem na hora — gera SAIDA real")
    @ApiResponse(responseCode = "201", description = "Aplicação registrada")
    @PostMapping
    @PreAuthorize("hasAuthority('CAIXA_MOVIMENTAR')")
    public ResponseEntity<AplicacaoFinanceiraResponse> aplicar(@Valid @RequestBody AplicacaoFinanceiraRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.aplicar(request));
    }

    @Operation(summary = "Resgatar", description = "Credita a conta bancária de origem — valorResgatado pode ser maior ou menor que o valorAplicado (rendimento positivo ou negativo)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Aplicação resgatada"),
            @ApiResponse(responseCode = "404", description = "Aplicação não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Aplicação já resgatada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/resgatar")
    @PreAuthorize("hasAuthority('CAIXA_MOVIMENTAR')")
    public AplicacaoFinanceiraResponse resgatar(@PathVariable Long id, @Valid @RequestBody ResgatarAplicacaoRequest request) {
        return service.resgatar(id, request);
    }
}
