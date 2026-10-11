package br.com.cactusdigital.erp.compras.controller;

import br.com.cactusdigital.erp.common.report.ReportEngine;
import br.com.cactusdigital.erp.common.report.ReportFormat;
import br.com.cactusdigital.erp.common.report.ReportHttpSupport;
import br.com.cactusdigital.erp.compras.dto.PedidoCompraRelatorioItemResponse;
import br.com.cactusdigital.erp.compras.entity.SituacaoPedidoCompra;
import br.com.cactusdigital.erp.compras.service.RelatorioComprasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Relatório gerencial de pedidos de compra por período/fornecedor/situação — fecha o
 * gap do módulo (nenhum relatório existia antes, ver ANÁLISE: MÓDULO DE RELATÓRIOS no
 * CLAUDE.md raiz). JSON (consulta/tela), PDF (impressão/arquivamento) e XLSX (análise),
 * todos calculados sob demanda a partir de {@link RelatorioComprasService}.
 */
@Tag(name = "Relatórios — Compras", description = "Pedidos de compra por período, com filtro opcional de fornecedor/situação — calculado sob demanda, não persistido")
@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
public class RelatorioComprasController {

    private static final DateTimeFormatter FORMATO_DATA_GERACAO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATO_DATA_PERIODO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RelatorioComprasService service;
    private final ReportEngine reportEngine;

    @Operation(summary = "Pedidos de compra por período", description = "Filtro por dataPedido, opcionalmente por fornecedor/situação")
    @ApiResponse(responseCode = "200", description = "Pedidos do período, ordenados por data")
    @GetMapping("/pedidos-compra-periodo")
    @PreAuthorize("hasAuthority('COMPRAS_CONSULTAR')")
    public List<PedidoCompraRelatorioItemResponse> pedidosPorPeriodo(
            @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim,
            @RequestParam(required = false) Long fornecedorId, @RequestParam(required = false) SituacaoPedidoCompra situacao) {
        return service.pedidosPorPeriodo(dataInicio, dataFim, fornecedorId, situacao);
    }

    @Operation(summary = "Exportar pedidos de compra por período em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/pedidos-compra-periodo/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('COMPRAS_CONSULTAR')")
    public ResponseEntity<byte[]> pedidosPorPeriodoPdf(
            @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim,
            @RequestParam(required = false) Long fornecedorId, @RequestParam(required = false) SituacaoPedidoCompra situacao) {
        return exportarPedidosPorPeriodo(dataInicio, dataFim, fornecedorId, situacao, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar pedidos de compra por período em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/pedidos-compra-periodo/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('COMPRAS_CONSULTAR')")
    public ResponseEntity<byte[]> pedidosPorPeriodoXlsx(
            @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim,
            @RequestParam(required = false) Long fornecedorId, @RequestParam(required = false) SituacaoPedidoCompra situacao) {
        return exportarPedidosPorPeriodo(dataInicio, dataFim, fornecedorId, situacao, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportarPedidosPorPeriodo(
            LocalDate dataInicio, LocalDate dataFim, Long fornecedorId, SituacaoPedidoCompra situacao, ReportFormat formato) {
        List<PedidoCompraRelatorioItemResponse> dados = service.pedidosPorPeriodo(dataInicio, dataFim, fornecedorId, situacao);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_PERIODO", periodo(dataInicio, dataFim));
        parametros.put("P_FILTRO", "Fornecedor: " + filtroOuTodos(fornecedorId) + "   |   Situação: "
                + (situacao != null ? situacao.name() : "Todas"));
        parametros.put("P_DATA_GERACAO", dataGeracao());
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/pedidos-compra-periodo.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "pedidos-compra-periodo");
    }

    private String periodo(LocalDate dataInicio, LocalDate dataFim) {
        return dataInicio.format(FORMATO_DATA_PERIODO) + " a " + dataFim.format(FORMATO_DATA_PERIODO);
    }

    private String filtroOuTodos(Long id) {
        return id != null ? "#" + id : "Todos";
    }

    private String dataGeracao() {
        return LocalDateTime.now().format(FORMATO_DATA_GERACAO);
    }
}
