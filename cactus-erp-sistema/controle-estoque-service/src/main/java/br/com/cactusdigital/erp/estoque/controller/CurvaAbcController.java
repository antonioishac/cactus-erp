package br.com.cactusdigital.erp.estoque.controller;

import br.com.cactusdigital.erp.common.report.ReportEngine;
import br.com.cactusdigital.erp.common.report.ReportFormat;
import br.com.cactusdigital.erp.common.report.ReportHttpSupport;
import br.com.cactusdigital.erp.estoque.dto.CurvaAbcItemResponse;
import br.com.cactusdigital.erp.estoque.service.CurvaAbcService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Relatórios — Curva ABC", description = "Classificação de relevância/giro por valor movimentado — calculada sob demanda, não persistida")
@RestController
@RequestMapping("/api/v1/relatorios/curva-abc")
@RequiredArgsConstructor
public class CurvaAbcController {

    private static final String TEMPLATE = "reports/curva-abc.jrxml";
    private static final DateTimeFormatter FORMATO_DATA_GERACAO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final CurvaAbcService service;
    private final ReportEngine reportEngine;

    @Operation(summary = "Calcular curva ABC", description = "A = até 80% do valor acumulado, B = até 95%, C = o resto — janela de tempo configurável")
    @ApiResponse(responseCode = "200", description = "Produtos classificados, ordenados por valor movimentado decrescente")
    @GetMapping
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public List<CurvaAbcItemResponse> calcular(
            @RequestParam(required = false) Long depositoId,
            @Parameter(description = "Janela de dias pra trás a considerar") @RequestParam(defaultValue = "90") int dias) {
        return service.calcular(depositoId, dias);
    }

    @Operation(summary = "Exportar curva ABC em PDF", description = "Mesmo cálculo de GET /relatorios/curva-abc, formatado como documento PDF pra impressão/arquivamento")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public ResponseEntity<byte[]> exportarPdf(@RequestParam(required = false) Long depositoId, @RequestParam(defaultValue = "90") int dias) {
        return exportar(depositoId, dias, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar curva ABC em Excel", description = "Mesmo cálculo de GET /relatorios/curva-abc, formatado como planilha XLSX pra análise")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('ESTOQUE_CONSULTAR')")
    public ResponseEntity<byte[]> exportarXlsx(@RequestParam(required = false) Long depositoId, @RequestParam(defaultValue = "90") int dias) {
        return exportar(depositoId, dias, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportar(Long depositoId, int dias, ReportFormat formato) {
        List<CurvaAbcItemResponse> dados = service.calcular(depositoId, dias);
        Map<String, Object> parametros = Map.of(
                "P_PERIODO", "Últimos " + dias + " dias",
                "P_DEPOSITO", depositoId != null ? "Depósito #" + depositoId : "Todos",
                "P_DATA_GERACAO", LocalDateTime.now().format(FORMATO_DATA_GERACAO));
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), TEMPLATE, dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "curva-abc");
    }
}
