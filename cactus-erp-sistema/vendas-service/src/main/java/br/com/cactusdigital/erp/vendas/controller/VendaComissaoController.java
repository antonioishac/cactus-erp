package br.com.cactusdigital.erp.vendas.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaComissaoResponse;
import br.com.cactusdigital.erp.vendas.service.VendaComissaoService;
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

@Tag(name = "Comissão de Venda", description = "Consulta de lançamentos de comissão — gerados automaticamente na confirmação da venda, nunca criados diretamente")
@RestController
@RequestMapping("/api/v1/comissoes")
@RequiredArgsConstructor
public class VendaComissaoController {

    private final VendaComissaoService service;

    @Operation(summary = "Listar comissões", description = "Opcionalmente filtrado por vendedor")
    @ApiResponse(responseCode = "200", description = "Página de comissões")
    @GetMapping
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public PageResponse<VendaComissaoResponse> listar(@RequestParam(required = false) Long vendedorId, Pageable pageable) {
        return PageResponse.of(service.listar(vendedorId, pageable));
    }

    @Operation(summary = "Buscar comissão por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comissão encontrada"),
            @ApiResponse(responseCode = "404", description = "Comissão não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public VendaComissaoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
}
