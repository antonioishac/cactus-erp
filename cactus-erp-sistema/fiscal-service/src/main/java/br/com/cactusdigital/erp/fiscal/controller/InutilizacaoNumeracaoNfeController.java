package br.com.cactusdigital.erp.fiscal.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.fiscal.dto.InutilizacaoNumeracaoNfeResponse;
import br.com.cactusdigital.erp.fiscal.dto.InutilizarNumeracaoNfeRequest;
import br.com.cactusdigital.erp.fiscal.service.InutilizacaoNumeracaoNfeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Inutilização de Numeração NF-e", description = "Regularização junto à SEFAZ de números de NF-e reservados que nunca viraram documento")
@RestController
@RequestMapping("/api/v1/inutilizacoes-nfe")
@RequiredArgsConstructor
public class InutilizacaoNumeracaoNfeController {

    private final InutilizacaoNumeracaoNfeService service;

    @Operation(summary = "Listar pedidos de inutilização da empresa", description = "Homologados e rejeitados — trilha de auditoria")
    @GetMapping
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public PageResponse<InutilizacaoNumeracaoNfeResponse> listar(@RequestParam Long empresaId, Pageable pageable) {
        return PageResponse.of(service.listar(empresaId, pageable));
    }

    @Operation(summary = "Buscar pedido de inutilização")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public InutilizacaoNumeracaoNfeResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Inutilizar faixa de numeração na SEFAZ",
            description = "Só números já reservados e que nunca viraram NF-e no ERP. Homologada (cStat 102) ou rejeitada, o pedido fica registrado (201). "
                    + "Homologada, regulariza os números pulados da faixa no cadastro-base-service. Em AC, BA e SP (Ajuste SINIEF 40/2026, desde 01/11/2026) "
                    + "nada vai à SEFAZ: o salto fica registrado como DISPENSADA e os números pulados são regularizados com essa referência.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido processado pela SEFAZ (HOMOLOGADA ou REJEITADA)"),
            @ApiResponse(responseCode = "409", description = "Faixa contém número de NF-e do ERP ou já inutilizado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Faixa inválida, número ainda não reservado, certificado/ambiente não configurado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "502", description = "Falha de comunicação com a SEFAZ ou o cadastro-base-service — nada registrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    @PreAuthorize("hasAuthority('FISCAL_GERENCIAR')")
    public ResponseEntity<InutilizacaoNumeracaoNfeResponse> inutilizar(@Valid @RequestBody InutilizarNumeracaoNfeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.inutilizar(request));
    }

    @Operation(summary = "Baixar o XML de retorno da SEFAZ (retInutNFe)")
    @GetMapping(value = "/{id}/xml", produces = MediaType.APPLICATION_XML_VALUE)
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public ResponseEntity<String> baixarXml(@PathVariable Long id) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"inutilizacao-nfe-" + id + ".xml\"")
                .body(service.buscarXml(id));
    }
}
