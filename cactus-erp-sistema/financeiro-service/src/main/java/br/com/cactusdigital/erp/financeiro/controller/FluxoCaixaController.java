package br.com.cactusdigital.erp.financeiro.controller;

import br.com.cactusdigital.erp.common.report.ReportEngine;
import br.com.cactusdigital.erp.common.report.ReportFormat;
import br.com.cactusdigital.erp.common.report.ReportHttpSupport;
import br.com.cactusdigital.erp.financeiro.dto.AgrupamentoFluxoCaixa;
import br.com.cactusdigital.erp.financeiro.dto.FluxoCaixaPeriodoResponse;
import br.com.cactusdigital.erp.financeiro.service.FluxoCaixaService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Relatórios Financeiros", description = "Fluxo de caixa projetado (Contas a Receber/Contas a Pagar ainda em aberto)")
@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
public class FluxoCaixaController {

    private static final DateTimeFormatter FORMATO_DATA_GERACAO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATO_DATA_PERIODO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final FluxoCaixaService service;
    private final ReportEngine reportEngine;

    @Operation(summary = "Fluxo de caixa projetado", description = "Soma valorSaldo de títulos ABERTA/PARCIAL com dataVencimento no intervalo, agrupado por dia ou mês — não é fluxo realizado (não considera baixas já pagas)")
    @ApiResponse(responseCode = "200", description = "Períodos do fluxo de caixa, em ordem cronológica, com saldo acumulado")
    @GetMapping("/fluxo-caixa")
    @PreAuthorize("hasAuthority('FINANCEIRO_CONSULTAR')")
    public List<FluxoCaixaPeriodoResponse> fluxoCaixa(
            @RequestParam Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(defaultValue = "MES") AgrupamentoFluxoCaixa agrupamento) {
        return service.calcular(empresaId, dataInicio, dataFim, agrupamento);
    }

    @Operation(summary = "Exportar fluxo de caixa projetado em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/fluxo-caixa/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('FINANCEIRO_CONSULTAR')")
    public ResponseEntity<byte[]> fluxoCaixaPdf(
            @RequestParam Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(defaultValue = "MES") AgrupamentoFluxoCaixa agrupamento) {
        return exportar(empresaId, dataInicio, dataFim, agrupamento, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar fluxo de caixa projetado em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/fluxo-caixa/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('FINANCEIRO_CONSULTAR')")
    public ResponseEntity<byte[]> fluxoCaixaXlsx(
            @RequestParam Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(defaultValue = "MES") AgrupamentoFluxoCaixa agrupamento) {
        return exportar(empresaId, dataInicio, dataFim, agrupamento, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportar(Long empresaId, LocalDate dataInicio, LocalDate dataFim,
            AgrupamentoFluxoCaixa agrupamento, ReportFormat formato) {
        List<FluxoCaixaPeriodoResponse> dados = service.calcular(empresaId, dataInicio, dataFim, agrupamento);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_PERIODO", dataInicio.format(FORMATO_DATA_PERIODO) + " a " + dataFim.format(FORMATO_DATA_PERIODO));
        parametros.put("P_FILTRO", "Empresa: #" + empresaId + "   |   Agrupamento: "
                + (agrupamento == AgrupamentoFluxoCaixa.MES ? "Mensal" : "Diário"));
        parametros.put("P_PADRAO_PERIODO", agrupamento == AgrupamentoFluxoCaixa.MES ? "MM/yyyy" : "dd/MM/yyyy");
        parametros.put("P_DATA_GERACAO", LocalDateTime.now().format(FORMATO_DATA_GERACAO));
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/fluxo-caixa.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "fluxo-caixa");
    }
}
