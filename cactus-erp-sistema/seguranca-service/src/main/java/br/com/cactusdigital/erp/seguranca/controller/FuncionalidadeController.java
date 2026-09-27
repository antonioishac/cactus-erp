package br.com.cactusdigital.erp.seguranca.controller;

import br.com.cactusdigital.erp.seguranca.dto.FuncionalidadeResponse;
import br.com.cactusdigital.erp.seguranca.service.FuncionalidadeService;
import org.springframework.http.ProblemDetail;
import br.com.cactusdigital.erp.common.rest.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Só leitura — ver {@link FuncionalidadeService}. */
@Tag(name = "Segurança — Funcionalidade", description = "Ação granular dentro de um módulo (padrão MODULO_RECURSO_ACAO) — vira o claim authorities do JWT")
@RestController
@RequestMapping("/api/v1/funcionalidades")
@RequiredArgsConstructor
public class FuncionalidadeController {

    private final FuncionalidadeService service;

    @Operation(summary = "Listar funcionalidades", description = "Busca paginada por módulo, opcional")
    @ApiResponse(responseCode = "200", description = "Página de funcionalidades")
    @GetMapping
    @PreAuthorize("hasAuthority('SEGURANCA_CONSULTAR')")
    public PageResponse<FuncionalidadeResponse> listar(
            @Parameter(description = "Filtro por id do módulo") @RequestParam(required = false) Long moduloId, Pageable pageable) {
        return PageResponse.of(service.listar(moduloId, pageable));
    }

    @Operation(summary = "Buscar funcionalidade por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Funcionalidade encontrada"),
            @ApiResponse(responseCode = "404", description = "Funcionalidade não encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SEGURANCA_CONSULTAR')")
    public FuncionalidadeResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
}
