package br.com.cactusdigital.erp.estoque.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.estoque.dto.SaldoAgregadoResponse;
import br.com.cactusdigital.erp.estoque.dto.SaldoEstoqueResponse;
import br.com.cactusdigital.erp.estoque.service.SaldoEstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Só leitura — mutação de saldo é exclusiva do ledger, ver {@link EstoqueMovimentoController}. */
@Tag(name = "Saldo de Estoque", description = "Consulta de saldo por produto×depósito — mutação só via lançamento de movimento")
@RestController
@RequestMapping("/api/v1/saldos")
@RequiredArgsConstructor
public class SaldoEstoqueController {

    private final SaldoEstoqueService service;

    @Operation(summary = "Listar saldos", description = "Busca paginada, opcionalmente filtrada por produto ou depósito")
    @ApiResponse(responseCode = "200", description = "Página de saldos")
    @GetMapping
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public PageResponse<SaldoEstoqueResponse> listar(
            @RequestParam(required = false) Long produtoId, @RequestParam(required = false) Long depositoId, Pageable pageable) {
        return PageResponse.of(service.listar(produtoId, depositoId, pageable));
    }

    @Operation(summary = "Buscar saldo de um produto num depósito específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saldo encontrado"),
            @ApiResponse(responseCode = "404", description = "Sem saldo registrado pra esse produto/depósito",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/produto/{produtoId}/deposito/{depositoId}")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public SaldoEstoqueResponse buscarPorProdutoEDeposito(@PathVariable Long produtoId, @PathVariable Long depositoId) {
        return service.buscarPorProdutoEDeposito(produtoId, depositoId);
    }

    @Operation(summary = "Saldo agregado por empresa", description = "Soma o saldo de cada produto em todos os depósitos "
            + "da empresa — usado pelo sincronizacao-service (Fase 6) pra compor o pull de produto pro PDV, que enxerga um "
            + "total por loja, não por depósito. Produto sem saldo lançado não aparece na resposta (trate ausência como zero)")
    @ApiResponse(responseCode = "200", description = "Saldos agregados dos produtos informados")
    @GetMapping("/agregado")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public List<SaldoAgregadoResponse> agregarPorEmpresaEProdutos(
            @RequestParam Long empresaId,
            @Parameter(description = "Ids de produto separados por vírgula") @RequestParam List<Long> produtoIds) {
        return service.agregarPorEmpresaEProdutos(empresaId, produtoIds);
    }
}
