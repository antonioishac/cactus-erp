package br.com.cactusdigital.erp.fiscal.controller;

import br.com.cactusdigital.erp.common.report.ReportEngine;
import br.com.cactusdigital.erp.common.report.ReportFormat;
import br.com.cactusdigital.erp.common.report.ReportHttpSupport;
import br.com.cactusdigital.erp.fiscal.dto.NotaFiscalPeriodoItemResponse;
import br.com.cactusdigital.erp.fiscal.service.RelatorioFiscalService;
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
 * Notas emitidas por período — relatório gerencial unificado do fiscal-service (NF-e +
 * NFC-e + NFS-e + CT-e numa grade só, ver ANÁLISE: MÓDULO DE RELATÓRIOS no CLAUDE.md
 * raiz). JSON (consulta/tela), PDF (impressão/arquivamento) e XLSX (análise), calculados
 * sob demanda a partir de {@link RelatorioFiscalService}.
 */
@Tag(name = "Relatórios — Fiscal", description = "Notas emitidas por período (NF-e/NFC-e/NFS-e/CT-e unificadas) — calculado sob demanda, não persistido")
@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
public class RelatorioFiscalController {

    private static final DateTimeFormatter FORMATO_DATA_GERACAO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATO_DATA_PERIODO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RelatorioFiscalService service;
    private final ReportEngine reportEngine;

    @Operation(summary = "Notas emitidas por período", description = "Unifica NF-e/NFC-e/NFS-e/CT-e por dataEmissao, opcionalmente filtrado por empresa")
    @ApiResponse(responseCode = "200", description = "Documentos fiscais do período, ordenados por data de emissão")
    @GetMapping("/notas-periodo")
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public List<NotaFiscalPeriodoItemResponse> notasPorPeriodo(
            @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim, @RequestParam(required = false) Long empresaId) {
        return service.notasPorPeriodo(dataInicio, dataFim, empresaId);
    }

    @Operation(summary = "Exportar notas emitidas por período em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/notas-periodo/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public ResponseEntity<byte[]> notasPorPeriodoPdf(
            @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim, @RequestParam(required = false) Long empresaId) {
        return exportarNotasPorPeriodo(dataInicio, dataFim, empresaId, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar notas emitidas por período em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/notas-periodo/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public ResponseEntity<byte[]> notasPorPeriodoXlsx(
            @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim, @RequestParam(required = false) Long empresaId) {
        return exportarNotasPorPeriodo(dataInicio, dataFim, empresaId, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportarNotasPorPeriodo(LocalDate dataInicio, LocalDate dataFim, Long empresaId, ReportFormat formato) {
        List<NotaFiscalPeriodoItemResponse> dados = service.notasPorPeriodo(dataInicio, dataFim, empresaId);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_PERIODO", periodo(dataInicio, dataFim));
        parametros.put("P_FILTRO", "Empresa: " + (empresaId != null ? "#" + empresaId : "Todas"));
        parametros.put("P_DATA_GERACAO", dataGeracao());
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/notas-periodo.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "notas-periodo");
    }

    private String periodo(LocalDate dataInicio, LocalDate dataFim) {
        return dataInicio.format(FORMATO_DATA_PERIODO) + " a " + dataFim.format(FORMATO_DATA_PERIODO);
    }

    private String dataGeracao() {
        return LocalDateTime.now().format(FORMATO_DATA_GERACAO);
    }
}
