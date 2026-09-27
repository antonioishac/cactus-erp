package br.com.cactusdigital.erp.vendas.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.vendas.dto.SagaExecucaoResponse;
import br.com.cactusdigital.erp.vendas.service.SagaExecucaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Consulta only — visibilidade operacional sobre execuções de saga (ver Javadoc de
 * {@code SagaExecucao}/{@code SagaOrchestrator}). Nível de acesso {@code VENDAS_GERENCIAR}
 * (mesmo critério de {@code cancelar} — é ferramenta de suporte/backoffice, não trabalho
 * comercial do dia a dia).
 */
@Tag(name = "Execuções de Saga", description = "Trilha de auditoria das sagas orquestradas (ex: confirmar venda) — visibilidade sobre falhas/compensações")
@RestController
@RequestMapping("/api/v1/sagas")
@RequiredArgsConstructor
public class SagaExecucaoController {

    private final SagaExecucaoService service;

    @Operation(summary = "Listar execuções de saga", description = "Filtra opcionalmente por referenciaTipo+referenciaId (ex: referenciaTipo=VENDA&referenciaId=42)")
    @ApiResponse(responseCode = "200", description = "Página de execuções de saga")
    @GetMapping
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public PageResponse<SagaExecucaoResponse> listar(
            @RequestParam(required = false) String referenciaTipo,
            @RequestParam(required = false) Long referenciaId,
            Pageable pageable) {
        return PageResponse.of(service.listar(referenciaTipo, referenciaId, pageable));
    }

    @Operation(summary = "Buscar execução de saga por id", description = "Inclui a lista de passos (executados/compensados/falhou-ao-compensar)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Execução encontrada"),
            @ApiResponse(responseCode = "404", description = "Execução não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_GERENCIAR')")
    public SagaExecucaoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
}
