package br.com.cactusdigital.erp.loja.controller;

import br.com.cactusdigital.erp.common.report.ReportEngine;
import br.com.cactusdigital.erp.common.report.ReportFormat;
import br.com.cactusdigital.erp.common.report.ReportHttpSupport;
import br.com.cactusdigital.erp.loja.dto.PedidoResponse;
import br.com.cactusdigital.erp.loja.entity.SituacaoPedido;
import br.com.cactusdigital.erp.loja.service.RelatorioPedidoService;
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
 * Pedidos da loja por período — relatório gerencial do loja-virtual-service (ver
 * ANÁLISE: MÓDULO DE RELATÓRIOS no CLAUDE.md raiz). JSON (consulta/tela), PDF
 * (impressão/arquivamento) e XLSX (análise), calculados sob demanda a partir de
 * {@link RelatorioPedidoService}. Mesmo RBAC admin-only já usado pelo resto do catálogo
 * (ver Javadoc de {@code Pedido}) — não o checkout público de {@code Carrinho}.
 */
@Tag(name = "Relatórios — Loja Virtual", description = "Pedidos por período — calculado sob demanda, não persistido")
@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
public class RelatorioPedidoController {

    private static final DateTimeFormatter FORMATO_DATA_GERACAO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATO_DATA_PERIODO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RelatorioPedidoService service;
    private final ReportEngine reportEngine;

    @Operation(summary = "Pedidos por período", description = "Filtro por período de criação do pedido, opcionalmente por loja/situação")
    @ApiResponse(responseCode = "200", description = "Pedidos do período, ordenados por data de criação")
    @GetMapping("/pedidos-loja-periodo")
    @PreAuthorize("hasAuthority('LOJA_CONSULTAR')")
    public List<PedidoResponse> pedidosPorPeriodo(
            @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim,
            @RequestParam(required = false) Long lojaId, @RequestParam(required = false) SituacaoPedido situacao) {
        return service.pedidosPorPeriodo(dataInicio, dataFim, lojaId, situacao);
    }

    @Operation(summary = "Exportar pedidos por período em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/pedidos-loja-periodo/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('LOJA_CONSULTAR')")
    public ResponseEntity<byte[]> pedidosPorPeriodoPdf(
            @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim,
            @RequestParam(required = false) Long lojaId, @RequestParam(required = false) SituacaoPedido situacao) {
        return exportarPedidosPorPeriodo(dataInicio, dataFim, lojaId, situacao, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar pedidos por período em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/pedidos-loja-periodo/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('LOJA_CONSULTAR')")
    public ResponseEntity<byte[]> pedidosPorPeriodoXlsx(
            @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim,
            @RequestParam(required = false) Long lojaId, @RequestParam(required = false) SituacaoPedido situacao) {
        return exportarPedidosPorPeriodo(dataInicio, dataFim, lojaId, situacao, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportarPedidosPorPeriodo(LocalDate dataInicio, LocalDate dataFim, Long lojaId, SituacaoPedido situacao, ReportFormat formato) {
        List<PedidoResponse> dados = service.pedidosPorPeriodo(dataInicio, dataFim, lojaId, situacao);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_PERIODO", periodo(dataInicio, dataFim));
        parametros.put("P_FILTRO", "Loja: " + filtroOuTodos(lojaId) + "   |   Situação: " + (situacao != null ? situacao.name() : "Todas"));
        parametros.put("P_DATA_GERACAO", dataGeracao());
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/pedidos-loja-periodo.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "pedidos-loja-periodo");
    }

    private String periodo(LocalDate dataInicio, LocalDate dataFim) {
        return dataInicio.format(FORMATO_DATA_PERIODO) + " a " + dataFim.format(FORMATO_DATA_PERIODO);
    }

    private String filtroOuTodos(Long id) {
        return id != null ? "#" + id : "Todas";
    }

    private String dataGeracao() {
        return LocalDateTime.now().format(FORMATO_DATA_GERACAO);
    }
}
