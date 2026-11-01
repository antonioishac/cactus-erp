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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Nota Fiscal Eletrônica", description = "RASCUNHO (composta a partir da Venda) → (emitir) → AUTORIZADA/DENEGADA/REJEITADA/ERRO, transmitida direto à SEFAZ")
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

    @Operation(summary = "Emitir a nota fiscal na SEFAZ",
            description = "Monta o XML (NF-e 4.00, modelo 55), assina com o certificado A1 da empresa, valida no XSD e transmite em modo síncrono. "
                    + "Retorno fiscal vira AUTORIZADA/DENEGADA/REJEITADA (cStat e motivo gravados); falha de comunicação vira ERRO, com a chave gravada, e pode ser reenviada. "
                    + "Aceita RASCUNHO, REJEITADA (reenvio com o mesmo número após corrigir o cadastro) e ERRO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retorno da SEFAZ processado (AUTORIZADA/DENEGADA/REJEITADA/ERRO)"),
            @ApiResponse(responseCode = "404", description = "Nota fiscal não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Nota fiscal não está RASCUNHO/REJEITADA/ERRO", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Documento inválido no leiaute, tributação não suportada, certificado/cadeia não configurados ou produção desabilitada — nada foi enviado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{id}/emitir")
    @PreAuthorize("hasAuthority('FISCAL_EMITIR')")
    public NotaFiscalEletronicaResponse emitir(@PathVariable Long id) {
        return service.emitir(id);
    }

    @Operation(summary = "Baixar o XML processado (nfeProc)", description = "NF-e assinada + protocolo da SEFAZ. Só para notas AUTORIZADA/DENEGADA")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "XML da nota", content = @Content(mediaType = MediaType.APPLICATION_XML_VALUE)),
            @ApiResponse(responseCode = "404", description = "Nota não encontrada ou sem XML processado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping(value = "/{id}/xml", produces = MediaType.APPLICATION_XML_VALUE)
    @PreAuthorize("hasAuthority('FISCAL_CONSULTAR')")
    public ResponseEntity<String> baixarXml(@PathVariable Long id) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"nfe-" + id + ".xml\"")
                .body(service.buscarXmlProcessado(id));
    }
}
