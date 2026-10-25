package br.com.cactusdigital.erp.vendas.controller;

import br.com.cactusdigital.erp.common.report.ReportEngine;
import br.com.cactusdigital.erp.common.report.ReportFormat;
import br.com.cactusdigital.erp.common.report.ReportHttpSupport;
import br.com.cactusdigital.erp.vendas.dto.CanalVenda;
import br.com.cactusdigital.erp.vendas.dto.ComissaoVendedorPeriodoResponse;
import br.com.cactusdigital.erp.vendas.dto.ProdutoMaisVendidoResponse;
import br.com.cactusdigital.erp.vendas.dto.VendaPeriodoItemResponse;
import br.com.cactusdigital.erp.vendas.service.RelatorioVendasService;
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
 * Relatórios gerenciais de vendas — fecha o maior gap do módulo (nenhum relatório
 * existia antes, ver ANÁLISE: MÓDULO DE RELATÓRIOS no CLAUDE.md raiz). Cada relatório
 * tem 3 formas de saída: JSON (consulta/tela), PDF (impressão/arquivamento) e XLSX
 * (análise) — todos calculados sob demanda a partir de {@link RelatorioVendasService},
 * nunca persistidos.
 */
@Tag(name = "Relatórios — Vendas", description = "Vendas por período, produtos mais vendidos e comissões por vendedor — calculados sob demanda, não persistidos")
@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
public class RelatorioVendasController {

    private static final DateTimeFormatter FORMATO_DATA_GERACAO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATO_DATA_PERIODO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RelatorioVendasService service;
    private final ReportEngine reportEngine;

    // ---------------------------------------------------------------- Vendas por período

    @Operation(summary = "Vendas por período", description = "Só vendas CONFIRMADA, filtro opcional por vendedor/cliente")
    @ApiResponse(responseCode = "200", description = "Vendas do período, ordenadas por data")
    @GetMapping("/vendas-periodo")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public List<VendaPeriodoItemResponse> vendasPorPeriodo(
            @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim,
            @RequestParam(required = false) Long vendedorId, @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) CanalVenda canal) {
        return service.vendasPorPeriodo(dataInicio, dataFim, vendedorId, clienteId, canal);
    }

    @Operation(summary = "Exportar vendas por período em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/vendas-periodo/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public ResponseEntity<byte[]> vendasPorPeriodoPdf(
            @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim,
            @RequestParam(required = false) Long vendedorId, @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) CanalVenda canal) {
        return exportarVendasPeriodo(dataInicio, dataFim, vendedorId, clienteId, canal, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar vendas por período em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/vendas-periodo/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public ResponseEntity<byte[]> vendasPorPeriodoXlsx(
            @RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim,
            @RequestParam(required = false) Long vendedorId, @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) CanalVenda canal) {
        return exportarVendasPeriodo(dataInicio, dataFim, vendedorId, clienteId, canal, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportarVendasPeriodo(LocalDate dataInicio, LocalDate dataFim, Long vendedorId, Long clienteId,
            CanalVenda canal, ReportFormat formato) {
        List<VendaPeriodoItemResponse> dados = service.vendasPorPeriodo(dataInicio, dataFim, vendedorId, clienteId, canal);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_PERIODO", periodo(dataInicio, dataFim));
        parametros.put("P_FILTRO", "Canal: " + (canal != null ? canal.name() : "Todos") + "   |   Vendedor: " + filtroOuTodos(vendedorId)
                + "   |   Cliente: " + filtroOuTodos(clienteId));
        parametros.put("P_DATA_GERACAO", dataGeracao());
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/vendas-periodo.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "vendas-periodo");
    }

    // ---------------------------------------------------------- Produtos mais vendidos

    @Operation(summary = "Produtos mais vendidos no período", description = "Agregado por produtoId, ordenado por quantidade vendida decrescente")
    @ApiResponse(responseCode = "200", description = "Produtos do período, ordenados por quantidade vendida")
    @GetMapping("/produtos-mais-vendidos")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public List<ProdutoMaisVendidoResponse> produtosMaisVendidos(@RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim,
            @RequestParam(required = false) CanalVenda canal) {
        return service.produtosMaisVendidos(dataInicio, dataFim, canal);
    }

    @Operation(summary = "Exportar produtos mais vendidos em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/produtos-mais-vendidos/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public ResponseEntity<byte[]> produtosMaisVendidosPdf(@RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim,
            @RequestParam(required = false) CanalVenda canal) {
        return exportarProdutosMaisVendidos(dataInicio, dataFim, canal, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar produtos mais vendidos em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/produtos-mais-vendidos/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public ResponseEntity<byte[]> produtosMaisVendidosXlsx(@RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim,
            @RequestParam(required = false) CanalVenda canal) {
        return exportarProdutosMaisVendidos(dataInicio, dataFim, canal, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportarProdutosMaisVendidos(LocalDate dataInicio, LocalDate dataFim, CanalVenda canal, ReportFormat formato) {
        List<ProdutoMaisVendidoResponse> dados = service.produtosMaisVendidos(dataInicio, dataFim, canal);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_PERIODO", periodo(dataInicio, dataFim));
        parametros.put("P_DATA_GERACAO", dataGeracao());
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/produtos-mais-vendidos.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "produtos-mais-vendidos");
    }

    // ------------------------------------------------------------ Comissões por vendedor

    @Operation(summary = "Comissões por vendedor no período", description = "Agrupado por dataLancamento da comissão, não pela data da venda")
    @ApiResponse(responseCode = "200", description = "Comissões do período, ordenadas por valor decrescente")
    @GetMapping("/comissoes-vendedor")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public List<ComissaoVendedorPeriodoResponse> comissoesPorVendedor(@RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim) {
        return service.comissoesPorVendedor(dataInicio, dataFim);
    }

    @Operation(summary = "Exportar comissões por vendedor em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/comissoes-vendedor/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public ResponseEntity<byte[]> comissoesPorVendedorPdf(@RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim) {
        return exportarComissoesPorVendedor(dataInicio, dataFim, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar comissões por vendedor em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/comissoes-vendedor/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('VENDAS_CONSULTAR')")
    public ResponseEntity<byte[]> comissoesPorVendedorXlsx(@RequestParam LocalDate dataInicio, @RequestParam LocalDate dataFim) {
        return exportarComissoesPorVendedor(dataInicio, dataFim, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportarComissoesPorVendedor(LocalDate dataInicio, LocalDate dataFim, ReportFormat formato) {
        List<ComissaoVendedorPeriodoResponse> dados = service.comissoesPorVendedor(dataInicio, dataFim);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_PERIODO", periodo(dataInicio, dataFim));
        parametros.put("P_DATA_GERACAO", dataGeracao());
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/comissoes-vendedor.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "comissoes-vendedor");
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
