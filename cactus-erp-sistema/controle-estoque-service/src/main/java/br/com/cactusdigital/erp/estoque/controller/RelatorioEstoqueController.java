package br.com.cactusdigital.erp.estoque.controller;

import br.com.cactusdigital.erp.common.report.ReportEngine;
import br.com.cactusdigital.erp.common.report.ReportFormat;
import br.com.cactusdigital.erp.common.report.ReportHttpSupport;
import br.com.cactusdigital.erp.estoque.dto.EstoqueMovimentoResponse;
import br.com.cactusdigital.erp.estoque.dto.SaldoEstoqueResponse;
import br.com.cactusdigital.erp.estoque.service.RelatorioEstoqueService;
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
 * Relatórios gerenciais do controle de estoque — saldo por depósito e ficha de
 * movimentação por produto (Curva ABC fica em {@link CurvaAbcController}, piloto da Fase
 * 17). Cada relatório tem 3 formas de saída: JSON (consulta/tela), PDF
 * (impressão/arquivamento) e XLSX (análise), calculados sob demanda a partir de
 * {@link RelatorioEstoqueService}.
 */
@Tag(name = "Relatórios — Estoque", description = "Saldo por depósito e ficha de movimentação por produto — calculados sob demanda, não persistidos")
@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
public class RelatorioEstoqueController {

    private static final DateTimeFormatter FORMATO_DATA_GERACAO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATO_DATA_PERIODO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RelatorioEstoqueService service;
    private final ReportEngine reportEngine;

    // --------------------------------------------------------------------- Saldo por depósito

    @Operation(summary = "Saldo de estoque por depósito", description = "Documento inteiro, sem paginação — filtro opcional por produto ou depósito")
    @ApiResponse(responseCode = "200", description = "Saldos encontrados")
    @GetMapping("/saldo-estoque")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public List<SaldoEstoqueResponse> saldoPorDeposito(@RequestParam(required = false) Long produtoId, @RequestParam(required = false) Long depositoId) {
        return service.saldoPorDeposito(produtoId, depositoId);
    }

    @Operation(summary = "Exportar saldo de estoque em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/saldo-estoque/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public ResponseEntity<byte[]> saldoPorDepositoPdf(@RequestParam(required = false) Long produtoId, @RequestParam(required = false) Long depositoId) {
        return exportarSaldoPorDeposito(produtoId, depositoId, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar saldo de estoque em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/saldo-estoque/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public ResponseEntity<byte[]> saldoPorDepositoXlsx(@RequestParam(required = false) Long produtoId, @RequestParam(required = false) Long depositoId) {
        return exportarSaldoPorDeposito(produtoId, depositoId, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportarSaldoPorDeposito(Long produtoId, Long depositoId, ReportFormat formato) {
        List<SaldoEstoqueResponse> dados = service.saldoPorDeposito(produtoId, depositoId);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_FILTRO", "Produto: " + filtroOuTodos(produtoId) + "   |   Depósito: " + filtroOuTodos(depositoId));
        parametros.put("P_DATA_GERACAO", dataGeracao());
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/saldo-estoque.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "saldo-estoque");
    }

    // -------------------------------------------------------------- Ficha de movimentação

    @Operation(summary = "Ficha de movimentação de um produto", description = "Ledger cronológico no período — depósito opcional estreita pra um único depósito")
    @ApiResponse(responseCode = "200", description = "Movimentos do produto no período, em ordem cronológica")
    @GetMapping("/ficha-movimentacao-produto")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public List<EstoqueMovimentoResponse> fichaMovimentacao(
            @RequestParam Long produtoId, @RequestParam(required = false) Long depositoId,
            @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim) {
        return service.fichaMovimentacao(produtoId, depositoId, dataInicio, dataFim);
    }

    @Operation(summary = "Exportar ficha de movimentação em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/ficha-movimentacao-produto/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public ResponseEntity<byte[]> fichaMovimentacaoPdf(
            @RequestParam Long produtoId, @RequestParam(required = false) Long depositoId,
            @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim) {
        return exportarFichaMovimentacao(produtoId, depositoId, dataInicio, dataFim, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar ficha de movimentação em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/ficha-movimentacao-produto/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public ResponseEntity<byte[]> fichaMovimentacaoXlsx(
            @RequestParam Long produtoId, @RequestParam(required = false) Long depositoId,
            @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim) {
        return exportarFichaMovimentacao(produtoId, depositoId, dataInicio, dataFim, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportarFichaMovimentacao(Long produtoId, Long depositoId, LocalDate dataInicio, LocalDate dataFim, ReportFormat formato) {
        List<EstoqueMovimentoResponse> dados = service.fichaMovimentacao(produtoId, depositoId, dataInicio, dataFim);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_PRODUTO", "#" + produtoId);
        parametros.put("P_PERIODO", periodo(dataInicio, dataFim));
        parametros.put("P_FILTRO", "Depósito: " + filtroOuTodos(depositoId));
        parametros.put("P_DATA_GERACAO", dataGeracao());
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/ficha-movimentacao-produto.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "ficha-movimentacao-produto");
    }

    // -------------------------------------------------------------------------- helpers

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
