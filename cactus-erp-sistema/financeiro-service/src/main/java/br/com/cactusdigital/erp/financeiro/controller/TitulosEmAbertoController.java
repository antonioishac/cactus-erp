package br.com.cactusdigital.erp.financeiro.controller;

import br.com.cactusdigital.erp.common.report.ReportEngine;
import br.com.cactusdigital.erp.common.report.ReportFormat;
import br.com.cactusdigital.erp.common.report.ReportHttpSupport;
import br.com.cactusdigital.erp.financeiro.dto.TipoTituloRelatorio;
import br.com.cactusdigital.erp.financeiro.dto.TituloEmAbertoResponse;
import br.com.cactusdigital.erp.financeiro.service.TitulosEmAbertoService;
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

/**
 * Relatório de títulos em aberto (Contas a Receber ou Contas a Pagar) por vencimento —
 * JSON (consulta/tela), PDF e XLSX, todos sob demanda a partir de
 * {@link TitulosEmAbertoService}. Filtro de vencimento opcional: omitido, lista tudo o que
 * está em aberto (posição completa da carteira).
 */
@Tag(name = "Relatórios Financeiros — Títulos em Aberto", description = "Contas a Receber/Pagar ABERTA/PARCIAL por vencimento, com atraso/multa/juros calculados")
@RestController
@RequestMapping("/api/v1/relatorios/titulos-em-aberto")
@RequiredArgsConstructor
public class TitulosEmAbertoController {

    private static final DateTimeFormatter FORMATO_DATA_GERACAO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATO_DATA_PERIODO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final TitulosEmAbertoService service;
    private final ReportEngine reportEngine;

    @Operation(summary = "Títulos em aberto", description = "tipo=RECEBER (pessoaId = cliente) ou PAGAR (pessoaId = fornecedor); ordenado por vencimento")
    @ApiResponse(responseCode = "200", description = "Títulos ABERTA/PARCIAL, ordenados por vencimento")
    @GetMapping
    @PreAuthorize("hasAuthority('FINANCEIRO_CONSULTAR')")
    public List<TituloEmAbertoResponse> listar(
            @RequestParam TipoTituloRelatorio tipo,
            @RequestParam Long empresaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Long pessoaId) {
        return service.listar(tipo, empresaId, dataInicio, dataFim, pessoaId);
    }

    @Operation(summary = "Exportar títulos em aberto em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('FINANCEIRO_CONSULTAR')")
    public ResponseEntity<byte[]> pdf(
            @RequestParam TipoTituloRelatorio tipo,
            @RequestParam Long empresaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Long pessoaId) {
        return exportar(tipo, empresaId, dataInicio, dataFim, pessoaId, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar títulos em aberto em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('FINANCEIRO_CONSULTAR')")
    public ResponseEntity<byte[]> xlsx(
            @RequestParam TipoTituloRelatorio tipo,
            @RequestParam Long empresaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Long pessoaId) {
        return exportar(tipo, empresaId, dataInicio, dataFim, pessoaId, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportar(TipoTituloRelatorio tipo, Long empresaId, LocalDate dataInicio,
            LocalDate dataFim, Long pessoaId, ReportFormat formato) {
        List<TituloEmAbertoResponse> dados = service.listar(tipo, empresaId, dataInicio, dataFim, pessoaId);
        boolean receber = tipo == TipoTituloRelatorio.RECEBER;
        String rotuloPessoa = receber ? "Cliente" : "Fornecedor";
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_TITULO", receber ? "Contas a Receber em Aberto" : "Contas a Pagar em Aberto");
        parametros.put("P_ROTULO_PESSOA", rotuloPessoa);
        parametros.put("P_PERIODO", periodo(dataInicio, dataFim));
        parametros.put("P_FILTRO", "Empresa: #" + empresaId + "   |   " + rotuloPessoa + ": "
                + (pessoaId != null ? "#" + pessoaId : "Todos"));
        parametros.put("P_DATA_GERACAO", LocalDateTime.now().format(FORMATO_DATA_GERACAO));
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/titulos-em-aberto.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, receber ? "contas-receber-em-aberto" : "contas-pagar-em-aberto");
    }

    private String periodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null && dataFim == null) {
            return "Todos";
        }
        return (dataInicio != null ? dataInicio.format(FORMATO_DATA_PERIODO) : "...")
                + " a " + (dataFim != null ? dataFim.format(FORMATO_DATA_PERIODO) : "...");
    }
}
