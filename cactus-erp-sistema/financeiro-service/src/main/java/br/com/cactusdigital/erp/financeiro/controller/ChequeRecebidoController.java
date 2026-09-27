package br.com.cactusdigital.erp.financeiro.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.financeiro.dto.ChequeRecebidoRequest;
import br.com.cactusdigital.erp.financeiro.dto.ChequeRecebidoResponse;
import br.com.cactusdigital.erp.financeiro.dto.CompensarChequeRecebidoRequest;
import br.com.cactusdigital.erp.financeiro.dto.DepositarChequeRecebidoRequest;
import br.com.cactusdigital.erp.financeiro.dto.DescontarChequeRecebidoRequest;
import br.com.cactusdigital.erp.financeiro.dto.DevolverChequeRequest;
import br.com.cactusdigital.erp.financeiro.service.ChequeRecebidoService;
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

@Tag(name = "Cheque Recebido", description = "Cheque de terceiro recebido como pagamento — EM_CARTEIRA → DEPOSITADO → COMPENSADO (gera ENTRADA) | DESCONTADO (ENTRADA imediata) | DEVOLVIDO")
@RestController
@RequestMapping("/api/v1/cheques-recebidos")
@RequiredArgsConstructor
public class ChequeRecebidoController {

    private final ChequeRecebidoService service;

    @Operation(summary = "Listar cheques recebidos", description = "Filtra por clienteId (opcional)")
    @ApiResponse(responseCode = "200", description = "Página de cheques recebidos")
    @GetMapping
    @PreAuthorize("hasAuthority('CAIXA_CONSULTAR')")
    public PageResponse<ChequeRecebidoResponse> listar(@RequestParam(required = false) Long clienteId, Pageable pageable) {
        return PageResponse.of(service.listar(clienteId, pageable));
    }

    @Operation(summary = "Registrar cheque recebido")
    @ApiResponse(responseCode = "201", description = "Cheque registrado")
    @PostMapping
    @PreAuthorize("hasAuthority('CAIXA_MOVIMENTAR')")
    public ResponseEntity<ChequeRecebidoResponse> criar(@Valid @RequestBody ChequeRecebidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    @Operation(summary = "Depositar cheque recebido", description = "Sem movimento ainda — só reserva a conta bancária de destino, confirma na compensação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cheque depositado"),
            @ApiResponse(responseCode = "409", description = "Cheque não está EM_CARTEIRA", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/depositar")
    @PreAuthorize("hasAuthority('CAIXA_MOVIMENTAR')")
    public ChequeRecebidoResponse depositar(@PathVariable Long id, @Valid @RequestBody DepositarChequeRecebidoRequest request) {
        return service.depositar(id, request);
    }

    @Operation(summary = "Compensar cheque recebido depositado", description = "Gera ENTRADA real na conta bancária do depósito")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cheque compensado"),
            @ApiResponse(responseCode = "409", description = "Cheque não está DEPOSITADO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/compensar")
    @PreAuthorize("hasAuthority('CAIXA_MOVIMENTAR')")
    public ChequeRecebidoResponse compensar(@PathVariable Long id, @Valid @RequestBody CompensarChequeRecebidoRequest request) {
        return service.compensar(id, request);
    }

    @Operation(summary = "Descontar cheque recebido em banco", description = "Dinheiro entra imediatamente (banco assume o risco) — direto de EM_CARTEIRA, sem passar por depositar")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cheque descontado"),
            @ApiResponse(responseCode = "409", description = "Cheque não está EM_CARTEIRA", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/descontar")
    @PreAuthorize("hasAuthority('CAIXA_MOVIMENTAR')")
    public ChequeRecebidoResponse descontar(@PathVariable Long id, @Valid @RequestBody DescontarChequeRecebidoRequest request) {
        return service.descontar(id, request);
    }

    @Operation(summary = "Marcar cheque recebido como devolvido", description = "Só permitido EM_CARTEIRA ou DEPOSITADO — depois de COMPENSADO/DESCONTADO é estorno bancário, fora de escopo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cheque marcado como devolvido"),
            @ApiResponse(responseCode = "409", description = "Situação do cheque não permite devolução", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/devolver")
    @PreAuthorize("hasAuthority('CAIXA_MOVIMENTAR')")
    public ChequeRecebidoResponse devolver(@PathVariable Long id, @Valid @RequestBody DevolverChequeRequest request) {
        return service.devolver(id, request);
    }
}
