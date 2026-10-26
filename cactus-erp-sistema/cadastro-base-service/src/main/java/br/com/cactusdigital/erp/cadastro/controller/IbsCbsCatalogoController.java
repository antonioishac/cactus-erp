package br.com.cactusdigital.erp.cadastro.controller;

import br.com.cactusdigital.erp.cadastro.dto.AliquotaIbsCbsResponse;
import br.com.cactusdigital.erp.cadastro.dto.ClassificacaoTributariaIbsCbsResponse;
import br.com.cactusdigital.erp.cadastro.dto.CstIbsCbsResponse;
import br.com.cactusdigital.erp.cadastro.service.IbsCbsCatalogoService;
import br.com.cactusdigital.erp.common.rest.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Tributação — IBS/CBS (Reforma Tributária)",
        description = "Catálogos oficiais (tabela da SVRS) de CST e cClassTrib do IBS/CBS e alíquotas por ano (LC 214/2025). Só leitura")
@RestController
@RequestMapping("/api/v1/tributacao/ibs-cbs")
@RequiredArgsConstructor
public class IbsCbsCatalogoController {

    private final IbsCbsCatalogoService service;

    @Operation(summary = "Listar CST do IBS/CBS", description = "Tabela oficial completa, com os indicadores e se o ERP já emite cada CST")
    @ApiResponse(responseCode = "200", description = "CSTs")
    @GetMapping("/cst")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CONSULTAR')")
    public List<CstIbsCbsResponse> listarCst() {
        return service.listarCst();
    }

    @Operation(summary = "Listar classificações tributárias (cClassTrib)", description = "Filtro opcional por CST e busca no código/descrição. "
            + "motivoNaoSuportada preenchido = não pode ser usada numa configuração tributária ainda")
    @ApiResponse(responseCode = "200", description = "Página de classificações")
    @GetMapping("/classificacoes")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CONSULTAR')")
    public PageResponse<ClassificacaoTributariaIbsCbsResponse> listarClassificacoes(
            @RequestParam(required = false) String cst, @RequestParam(required = false) String busca, Pageable pageable) {
        return PageResponse.of(service.listarClassificacoes(cst, busca, pageable));
    }

    @Operation(summary = "Buscar classificação tributária (cClassTrib)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Classificação encontrada"),
            @ApiResponse(responseCode = "404", description = "Classificação inexistente", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/classificacoes/{codigo}")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CONSULTAR')")
    public ClassificacaoTributariaIbsCbsResponse buscarClassificacao(@PathVariable String codigo) {
        return service.buscarClassificacao(codigo);
    }

    @Operation(summary = "Alíquotas de IBS/CBS vigentes", description = "Alíquotas (%) do ano da data informada (default: hoje). "
            + "Usado pelo emissor de documento fiscal")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Alíquotas do ano"),
            @ApiResponse(responseCode = "422", description = "Ano sem alíquota definida em lei", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/aliquotas")
    @PreAuthorize("hasAuthority('CADASTRO_TRIBUTACAO_CONSULTAR')")
    public AliquotaIbsCbsResponse aliquotas(@RequestParam(required = false) LocalDate data) {
        return service.aliquotasVigentes(data != null ? data : LocalDate.now());
    }
}
