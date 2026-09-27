package br.com.cactusdigital.erp.sincronizacao.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.LoteSincronizacaoResumoResponse;
import br.com.cactusdigital.erp.sincronizacao.dto.RegistroSincronizacaoResponse;
import br.com.cactusdigital.erp.sincronizacao.entity.StatusRegistroSincronizacao;
import br.com.cactusdigital.erp.sincronizacao.service.SincronizacaoAuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Sincronização — Auditoria", description = "Consulta de lotes/registros e reprocessamento manual de registro em ERRO")
@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
public class SincronizacaoAuditoriaController {

    private final SincronizacaoAuditoriaService service;

    @Operation(summary = "Listar lotes recebidos", description = "Busca paginada, opcionalmente filtrada por loja")
    @ApiResponse(responseCode = "200", description = "Página de lotes")
    @GetMapping("/lotes")
    @PreAuthorize("hasAuthority('SINCRONIZACAO_CONSULTAR')")
    public PageResponse<LoteSincronizacaoResumoResponse> listarLotes(@RequestParam(required = false) Long lojaId, Pageable pageable) {
        return PageResponse.of(service.listarLotes(lojaId, pageable));
    }

    @Operation(summary = "Listar registros de sincronização", description = "Busca paginada, opcionalmente filtrada por status e/ou loja")
    @ApiResponse(responseCode = "200", description = "Página de registros")
    @GetMapping("/registros")
    @PreAuthorize("hasAuthority('SINCRONIZACAO_CONSULTAR')")
    public PageResponse<RegistroSincronizacaoResponse> listarRegistros(
            @RequestParam(required = false) StatusRegistroSincronizacao status,
            @RequestParam(required = false) Long lojaId,
            Pageable pageable) {
        return PageResponse.of(service.listarRegistros(status, lojaId, pageable));
    }

    @Operation(summary = "Reprocessar registro em erro", description = "Só permitido com status=ERRO — devolve pra RECEBIDO, o outbox republica no próximo ciclo. usuarioId vem do JWT de quem chama, nunca do corpo da requisição")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro devolvido pra RECEBIDO"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Registro não está em ERRO",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/registros/{id}/reprocessar")
    @PreAuthorize("hasAuthority('SINCRONIZACAO_GERENCIAR')")
    public RegistroSincronizacaoResponse reprocessar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long usuarioId = jwt.getClaim("usuarioId");
        return service.reprocessar(id, usuarioId);
    }
}
