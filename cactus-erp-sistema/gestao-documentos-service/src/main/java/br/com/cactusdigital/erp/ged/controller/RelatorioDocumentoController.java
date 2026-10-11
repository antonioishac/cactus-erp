package br.com.cactusdigital.erp.ged.controller;

import br.com.cactusdigital.erp.common.domain.Situacao;
import br.com.cactusdigital.erp.common.report.ReportEngine;
import br.com.cactusdigital.erp.common.report.ReportFormat;
import br.com.cactusdigital.erp.common.report.ReportHttpSupport;
import br.com.cactusdigital.erp.ged.dto.DocumentoResponse;
import br.com.cactusdigital.erp.ged.service.RelatorioDocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * Documentos por pasta/tipo — relatório gerencial do GED (ver ANÁLISE: MÓDULO DE
 * RELATÓRIOS no CLAUDE.md raiz). JSON (consulta/tela), PDF (impressão/arquivamento) e
 * XLSX (análise), calculados sob demanda a partir de {@link RelatorioDocumentoService}.
 */
@Tag(name = "Relatórios — GED", description = "Documentos por pasta/tipo — calculado sob demanda, não persistido")
@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
public class RelatorioDocumentoController {

    private static final DateTimeFormatter FORMATO_DATA_GERACAO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final RelatorioDocumentoService service;
    private final ReportEngine reportEngine;

    @Operation(summary = "Documentos por pasta/tipo", description = "Filtro opcional por pasta, tipo de documento e situação")
    @ApiResponse(responseCode = "200", description = "Documentos encontrados")
    @GetMapping("/documentos-cadastro")
    @PreAuthorize("hasAuthority('GED_CONSULTAR')")
    public List<DocumentoResponse> documentos(
            @RequestParam(required = false) Long pastaId, @RequestParam(required = false) Long tipoDocumentoId,
            @RequestParam(required = false) Situacao situacao) {
        return service.documentos(pastaId, tipoDocumentoId, situacao);
    }

    @Operation(summary = "Exportar documentos por pasta/tipo em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/documentos-cadastro/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('GED_CONSULTAR')")
    public ResponseEntity<byte[]> documentosPdf(
            @RequestParam(required = false) Long pastaId, @RequestParam(required = false) Long tipoDocumentoId,
            @RequestParam(required = false) Situacao situacao) {
        return exportarDocumentos(pastaId, tipoDocumentoId, situacao, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar documentos por pasta/tipo em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/documentos-cadastro/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('GED_CONSULTAR')")
    public ResponseEntity<byte[]> documentosXlsx(
            @RequestParam(required = false) Long pastaId, @RequestParam(required = false) Long tipoDocumentoId,
            @RequestParam(required = false) Situacao situacao) {
        return exportarDocumentos(pastaId, tipoDocumentoId, situacao, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportarDocumentos(Long pastaId, Long tipoDocumentoId, Situacao situacao, ReportFormat formato) {
        List<DocumentoResponse> dados = service.documentos(pastaId, tipoDocumentoId, situacao);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_FILTRO", "Pasta: " + filtroOuTodos(pastaId) + "   |   Tipo: " + filtroOuTodos(tipoDocumentoId)
                + "   |   Situação: " + (situacao != null ? situacao.name() : "Todas"));
        parametros.put("P_DATA_GERACAO", dataGeracao());
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/documentos-cadastro.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "documentos-cadastro");
    }

    private String filtroOuTodos(Long id) {
        return id != null ? "#" + id : "Todos";
    }

    private String dataGeracao() {
        return LocalDateTime.now().format(FORMATO_DATA_GERACAO);
    }
}
