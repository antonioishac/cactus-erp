package br.com.cactusdigital.erp.bi.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.bi.dto.ComprasFornecedorIndicadorResponse;
import br.com.cactusdigital.erp.bi.dto.CurvaAbcIndicadorResponse;
import br.com.cactusdigital.erp.bi.dto.EstoqueValorIndicadorResponse;
import br.com.cactusdigital.erp.bi.service.IndicadoresService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Indicadores", description = "Schema de leitura do BI — snapshots gerados pelo ETL, não calculados em tempo real")
@RestController
@RequestMapping("/api/v1/indicadores")
@RequiredArgsConstructor
public class IndicadoresController {

    private final IndicadoresService service;

    @Operation(summary = "Valor de estoque por produto×depósito", description = "Snapshot da data de referência informada (default: hoje) — gerado a partir de saldo_estoque no último ETL")
    @ApiResponse(responseCode = "200", description = "Página de indicadores de valor de estoque")
    @GetMapping("/estoque-valor")
    @PreAuthorize("hasAuthority('BI_CONSULTAR')")
    public PageResponse<EstoqueValorIndicadorResponse> estoqueValor(
            @RequestParam(required = false) LocalDate dataReferencia, @RequestParam(required = false) Long depositoId, Pageable pageable) {
        return PageResponse.of(service.estoqueValor(dataReferencia != null ? dataReferencia : LocalDate.now(), depositoId, pageable));
    }

    @Operation(summary = "Curva ABC histórica", description = "Snapshot persistido por data de referência — o controle-estoque-service só calcula sob demanda, aqui dá pra comparar ao longo do tempo")
    @ApiResponse(responseCode = "200", description = "Página de itens classificados")
    @GetMapping("/curva-abc")
    @PreAuthorize("hasAuthority('BI_CONSULTAR')")
    public PageResponse<CurvaAbcIndicadorResponse> curvaAbc(
            @RequestParam(required = false) LocalDate dataReferencia, @RequestParam(required = false) Long depositoId, Pageable pageable) {
        return PageResponse.of(service.curvaAbc(dataReferencia != null ? dataReferencia : LocalDate.now(), depositoId, pageable));
    }

    @Operation(summary = "Resumo de compras por fornecedor", description = "Agregado mensal — anoMes no formato YYYY-MM, default: mês atual")
    @ApiResponse(responseCode = "200", description = "Página de resumos por fornecedor")
    @GetMapping("/compras-fornecedor")
    @PreAuthorize("hasAuthority('BI_CONSULTAR')")
    public PageResponse<ComprasFornecedorIndicadorResponse> comprasFornecedor(
            @RequestParam(required = false) String anoMes, Pageable pageable) {
        String periodo = anoMes != null ? anoMes : YearMonth.now().toString();
        return PageResponse.of(service.comprasFornecedor(periodo, pageable));
    }
}
