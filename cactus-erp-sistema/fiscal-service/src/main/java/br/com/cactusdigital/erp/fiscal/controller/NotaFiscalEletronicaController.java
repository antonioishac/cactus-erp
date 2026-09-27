package br.com.cactusdigital.erp.fiscal.controller;

import br.com.cactusdigital.erp.common.rest.PageResponse;
import br.com.cactusdigital.erp.fiscal.dto.NotaFiscalEletronicaResponse;
import br.com.cactusdigital.erp.fiscal.service.NotaFiscalEletronicaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Nota Fiscal Eletrônica", description = "RASCUNHO (composta a partir da Venda) → (emitir) → ENVIADA → AUTORIZADA/REJEITADA/ERRO, via ACBr Monitor")
@RestController
@RequestMapping("/api/v1/notas-fiscais")
@RequiredArgsConstructor
public class NotaFiscalEletronicaController {

    private final NotaFiscalEletronicaService service;

    @Operation(summary = "Listar notas fiscais eletrônicas")
    @ApiResponse(responseCode = "200", description = "Página de notas fiscais")
    @GetMapping
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public PageResponse<NotaFiscalEletronicaResponse> listar(Pageable pageable) {
        return PageResponse.of(service.listar(pageable));
    }

    @Operation(summary = "Buscar nota fiscal por id", description = "Inclui os itens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nota fiscal encontrada"),
            @ApiResponse(responseCode = "404", description = "Nota fiscal não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public NotaFiscalEletronicaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @Operation(summary = "Gerar nota fiscal a partir de uma venda",
            description = "A venda precisa estar CONFIRMADA (vendas-service). Compõe o RASCUNHO com itens/tributação já resolvida — não emite ainda, ver POST /notas-fiscais/{id}/emitir")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Nota fiscal composta em RASCUNHO"),
            @ApiResponse(responseCode = "404", description = "Venda, cliente, produto ou empresa não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Esta venda já tem nota fiscal gerada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Venda não está CONFIRMADA, ou empresa sem configuração fiscal completa", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "502", description = "Falha ao consultar vendas-service ou cadastro-base-service", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/de-venda/{vendaId}")
    @PreAuthorize("hasAuthority('FISCAL_EMITIR')")
    public ResponseEntity<NotaFiscalEletronicaResponse> gerarDeVenda(@PathVariable Long vendaId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.gerarDeVenda(vendaId));
    }

    @Operation(summary = "Emitir a nota fiscal via ACBr Monitor",
            description = "Monta o INI (modelo 55) e envia NFE.CriarEnviarNFe ao ACBr Monitor — sucesso vira AUTORIZADA, rejeição fiscal vira REJEITADA, falha de comunicação vira ERRO (nunca lança exceção que impeça rastrear o resultado)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retorno do ACBr Monitor processado (AUTORIZADA/REJEITADA/ERRO)"),
            @ApiResponse(responseCode = "404", description = "Nota fiscal não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Nota fiscal não está RASCUNHO/ERRO", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/emitir")
    @PreAuthorize("hasAuthority('FISCAL_EMITIR')")
    public NotaFiscalEletronicaResponse emitir(@PathVariable Long id) {
        return service.emitir(id);
    }
}
