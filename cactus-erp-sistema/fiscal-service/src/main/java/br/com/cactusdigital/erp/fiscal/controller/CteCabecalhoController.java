package br.com.cactusdigital.erp.fiscal.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.fiscal.dto.CteCabecalhoResponse;
import br.com.cactusdigital.erp.fiscal.dto.GerarCteDeVendaRequest;
import br.com.cactusdigital.erp.fiscal.service.CteCabecalhoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CT-e (Conhecimento de Transporte Eletrônico)", description = "RASCUNHO (composto a partir do frete de uma Venda) → (emitir) → ENVIADA → AUTORIZADA/REJEITADA/ERRO, via ACBr Monitor — só modal Rodoviário, ver Javadoc de CteCabecalho")
@RestController
@RequestMapping("/api/v1/ctes")
@RequiredArgsConstructor
public class CteCabecalhoController {

    private final CteCabecalhoService service;

    @Operation(summary = "Listar CT-e")
    @ApiResponse(responseCode = "200", description = "Página de CT-e")
    @GetMapping
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public PageResponse<CteCabecalhoResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Buscar CT-e por id", description = "Inclui os componentes de valor do frete")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CT-e encontrado"),
            @ApiResponse(responseCode = "404", description = "CT-e não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public CteCabecalhoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Gerar CT-e a partir do frete de uma venda",
            description = "A venda precisa estar CONFIRMADA e ter VendaFrete definido com transportadora (POST /vendas/{id}/frete no vendas-service). tomador só suporta REMETENTE ou DESTINATARIO nesta fase")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "CT-e composto em RASCUNHO"),
            @ApiResponse(responseCode = "404", description = "Venda, cliente, transportadora ou empresa não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Esta venda já tem CT-e gerado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Venda não CONFIRMADA, sem frete, frete sem transportadora, ou tomador não suportado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "502", description = "Falha ao consultar vendas-service ou cadastro-base-service", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/de-venda/{vendaId}")
    @PreAuthorize("hasAuthority('FISCAL_EMITIR')")
    public ResponseEntity<CteCabecalhoResponse> gerarDeVenda(@PathVariable Long vendaId, @Valid @RequestBody GerarCteDeVendaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.gerarDeVenda(vendaId, request));
    }

    @Operation(summary = "Emitir o CT-e via ACBr Monitor",
            description = "Monta o INI (modelo 57, modal Rodoviário) e envia CTE.CriarEnviarCTe ao ACBr Monitor — sucesso vira AUTORIZADA, rejeição fiscal vira REJEITADA, falha de comunicação vira ERRO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retorno do ACBr Monitor processado (AUTORIZADA/REJEITADA/ERRO)"),
            @ApiResponse(responseCode = "404", description = "CT-e não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "CT-e não está RASCUNHO/ERRO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/emitir")
    @PreAuthorize("hasAuthority('FISCAL_EMITIR')")
    public CteCabecalhoResponse emitir(@PathVariable Long id) {
        return service.emitir(id);
    }
}
