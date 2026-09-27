package br.com.cactusdigital.erp.financeiro.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.financeiro.dto.CancelarChequeEmitidoRequest;
import br.com.cactusdigital.erp.financeiro.dto.ChequeEmitidoRequest;
import br.com.cactusdigital.erp.financeiro.dto.ChequeEmitidoResponse;
import br.com.cactusdigital.erp.financeiro.dto.CompensarChequeEmitidoRequest;
import br.com.cactusdigital.erp.financeiro.dto.DevolverChequeRequest;
import br.com.cactusdigital.erp.financeiro.service.ChequeEmitidoService;
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

@Tag(name = "Cheque Emitido", description = "Cheque emitido pela própria empresa — EMITIDO → COMPENSADO (gera SAIDA real) | DEVOLVIDO | CANCELADO")
@RestController
@RequestMapping("/api/v1/cheques-emitidos")
@RequiredArgsConstructor
public class ChequeEmitidoController {

    private final ChequeEmitidoService service;

    @Operation(summary = "Listar cheques emitidos", description = "Filtra por contaBancariaId (opcional)")
    @ApiResponse(responseCode = "200", description = "Página de cheques emitidos")
    @GetMapping
    @PreAuthorize("hasAuthority('CAIXA_CONSULTAR')")
    public PageResponse<ChequeEmitidoResponse> listar(@RequestParam(required = false) Long contaBancariaId, Pageable pageable) {
        return PageResponse.of(service.listar(contaBancariaId, pageable));
    }

    @Operation(summary = "Registrar cheque emitido")
    @ApiResponse(responseCode = "201", description = "Cheque registrado")
    @PostMapping
    @PreAuthorize("hasAuthority('CAIXA_MOVIMENTAR')")
    public ResponseEntity<ChequeEmitidoResponse> criar(@Valid @RequestBody ChequeEmitidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Compensar cheque emitido", description = "Gera SAIDA real na conta bancária do cheque")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cheque compensado"),
            @ApiResponse(responseCode = "409", description = "Cheque não está mais EMITIDO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/compensar")
    @PreAuthorize("hasAuthority('CAIXA_MOVIMENTAR')")
    public ChequeEmitidoResponse compensar(@PathVariable Long id, @Valid @RequestBody CompensarChequeEmitidoRequest request) {
        return service.compensar(id, request);
    }

    @Operation(summary = "Marcar cheque emitido como devolvido", description = "Sem movimento — dinheiro só sai de fato na compensação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cheque marcado como devolvido"),
            @ApiResponse(responseCode = "409", description = "Cheque não está mais EMITIDO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/devolver")
    @PreAuthorize("hasAuthority('CAIXA_MOVIMENTAR')")
    public ChequeEmitidoResponse devolver(@PathVariable Long id, @Valid @RequestBody DevolverChequeRequest request) {
        return service.devolver(id, request);
    }

    @Operation(summary = "Cancelar cheque emitido", description = "Cheque nunca usado (rasgado/inutilizado)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cheque cancelado"),
            @ApiResponse(responseCode = "409", description = "Cheque não está mais EMITIDO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAuthority('CAIXA_GERENCIAR')")
    public ChequeEmitidoResponse cancelar(@PathVariable Long id, @Valid @RequestBody CancelarChequeEmitidoRequest request) {
        return service.cancelar(id, request);
    }
}
