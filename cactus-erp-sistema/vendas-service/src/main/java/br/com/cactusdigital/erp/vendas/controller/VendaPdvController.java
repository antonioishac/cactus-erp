package br.com.cactusdigital.erp.vendas.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaPdvResponse;
import br.com.cactusdigital.erp.vendas.service.VendaPdvService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Vendas do PDV", description = "Vendas de balcão e devoluções vindas do cactus-pdv pela sincronização (só leitura — nascem no PDV). Não baixam estoque: o PDV já manda o ESTOQUE_MOVIMENTO")
@RestController
@RequestMapping("/api/v1/vendas-pdv")
@RequiredArgsConstructor
public class VendaPdvController {

    private final VendaPdvService service;

    @Operation(summary = "Listar vendas do PDV por período", description = "Inclui itens, pagamentos (fracionados) e devoluções. Filtro opcional por terminal")
    @ApiResponse(responseCode = "200", description = "Página de vendas do PDV")
    @GetMapping
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public PageResponse<VendaPdvResponse> listar(
            @RequestParam Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Long pdvCaixaId,
            @PageableDefault(sort = "dataHoraVenda", direction = Sort.Direction.DESC) Pageable pageable) {
        return PageResponse.of(service.listar(empresaId, dataInicio, dataFim, pdvCaixaId, pageable));
    }

    @Operation(summary = "Buscar venda do PDV por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venda encontrada"),
            @ApiResponse(responseCode = "404", description = "Venda não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public VendaPdvResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
}
