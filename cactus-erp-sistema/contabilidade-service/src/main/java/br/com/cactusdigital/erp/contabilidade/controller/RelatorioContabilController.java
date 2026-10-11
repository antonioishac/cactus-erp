package br.com.cactusdigital.erp.contabilidade.controller;

import br.com.cactusdigital.erp.common.report.ReportEngine;
import br.com.cactusdigital.erp.common.report.ReportFormat;
import br.com.cactusdigital.erp.common.report.ReportHttpSupport;
import br.com.cactusdigital.erp.contabilidade.dto.BalanceteItemResponse;
import br.com.cactusdigital.erp.contabilidade.dto.PlanoContaResponse;
import br.com.cactusdigital.erp.contabilidade.dto.RazaoItemResponse;
import br.com.cactusdigital.erp.contabilidade.service.PlanoContaService;
import br.com.cactusdigital.erp.contabilidade.service.RelatorioContabilService;
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

@Tag(name = "Relatórios Contábeis", description = "Balancete e Razão — calculados sob demanda a partir do Livro Diário, sem snapshot persistido")
@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
public class RelatorioContabilController {

    private static final DateTimeFormatter FORMATO_DATA_GERACAO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATO_DATA_PERIODO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RelatorioContabilService service;
    private final PlanoContaService planoContaService;
    private final ReportEngine reportEngine;

    @Operation(summary = "Balancete de verificação", description = "Uma linha por conta analítica com movimento no período — só entram contas com ao menos 1 partida lançada")
    @ApiResponse(responseCode = "200", description = "Linhas do balancete, ordenadas por código de conta")
    @GetMapping("/balancete")
    @PreAuthorize("hasAuthority('CONTABIL_CONSULTAR')")
    public List<BalanceteItemResponse> balancete(
            @RequestParam Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return service.balancete(empresaId, dataInicio, dataFim);
    }

    @Operation(summary = "Exportar balancete em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/balancete/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('CONTABIL_CONSULTAR')")
    public ResponseEntity<byte[]> balancetePdf(
            @RequestParam Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return exportarBalancete(empresaId, dataInicio, dataFim, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar balancete em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/balancete/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('CONTABIL_CONSULTAR')")
    public ResponseEntity<byte[]> balanceteXlsx(
            @RequestParam Long empresaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return exportarBalancete(empresaId, dataInicio, dataFim, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportarBalancete(Long empresaId, LocalDate dataInicio, LocalDate dataFim, ReportFormat formato) {
        List<BalanceteItemResponse> dados = service.balancete(empresaId, dataInicio, dataFim);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_PERIODO", periodo(dataInicio, dataFim));
        parametros.put("P_FILTRO", "Empresa: #" + empresaId);
        parametros.put("P_DATA_GERACAO", dataGeracao());
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/balancete.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "balancete");
    }

    @Operation(summary = "Livro Razão de uma conta", description = "Extrato cronológico das partidas da conta no período, com saldo corrido")
    @ApiResponse(responseCode = "200", description = "Linhas do razão, em ordem cronológica")
    @GetMapping("/razao")
    @PreAuthorize("hasAuthority('CONTABIL_CONSULTAR')")
    public List<RazaoItemResponse> razao(
            @RequestParam Long contaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return service.razao(contaId, dataInicio, dataFim);
    }

    @Operation(summary = "Exportar Livro Razão em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/razao/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('CONTABIL_CONSULTAR')")
    public ResponseEntity<byte[]> razaoPdf(
            @RequestParam Long contaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return exportarRazao(contaId, dataInicio, dataFim, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar Livro Razão em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/razao/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('CONTABIL_CONSULTAR')")
    public ResponseEntity<byte[]> razaoXlsx(
            @RequestParam Long contaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return exportarRazao(contaId, dataInicio, dataFim, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportarRazao(Long contaId, LocalDate dataInicio, LocalDate dataFim, ReportFormat formato) {
        List<RazaoItemResponse> dados = service.razao(contaId, dataInicio, dataFim);
        PlanoContaResponse conta = planoContaService.buscarPorId(contaId);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_PERIODO", periodo(dataInicio, dataFim));
        parametros.put("P_FILTRO", "Conta: " + conta.codigo() + " - " + conta.nome());
        parametros.put("P_DATA_GERACAO", dataGeracao());
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/razao.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "razao");
    }

    private String periodo(LocalDate dataInicio, LocalDate dataFim) {
        return dataInicio.format(FORMATO_DATA_PERIODO) + " a " + dataFim.format(FORMATO_DATA_PERIODO);
    }

    private String dataGeracao() {
        return LocalDateTime.now().format(FORMATO_DATA_GERACAO);
    }
}
