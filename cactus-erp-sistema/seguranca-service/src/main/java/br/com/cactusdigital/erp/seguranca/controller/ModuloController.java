package br.com.cactusdigital.erp.seguranca.controller;

import br.com.cactusdigital.erp.seguranca.dto.ModuloResponse;
import br.com.cactusdigital.erp.seguranca.service.ModuloService;
import org.springframework.http.ProblemDetail;
import br.com.cactusdigital.erp.common.rest.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RestController;

/** Só leitura — ver {@link ModuloService}. */
@Tag(name = "Segurança — Módulo", description = "Um por serviço planejado do Cactus ERP — só leitura, semeado via migration")
@RestController
@RequestMapping("/api/v1/modulos")
@RequiredArgsConstructor
public class ModuloController {

    private final ModuloService service;

    @Operation(summary = "Listar módulos")
    @ApiResponse(responseCode = "200", description = "Página de módulos")
    @GetMapping
    @PreAuthorize("hasAuthority('SEGURANCA_CONSULTAR')")
    public PageResponse<ModuloResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Buscar módulo por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Módulo encontrado"),
            @ApiResponse(responseCode = "404", description = "Módulo não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SEGURANCA_CONSULTAR')")
    public ModuloResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
}
