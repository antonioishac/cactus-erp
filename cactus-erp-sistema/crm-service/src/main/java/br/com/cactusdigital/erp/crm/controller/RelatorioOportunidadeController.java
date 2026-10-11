package br.com.cactusdigital.erp.crm.controller;

import br.com.cactusdigital.erp.common.report.ReportEngine;
import br.com.cactusdigital.erp.common.report.ReportFormat;
import br.com.cactusdigital.erp.common.report.ReportHttpSupport;
import br.com.cactusdigital.erp.crm.dto.OportunidadeResponse;
import br.com.cactusdigital.erp.crm.entity.SituacaoOportunidade;
import br.com.cactusdigital.erp.crm.service.RelatorioOportunidadeService;
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
 * Funil de oportunidades — relatório gerencial do CRM (ver ANÁLISE: MÓDULO DE
 * RELATÓRIOS no CLAUDE.md raiz). JSON (consulta/tela), PDF (impressão/arquivamento) e
 * XLSX (análise), calculados sob demanda a partir de {@link RelatorioOportunidadeService}.
 */
@Tag(name = "Relatórios — CRM", description = "Funil de oportunidades — calculado sob demanda, não persistido")
@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
public class RelatorioOportunidadeController {

    private static final DateTimeFormatter FORMATO_DATA_GERACAO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final RelatorioOportunidadeService service;
    private final ReportEngine reportEngine;

    @Operation(summary = "Funil de oportunidades", description = "Situação default ABERTA (o pipeline em si) — informe explicitamente pra ver histórico de GANHA/PERDIDA")
    @ApiResponse(responseCode = "200", description = "Oportunidades do funil, ordenadas por estágio")
    @GetMapping("/funil-oportunidades")
    @PreAuthorize("hasAuthority('CRM_CONSULTAR')")
    public List<OportunidadeResponse> funil(@RequestParam(required = false) SituacaoOportunidade situacao) {
        return service.funil(situacao);
    }

    @Operation(summary = "Exportar funil de oportunidades em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/funil-oportunidades/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('CRM_CONSULTAR')")
    public ResponseEntity<byte[]> funilPdf(@RequestParam(required = false) SituacaoOportunidade situacao) {
        return exportarFunil(situacao, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar funil de oportunidades em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/funil-oportunidades/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('CRM_CONSULTAR')")
    public ResponseEntity<byte[]> funilXlsx(@RequestParam(required = false) SituacaoOportunidade situacao) {
        return exportarFunil(situacao, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportarFunil(SituacaoOportunidade situacao, ReportFormat formato) {
        List<OportunidadeResponse> dados = service.funil(situacao);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_FILTRO", "Situação: " + (situacao != null ? situacao.name() : "ABERTA (padrão)"));
        parametros.put("P_DATA_GERACAO", dataGeracao());
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/funil-oportunidades.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "funil-oportunidades");
    }

    private String dataGeracao() {
        return LocalDateTime.now().format(FORMATO_DATA_GERACAO);
    }
}
