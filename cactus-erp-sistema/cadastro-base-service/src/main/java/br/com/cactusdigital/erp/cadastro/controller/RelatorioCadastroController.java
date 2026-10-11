package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.ClienteResponse;
import br.com.cactusdigital.erp.cadastro.dto.FornecedorResponse;
import br.com.cactusdigital.erp.cadastro.dto.ProdutoResponse;
import br.com.cactusdigital.erp.cadastro.entity.TipoPessoa;
import br.com.cactusdigital.erp.cadastro.service.RelatorioCadastroService;
import br.com.cactusdigital.erp.common.domain.Situacao;
import br.com.cactusdigital.erp.common.report.ReportEngine;
import br.com.cactusdigital.erp.common.report.ReportFormat;
import br.com.cactusdigital.erp.common.report.ReportHttpSupport;
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
 * Listagem cadastral de clientes/fornecedores/produtos — o relatório mais simples do
 * lote da Fase 17 (ver ANÁLISE: MÓDULO DE RELATÓRIOS no CLAUDE.md raiz). Cada listagem
 * tem 3 formas de saída: JSON (consulta/tela), PDF (impressão/arquivamento) e XLSX
 * (análise), reaproveitando os mesmos filtros já usados pelo {@code GET} paginado de
 * cada recurso (`ClienteController`/`FornecedorController`/`ProdutoController`).
 */
@Tag(name = "Relatórios — Cadastro", description = "Listagem cadastral de clientes/fornecedores/produtos — documento inteiro, sem paginação")
@RestController
@RequestMapping("/api/v1/relatorios")
@RequiredArgsConstructor
public class RelatorioCadastroController {

    private static final DateTimeFormatter FORMATO_DATA_GERACAO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final RelatorioCadastroService service;
    private final ReportEngine reportEngine;

    // -------------------------------------------------------------------------- Clientes

    @Operation(summary = "Listagem cadastral de clientes", description = "Mesmos filtros opcionais de GET /clientes, sem paginação")
    @ApiResponse(responseCode = "200", description = "Clientes encontrados")
    @GetMapping("/clientes-cadastro")
    @PreAuthorize("hasAuthority('CADASTRO_CLIENTE_CONSULTAR')")
    public List<ClienteResponse> clientes(
            @RequestParam(required = false) String nome, @RequestParam(required = false) String documento,
            @RequestParam(required = false) TipoPessoa tipoPessoa, @RequestParam(required = false) Situacao situacao) {
        return service.clientes(nome, documento, tipoPessoa, situacao);
    }

    @Operation(summary = "Exportar listagem cadastral de clientes em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/clientes-cadastro/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('CADASTRO_CLIENTE_CONSULTAR')")
    public ResponseEntity<byte[]> clientesPdf(
            @RequestParam(required = false) String nome, @RequestParam(required = false) String documento,
            @RequestParam(required = false) TipoPessoa tipoPessoa, @RequestParam(required = false) Situacao situacao) {
        return exportarClientes(nome, documento, tipoPessoa, situacao, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar listagem cadastral de clientes em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/clientes-cadastro/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('CADASTRO_CLIENTE_CONSULTAR')")
    public ResponseEntity<byte[]> clientesXlsx(
            @RequestParam(required = false) String nome, @RequestParam(required = false) String documento,
            @RequestParam(required = false) TipoPessoa tipoPessoa, @RequestParam(required = false) Situacao situacao) {
        return exportarClientes(nome, documento, tipoPessoa, situacao, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportarClientes(String nome, String documento, TipoPessoa tipoPessoa, Situacao situacao, ReportFormat formato) {
        List<ClienteResponse> dados = service.clientes(nome, documento, tipoPessoa, situacao);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_FILTRO", filtroPessoa(nome, documento, tipoPessoa, situacao));
        parametros.put("P_DATA_GERACAO", dataGeracao());
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/clientes-cadastro.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "clientes-cadastro");
    }

    // --------------------------------------------------------------------- Fornecedores

    @Operation(summary = "Listagem cadastral de fornecedores", description = "Mesmos filtros opcionais de GET /fornecedores, sem paginação")
    @ApiResponse(responseCode = "200", description = "Fornecedores encontrados")
    @GetMapping("/fornecedores-cadastro")
    @PreAuthorize("hasAuthority('CADASTRO_FORNECEDOR_CONSULTAR')")
    public List<FornecedorResponse> fornecedores(
            @RequestParam(required = false) String nome, @RequestParam(required = false) String documento,
            @RequestParam(required = false) TipoPessoa tipoPessoa, @RequestParam(required = false) Situacao situacao) {
        return service.fornecedores(nome, documento, tipoPessoa, situacao);
    }

    @Operation(summary = "Exportar listagem cadastral de fornecedores em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/fornecedores-cadastro/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('CADASTRO_FORNECEDOR_CONSULTAR')")
    public ResponseEntity<byte[]> fornecedoresPdf(
            @RequestParam(required = false) String nome, @RequestParam(required = false) String documento,
            @RequestParam(required = false) TipoPessoa tipoPessoa, @RequestParam(required = false) Situacao situacao) {
        return exportarFornecedores(nome, documento, tipoPessoa, situacao, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar listagem cadastral de fornecedores em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/fornecedores-cadastro/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('CADASTRO_FORNECEDOR_CONSULTAR')")
    public ResponseEntity<byte[]> fornecedoresXlsx(
            @RequestParam(required = false) String nome, @RequestParam(required = false) String documento,
            @RequestParam(required = false) TipoPessoa tipoPessoa, @RequestParam(required = false) Situacao situacao) {
        return exportarFornecedores(nome, documento, tipoPessoa, situacao, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportarFornecedores(String nome, String documento, TipoPessoa tipoPessoa, Situacao situacao, ReportFormat formato) {
        List<FornecedorResponse> dados = service.fornecedores(nome, documento, tipoPessoa, situacao);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_FILTRO", filtroPessoa(nome, documento, tipoPessoa, situacao));
        parametros.put("P_DATA_GERACAO", dataGeracao());
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/fornecedores-cadastro.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "fornecedores-cadastro");
    }

    // -------------------------------------------------------------------------- Produtos

    @Operation(summary = "Listagem cadastral de produtos", description = "Mesmos filtros opcionais de GET /produtos, sem paginação")
    @ApiResponse(responseCode = "200", description = "Produtos encontrados")
    @GetMapping("/produtos-cadastro")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CONSULTAR')")
    public List<ProdutoResponse> produtos(
            @RequestParam(required = false) String nome, @RequestParam(required = false) String codigoInterno,
            @RequestParam(required = false) String gtin, @RequestParam(required = false) Long produtoSubgrupoId,
            @RequestParam(required = false) Long produtoMarcaId, @RequestParam(required = false) Situacao situacao) {
        return service.produtos(nome, codigoInterno, gtin, produtoSubgrupoId, produtoMarcaId, situacao);
    }

    @Operation(summary = "Exportar listagem cadastral de produtos em PDF")
    @ApiResponse(responseCode = "200", description = "Arquivo PDF gerado")
    @GetMapping(value = "/produtos-cadastro/pdf", produces = "application/pdf")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CONSULTAR')")
    public ResponseEntity<byte[]> produtosPdf(
            @RequestParam(required = false) String nome, @RequestParam(required = false) String codigoInterno,
            @RequestParam(required = false) String gtin, @RequestParam(required = false) Long produtoSubgrupoId,
            @RequestParam(required = false) Long produtoMarcaId, @RequestParam(required = false) Situacao situacao) {
        return exportarProdutos(nome, codigoInterno, gtin, produtoSubgrupoId, produtoMarcaId, situacao, ReportFormat.PDF);
    }

    @Operation(summary = "Exportar listagem cadastral de produtos em Excel")
    @ApiResponse(responseCode = "200", description = "Arquivo XLSX gerado")
    @GetMapping(value = "/produtos-cadastro/xlsx", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @PreAuthorize("hasAuthority('CADASTRO_PRODUTO_CONSULTAR')")
    public ResponseEntity<byte[]> produtosXlsx(
            @RequestParam(required = false) String nome, @RequestParam(required = false) String codigoInterno,
            @RequestParam(required = false) String gtin, @RequestParam(required = false) Long produtoSubgrupoId,
            @RequestParam(required = false) Long produtoMarcaId, @RequestParam(required = false) Situacao situacao) {
        return exportarProdutos(nome, codigoInterno, gtin, produtoSubgrupoId, produtoMarcaId, situacao, ReportFormat.XLSX);
    }

    private ResponseEntity<byte[]> exportarProdutos(String nome, String codigoInterno, String gtin, Long produtoSubgrupoId,
            Long produtoMarcaId, Situacao situacao, ReportFormat formato) {
        List<ProdutoResponse> dados = service.produtos(nome, codigoInterno, gtin, produtoSubgrupoId, produtoMarcaId, situacao);
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("P_FILTRO", "Nome: " + valorOuTodos(nome) + "   |   Situação: " + (situacao != null ? situacao.name() : "Todas"));
        parametros.put("P_DATA_GERACAO", dataGeracao());
        byte[] conteudo = reportEngine.gerar(getClass().getClassLoader(), "reports/produtos-cadastro.jrxml", dados, parametros, formato);
        return ReportHttpSupport.download(conteudo, formato, "produtos-cadastro");
    }

    // -------------------------------------------------------------------------- helpers

    private String filtroPessoa(String nome, String documento, TipoPessoa tipoPessoa, Situacao situacao) {
        return "Nome: " + valorOuTodos(nome) + "   |   Documento: " + valorOuTodos(documento)
                + "   |   Tipo: " + (tipoPessoa != null ? tipoPessoa.name() : "Todos")
                + "   |   Situação: " + (situacao != null ? situacao.name() : "Todas");
    }

    private String valorOuTodos(String valor) {
        return valor != null && !valor.isBlank() ? valor : "Todos";
    }

    private String dataGeracao() {
        return LocalDateTime.now().format(FORMATO_DATA_GERACAO);
    }
}
