package br.com.cactusdigital.erp.bi.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.common.report.ReportEngine;
import br.com.cactusdigital.erp.common.report.ReportFormat;
import br.com.cactusdigital.erp.common.report.ReportHttpSupport;
import br.com.cactusdigital.erp.bi.dto.CanalVenda;
import br.com.cactusdigital.erp.bi.dto.ComprasFornecedorIndicadorResponse;
import br.com.cactusdigital.erp.bi.dto.FaturamentoIndicadorResponse;
import br.com.cactusdigital.erp.bi.dto.CurvaAbcIndicadorResponse;
import br.com.cactusdigital.erp.bi.dto.EstoqueValorIndicadorResponse;
import br.com.cactusdigital.erp.bi.service.IndicadoresService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Schema de leitura do BI — snapshots gerados pelo ETL, não calculados em tempo real.
 * Cada indicador tem 3 formas de saída: JSON paginado (tela), PDF (impressão/arquivamento)
 * e XLSX (análise) — PDF/XLSX usam a variante não paginada do mesmo cálculo em
 * {@link IndicadoresService} (o documento exportado é o snapshot inteiro, não uma página).
 */
@Tag(name = "Indicadores", description = "Schema de leitura do BI — snapshots gerados pelo ETL, não calculados em tempo real")
@RestController
@RequestMapping("/api/v1/indicadores")
@RequiredArgsConstructor
public class IndicadoresController {

    private static final DateTimeFormatter FORMATO_DATA_GERACAO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATO_DATA_REFERENCIA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final IndicadoresService service;
    private final ReportEngine reportEngine;

    // ------------------------------------------------------------------- Valor de estoque

    @Operation(summary = "Valor de estoque por produto×depósito", description = "Snapshot da data de referência informada (default: hoje) — gerado a partir de saldo_estoque no último ETL")
    @ApiResponse(responseCode = "200", description = "Página de indicadores de valor de estoque")
    @GetMapping("/estoque-valor")
    @PreAuthorize("hasAuthority('BI_CONSULTAR')")
    public PageResponse<EstoqueValorIndicadorResponse> estoqueValor(
            @RequestParam(required = false) LocalDate dataReferencia, @RequestParam(required = false) Long depositoId, Pageable pageable) {
        return PageResponse.of(service.estoqueValor(dataReferencia != null ? dataReferencia : LocalDate.now(), depositoId, pageable));
    }

    @Operation(summary = "Exportar valor de estoque em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/estoque-valor/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('BI_CONSULTAR')")
    public ResponseEntity<byte[]> estoqueValorPdf(@RequestParam(required = false) LocalDate dataReferencia, @RequestParam(required = false) Long depositoId) {
        return exportarEstoqueValor(dataReferencia, depositoId, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar valor de estoque em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/estoque-valor/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('BI_CONSULTAR')")
    public ResponseEntity<byte[]> estoqueValorXlsx(@RequestParam(required = false) LocalDate dataReferencia, @RequestParam(required = false) Long depositoId) {
        return exportarEstoqueValor(dataReferencia, depositoId, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportarEstoqueValor(LocalDate dataReferencia, Long depositoId, ReportFormat formato) {
        LocalDate referencia = dataReferencia != null ? dataReferencia : LocalDate.now();
        List<EstoqueValorIndicadorResponse> dados = service.estoqueValor(referencia, depositoId);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_DATA_REFERENCIA", referencia.format(FORMATO_DATA_REFERENCIA));
        parametros.put("P_DEPOSITO", filtroDeposito(depositoId));
        parametros.put("P_DATA_GERACAO", dataGeracao());
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/estoque-valor.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "estoque-valor");
    }

    // ------------------------------------------------------------------------- Curva ABC

    @Operation(summary = "Curva ABC histórica", description = "Snapshot persistido por data de referência — o controle-estoque-service só calcula sob demanda, aqui dá pra comparar ao longo do tempo")
    @ApiResponse(responseCode = "200", description = "Página de itens classificados")
    @GetMapping("/curva-abc")
    @PreAuthorize("hasAuthority('BI_CONSULTAR')")
    public PageResponse<CurvaAbcIndicadorResponse> curvaAbc(
            @RequestParam(required = false) LocalDate dataReferencia, @RequestParam(required = false) Long depositoId, Pageable pageable) {
        return PageResponse.of(service.curvaAbc(dataReferencia != null ? dataReferencia : LocalDate.now(), depositoId, pageable));
    }

    @Operation(summary = "Exportar curva ABC histórica em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/curva-abc/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('BI_CONSULTAR')")
    public ResponseEntity<byte[]> curvaAbcPdf(@RequestParam(required = false) LocalDate dataReferencia, @RequestParam(required = false) Long depositoId) {
        return exportarCurvaAbc(dataReferencia, depositoId, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar curva ABC histórica em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/curva-abc/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('BI_CONSULTAR')")
    public ResponseEntity<byte[]> curvaAbcXlsx(@RequestParam(required = false) LocalDate dataReferencia, @RequestParam(required = false) Long depositoId) {
        return exportarCurvaAbc(dataReferencia, depositoId, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportarCurvaAbc(LocalDate dataReferencia, Long depositoId, ReportFormat formato) {
        LocalDate referencia = dataReferencia != null ? dataReferencia : LocalDate.now();
        List<CurvaAbcIndicadorResponse> dados = service.curvaAbc(referencia, depositoId);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_DATA_REFERENCIA", referencia.format(FORMATO_DATA_REFERENCIA));
        parametros.put("P_DEPOSITO", filtroDeposito(depositoId));
        parametros.put("P_DATA_GERACAO", dataGeracao());
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/curva-abc-historico.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "curva-abc-historico");
    }

    // ---------------------------------------------------------------- Compras por fornecedor

    @Operation(summary = "Resumo de compras por fornecedor", description = "Agregado mensal — anoMes no formato YYYY-MM, default: mês atual")
    @ApiResponse(responseCode = "200", description = "Página de resumos por fornecedor")
    @GetMapping("/compras-fornecedor")
    @PreAuthorize("hasAuthority('BI_CONSULTAR')")
    public PageResponse<ComprasFornecedorIndicadorResponse> comprasFornecedor(
            @RequestParam(required = false) String anoMes, Pageable pageable) {
        String periodo = anoMes != null ? anoMes : YearMonth.now().toString();
        return PageResponse.of(service.comprasFornecedor(periodo, pageable));
    }

    @Operation(summary = "Exportar compras por fornecedor em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/compras-fornecedor/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('BI_CONSULTAR')")
    public ResponseEntity<byte[]> comprasFornecedorPdf(@RequestParam(required = false) String anoMes) {
        return exportarComprasFornecedor(anoMes, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar compras por fornecedor em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/compras-fornecedor/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('BI_CONSULTAR')")
    public ResponseEntity<byte[]> comprasFornecedorXlsx(@RequestParam(required = false) String anoMes) {
        return exportarComprasFornecedor(anoMes, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportarComprasFornecedor(String anoMes, ReportFormat formato) {
        String periodo = anoMes != null ? anoMes : YearMonth.now().toString();
        List<ComprasFornecedorIndicadorResponse> dados = service.comprasFornecedor(periodo);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_ANO_MES", periodo);
        parametros.put("P_DATA_GERACAO", dataGeracao());
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/compras-fornecedor.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "compras-fornecedor");
    }

    // ----------------------------------------------------------------------- Faturamento

    @Operation(summary = "Faturamento e ticket médio", description = "Série diária por empresa × canal (B2B + PDV) e totais do período. "
            + "Ticket médio = valor líquido / quantidade de vendas. Default: mês atual até hoje. Canal vazio = os dois")
    @ApiResponse(responseCode = "200", description = "Totais e série diária")
    @GetMapping("/faturamento")
    @PreAuthorize("hasAuthority('BI_CONSULTAR')")
    public FaturamentoIndicadorResponse faturamento(
            @RequestParam(required = false) LocalDate dataInicio, @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) Long empresaId, @RequestParam(required = false) CanalVenda canal) {
        return service.faturamento(inicioOuPadrao(dataInicio), fimOuPadrao(dataFim), empresaId, canal);
    }

    @Operation(summary = "Exportar faturamento em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/faturamento/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('BI_CONSULTAR')")
    public ResponseEntity<byte[]> faturamentoPdf(
            @RequestParam(required = false) LocalDate dataInicio, @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) Long empresaId, @RequestParam(required = false) CanalVenda canal) {
        return exportarFaturamento(dataInicio, dataFim, empresaId, canal, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar faturamento em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/faturamento/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('BI_CONSULTAR')")
    public ResponseEntity<byte[]> faturamentoXlsx(
            @RequestParam(required = false) LocalDate dataInicio, @RequestParam(required = false) LocalDate dataFim,
            @RequestParam(required = false) Long empresaId, @RequestParam(required = false) CanalVenda canal) {
        return exportarFaturamento(dataInicio, dataFim, empresaId, canal, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportarFaturamento(LocalDate dataInicio, LocalDate dataFim, Long empresaId, CanalVenda canal, ReportFormat formato) {
        LocalDate inicio = inicioOuPadrao(dataInicio);
        LocalDate fim = fimOuPadrao(dataFim);
        FaturamentoIndicadorResponse resultado = service.faturamento(inicio, fim, empresaId, canal);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_FILTRO", "Período: " + inicio.format(FORMATO_DATA_REFERENCIA) + " a " + fim.format(FORMATO_DATA_REFERENCIA)
                + " | Empresa: " + (empresaId != null ? "#" + empresaId : "Todas")
                + " | Canal: " + (canal != null ? canal.name() : "Todos"));
        parametros.put("P_DATA_GERACAO", dataGeracao());
        parametros.put("P_TICKET_MEDIO", resultado.ticketMedio());
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/faturamento.jrxml", resultado.dias(), parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "faturamento");
    }

    private LocalDate inicioOuPadrao(LocalDate dataInicio) {
        return dataInicio != null ? dataInicio : LocalDate.now().withDayOfMonth(1);
    }

    private LocalDate fimOuPadrao(LocalDate dataFim) {
        return dataFim != null ? dataFim : LocalDate.now();
    }

    // -------------------------------------------------------------------------- helpers

    private String filtroDeposito(Long depositoId) {
        return depositoId != null ? "#" + depositoId : "Todos";
    }

    private String dataGeracao() {
        return LocalDateTime.now().format(FORMATO_DATA_GERACAO);
    }
}
