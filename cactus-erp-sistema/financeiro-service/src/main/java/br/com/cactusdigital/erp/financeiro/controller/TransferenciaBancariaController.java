package br.com.cactusdigital.erp.financeiro.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.financeiro.dto.TransferenciaBancariaRequest;
import br.com.cactusdigital.erp.financeiro.dto.TransferenciaBancariaResponse;
import br.com.cactusdigital.erp.financeiro.service.TransferenciaBancariaService;
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

@Tag(name = "Tesouraria — Transferência Bancária", description = "Transferência entre 2 contas bancárias da própria empresa — débito e crédito atômicos, sem estado 'em trânsito'")
@RestController
@RequestMapping("/api/v1/transferencias-bancarias")
@RequiredArgsConstructor
public class TransferenciaBancariaController {

    private final TransferenciaBancariaService service;

    @Operation(summary = "Listar transferências bancárias", description = "Filtra por contaBancariaId (origem OU destino, opcional)")
    @ApiResponse(responseCode = "200", description = "Página de transferências")
    @GetMapping
    @PreAuthorize("hasAuthority('CAIXA_CONSULTAR')")
    public PageResponse<TransferenciaBancariaResponse> listar(@RequestParam(required = false) Long contaBancariaId, Pageable pageable) {
        return PageResponse.of(service.listar(contaBancariaId, pageable));
    }

    @Operation(summary = "Buscar transferência por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transferência encontrada"),
            @ApiResponse(responseCode = "404", description = "Transferência não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CAIXA_CONSULTAR')")
    public TransferenciaBancariaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Transferir entre contas bancárias", description = "Debita a origem e credita o destino atomicamente — 2 MovimentoCaixaBanco gerados na mesma transação")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transferência realizada"),
            @ApiResponse(responseCode = "404", description = "Conta de origem ou destino não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Conta de origem ou destino inativa", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Conta de origem igual à de destino", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CAIXA_MOVIMENTAR')")
    public ResponseEntity<TransferenciaBancariaResponse> transferir(@Valid @RequestBody TransferenciaBancariaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.transferir(request));
    }
}
